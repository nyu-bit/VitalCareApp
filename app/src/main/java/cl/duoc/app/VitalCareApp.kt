package cl.duoc.app

import androidx.compose.runtime.Composable
import cl.duoc.app.navigation.AnimatedNavGraph
import cl.duoc.app.ui.theme.VitalCareTheme

/**
 * Punto de entrada principal de la aplicación VitalCare
 * Maneja la navegación con animaciones y tema de Material Design 3
 * HU-08: Animaciones visuales y transiciones suaves
 */
@Composable
fun VitalCareApp() {
    VitalCareTheme {
        AnimatedNavGraph()
    }
}
