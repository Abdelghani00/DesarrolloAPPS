package com.example.fit_ttacker.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

data class PhotoLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    //  CORREGIDO: Usar AND en lugar de OR
    fun isValid(): Boolean = latitude != 0.0 && longitude != 0.0

    override fun toString(): String {
        return if (isValid()) {
            "Lat: ${String.format("%.6f", latitude)}, Lon: ${String.format("%.6f", longitude)}"
        } else {
            "Sin ubicación GPS"
        }
    }
}

object ExifHelper {
    private const val TAG = "ExifHelper"

    fun getLocationFromUri(context: Context, uri: Uri): PhotoLocation {
        Log.d(TAG, "🔍 Intentando extraer ubicación de: $uri")

        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

            if (inputStream == null) {
                Log.e(TAG, " No se pudo abrir el InputStream")
                return PhotoLocation()
            }

            inputStream.use { stream ->
                val exif = ExifInterface(stream)

                // Método 1: getLatLong (más directo y confiable)
                val latLong = FloatArray(2)
                val hasLatLong = exif.getLatLong(latLong)

                if (hasLatLong && (latLong[0] != 0.0f || latLong[1] != 0.0f)) {
                    Log.d(TAG, " Coordenadas encontradas (Método 1): Lat=${latLong[0]}, Lon=${latLong[1]}")
                    return PhotoLocation(
                        latitude = latLong[0].toDouble(),
                        longitude = latLong[1].toDouble()
                    )
                }

                // Método 2: Leer atributos GPS directamente
                val latStr = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                val lonStr = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
                val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
                val lonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

                Log.d(TAG, " GPS_LATITUDE: $latStr")
                Log.d(TAG, " GPS_LONGITUDE: $lonStr")
                Log.d(TAG, " GPS_LATITUDE_REF: $latRef")
                Log.d(TAG, " GPS_LONGITUDE_REF: $lonRef")

                if (!latStr.isNullOrEmpty() && !lonStr.isNullOrEmpty() &&
                    latStr != "0/1,0/1,0/1" && lonStr != "0/1,0/1,0/1") {

                    val lat = convertToDegree(latStr)
                    val lon = convertToDegree(lonStr)

                    // Solo procesar si las coordenadas son válidas
                    if (lat != 0.0 && lon != 0.0) {
                        // Ajustar signo según la referencia (N/S/E/W)
                        val finalLat = if (latRef == "S") -lat else lat
                        val finalLon = if (lonRef == "W") -lon else lon

                        Log.d(TAG, " Coordenadas convertidas (Método 2): Lat=$finalLat, Lon=$finalLon")
                        return PhotoLocation(finalLat, finalLon)
                    }
                }

                Log.w(TAG, " No se encontraron datos GPS válidos en la imagen")
                PhotoLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, " Error al extraer ubicación: ${e.message}", e)
            e.printStackTrace()
            PhotoLocation()
        }
    }

    /**
     * Convierte coordenadas GPS en formato DMS (grados, minutos, segundos) a decimal
     * Formato esperado: "51/1,30/1,0/1" -> 51.5 grados
     * Formato alternativo: "51,30,0" -> 51.5 grados
     */
    private fun convertToDegree(stringDMS: String): Double {
        return try {
            Log.d(TAG, " Convirtiendo DMS: $stringDMS")

            val dms = stringDMS.split(",")
                .map { part ->
                    val trimmed = part.trim()
                    if (trimmed.contains("/")) {
                        // Formato fracción: "51/1"
                        val parts = trimmed.split("/")
                        if (parts.size == 2 && parts[1] != "0") {
                            parts[0].toDouble() / parts[1].toDouble()
                        } else {
                            0.0
                        }
                    } else {
                        // Formato decimal directo: "51.5"
                        trimmed.toDoubleOrNull() ?: 0.0
                    }
                }

            if (dms.size >= 3) {
                val degrees = dms[0]
                val minutes = dms[1]
                val seconds = dms[2]
                val result = degrees + (minutes / 60.0) + (seconds / 3600.0)
                Log.d(TAG, " Resultado conversión: $result° (de $degrees° $minutes' $seconds\")")
                result
            } else {
                Log.w(TAG, " Formato DMS inválido, se esperaban 3 partes")
                0.0
            }
        } catch (e: Exception) {
            Log.e(TAG, " Error convirtiendo coordenadas DMS: $stringDMS", e)
            0.0
        }
    }

    /**
     * Obtiene información completa de EXIF para depuración
     */
    fun debugExifData(context: Context, uri: Uri): String {
        val debugInfo = StringBuilder()
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)

                debugInfo.append("=== EXIF DEBUG INFO ===\n")
                debugInfo.append("URI: $uri\n\n")

                // GPS Data
                debugInfo.append("--- DATOS GPS ---\n")
                val gpsLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "N/A"
                val gpsLon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "N/A"
                val gpsLatRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) ?: "N/A"
                val gpsLonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF) ?: "N/A"

                debugInfo.append("GPS_LATITUDE: $gpsLat\n")
                debugInfo.append("GPS_LONGITUDE: $gpsLon\n")
                debugInfo.append("GPS_LATITUDE_REF: $gpsLatRef\n")
                debugInfo.append("GPS_LONGITUDE_REF: $gpsLonRef\n")
                debugInfo.append("GPS_ALTITUDE: ${exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE) ?: "N/A"}\n")
                debugInfo.append("GPS_TIMESTAMP: ${exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP) ?: "N/A"}\n")
                debugInfo.append("GPS_DATESTAMP: ${exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP) ?: "N/A"}\n")

                // Intentar obtener con getLatLong
                val latLong = FloatArray(2)
                val hasCoords = exif.getLatLong(latLong)
                debugInfo.append("\n--- RESULTADO getLatLong() ---\n")
                if (hasCoords) {
                    debugInfo.append(" Latitud: ${latLong[0]}\n")
                    debugInfo.append(" Longitud: ${latLong[1]}\n")
                } else {
                    debugInfo.append(" No se pudieron obtener coordenadas\n")
                }

                // Camera Data
                debugInfo.append("\n--- DATOS DE CÁMARA ---\n")
                debugInfo.append("DATETIME: ${exif.getAttribute(ExifInterface.TAG_DATETIME) ?: "N/A"}\n")
                debugInfo.append("MAKE: ${exif.getAttribute(ExifInterface.TAG_MAKE) ?: "N/A"}\n")
                debugInfo.append("MODEL: ${exif.getAttribute(ExifInterface.TAG_MODEL) ?: "N/A"}\n")
                debugInfo.append("ORIENTATION: ${exif.getAttribute(ExifInterface.TAG_ORIENTATION) ?: "N/A"}\n")
                debugInfo.append("IMAGE_WIDTH: ${exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) ?: "N/A"}\n")
                debugInfo.append("IMAGE_LENGTH: ${exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) ?: "N/A"}\n")

                // Software
                debugInfo.append("SOFTWARE: ${exif.getAttribute(ExifInterface.TAG_SOFTWARE) ?: "N/A"}\n")
            }
        } catch (e: Exception) {
            debugInfo.append("\n ERROR: ${e.message}\n")
            debugInfo.append("Stack trace:\n")
            debugInfo.append(e.stackTraceToString())
            Log.e(TAG, "Error obteniendo datos EXIF", e)
        }

        val result = debugInfo.toString()
        Log.d(TAG, result)
        return result
    }

    /**
     * Verifica si una imagen tiene datos GPS válidos
     */
    fun hasGpsData(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                val latLong = FloatArray(2)
                val hasCoords = exif.getLatLong(latLong)
                hasCoords && (latLong[0] != 0.0f || latLong[1] != 0.0f)
            } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error verificando GPS", e)
            false
        }
    }
}