package cl.duoc.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.app.ui.dashboard.DashboardScreen
import cl.duoc.app.ui.vitalsigns.VitalesScreen
import cl.duoc.app.ui.alerts.AlertasScreen
import cl.duoc.app.ui.alerts.AlertsViewModel

/**
 * Estructura de navegación principal de VitalCare
 * Define 5 rutas principales con BottomNavigationBar
 *
 * Rutas:
 * - dashboard: Pantalla principal
 * - vitales: Signos vitales
 * - ubicacion: Ubicación (placeholder)
 * - alertas: Alertas del usuario
 * - clima: Información climática (placeholder)
 */
@Composable
fun VitalCareNavigation() {
    val navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize()) {
        // NavHost ocupa el espacio disponible, dejando espacio para BottomNavigationBar
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // ========== RUTA: DASHBOARD ==========
            composable(route = "dashboard") {
                DashboardScreen(
                    onNavigateToAlertas = { navigateTo(navController, "alertas") },
                    onNavigateToVitales = { navigateTo(navController, "vitales") },
                    onNavigateToUbicacion = { navigateTo(navController, "ubicacion") }
                )
            }

            // ========== RUTA: VITALES ==========
            composable(route = "vitales") {
                VitalesScreen(
                    onNavigateBack = { navigateTo(navController, "dashboard") }
                )
            }

            // ========== RUTA: UBICACIÓN ==========
            composable(route = "ubicacion") {
                UbicacionScreenPlaceholder(
                    onNavigateBack = { navigateTo(navController, "dashboard") }
                )
            }

            // ========== RUTA: ALERTAS ==========
            composable(route = "alertas") {
                val viewModel: AlertsViewModel = viewModel()
                AlertasScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // ========== RUTA: CLIMA ==========
            composable(route = "clima") {
                ClimaScreenPlaceholder(
                    onNavigateBack = { navigateTo(navController, "dashboard") }
                )
            }
        }

        // BottomNavigationBar profesional con Material 3
        VitalCareBottomNavigationBar(navController = navController)
    }
}

/**
 * BottomNavigationBar profesional con Material 3
 * Contiene 5 items: Dashboard, Vitales, Ubicación, Alertas, Clima
 */
@Composable
private fun VitalCareBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        // Item 1: Dashboard
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall) },
            selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
            onClick = { navigateTo(navController, "dashboard") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Item 2: Vitales
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Vitales",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Vitales", style = MaterialTheme.typography.labelSmall) },
            selected = currentDestination?.hierarchy?.any { it.route == "vitales" } == true,
            onClick = { navigateTo(navController, "vitales") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Item 3: Ubicación
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Ubicación", style = MaterialTheme.typography.labelSmall) },
            selected = currentDestination?.hierarchy?.any { it.route == "ubicacion" } == true,
            onClick = { navigateTo(navController, "ubicacion") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Item 4: Alertas
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alertas",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Alertas", style = MaterialTheme.typography.labelSmall) },
            selected = currentDestination?.hierarchy?.any { it.route == "alertas" } == true,
            onClick = { navigateTo(navController, "alertas") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Item 5: Clima
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = "Clima",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Clima", style = MaterialTheme.typography.labelSmall) },
            selected = currentDestination?.hierarchy?.any { it.route == "clima" } == true,
            onClick = { navigateTo(navController, "clima") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

/**
 * Función auxiliar para navegar con estado preservado
 */
private fun navigateTo(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Pantalla placeholder para Ubicación
 * Reemplazar con UbicacionScreen real cuando esté disponible
 */
@Composable
private fun UbicacionScreenPlaceholder(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Ubicación",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Pantalla de Ubicación",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Próximamente disponible",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Pantalla placeholder para Clima
 * Reemplazar con ClimaScreen real cuando esté disponible
 */
@Composable
private fun ClimaScreenPlaceholder(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = "Clima",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Pantalla de Clima",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Próximamente disponible",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

