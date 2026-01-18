package com.example.fit_ttacker.ui.theme.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit_ttacker.data.repository.PhotoRepository
import com.example.fit_ttacker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    email: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val photoRepository = remember { PhotoRepository(context) }
    val userPhotos = remember { photoRepository.getPhotosByUser(username) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Perfil",
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(60.dp),
                    color = CoveBlue2.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Avatar de usuario",
                            modifier = Modifier.size(80.dp),
                            tint = CoveBlue2
                        )
                    }
                }

                // Nombre
                Text(
                    text = username,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = email,
                    fontSize = 16.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Estadísticas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatsCard(
                        count = userPhotos.size.toString(),
                        title = "Fotos",
                        iconVector = Icons.Default.PhotoLibrary
                    )

                    StatsCard(
                        count = userPhotos.distinctBy { it.filterApplied }.size.toString(),
                        title = "Filtros",
                        iconVector = Icons.Default.FilterVintage
                    )
                }

                // Opciones de perfil
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column {
                        OptionItem(
                            iconVector = Icons.Default.Info,
                            title = "Información de la cuenta",
                            onClick = { }
                        )

                        HorizontalDivider(
                            color = DarkSurface,
                            thickness = 1.dp
                        )

                        OptionItem(
                            iconVector = Icons.Default.Settings,
                            title = "Configuración",
                            onClick = { }
                        )

                        HorizontalDivider(
                            color = DarkSurface,
                            thickness = 1.dp
                        )

                        OptionItem(
                            iconVector = Icons.Default.Logout,
                            title = "Cerrar sesión",
                            onClick = onLogout,
                            isRed = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    count: String,
    title: String,
    iconVector: ImageVector
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                iconVector,
                contentDescription = null,
                tint = CoveYellow1,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun OptionItem(
    iconVector: ImageVector,
    title: String,
    onClick: () -> Unit,
    isRed: Boolean = false
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                iconVector,
                contentDescription = null,
                tint = if (isRed) ErrorColor else CoveBlue2
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = if (isRed) ErrorColor else TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = TextSecondary.copy(alpha = 0.5f)
            )
        }
    }
}