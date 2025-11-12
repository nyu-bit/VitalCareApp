package cl.duoc.app.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado y lógica de un formulario
 * Sigue el patrón MVVM y expone el estado mediante StateFlow
 */
class FormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    /**
     * Procesa eventos de la UI
     */
    fun onEvent(event: FormUiEvent) {
        when (event) {
            is FormUiEvent.NameChanged -> {
                _uiState.update { it.copy(name = event.name, nameError = null) }
            }
            is FormUiEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, emailError = null) }
            }
            is FormUiEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, passwordError = null) }
            }
            is FormUiEvent.ConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = event.confirmPassword, confirmPasswordError = null) }
            }
            is FormUiEvent.SubmitForm -> {
                submitForm()
            }
            is FormUiEvent.DismissError -> {
                _uiState.update { it.copy(generalError = null) }
            }
        }
    }

    /**
     * Validadores para cada campo usando validaciones personalizadas
     */
    private val nameValidator = FormValidators.combine(
        FormValidators.required("El nombre"),
        FormValidators.minLength(3, "El nombre"),
        FormValidators.onlyLetters("El nombre")
    )

    private val emailValidator = FormValidators.emailStrict

    private val passwordValidator = FormValidators.password(
        minLength = 6,
        requireUppercase = false,
        requireLowercase = true,
        requireDigit = false,
        requireSpecialChar = false
    )

    /**
     * Valida un campo individual en tiempo real
     */
    private fun validateField(field: String, value: String) {
        when (field) {
            "name" -> {
                val error = nameValidator.getErrorOrNull(value)
                _uiState.update { it.copy(nameError = error) }
            }
            "email" -> {
                val error = emailValidator.getErrorOrNull(value)
                _uiState.update { it.copy(emailError = error) }
            }
            "password" -> {
                val error = passwordValidator.getErrorOrNull(value)
                _uiState.update { it.copy(passwordError = error) }
                // Re-validar confirmación si ya tiene valor
                if (_uiState.value.confirmPassword.isNotEmpty()) {
                    validatePasswordMatch()
                }
            }
            "confirmPassword" -> {
                validatePasswordMatch()
            }
        }
    }

    /**
     * Valida coincidencia de contraseñas
     */
    private fun validatePasswordMatch() {
        val matchValidator = FormValidators.passwordMatch(_uiState.value.password)
        val error = matchValidator.getErrorOrNull(_uiState.value.confirmPassword)
        _uiState.update { it.copy(confirmPasswordError = error) }
    }

    /**
     * Valida y envía el formulario usando validadores personalizados
     */
    private fun submitForm() {
        // Limpiar errores previos
        _uiState.update { 
            it.copy(
                nameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                generalError = null
            )
        }

        val currentState = _uiState.value
        var hasErrors = false

        // Validar nombre usando validador personalizado
        val nameResult = nameValidator.validate(currentState.name)
        if (nameResult is ValidationResult.Error) {
            _uiState.update { it.copy(nameError = nameResult.message) }
            hasErrors = true
        }

        // Validar email usando validador personalizado
        val emailResult = emailValidator.validate(currentState.email)
        if (emailResult is ValidationResult.Error) {
            _uiState.update { it.copy(emailError = emailResult.message) }
            hasErrors = true
        }

        // Validar contraseña usando validador personalizado
        val passwordResult = passwordValidator.validate(currentState.password)
        if (passwordResult is ValidationResult.Error) {
            _uiState.update { it.copy(passwordError = passwordResult.message) }
            hasErrors = true
        }

        // Validar coincidencia de contraseñas
        val matchValidator = FormValidators.passwordMatch(currentState.password)
        val confirmPasswordResult = matchValidator.validate(currentState.confirmPassword)
        if (confirmPasswordResult is ValidationResult.Error) {
            _uiState.update { it.copy(confirmPasswordError = confirmPasswordResult.message) }
            hasErrors = true
        }

        // Si no hay errores, procesar el formulario
        if (!hasErrors) {
            processForm()
        }
    }

    /**
     * Procesa el formulario (simulación de llamada a repositorio)
     */
    private fun processForm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Simular operación asíncrona (llamada a repositorio)
                kotlinx.coroutines.delay(2000)
                
                // Simulación de éxito
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        generalError = "Error al procesar el formulario: ${e.message}"
                    )
                }
            }
        }
    }



    /**
     * Resetea el formulario a su estado inicial
     */
    fun resetForm() {
        _uiState.value = FormUiState()
    }
}
