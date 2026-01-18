package com.example.fit_ttacker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.fit_ttacker.ui.theme.PhotoFilterTheme
import com.example.fit_ttacker.ui.theme.navigation.AppNavigation
import com.example.fit_ttacker.utils.OpenCVHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar OpenCV
        val opencvInitialized = OpenCVHelper.initialize(this)

        // Log para verificar inicialización (opcional)
        if (opencvInitialized) {
            println(" OpenCV inicializado correctamente")
        } else {
            println(" OpenCV no se pudo inicializar")
        }

        setContent {
            // Usar el tema PhotoFilterTheme con la paleta Cove
            PhotoFilterTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}