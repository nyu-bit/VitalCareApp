package cl.duoc.app.navigation

/**
 * Rutas de navegación de la aplicación
 * Centraliza todas las rutas para evitar strings mágicos
 */
sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object PacienteList : NavRoutes("pacientes")
    object PacienteForm : NavRoutes("pacientes/form")
    object PacienteDetail : NavRoutes("pacientes/{pacienteId}") {
        fun createRoute(pacienteId: Long) = "pacientes/$pacienteId"
    }
    object CitaList : NavRoutes("citas")
    object CitaForm : NavRoutes("citas/form")
    object CitaDetail : NavRoutes("citas/{citaId}") {
        fun createRoute(citaId: Long) = "citas/$citaId"
    }
    object EspecialidadList : NavRoutes("especialidades")
    
    companion object {
        // Argumentos de navegación
        const val ARG_PACIENTE_ID = "pacienteId"
        const val ARG_CITA_ID = "citaId"
    }
}
