package cl.duoc.app.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.usecase.SaveUserUseCase
import cl.duoc.app.domain.usecase.ValidateUserDataUseCase
import cl.duoc.app.model.User
import cl.duoc.app.ui.form.FormValidators
import cl.duoc.app.ui.form.ValidationResult
import cl.duoc.app.utils.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de registro de usuario (HU-02)
 * 
 * Responsabilidades:
 * - Gestionar el estado del formulario de registro
 * - Validar campos en tiempo real
 * - Coordinar el guardado del usuario a través del UseCase
 * - Manejar estados de éxito y error
 */
class RegistrationViewModel(
    private val saveUserUseCase: SaveUserUseCase,
    private val validateUserDataUseCase: ValidateUserDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el nombre y valida en tiempo real
     */
    fun onNameChanged(name: String) {
        _uiState.update { 
            it.copy(
                name = name,
                nameError = validateName(name)
            )
        }
    }

    /**
     * Actualiza el email y valida en tiempo real
     */
    fun onEmailChanged(email: String) {
        _uiState.update { 
            it.copy(
                email = email,
                emailError = validateEmail(email)
            )
        }
    }

    /**
     * Actualiza el teléfono y valida en tiempo real
     */
    fun onPhoneChanged(phone: String) {
        // Solo permitir números
        val cleanPhone = phone.filter { it.isDigit() }
        _uiState.update { 
            it.copy(
                phone = cleanPhone,
                phoneError = validatePhone(cleanPhone)
            )
        }
    }

    /**
     * Actualiza el RUT y valida en tiempo real
     */
    fun onRutChanged(rut: String) {
        _uiState.update { 
            it.copy(
                rut = rut,
                rutError = validateRut(rut)
            )
        }
    }

    /**
     * Actualiza la fecha de nacimiento
     */
    fun onBirthDateChanged(birthDate: String) {
        _uiState.update { 
            it.copy(
                birthDate = birthDate,
                birthDateError = validateBirthDate(birthDate)
            )
        }
    }

    /**
     * Actualiza la dirección
     */
    fun onAddressChanged(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    /**
     * Registra un nuevo usuario
     */
    fun registerUser() {
        // Validar todos los campos
        val nameError = validateName(_uiState.value.name)
        val emailError = validateEmail(_uiState.value.email)
        val phoneError = validatePhone(_uiState.value.phone)
        val rutError = validateRut(_uiState.value.rut)

        // Actualizar errores en UI
        _uiState.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                phoneError = phoneError,
                rutError = rutError
            )
        }

        // Si hay errores, no continuar
        if (nameError != null || emailError != null || phoneError != null || rutError != null) {
            _uiState.update {
                it.copy(generalError = "Por favor, corrige los errores en el formulario")
            }
            return
        }

        // Proceder con el registro
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, generalError = null) }

            try {
                // Crear usuario
                val user = User(
                    id = System.currentTimeMillis().toString(),
                    name = _uiState.value.name.trim(),
                    email = _uiState.value.email.trim(),
                    phone = _uiState.value.phone.ifBlank { null },
                    rut = _uiState.value.rut.ifBlank { null },
                    birthDate = _uiState.value.birthDate.ifBlank { null },
                    address = _uiState.value.address.ifBlank { null }
                )

                // Validar con UseCase (validaciones de negocio)
                validateUserDataUseCase(user)

                // Guardar usuario
                val result = saveUserUseCase(user)

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            registrationSuccess = true,
                            registeredUser = user
                        )
                    }
                    ErrorHandler.logInfo("RegistrationViewModel", "Usuario registrado: ${user.name}")
                } else {
                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            generalError = "No se pudo registrar el usuario. Intenta nuevamente."
                        )
                    }
                }
            } catch (e: IllegalArgumentException) {
                // Error de validación de negocio
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        generalError = e.message ?: "Datos inválidos"
                    )
                }
                ErrorHandler.logWarning("RegistrationViewModel", "Validación falló: ${e.message}")
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "RegistrationViewModel",
                    customMessage = "Error al registrar usuario"
                )
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        generalError = errorMessage
                    )
                }
            }
        }
    }

    /**
     * Limpia el formulario después de un registro exitoso
     */
    fun resetForm() {
        _uiState.value = RegistrationUiState()
    }

    /**
     * Limpia el error general
     */
    fun clearGeneralError() {
        _uiState.update { it.copy(generalError = null) }
    }

    // ========== Validaciones privadas ==========

    private fun validateName(name: String): String? {
        return when (val result = FormValidators.required("El nombre").validate(name)) {
            is ValidationResult.Success -> {
                when (val lengthResult = FormValidators.minLength(2, "El nombre").validate(name)) {
                    is ValidationResult.Success -> null
                    is ValidationResult.Error -> lengthResult.message
                }
            }
            is ValidationResult.Error -> result.message
        }
    }

    private fun validateEmail(email: String): String? {
        return when (val result = FormValidators.email.validate(email)) {
            is ValidationResult.Success -> null
            is ValidationResult.Error -> result.message
        }
    }

    private fun validatePhone(phone: String): String? {
        val localPhone = phone
        if (localPhone.isBlank()) return null // Opcional
        return when (val result = FormValidators.phoneChile.validate(localPhone)) {
            is ValidationResult.Success -> null
            is ValidationResult.Error -> result.message
        }
    }

    private fun validateRut(rut: String): String? {
        val localRut = rut
        if (localRut.isBlank()) return "El RUT es requerido"
        return when (val result = FormValidators.rutChile.validate(localRut)) {
            is ValidationResult.Success -> null
            is ValidationResult.Error -> result.message
        }
    }

    private fun validateBirthDate(birthDate: String): String? {
        if (birthDate.isBlank()) return null // Opcional
        
        // Validación básica de formato
        val dateRegex = """\d{2}/\d{2}/\d{4}""".toRegex()
        return if (birthDate.matches(dateRegex)) null 
               else "Formato inválido (DD/MM/YYYY)"
    }
}

/**
 * Estado de la UI para el registro de usuario
 */
data class RegistrationUiState(
    // Datos del formulario
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val rut: String = "",
    val birthDate: String = "",
    val address: String = "",
    
    // Errores por campo
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val rutError: String? = null,
    val birthDateError: String? = null,
    
    // Estados del proceso
    val isRegistering: Boolean = false,
    val registrationSuccess: Boolean = false,
    val generalError: String? = null,
    
    // Usuario registrado
    val registeredUser: User? = null
) {
    /**
     * Indica si el formulario es válido
     */
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                rut.isNotBlank() &&
                nameError == null &&
                emailError == null &&
                phoneError == null &&
                rutError == null &&
                birthDateError == null
}
