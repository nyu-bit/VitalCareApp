package cl.duoc.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.duoc.app.ui.navigation.VitalCareNavigation
import cl.duoc.app.ui.theme.VitalCareTheme

/**
 * MainActivity de VitalCare
 * Punto de entrada de la aplicación
 * Contiene el NavHost con todas las pantallas
 */
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VitalCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // NavHost con BottomNavigationBar integrada
                    VitalCareNavigation()
                }
            }
        }
    }
}