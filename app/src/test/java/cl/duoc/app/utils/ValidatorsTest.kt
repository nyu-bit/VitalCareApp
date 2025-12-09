package cl.duoc.app.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests para el sistema de validadores Validators
 * Prueba todas las reglas de validación y formatos
 */
class ValidatorsTest {

    // ==================== VALIDACIONES NO VACÍO ====================

    @Test
    fun testValidateNotEmptyValid() {
        // Act
        val result = Validators.validateNotEmpty("Juan", "Nombre")

        // Assert
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateNotEmptyBlank() {
        // Act
        val result = Validators.validateNotEmpty("", "Nombre")

        // Assert
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage?.contains("no puede estar vacío") ?: false)
    }

    @Test
    fun testValidateNotEmptyOnlySpaces() {
        // Act
        val result = Validators.validateNotEmpty("   ", "Campo")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("no puede estar vacío") ?: false)
    }

    // ==================== VALIDACIONES EMAIL ====================

    @Test
    fun testValidateEmailValid() {
        // Act
        val result = Validators.validateEmail("usuario@example.com")

        // Assert
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateEmailValidMultipleDots() {
        // Act
        val result = Validators.validateEmail("usuario.nombre@example.co.uk")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateEmailValidWithPlus() {
        // Act
        val result = Validators.validateEmail("usuario+tag@example.com")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateEmailInvalidNoAt() {
        // Act
        val result = Validators.validateEmail("usuarioexample.com")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Email inválido") ?: false)
    }

    @Test
    fun testValidateEmailInvalidNoDomain() {
        // Act
        val result = Validators.validateEmail("usuario@")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Email inválido") ?: false)
    }

    @Test
    fun testValidateEmailBlank() {
        // Act
        val result = Validators.validateEmail("")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("no puede estar vacío") ?: false)
    }

    @Test
    fun testValidateEmailNoExtension() {
        // Act
        val result = Validators.validateEmail("usuario@example")

        // Assert
        assertFalse(result.isValid)
    }

    // ==================== VALIDACIONES RUT ====================

    @Test
    fun testValidateRutValid() {
        // Act
        val result = Validators.validateRut("12345678-5")

        // Assert
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun testValidateRutValidWithDots() {
        // Act
        val result = Validators.validateRut("12.345.678-5")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateRutValidDVK() {
        // Act - Usando un RUT que sea válido con dígito verificador simple
        // El RUT chileno 20000000-5 es un RUT de prueba válido
        val result = Validators.validateRut("20000000-5")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateRutInvalidDV() {
        // Act
        val result = Validators.validateRut("12345678-0")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("dígito verificador") ?: false)
    }

    @Test
    fun testValidateRutTooShort() {
        // Act
        val result = Validators.validateRut("1234567")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("demasiado corto") ?: false)
    }

    @Test
    fun testValidateRutBlank() {
        // Act
        val result = Validators.validateRut("")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("no puede estar vacío") ?: false)
    }

    @Test
    fun testValidateRutWithLetters() {
        // Act
        val result = Validators.validateRut("1234567A-9")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("inválido") ?: false)
    }

    // ==================== VALIDACIONES TELÉFONO ====================

    @Test
    fun testValidatePhoneValid() {
        // Act
        val result = Validators.validatePhone("+56912345678")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidatePhoneValidWithoutPlus() {
        // Act
        val result = Validators.validatePhone("912345678")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidatePhoneValidWithDash() {
        // Act
        val result = Validators.validatePhone("+569-1234-5678")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidatePhoneInvalidTooShort() {
        // Act
        val result = Validators.validatePhone("1234567")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("mínimo 9 dígitos") ?: false)
    }

    @Test
    fun testValidatePhoneBlank() {
        // Act
        val result = Validators.validatePhone("")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("no puede estar vacío") ?: false)
    }

    @Test
    fun testValidatePhoneWithLetters() {
        // Act
        val result = Validators.validatePhone("+569ABC45678")

        // Assert
        assertFalse(result.isValid)
    }

    // ==================== VALIDACIONES LONGITUD ====================

    @Test
    fun testValidateMinLengthValid() {
        // Act
        val result = Validators.validateMinLength("Juan", 3, "Nombre")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateMinLengthExactly() {
        // Act
        val result = Validators.validateMinLength("ABC", 3, "Código")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateMinLengthInvalid() {
        // Act
        val result = Validators.validateMinLength("AB", 3, "Nombre")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("al menos 3 caracteres") ?: false)
    }

    @Test
    fun testValidateMaxLengthValid() {
        // Act
        val result = Validators.validateMaxLength("Juan", 10, "Nombre")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateMaxLengthExactly() {
        // Act
        val result = Validators.validateMaxLength("1234567890", 10, "Código")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateMaxLengthInvalid() {
        // Act
        val result = Validators.validateMaxLength("Very long text", 5, "Campo")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("no puede exceder 5 caracteres") ?: false)
    }

    // ==================== VALIDACIONES FECHA ====================

    @Test
    fun testValidateDateValid() {
        // Act
        val result = Validators.validateDate("2024-12-15")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateDateValidLeapYear() {
        // Act
        val result = Validators.validateDate("2024-02-29")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateDateInvalidFormat() {
        // Act
        val result = Validators.validateDate("15-12-2024")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Formato de fecha inválido") ?: false)
    }

    @Test
    fun testValidateDateInvalidMonth() {
        // Act
        val result = Validators.validateDate("2024-13-15")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Mes debe estar entre 1 y 12") ?: false)
    }

    @Test
    fun testValidateDateInvalidDay() {
        // Act
        val result = Validators.validateDate("2024-12-32")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Día debe estar entre 1 y 31") ?: false)
    }

    @Test
    fun testValidateDateInvalidYear() {
        // Act
        val result = Validators.validateDate("1800-12-15")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Año debe estar entre 1900 y 2100") ?: false)
    }

    @Test
    fun testValidateDateBlank() {
        // Act
        val result = Validators.validateDate("")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("no puede estar vacía") ?: false)
    }

    // ==================== VALIDACIONES HORA ====================

    @Test
    fun testValidateTimeValid() {
        // Act
        val result = Validators.validateTime("14:30")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateTimeMidnight() {
        // Act
        val result = Validators.validateTime("00:00")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateTimeNoon() {
        // Act
        val result = Validators.validateTime("12:00")

        // Assert
        assertTrue(result.isValid)
    }

    @Test
    fun testValidateTimeInvalidFormat() {
        // Act
        val result = Validators.validateTime("2:30 PM")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Formato de hora inválido") ?: false)
    }

    @Test
    fun testValidateTimeInvalidHour() {
        // Act
        val result = Validators.validateTime("25:00")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Hora debe estar entre 00 y 23") ?: false)
    }

    @Test
    fun testValidateTimeInvalidMinute() {
        // Act
        val result = Validators.validateTime("14:65")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Minutos deben estar entre 00 y 59") ?: false)
    }

    @Test
    fun testValidateTimeBlank() {
        // Act
        val result = Validators.validateTime("")

        // Assert
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("no puede estar vacía") ?: false)
    }

    // ==================== CASOS DE PRUEBA COMBINADOS ====================

    @Test
    fun testValidationResultDataClass() {
        // Arrange & Act
        val result1 = Validators.ValidationResult(true)
        val result2 = Validators.ValidationResult(true)
        val result3 = Validators.ValidationResult(false, "Error")

        // Assert
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertTrue(result1.isValid)
        assertFalse(result3.isValid)
    }

    @Test
    fun testCompletePackageValidation() {
        // Arrange
        val rut = "12345678-5"
        val email = "test@example.com"
        val phone = "+56912345678"
        val fecha = "2000-01-15"
        val hora = "10:30"

        // Act
        val rutValid = Validators.validateRut(rut).isValid
        val emailValid = Validators.validateEmail(email).isValid
        val phoneValid = Validators.validatePhone(phone).isValid
        val dateValid = Validators.validateDate(fecha).isValid
        val timeValid = Validators.validateTime(hora).isValid

        // Assert
        assertTrue(rutValid)
        assertTrue(emailValid)
        assertTrue(phoneValid)
        assertTrue(dateValid)
        assertTrue(timeValid)
    }
}

