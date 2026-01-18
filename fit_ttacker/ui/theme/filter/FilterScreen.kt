
package com.example.fit_ttacker.ui.theme.filter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit_ttacker.data.repository.PhotoRepository
import com.example.fit_ttacker.utils.OpenCVHelper
import com.example.fit_ttacker.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    imageUri: Uri,
    location: Pair<Double, Double>?,
    username: String,
    onPhotoSaved: () -> Unit,
    onBack: () -> Unit = {},
    email: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photoRepository = remember { PhotoRepository(context) }

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var filteredBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedFilter by remember { mutableStateOf("Original") }
    var isProcessing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val filters = listOf(
        "Original",
        "Escala de Grises",
        "Sepia",
        "Desenfoque",
        "Nitidez",
        "Invertir",
        "Brillo+",
        "Contraste+",
        "Vintage"
    )

    // Cargar imagen
    LaunchedEffect(imageUri) {
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(imageUri)?.use {
                originalBitmap = BitmapFactory.decodeStream(it)
                filteredBitmap = originalBitmap
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Aplicar Filtros",
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground,
                            DarkSurface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // Imagen principal
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        filteredBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Imagen con filtro",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Indicador de procesamiento sobre la imagen
                        if (isProcessing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = CoveYellow1,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Aplicando filtro...",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Título de filtros
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        Icons.Default.FilterVintage,
                        contentDescription = null,
                        tint = CoveBlue2,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Filtros disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Lista de filtros
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                                scope.launch {
                                    isProcessing = true
                                    filteredBitmap = withContext(Dispatchers.Default) {
                                        originalBitmap?.let { original ->
                                            when (filter) {
                                                "Escala de Grises" -> OpenCVHelper.applyGrayscaleFilter(context, original)
                                                "Sepia" -> OpenCVHelper.applySepiaFilter(context, original)
                                                "Desenfoque" -> OpenCVHelper.applyBlurFilter(context, original)
                                                "Nitidez" -> OpenCVHelper.applySharpenFilter(context, original)
                                                "Invertir" -> OpenCVHelper.applyInvertFilter(context, original)
                                                "Brillo+" -> OpenCVHelper.applyBrightnessFilter(context, original)
                                                "Contraste+" -> OpenCVHelper.applyContrastFilter(context, original)
                                                "Vintage" -> OpenCVHelper.applyVintageFilter(context, original)
                                                else -> original
                                            }
                                        }
                                    }
                                    isProcessing = false
                                }
                            },
                            enabled = !isProcessing && !isSaving,
                            label = {
                                Text(
                                    filter,
                                    fontSize = 14.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CardBackground,
                                selectedContainerColor = CoveYellow1,
                                labelColor = TextPrimary,
                                selectedLabelColor = Color.Black
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == filter,
                                borderColor = CoveBlue2.copy(alpha = 0.3f),
                                selectedBorderColor = CoveYellow1,
                                disabledBorderColor = CoveBlue2.copy(alpha = 0.1f),
                                disabledSelectedBorderColor = CoveYellow1.copy(alpha = 0.3f),
                                borderWidth = 1.dp,
                                selectedBorderWidth = 2.dp
                            )
                        )
                    }
                }

                // Información
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Text(
                            text = "Información de la foto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CoveBlue2,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Usuario
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = CoveBlue2,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Usuario: $username",
                                color = TextPrimary
                            )
                        }

                        // Filtro
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.FilterVintage,
                                contentDescription = null,
                                tint = CoveBlue2,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Filtro: $selectedFilter",
                                color = TextPrimary
                            )
                        }

                        // Ubicación
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CoveBlue2,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            location?.let { (lat, lon) ->
                                Text(
                                    text = "Ubicación: ${String.format("%.4f", lat)}, ${String.format("%.4f", lon)}",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } ?: Text(
                                text = "Sin ubicación GPS",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón guardar
                Button(
                    onClick = {
                        scope.launch {
                            isSaving = true
                            try {
                                filteredBitmap?.let { bitmap ->
                                    location?.let { (lat, lon) ->
                                        photoRepository.savePhoto(
                                            bitmap = bitmap,
                                            username = username,
                                            email = "",
                                            latitude = lat,
                                            longitude = lon,
                                            filterApplied = selectedFilter
                                        )

                                        Toast.makeText(
                                            context,
                                            "Foto guardada exitosamente",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        onPhotoSaved()
                                    } ?: Toast.makeText(
                                        context,
                                        "No se pudo obtener la ubicación GPS",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error al guardar: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isProcessing && !isSaving && location != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CoveYellow1,
                        disabledContainerColor = CoveYellow1.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    } else {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isSaving) "Guardando..." else "Guardar Fotografía",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
