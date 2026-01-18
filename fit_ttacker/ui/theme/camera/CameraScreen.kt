package com.example.fit_ttacker.ui.theme.camera

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.fit_ttacker.utils.PermissionHelper
import com.example.fit_ttacker.utils.ExifHelper
import com.example.fit_ttacker.utils.PhotoLocation
import com.example.fit_ttacker.ui.theme.*

private const val TAG = "CameraScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onPhotoSelected: (Uri, PhotoLocation) -> Unit,
    onViewGallery: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var photoLocation by remember { mutableStateOf(PhotoLocation()) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var showDebugDialog by remember { mutableStateOf(false) }
    var debugInfo by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d(TAG, "Imagen seleccionada: $uri")
        uri?.let {
            selectedImageUri = it
            isLoadingLocation = true

            // Extraer coordenadas cuando se selecciona la imagen
            try {
                photoLocation = ExifHelper.getLocationFromUri(context, it)
                Log.d(TAG, "Ubicación extraída: $photoLocation")
            } catch (e: Exception) {
                Log.e(TAG, "Error al extraer ubicación", e)
                photoLocation = PhotoLocation()
            } finally {
                isLoadingLocation = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        Log.d(TAG, "Permisos otorgados: $allGranted")
        if (allGranted) {
            galleryLauncher.launch("image/*")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Seleccionar Foto",
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver al menú",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                ),
                actions = {
                    IconButton(onClick = onViewGallery) {
                        Icon(
                            Icons.Default.Collections,
                            contentDescription = "Ver galería",
                            tint = CoveBlue2
                        )
                    }
                }
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                selectedImageUri?.let { uri ->
                    // Vista previa de la imagen seleccionada
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBackground
                        )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Información de ubicación
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable {
                                debugInfo = ExifHelper.debugExifData(context, uri)
                                showDebugDialog = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Ubicación",
                                        tint = if (photoLocation.isValid()) CoveBlue2 else TextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Ubicación GPS",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                        if (isLoadingLocation) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = CoveBlue2
                                            )
                                        } else if (photoLocation.isValid()) {
                                            Text(
                                                text = "${String.format("%.6f", photoLocation.latitude)}, ${String.format("%.6f", photoLocation.longitude)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = CoveBlue2,
                                                fontWeight = FontWeight.Medium
                                            )
                                        } else {
                                            Text(
                                                text = "Sin datos de ubicación",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Ver información EXIF",
                                    tint = CoveBlue2,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (!isLoadingLocation && !photoLocation.isValid()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💡 Toca aquí para ver información de depuración",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            Log.d(TAG, "Aplicando filtros con ubicación: $photoLocation")
                            onPhotoSelected(uri, photoLocation)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoveYellow1
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoFixHigh,
                            contentDescription = "Aplicar filtros",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Aplicar Filtros",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            selectedImageUri = null
                            photoLocation = PhotoLocation()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(CoveBlue2, CoveBlue2))
                        )
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = "Cambiar foto",
                            tint = CoveBlue2
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Seleccionar Otra Foto",
                            color = TextPrimary
                        )
                    }
                } ?: run {
                    // Icono principal
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Icono de galería",
                        tint = CoveBlue2,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Sube una fotografía",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Selecciona una foto de tu galería para aplicarle filtros",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 48.dp)
                    )

                    Button(
                        onClick = {
                            if (PermissionHelper.hasCameraPermission(context)) {
                                galleryLauncher.launch("image/*")
                            } else {
                                permissionLauncher.launch(PermissionHelper.STORAGE_PERMISSIONS)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoveYellow1
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = "Seleccionar foto",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Seleccionar Foto",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Diálogo de debug
    if (showDebugDialog) {
        AlertDialog(
            onDismissRequest = { showDebugDialog = false },
            title = {
                Text("Información EXIF", color = TextPrimary)
            },
            text = {
                Text(
                    text = debugInfo,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            },
            confirmButton = {
                TextButton(onClick = { showDebugDialog = false }) {
                    Text("Cerrar", color = CoveBlue2)
                }
            },
            containerColor = CardBackground
        )
    }
}
