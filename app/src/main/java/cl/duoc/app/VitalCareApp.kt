package cl.duoc.app

import androidx.compose.runtime.*
import cl.duoc.app.ui.login.LoginScreen
import cl.duoc.app.ui.profile.ProfileScreen

/**
 * Punto de entrada principal de la aplicación VitalCare
 * Maneja la navegación básica entre pantallas
 */
@Composable
fun VitalCareApp() {
    var currentScreen by remember { mutableStateOf("login") }
    
    when (currentScreen) {
        "login" -> LoginScreen(
            onLoginSuccess = { currentScreen = "profile" }
        )
        "profile" -> ProfileScreen()
        else -> LoginScreen(
            onLoginSuccess = { currentScreen = "profile" }
        )
    }
}
