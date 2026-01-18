package com.example.fit_ttacker.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.fit_ttacker.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class PhotoRepository(private val context: Context) {

    private val photosDir = File(context.filesDir, "photos")
    private val metadataFile = File(context.filesDir, "photos_metadata.json")

    init {
        // Crear directorio si no existe
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }

        // Crear archivo de metadatos si no existe
        if (!metadataFile.exists()) {
            metadataFile.writeText("[]")
        }
    }

    // Guardar foto con metadatos
    suspend fun savePhoto(
        bitmap: Bitmap,
        username: String,
        email: String,
        latitude: Double,
        longitude: Double,
        filterApplied: String
    ): Photo = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val fileName = "photo_${username}_$timestamp.jpg"
        val file = File(photosDir, fileName)

        // Guardar imagen
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        // Crear objeto Photo
        val photo = Photo(
            id = timestamp.toString(),
            username = username,
            email = email,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp,
            filePath = file.absolutePath,
            filterApplied = filterApplied
        )

        // Guardar metadatos
        saveMetadata(photo)

        photo
    }

    // Guardar metadatos en JSON
    private fun saveMetadata(photo: Photo) {
        val photos = getPhotos().toMutableList()
        photos.add(photo)

        val jsonArray = JSONArray()
        photos.forEach { p ->
            val jsonObject = JSONObject().apply {
                put("id", p.id)
                put("username", p.username)
                put("email", p.email)
                put("latitude", p.latitude)
                put("longitude", p.longitude)
                put("timestamp", p.timestamp)
                put("filePath", p.filePath)
                put("filterApplied", p.filterApplied)
            }
            jsonArray.put(jsonObject)
        }

        metadataFile.writeText(jsonArray.toString())
    }

    // Obtener todas las fotos guardadas
    fun getPhotos(): List<Photo> {
        return try {
            val jsonString = metadataFile.readText()
            val jsonArray = JSONArray(jsonString)

            (0 until jsonArray.length()).mapNotNull { i ->
                try {
                    val jsonObject = jsonArray.getJSONObject(i)
                    Photo(
                        id = jsonObject.getString("id"),
                        username = jsonObject.getString("username"),
                        email = jsonObject.getString("email"),
                        latitude = jsonObject.getDouble("latitude"),
                        longitude = jsonObject.getDouble("longitude"),
                        timestamp = jsonObject.getLong("timestamp"),
                        filePath = jsonObject.getString("filePath"),
                        filterApplied = jsonObject.optString("filterApplied", "Original")
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Obtener fotos por usuario
    fun getPhotosByUser(username: String): List<Photo> {
        return getPhotos().filter { it.username == username }
    }

    // Eliminar foto
    suspend fun deletePhoto(photo: Photo) = withContext(Dispatchers.IO) {
        // Eliminar archivo
        File(photo.filePath).delete()

        // Actualizar metadatos
        val photos = getPhotos().toMutableList()
        photos.removeAll { it.id == photo.id }

        val jsonArray = JSONArray()
        photos.forEach { p ->
            val jsonObject = JSONObject().apply {
                put("id", p.id)
                put("username", p.username)
                put("email", p.email)
                put("latitude", p.latitude)
                put("longitude", p.longitude)
                put("timestamp", p.timestamp)
                put("filePath", p.filePath)
                put("filterApplied", p.filterApplied)
            }
            jsonArray.put(jsonObject)
        }

        metadataFile.writeText(jsonArray.toString())
    }
}