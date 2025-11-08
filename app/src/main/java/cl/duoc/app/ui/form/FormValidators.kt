package cl.duoc.app.ui.form

import cl.duoc.app.utils.Constants

/**
 * Resultado de una validación
 * Puede ser exitoso o contener un mensaje de error
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

/**
 * Validador genérico funcional
 */
fun interface Validator {
    fun validate(value: String): ValidationResult
}

/**
 * Objeto que contiene validaciones reutilizables personalizadas
 * para diferentes tipos de campos de formulario
 */
object FormValidators {

    /**
     * Valida que un campo no esté vacío
     */
    fun required(fieldName: String = "Este campo"): Validator = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.Error("$fieldName es requerido")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida longitud mínima
     */
    fun minLength(length: Int, fieldName: String = "Este campo"): Validator = Validator { value ->
        if (value.length < length) {
            ValidationResult.Error("$fieldName debe tener al menos $length caracteres")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida longitud máxima
     */
    fun maxLength(length: Int, fieldName: String = "Este campo"): Validator = Validator { value ->
        if (value.length > length) {
            ValidationResult.Error("$fieldName no puede exceder $length caracteres")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida formato de email
     */
    val email: Validator = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.Error("El email es requerido")
        } else {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            if (value.matches(emailRegex)) {
                ValidationResult.Success
            } else {
                ValidationResult.Error("Formato de email inválido")
            }
        }
    }

    /**
     * Valida formato de email con patrón más estricto
     */
    val emailStrict: Validator = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.Error("El email es requerido")
        } else {
            val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
            if (value.matches(emailRegex)) {
                ValidationResult.Success
            } else {
                ValidationResult.Error("Ingrese un email válido (ejemplo@dominio.com)")
            }
        }
    }

    /**
     * Valida que la contraseña cumpla requisitos de seguridad
     */
    fun password(
        minLength: Int = Constants.Validation.MIN_PASSWORD_LENGTH,
        requireUppercase: Boolean = false,
        requireLowercase: Boolean = false,
        requireDigit: Boolean = false,
        requireSpecialChar: Boolean = false
    ): Validator = Validator { value ->
        if (value.isBlank()) {
            return@Validator ValidationResult.Error("La contraseña es requerida")
        }
        
        if (value.length < minLength) {
            return@Validator ValidationResult.Error("La contraseña debe tener al menos $minLength caracteres")
        }
        
        if (requireUppercase && !value.any { it.isUpperCase() }) {
            return@Validator ValidationResult.Error("La contraseña debe contener al menos una mayúscula")
        }
        
        if (requireLowercase && !value.any { it.isLowerCase() }) {
            return@Validator ValidationResult.Error("La contraseña debe contener al menos una minúscula")
        }
        
        if (requireDigit && !value.any { it.isDigit() }) {
            return@Validator ValidationResult.Error("La contraseña debe contener al menos un número")
        }
        
        if (requireSpecialChar && !value.any { !it.isLetterOrDigit() }) {
            return@Validator ValidationResult.Error("La contraseña debe contener al menos un carácter especial")
        }
        
        ValidationResult.Success
    }

    /**
     * Valida coincidencia de contraseñas
     */
    fun passwordMatch(originalPassword: String): Validator = Validator { confirmPassword ->
        if (confirmPassword.isBlank()) {
            ValidationResult.Error("Debe confirmar la contraseña")
        } else if (originalPassword != confirmPassword) {
            ValidationResult.Error("Las contraseñas no coinciden")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida que solo contenga letras
     */
    fun onlyLetters(fieldName: String = "Este campo"): Validator = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.Error("$fieldName es requerido")
        } else if (!value.all { it.isLetter() || it.isWhitespace() }) {
            ValidationResult.Error("$fieldName solo puede contener letras")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida que solo contenga números
     */
    fun onlyDigits(fieldName: String = "Este campo"): Validator = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.Error("$fieldName es requerido")
        } else if (!value.all { it.isDigit() }) {
            ValidationResult.Error("$fieldName solo puede contener números")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida formato de teléfono chileno
     */
    val phoneChile: Validator = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.Error("El teléfono es requerido")
        } else {
            val phoneRegex = "^(\\+?56)?[9][0-9]{8}$".toRegex()
            if (value.replace("\\s".toRegex(), "").matches(phoneRegex)) {
                ValidationResult.Success
            } else {
                ValidationResult.Error("Formato de teléfono inválido (9XXXXXXXX)")
            }
        }
    }

    /**
     * Valida RUT chileno
     */
    val rutChile: Validator = Validator { value ->
        if (value.isBlank()) {
            return@Validator ValidationResult.Error("El RUT es requerido")
        }
        
        // Limpiar RUT (quitar puntos y guión)
        val cleanRut = value.replace(".", "").replace("-", "").trim()
        
        if (cleanRut.length < 2) {
            return@Validator ValidationResult.Error("RUT inválido")
        }
        
        val rutBody = cleanRut.substring(0, cleanRut.length - 1)
        val dv = cleanRut.last().toString()
        
        if (!rutBody.all { it.isDigit() }) {
            return@Validator ValidationResult.Error("RUT debe contener solo números")
        }
        
        // Calcular dígito verificador
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
        
        if (dv.uppercase() != expectedDv) {
            ValidationResult.Error("Dígito verificador incorrecto")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida rango numérico
     */
    fun numberRange(min: Int, max: Int, fieldName: String = "El valor"): Validator = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.Error("$fieldName es requerido")
        } else {
            val number = value.toIntOrNull()
            if (number == null) {
                ValidationResult.Error("$fieldName debe ser un número")
            } else if (number < min || number > max) {
                ValidationResult.Error("$fieldName debe estar entre $min y $max")
            } else {
                ValidationResult.Success
            }
        }
    }

    /**
     * Valida que un valor no esté en una lista negra
     */
    fun notIn(blacklist: List<String>, fieldName: String = "Este valor"): Validator = Validator { value ->
        if (blacklist.contains(value.lowercase())) {
            ValidationResult.Error("$fieldName no está permitido")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida patrón regex personalizado
     */
    fun pattern(regex: Regex, errorMessage: String): Validator = Validator { value ->
        if (value.matches(regex)) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errorMessage)
        }
    }

    /**
     * Combina múltiples validadores
     * Se ejecutan en orden y se detiene en el primer error
     */
    fun combine(vararg validators: Validator): Validator = Validator { value ->
        for (validator in validators) {
            val result = validator.validate(value)
            if (result is ValidationResult.Error) {
                return@Validator result
            }
        }
        ValidationResult.Success
    }
}

/**
 * Extensión para validar un campo y obtener el mensaje de error si existe
 */
fun Validator.getErrorOrNull(value: String): String? {
    return when (val result = validate(value)) {
        is ValidationResult.Success -> null
        is ValidationResult.Error -> result.message
    }
}

/**
 * Extensión para verificar si un campo es válido
 */
fun Validator.isValid(value: String): Boolean {
    return validate(value) is ValidationResult.Success
}
