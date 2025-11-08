package cl.duoc.app.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado para formulario con validaciones reactivas en tiempo real
 */
data class ReactiveFormState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val rut: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val age: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val rutError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val ageError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * ViewModel que demuestra validaciones reactivas en tiempo real
 * Los errores se muestran mientras el usuario escribe
 */
class ReactiveFormViewModel : ViewModel() {

    private val _state = MutableStateFlow(ReactiveFormState())
    val state: StateFlow<ReactiveFormState> = _state.asStateFlow()

    // Validadores personalizados
    private val nameValidator = FormValidators.combine(
        FormValidators.required("El nombre"),
        FormValidators.minLength(3, "El nombre"),
        FormValidators.maxLength(50, "El nombre"),
        FormValidators.onlyLetters("El nombre")
    )

    private val emailValidator = FormValidators.emailStrict
    private val phoneValidator = FormValidators.phoneChile
    private val rutValidator = FormValidators.rutChile
    private val ageValidator = FormValidators.combine(
        FormValidators.onlyDigits("La edad"),
        FormValidators.numberRange(18, 120, "La edad")
    )
    private val passwordValidator = FormValidators.password(
        minLength = 8,
        requireUppercase = true,
        requireLowercase = true,
        requireDigit = true,
        requireSpecialChar = false
    )

    /**
     * Actualiza nombre y valida en tiempo real
     */
    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name) }
        validateName()
        checkFormValidity()
    }

    /**
     * Actualiza email y valida en tiempo real
     */
    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
        validateEmail()
        checkFormValidity()
    }

    /**
     * Actualiza teléfono y valida en tiempo real
     */
    fun onPhoneChanged(phone: String) {
        _state.update { it.copy(phone = phone) }
        validatePhone()
        checkFormValidity()
    }

    /**
     * Actualiza RUT y valida en tiempo real
     */
    fun onRutChanged(rut: String) {
        _state.update { it.copy(rut = rut) }
        validateRut()
        checkFormValidity()
    }

    /**
     * Actualiza edad y valida en tiempo real
     */
    fun onAgeChanged(age: String) {
        // Solo permitir números
        if (age.isEmpty() || age.all { it.isDigit() }) {
            _state.update { it.copy(age = age) }
            validateAge()
            checkFormValidity()
        }
    }

    /**
     * Actualiza contraseña y valida en tiempo real
     */
    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
        validatePassword()
        // Re-validar confirmación si ya tiene valor
        if (_state.value.confirmPassword.isNotEmpty()) {
            validateConfirmPassword()
        }
        checkFormValidity()
    }

    /**
     * Actualiza confirmación de contraseña y valida en tiempo real
     */
    fun onConfirmPasswordChanged(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword) }
        validateConfirmPassword()
        checkFormValidity()
    }

    /**
     * Validaciones individuales
     */
    private fun validateName() {
        val error = nameValidator.getErrorOrNull(_state.value.name)
        _state.update { it.copy(nameError = error) }
    }

    private fun validateEmail() {
        val error = emailValidator.getErrorOrNull(_state.value.email)
        _state.update { it.copy(emailError = error) }
    }

    private fun validatePhone() {
        val error = phoneValidator.getErrorOrNull(_state.value.phone)
        _state.update { it.copy(phoneError = error) }
    }

    private fun validateRut() {
        val error = rutValidator.getErrorOrNull(_state.value.rut)
        _state.update { it.copy(rutError = error) }
    }

    private fun validateAge() {
        val error = ageValidator.getErrorOrNull(_state.value.age)
        _state.update { it.copy(ageError = error) }
    }

    private fun validatePassword() {
        val error = passwordValidator.getErrorOrNull(_state.value.password)
        _state.update { it.copy(passwordError = error) }
    }

    private fun validateConfirmPassword() {
        val matchValidator = FormValidators.passwordMatch(_state.value.password)
        val error = matchValidator.getErrorOrNull(_state.value.confirmPassword)
        _state.update { it.copy(confirmPasswordError = error) }
    }

    /**
     * Verifica si todo el formulario es válido
     */
    private fun checkFormValidity() {
        val currentState = _state.value
        val isValid = currentState.name.isNotEmpty() &&
                currentState.email.isNotEmpty() &&
                currentState.phone.isNotEmpty() &&
                currentState.rut.isNotEmpty() &&
                currentState.age.isNotEmpty() &&
                currentState.password.isNotEmpty() &&
                currentState.confirmPassword.isNotEmpty() &&
                currentState.nameError == null &&
                currentState.emailError == null &&
                currentState.phoneError == null &&
                currentState.rutError == null &&
                currentState.ageError == null &&
                currentState.passwordError == null &&
                currentState.confirmPasswordError == null

        _state.update { it.copy(isFormValid = isValid) }
    }

    /**
     * Envía el formulario
     */
    fun submitForm() {
        if (!_state.value.isFormValid) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                // Simular envío
                kotlinx.coroutines.delay(2000)
                
                // Éxito - Resetear formulario
                _state.value = ReactiveFormState()
                
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Valida todos los campos manualmente
     */
    fun validateAll() {
        validateName()
        validateEmail()
        validatePhone()
        validateRut()
        validateAge()
        validatePassword()
        validateConfirmPassword()
        checkFormValidity()
    }
}
