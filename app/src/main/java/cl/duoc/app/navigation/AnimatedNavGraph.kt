package cl.duoc.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.app.ui.dashboard.DashboardScreen
import cl.duoc.app.ui.login.LoginScreen
import cl.duoc.app.ui.profile.ProfileScreen

/**
 * Destinos de navegación
 */
object NavigationDestinations {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"
}

/**
 * NavGraph con animaciones usando la API nativa de Compose
 * HU-08: Animaciones visuales y transiciones suaves
 */
@Composable
fun AnimatedNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationDestinations.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(
            route = NavigationDestinations.LOGIN
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationDestinations.DASHBOARD) {
                        // Limpiar el back stack para evitar volver al login
                        popUpTo(NavigationDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Dashboard
        composable(
            route = NavigationDestinations.DASHBOARD
        ) {
            DashboardScreen(
                onNavigateToProfile = {
                    navController.navigate(NavigationDestinations.PROFILE)
                }
            )
        }
        
        // Pantalla de Profile
        composable(
            route = NavigationDestinations.PROFILE
        ) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
