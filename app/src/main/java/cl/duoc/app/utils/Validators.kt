package cl.duoc.app.utils

/**
 * Sistema de validaciones desacoplado de la UI
 * Contiene reglas reutilizables para diferentes tipos de campos
 */
object Validators {
    
    /**
     * Resultado de una validación
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    
    /**
     * Valida que un campo no esté vacío
     */
    fun validateNotEmpty(value: String, fieldName: String = "Campo"): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult(false, "$fieldName no puede estar vacío")
        } else {
            ValidationResult(true)
        }
    }
    
    /**
     * Valida formato de email
     */
    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(false, "Email no puede estar vacío")
        }
        
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return if (emailRegex.matches(email)) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "Email inválido")
        }
    }
    
    /**
     * Valida formato de RUT chileno
     */
    fun validateRut(rut: String): ValidationResult {
        if (rut.isBlank()) {
            return ValidationResult(false, "RUT no puede estar vacío")
        }
        
        // Formato: 12345678-9 o 12.345.678-9
        val cleanRut = rut.replace(".", "").replace("-", "")
        
        if (cleanRut.length < 8) {
            return ValidationResult(false, "RUT demasiado corto")
        }
        
        val rutBody = cleanRut.dropLast(1)
        val dv = cleanRut.last().uppercaseChar()
        
        // Validar que el cuerpo solo contenga números
        if (!rutBody.all { it.isDigit() }) {
            return ValidationResult(false, "RUT inválido")
        }
        
        // Calcular dígito verificador
        var sum = 0
        var multiplier = 2
        
        for (i in rutBody.length - 1 downTo 0) {
            sum += rutBody[i].toString().toInt() * multiplier
            multiplier = if (multiplier == 7) 2 else multiplier + 1
        }
        
        val expectedDv = when (val mod = 11 - (sum % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> mod.toString()[0]
        }
        
        return if (dv == expectedDv) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "RUT inválido (dígito verificador incorrecto)")
        }
    }
    
    /**
     * Valida formato de teléfono chileno
     */
    fun validatePhone(phone: String): ValidationResult {
        if (phone.isBlank()) {
            return ValidationResult(false, "Teléfono no puede estar vacío")
        }
        
        val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
        
        // Acepta: +56912345678, 912345678, etc
        return if (cleanPhone.length >= 9 && cleanPhone.all { it.isDigit() }) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "Teléfono inválido (mínimo 9 dígitos)")
        }
    }
    
    /**
     * Valida longitud mínima
     */
    fun validateMinLength(value: String, minLength: Int, fieldName: String = "Campo"): ValidationResult {
        return if (value.length >= minLength) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "$fieldName debe tener al menos $minLength caracteres")
        }
    }
    
    /**
     * Valida longitud máxima
     */
    fun validateMaxLength(value: String, maxLength: Int, fieldName: String = "Campo"): ValidationResult {
        return if (value.length <= maxLength) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "$fieldName no puede exceder $maxLength caracteres")
        }
    }
    
    /**
     * Valida formato de fecha (yyyy-MM-dd)
     */
    fun validateDate(date: String): ValidationResult {
        if (date.isBlank()) {
            return ValidationResult(false, "Fecha no puede estar vacía")
        }
        
        val dateRegex = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()
        if (!dateRegex.matches(date)) {
            return ValidationResult(false, "Formato de fecha inválido (usar yyyy-MM-dd)")
        }
        
        // Validar valores de mes y día
        val parts = date.split("-")
        val year = parts[0].toIntOrNull() ?: return ValidationResult(false, "Año inválido")
        val month = parts[1].toIntOrNull() ?: return ValidationResult(false, "Mes inválido")
        val day = parts[2].toIntOrNull() ?: return ValidationResult(false, "Día inválido")
        
        if (month < 1 || month > 12) {
            return ValidationResult(false, "Mes debe estar entre 1 y 12")
        }
        
        if (day < 1 || day > 31) {
            return ValidationResult(false, "Día debe estar entre 1 y 31")
        }
        
        if (year < 1900 || year > 2100) {
            return ValidationResult(false, "Año debe estar entre 1900 y 2100")
        }
        
        return ValidationResult(true)
    }
    
    /**
     * Valida formato de hora (HH:mm)
     */
    fun validateTime(time: String): ValidationResult {
        if (time.isBlank()) {
            return ValidationResult(false, "Hora no puede estar vacía")
        }
        
        val timeRegex = "^\\d{2}:\\d{2}$".toRegex()
        if (!timeRegex.matches(time)) {
            return ValidationResult(false, "Formato de hora inválido (usar HH:mm)")
        }
        
        val parts = time.split(":")
        val hour = parts[0].toIntOrNull() ?: return ValidationResult(false, "Hora inválida")
        val minute = parts[1].toIntOrNull() ?: return ValidationResult(false, "Minuto inválido")
        
        if (hour < 0 || hour > 23) {
            return ValidationResult(false, "Hora debe estar entre 00 y 23")
        }
        
        if (minute < 0 || minute > 59) {
            return ValidationResult(false, "Minutos deben estar entre 00 y 59")
        }
        
        return ValidationResult(true)
    }
    
    /**
     * Formatea RUT agregando puntos y guión
     */
    fun formatRut(rut: String): String {
        val clean = rut.replace(".", "").replace("-", "")
        if (clean.length < 2) return clean
        
        val dv = clean.last()
        val body = clean.dropLast(1)
        
        val formatted = body.reversed().chunked(3).joinToString(".").reversed()
        return "$formatted-$dv"
    }
    
    /**
     * Formatea teléfono
     */
    fun formatPhone(phone: String): String {
        val clean = phone.replace("+", "").replace(" ", "").replace("-", "")
        
        return when {
            clean.startsWith("56") && clean.length >= 11 -> "+${clean.substring(0, 2)} ${clean.substring(2, 3)} ${clean.substring(3)}"
            clean.length == 9 -> "+56 ${clean.substring(0, 1)} ${clean.substring(1)}"
            else -> clean
        }
    }
}
