package cl.duoc.app.model

/**
 * Funciones de extensión para formateo de datos de signos vitales
 */

/**
 * Formatea la fecha y hora en un formato legible
 */
fun Long.formatDateTime(): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("es", "CL"))
    return sdf.format(java.util.Date(this))
}

