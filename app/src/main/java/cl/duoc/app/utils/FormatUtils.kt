package cl.duoc.app.utils

/**
 * Utilidades comunes para formateo y conversión de datos
 */
object FormatUtils {
    
    /**
     * Formatea un timestamp a fecha legible
     */
    fun formatDate(timestamp: Long, pattern: String = "dd/MM/yyyy HH:mm"): String {
        return try {
            java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
                .format(java.util.Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Formatea solo la hora
     */
    fun formatTime(timestamp: Long): String {
        return formatDate(timestamp, "HH:mm:ss")
    }
    
    /**
     * Formatea solo la fecha
     */
    fun formatDateOnly(timestamp: Long): String {
        return formatDate(timestamp, "dd/MM/yyyy")
    }
    
    /**
     * Formatea presión arterial
     */
    fun formatBloodPressure(systolic: Int, diastolic: Int): String {
        return "$systolic/$diastolic mmHg"
    }
    
    /**
     * Formatea frecuencia cardíaca
     */
    fun formatHeartRate(heartRate: Int): String {
        return "$heartRate bpm"
    }
    
    /**
     * Formatea saturación de oxígeno
     */
    fun formatOxygenSaturation(saturation: Int): String {
        return "$saturation%"
    }
}
