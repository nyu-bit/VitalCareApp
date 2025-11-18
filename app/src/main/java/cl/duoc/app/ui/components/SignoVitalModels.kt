package cl.duoc.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Tipos de signos vitales
enum class TipoSignoVital {
    PRESION_ARTERIAL,
    FRECUENCIA_CARDIACA,
    SATURACION_OXIGENO,
    TEMPERATURA
}

// Data class para representar un signo vital
data class SignoVital(
    val id: String,
    val tipo: TipoSignoVital,
    val valor: Double,
    val unidad: String,
    val estado: String, // "Normal", "Advertencia", "Peligro"
    val fechaHora: Long,
    val notas: String? = null
)

// Función para obtener el icono según el tipo
fun getTipoSignoVitalIcon(tipo: TipoSignoVital): ImageVector {
    return when (tipo) {
        TipoSignoVital.PRESION_ARTERIAL -> Icons.Default.FavoriteBorder
        TipoSignoVital.FRECUENCIA_CARDIACA -> Icons.Default.Favorite
        TipoSignoVital.SATURACION_OXIGENO -> Icons.Default.Air
        TipoSignoVital.TEMPERATURA -> Icons.Default.Thermostat
    }
}

// Función para obtener el nombre en español
fun getTipoSignoVitalNombre(tipo: TipoSignoVital): String {
    return when (tipo) {
        TipoSignoVital.PRESION_ARTERIAL -> "Presión Arterial"
        TipoSignoVital.FRECUENCIA_CARDIACA -> "Frecuencia Cardíaca"
        TipoSignoVital.SATURACION_OXIGENO -> "Saturación de Oxígeno"
        TipoSignoVital.TEMPERATURA -> "Temperatura"
    }
}

