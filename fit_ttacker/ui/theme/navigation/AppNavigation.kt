package com.example.fit_ttacker.ui.theme.navigation

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fit_ttacker.ui.theme.camera.CameraScreen
import com.example.fit_ttacker.ui.theme.filter.FilterScreen
import com.example.fit_ttacker.ui.theme.login.LoginScreen
import com.example.fit_ttacker.ui.theme.register.RegisterScreen
import com.example.fit_ttacker.ui.theme.gallery.GalleryScreen
import com.example.fit_ttacker.ui.theme.home.HomeScreen
import com.example.fit_ttacker.ui.theme.profile.ProfileScreen
import com.example.fit_ttacker.utils.PhotoLocation

// Rutas de navegación
private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CAMERA = "camera"
    const val FILTER = "filter"
    const val GALLERY = "gallery"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Estado persistente que sobrevive a recreaciones
    var currentUser by rememberSaveable { mutableStateOf<Pair<String, String>?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPhotoLocation by remember { mutableStateOf<PhotoLocation?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // ============ PANTALLA DE LOGIN ============
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { username, email ->
                    currentUser = Pair(username, email)
                    navController.navigateToHome()
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // ============ PANTALLA DE REGISTRO ============
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ============ PANTALLA DE HOME ============
        composable(Routes.HOME) {
            currentUser?.let { (username, email) ->
                HomeScreen(
                    username = username,
                    email = email,
                    onSelectPhoto = {
                        navController.navigate(Routes.CAMERA)
                    },
                    onViewGallery = {
                        navController.navigate(Routes.GALLERY)
                    },
                    onViewProfile = {
                        navController.navigate(Routes.PROFILE)
                    },
                    onLogout = {
                        currentUser = null
                        selectedImageUri = null
                        selectedPhotoLocation = null
                        navController.navigateToLogin()
                    }
                )
            } ?: run {
                LaunchedEffect(Unit) {
                    navController.navigateToLogin()
                }
            }
        }

        // ============ PANTALLA DE CÁMARA ============
        composable(Routes.CAMERA) {
            CameraScreen(
                onPhotoSelected = { uri, location ->
                    selectedImageUri = uri
                    selectedPhotoLocation = location
                    navController.navigate(Routes.FILTER)
                },
                onViewGallery = {
                    navController.navigate(Routes.GALLERY)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ============ PANTALLA DE FILTROS ============
        composable(Routes.FILTER) {
            val uri = selectedImageUri
            val user = currentUser
            val location = selectedPhotoLocation

            if (uri != null && user != null) {
                val (username, email) = user
                FilterScreen(
                    imageUri = uri,
                    username = username,
                    email = email,
                    location = location?.let { Pair(it.latitude, it.longitude) },
                    onPhotoSaved = {
                        selectedImageUri = null
                        selectedPhotoLocation = null
                        navController.navigate(Routes.GALLERY) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // ============ PANTALLA DE GALERÍA ============
        composable(Routes.GALLERY) {
            currentUser?.let { (username, _) ->
                GalleryScreen(
                    username = username,
                    onBack = {
                        navController.popBackStack()
                    },
                    onTakeNewPhoto = {
                        navController.navigate(Routes.CAMERA) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            } ?: run {
                LaunchedEffect(Unit) {
                    navController.navigateToLogin()
                }
            }
        }

        // ============ PANTALLA DE PERFIL ============
        composable(Routes.PROFILE) {
            currentUser?.let { (username, email) ->
                ProfileScreen(
                    username = username,
                    email = email,
                    onBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        currentUser = null
                        selectedImageUri = null
                        selectedPhotoLocation = null
                        navController.navigateToLogin()
                    }
                )
            } ?: run {
                LaunchedEffect(Unit) {
                    navController.navigateToLogin()
                }
            }
        }
    }
}

// ============ EXTENSIONES DE NAVEGACIÓN ============

private fun NavHostController.navigateToLogin() {
    navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToHome() {
    navigate(Routes.HOME) {
        popUpTo(Routes.LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

/*
package com.example.fit_ttacker.ui.theme.navigation
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fit_ttacker.ui.theme.camera.CameraScreen
import com.example.fit_ttacker.ui.theme.filter.FilterScreen
import com.example.fit_ttacker.ui.theme.login.LoginScreen
import com.example.fit_ttacker.ui.theme.register.RegisterScreen
import com.example.fit_ttacker.ui.theme.gallery.GalleryScreen
import com.example.fit_ttacker.ui.theme.home.HomeScreen
import com.example.fit_ttacker.ui.theme.profile.ProfileScreen

// Rutas de navegación
private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CAMERA = "camera"
    const val FILTER = "filter"
    const val GALLERY = "gallery"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Estado persistente que sobrevive a recreaciones
    var currentUser by rememberSaveable { mutableStateOf<Pair<String, String>?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // ============ PANTALLA DE LOGIN ============
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { username, email ->
                    currentUser = Pair(username, email)
                    navController.navigateToHome()
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // ============ PANTALLA DE REGISTRO ============
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Volver a login después de registrarse
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ============ PANTALLA DE HOME ============
        composable(Routes.HOME) {
            currentUser?.let { (username, email) ->
                HomeScreen(
                    username = username,
                    email = email,
                    onSelectPhoto = {
                        navController.navigate(Routes.CAMERA)
                    },
                    onViewGallery = {
                        navController.navigate(Routes.GALLERY)
                    },
                    onViewProfile = {
                        navController.navigate(Routes.PROFILE)
                    },
                    onLogout = {
                        currentUser = null
                        selectedImageUri = null
                        navController.navigateToLogin()
                    }
                )
            } ?: run {
                // Si no hay usuario, redirigir a login
                LaunchedEffect(Unit) {
                    navController.navigateToLogin()
                }
            }
        }

        // ============ PANTALLA DE CÁMARA ============
        composable(Routes.CAMERA) {
            CameraScreen(
                onPhotoSelected = { uri ->
                    selectedImageUri = uri
                    navController.navigate(Routes.FILTER)
                },
                onViewGallery = {
                    navController.navigate(Routes.GALLERY)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ============ PANTALLA DE FILTROS ============
        composable(Routes.FILTER) {
            val uri = selectedImageUri
            val user = currentUser

            if (uri != null && user != null) {
                val (username, email) = user
                FilterScreen(
                    imageUri = uri,
                    username = username,
                    email = email,
                    location=null,
                    onPhotoSaved = {
                        // Limpiar la imagen seleccionada después de guardar
                        selectedImageUri = null
                        navController.navigate(Routes.GALLERY) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                // Si no hay imagen o usuario, volver atrás
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // ============ PANTALLA DE GALERÍA ============
        composable(Routes.GALLERY) {
            currentUser?.let { (username, _) ->
                GalleryScreen(
                    username = username,
                    onBack = {
                        navController.popBackStack()
                    },
                    onTakeNewPhoto = {
                        navController.navigate(Routes.CAMERA) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            } ?: run {
                // Si no hay usuario, redirigir a login
                LaunchedEffect(Unit) {
                    navController.navigateToLogin()
                }
            }
        }

        // ============ PANTALLA DE PERFIL ============
        composable(Routes.PROFILE) {
            currentUser?.let { (username, email) ->
                ProfileScreen(
                    username = username,
                    email = email,
                    onBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        currentUser = null
                        selectedImageUri = null
                        navController.navigateToLogin()
                    }
                )
            } ?: run {
                // Si no hay usuario, redirigir a login
                LaunchedEffect(Unit) {
                    navController.navigateToLogin()
                }
            }
        }
    }
}

// ============ EXTENSIONES DE NAVEGACIÓN ============
// Funciones helper para navegación más limpia y consistente

private fun NavHostController.navigateToLogin() {
    navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToHome() {
    navigate(Routes.HOME) {
        popUpTo(Routes.LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

*/
