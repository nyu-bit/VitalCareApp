package cl.duoc.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cl.duoc.app.ui.HomeScreen
import cl.duoc.app.ui.HomeViewModel
import cl.duoc.app.ui.pacientes.PacienteDetailScreen
import cl.duoc.app.ui.pacientes.PacienteFormScreen
import cl.duoc.app.ui.pacientes.PacientesListScreen
import cl.duoc.app.ui.citas.CitaDetailScreen
import cl.duoc.app.ui.citas.CitaFormScreen
import cl.duoc.app.ui.citas.CitasListScreen
import cl.duoc.app.ui.especialidades.EspecialidadesListScreen

/**
 * NavHost principal de la aplicación
 * Configura todas las rutas y destinos de navegación
 */
@Composable
fun VitalCareNavHost(
    navController: NavHostController,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = modifier
    ) {
        // Pantalla principal
        composable(NavRoutes.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToPacientes = {
                    navController.navigate(NavRoutes.PacienteList.route)
                },
                onNavigateToCitas = {
                    navController.navigate(NavRoutes.CitaList.route)
                },
                onNavigateToEspecialidades = {
                    navController.navigate(NavRoutes.EspecialidadList.route)
                },
                onPacienteClick = { pacienteId ->
                    navController.navigate(NavRoutes.PacienteDetail.createRoute(pacienteId))
                }
            )
        }
        
        // Lista de pacientes
        composable(NavRoutes.PacienteList.route) {
            PacientesListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() },
                onPacienteClick = { pacienteId ->
                    navController.navigate(NavRoutes.PacienteDetail.createRoute(pacienteId))
                },
                onAddPaciente = {
                    navController.navigate(NavRoutes.PacienteForm.route)
                }
            )
        }
        
        // Formulario de paciente
        composable(NavRoutes.PacienteForm.route) {
            PacienteFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() },
                onSaveSuccess = { navController.navigateUp() }
            )
        }
        
        // Detalle de paciente
        composable(
            route = NavRoutes.PacienteDetail.route,
            arguments = listOf(
                navArgument(NavRoutes.ARG_PACIENTE_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getLong(NavRoutes.ARG_PACIENTE_ID) ?: 0L
            PacienteDetailScreen(
                pacienteId = pacienteId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Lista de citas
        composable(NavRoutes.CitaList.route) {
            CitasListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() },
                onCitaClick = { citaId ->
                    navController.navigate(NavRoutes.CitaDetail.createRoute(citaId))
                },
                onAddCita = {
                    navController.navigate(NavRoutes.CitaForm.route)
                }
            )
        }
        
        // Formulario de cita
        composable(NavRoutes.CitaForm.route) {
            CitaFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() },
                onSaveSuccess = { navController.navigateUp() }
            )
        }
        
        // Detalle de cita
        composable(
            route = NavRoutes.CitaDetail.route,
            arguments = listOf(
                navArgument(NavRoutes.ARG_CITA_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val citaId = backStackEntry.arguments?.getLong(NavRoutes.ARG_CITA_ID) ?: 0L
            CitaDetailScreen(
                citaId = citaId,
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        // Lista de especialidades
        composable(NavRoutes.EspecialidadList.route) {
            EspecialidadesListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
