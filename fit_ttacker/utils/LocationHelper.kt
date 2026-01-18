package com.example.fit_ttacker.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationHelper {

    fun getCurrentLocation(
        context: Context,
        onLocationReceived: (Double, Double) -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        onLocationReceived(it.latitude, it.longitude)
                    } ?: run {
                        // Si no hay ubicación, devolver coordenadas por defecto
                        onLocationReceived(0.0, 0.0)
                    }
                }
                .addOnFailureListener {
                    // En caso de error, devolver coordenadas por defecto
                    onLocationReceived(0.0, 0.0)
                }
        } else {
            // Sin permisos, devolver coordenadas por defecto
            onLocationReceived(0.0, 0.0)
        }
    }
}