package cl.duoc.app.utils

/**
 * Extensiones útiles para tipos comunes
 */

/**
 * Extensión para String - Validar si es un email válido
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return this.matches(emailRegex)
}

/**
 * Extensión para String - Capitalizar primera letra
 */
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

/**
 * Extensión para String - Validar si es un RUT chileno válido
 */
fun String.isValidRut(): Boolean {
    val cleanRut = this.replace(".", "").replace("-", "").trim()
    if (cleanRut.length < 2) return false
    
    val rutBody = cleanRut.substring(0, cleanRut.length - 1)
    val dv = cleanRut.last().toString()
    
    if (!rutBody.all { it.isDigit() }) return false
    
    var sum = 0
    var multiplier = 2
    
    for (i in rutBody.length - 1 downTo 0) {
        sum += rutBody[i].toString().toInt() * multiplier
        multiplier = if (multiplier == 7) 2 else multiplier + 1
    }
    
    val calculatedDv = 11 - (sum % 11)
    val expectedDv = when (calculatedDv) {
        11 -> "0"
        10 -> "K"
        else -> calculatedDv.toString()
    }
    
    return dv.uppercase() == expectedDv
}

/**
 * Extensión para Int - Verificar si está en un rango
 */
fun Int.isInRange(min: Int, max: Int): Boolean {
    return this in min..max
}

/**
 * Extensión para Long - Convertir a fecha formateada
 */
fun Long.toFormattedDate(pattern: String = "dd/MM/yyyy HH:mm"): String {
    return FormatUtils.formatDate(this, pattern)
}

/**
 * Extensión para List - Validar si no está vacía
 */
fun <T> List<T>?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

/**
 * Extensión para obtener el primer elemento o un valor por defecto
 */
fun <T> List<T>.firstOrDefault(default: T): T {
    return this.firstOrNull() ?: default
}
