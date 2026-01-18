package com.example.fit_ttacker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema oscuro con paleta Cove
private val CoveDarkColorScheme = darkColorScheme(
    primary = CoveBlue2,
    onPrimary = Color.White,
    primaryContainer = CoveBlue1,
    onPrimaryContainer = Color.White,

    secondary = CoveYellow1,
    onSecondary = Color.Black,
    secondaryContainer = CoveYellow2,
    onSecondaryContainer = Color.Black,

    tertiary = CoveBlue2,
    onTertiary = Color.White,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,

    error = ErrorColor,
    onError = Color.White,

    outline = CoveBlue2.copy(alpha = 0.5f),
    outlineVariant = CoveBlue1.copy(alpha = 0.3f)
)

// Tema claro con paleta Cove (opcional)
private val CoveLightColorScheme = lightColorScheme(
    primary = CoveBlue1,
    onPrimary = Color.White,
    primaryContainer = CoveBlue2,
    onPrimaryContainer = Color.White,

    secondary = CoveYellow1,
    onSecondary = Color.Black,
    secondaryContainer = CoveYellow2,
    onSecondaryContainer = Color.Black,

    background = Color(0xFFF5F9FC),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),

    error = ErrorColor,
    onError = Color.White
)

@Composable
fun PhotoFilterTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> CoveDarkColorScheme
        else -> CoveLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}