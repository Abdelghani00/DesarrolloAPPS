package com.example.fit_ttacker.data.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

data class Photo(
    val id: String = System.currentTimeMillis().toString(),
    val username: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val filePath: String,
    val filterApplied: String = "Original"
) {
    // Helper para cargar el bitmap desde el archivo
    fun getBitmap(): Bitmap? {
        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            null
        }
    }

    // Helper para obtener la fecha formateada
    fun getFormattedDate(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}