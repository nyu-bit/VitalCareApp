package cl.duoc.app.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import cl.duoc.app.ui.animations.Transitions
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
 * NavGraph con animaciones usando Accompanist
 * HU-08: Animaciones visuales y transiciones suaves
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedNavGraph(
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = NavigationDestinations.LOGIN
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { Transitions.slideInFromRight() },
        exitTransition = { Transitions.slideOutToLeft() },
        popEnterTransition = { Transitions.slideInFromLeft() },
        popExitTransition = { Transitions.slideOutToRight() }
    ) {
        // Pantalla de Login
        composable(
            route = NavigationDestinations.LOGIN,
            enterTransition = {
                when (initialState.destination.route) {
                    NavigationDestinations.DASHBOARD -> Transitions.slideInFromLeft()
                    else -> Transitions.fadeTransition()
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    NavigationDestinations.DASHBOARD -> Transitions.slideOutToLeft()
                    else -> Transitions.fadeOutTransition()
                }
            }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationDestinations.DASHBOARD) {
                        // Opcional: limpiar el back stack
                        // popUpTo(NavigationDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        // Pantalla de Dashboard
        composable(
            route = NavigationDestinations.DASHBOARD,
            enterTransition = {
                when (initialState.destination.route) {
                    NavigationDestinations.LOGIN -> Transitions.slideInFromRight()
                    NavigationDestinations.PROFILE -> Transitions.slideInFromLeft()
                    else -> Transitions.fadeTransition()
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    NavigationDestinations.PROFILE -> Transitions.slideOutToLeft()
                    NavigationDestinations.LOGIN -> Transitions.slideOutToRight()
                    else -> Transitions.fadeOutTransition()
                }
            }
        ) {
            DashboardScreen(
                onNavigateToProfile = {
                    navController.navigate(NavigationDestinations.PROFILE)
                }
            )
        }
        
        // Pantalla de Profile
        composable(
            route = NavigationDestinations.PROFILE,
            enterTransition = {
                Transitions.slideInFromRight()
            },
            exitTransition = {
                Transitions.slideOutToRight()
            }
        ) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
