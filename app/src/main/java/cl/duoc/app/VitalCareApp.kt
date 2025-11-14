package cl.duoc.app

import androidx.compose.runtime.Composable
import cl.duoc.app.navigation.AnimatedNavGraph

/**
 * Punto de entrada principal de la aplicación VitalCare
 * Maneja la navegación con animaciones
 * HU-08: Animaciones visuales y transiciones suaves
 */
@Composable
fun VitalCareApp() {
    AnimatedNavGraph()
}
