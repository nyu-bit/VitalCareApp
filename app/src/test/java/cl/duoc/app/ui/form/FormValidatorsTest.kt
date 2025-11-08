package cl.duoc.app.ui.form

import cl.duoc.app.ui.form.FormValidators.getErrorOrNull
import cl.duoc.app.ui.form.FormValidators.isValid
import org.junit.Assert.*
import org.junit.Test

/**
 * Pruebas unitarias para FormValidators
 * Cubre validaciones exitosas y casos de error
 */
class FormValidatorsTest {

    @Test
    fun `required validator returns success when value is not blank`() {
        val validator = FormValidators.required("Campo")
        val result = validator.validate("valor")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `required validator returns error when value is blank`() {
        val validator = FormValidators.required("Campo")
        val error = validator.getErrorOrNull("")
        assertNotNull(error)
        assertTrue(error!!.contains("Campo"))
    }

    @Test
    fun `minLength validator returns success when length is valid`() {
        val validator = FormValidators.minLength(3, "Campo")
        assertTrue(validator.isValid("abc"))
        assertTrue(validator.isValid("abcd"))
    }

    @Test
    fun `minLength validator returns error when length is too short`() {
        val validator = FormValidators.minLength(5, "Campo")
        val error = validator.getErrorOrNull("abc")
        assertNotNull(error)
        assertTrue(error!!.contains("5"))
    }

    @Test
    fun `email validator accepts valid emails`() {
        val validator = FormValidators.emailStrict
        assertTrue(validator.isValid("test@example.com"))
        assertTrue(validator.isValid("user.name@domain.co"))
        assertTrue(validator.isValid("user+tag@example.com"))
    }

    @Test
    fun `email validator rejects invalid emails`() {
        val validator = FormValidators.emailStrict
        assertFalse(validator.isValid("invalid"))
        assertFalse(validator.isValid("@example.com"))
        assertFalse(validator.isValid("user@"))
        assertFalse(validator.isValid(""))
    }

    @Test
    fun `password validator with requirements validates correctly`() {
        val validator = FormValidators.password(
            minLength = 8,
            requireUppercase = true,
            requireLowercase = true,
            requireDigit = true
        )
        
        assertTrue(validator.isValid("Password123"))
        assertFalse(validator.isValid("password"))
        assertFalse(validator.isValid("PASSWORD123"))
        assertFalse(validator.isValid("Password"))
        assertFalse(validator.isValid("Pass1"))
    }

    @Test
    fun `passwordMatch validator returns success when passwords match`() {
        val validator = FormValidators.passwordMatch("password123")
        assertTrue(validator.isValid("password123"))
    }

    @Test
    fun `passwordMatch validator returns error when passwords dont match`() {
        val validator = FormValidators.passwordMatch("password123")
        val error = validator.getErrorOrNull("different")
        assertNotNull(error)
        assertTrue(error!!.contains("coinciden"))
    }

    @Test
    fun `onlyLetters validator accepts only letters`() {
        val validator = FormValidators.onlyLetters("Nombre")
        assertTrue(validator.isValid("Juan"))
        assertTrue(validator.isValid("María José"))
        assertFalse(validator.isValid("Juan123"))
        assertFalse(validator.isValid("Juan@"))
    }

    @Test
    fun `onlyDigits validator accepts only numbers`() {
        val validator = FormValidators.onlyDigits("Código")
        assertTrue(validator.isValid("123456"))
        assertFalse(validator.isValid("123abc"))
        assertFalse(validator.isValid(""))
    }

    @Test
    fun `numberRange validator validates range correctly`() {
        val validator = FormValidators.numberRange(18, 120, "Edad")
        assertTrue(validator.isValid("18"))
        assertTrue(validator.isValid("50"))
        assertTrue(validator.isValid("120"))
        
        assertFalse(validator.isValid("17"))
        assertFalse(validator.isValid("121"))
        assertFalse(validator.isValid("abc"))
    }

    @Test
    fun `phoneChile validator accepts valid Chilean phones`() {
        val validator = FormValidators.phoneChile
        assertTrue(validator.isValid("912345678"))
        assertTrue(validator.isValid("+56912345678"))
        assertTrue(validator.isValid("56912345678"))
    }

    @Test
    fun `phoneChile validator rejects invalid phones`() {
        val validator = FormValidators.phoneChile
        assertFalse(validator.isValid("812345678")) // No empieza con 9
        assertFalse(validator.isValid("91234567"))  // Muy corto
        assertFalse(validator.isValid(""))
    }

    @Test
    fun `rutChile validator accepts valid RUT`() {
        val validator = FormValidators.rutChile
        assertTrue(validator.isValid("11111111-1"))
        assertTrue(validator.isValid("22222222-2"))
    }

    @Test
    fun `rutChile validator rejects invalid RUT`() {
        val validator = FormValidators.rutChile
        assertFalse(validator.isValid("11111111-2")) // DV incorrecto
        assertFalse(validator.isValid(""))
        assertFalse(validator.isValid("abc"))
    }

    @Test
    fun `combine validator runs all validators in sequence`() {
        val validator = FormValidators.combine(
            FormValidators.required("Nombre"),
            FormValidators.minLength(3, "Nombre"),
            FormValidators.onlyLetters("Nombre")
        )
        
        assertTrue(validator.isValid("Juan"))
        assertFalse(validator.isValid(""))      // Falla required
        assertFalse(validator.isValid("Jo"))    // Falla minLength
        assertFalse(validator.isValid("Juan123")) // Falla onlyLetters
    }

    @Test
    fun `pattern validator with custom regex works correctly`() {
        val validator = FormValidators.pattern(
            regex = "^[A-Z]{3}-\\d{4}$".toRegex(),
            errorMessage = "Formato debe ser ABC-1234"
        )
        
        assertTrue(validator.isValid("ABC-1234"))
        assertFalse(validator.isValid("abc-1234"))
        assertFalse(validator.isValid("AB-1234"))
        assertFalse(validator.isValid("ABC-123"))
    }

    @Test
    fun `notIn validator rejects blacklisted values`() {
        val validator = FormValidators.notIn(
            listOf("admin", "root", "test"),
            "Usuario"
        )
        
        assertFalse(validator.isValid("admin"))
        assertFalse(validator.isValid("Admin")) // Case insensitive
        assertTrue(validator.isValid("user"))
    }
}
