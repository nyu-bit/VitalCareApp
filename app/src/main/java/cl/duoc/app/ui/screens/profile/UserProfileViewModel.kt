package cl.duoc.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.repository.UserRepository
import cl.duoc.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado UI para la pantalla del perfil de usuario
 */
data class UserProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val editedUser: User? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val successMessage: String? = null
)

/**
 * ViewModel para la pantalla del perfil de usuario
 * Gestiona visualización, edición y actualización de datos personales
 */
class UserProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String = "" // Se asignará desde el contexto

    init {
        // Se debe llamar a loadUserProfile con el ID del usuario autenticado
    }

    /**
     * Carga el perfil del usuario autenticado
     */
    fun loadUserProfile(userId: String) {
        this.currentUserId = userId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            hasError = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasError = true,
                            errorMessage = "Usuario no encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = e.message ?: "Error al cargar el perfil"
                    )
                }
            }
        }
    }

    /**
     * Entra en modo edición
     */
    fun enterEditMode() {
        _uiState.update { currentState ->
            currentState.copy(
                isEditing = true,
                editedUser = currentState.user?.copy()
            )
        }
    }

    /**
     * Sale del modo edición sin guardar
     */
    fun cancelEdit() {
        _uiState.update {
            it.copy(
                isEditing = false,
                editedUser = null,
                validationErrors = emptyMap()
            )
        }
    }

    /**
     * Actualiza un campo del perfil en modo edición
     */
    fun updateField(fieldName: String, value: String) {
        _uiState.update { currentState ->
            val edited = currentState.editedUser ?: currentState.user ?: return@update currentState

            val updatedUser = when (fieldName) {
                "name" -> edited.copy(name = value)
                "email" -> edited.copy(email = value)
                "phone" -> edited.copy(phone = value)
                "rut" -> edited.copy(rut = value)
                "birthDate" -> edited.copy(birthDate = value)
                "address" -> edited.copy(address = value)
                else -> edited
            }

            currentState.copy(editedUser = updatedUser)
        }
    }

    /**
     * Valida los datos del usuario
     */
    private fun validateUser(user: User): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (user.name.isBlank()) {
            errors["name"] = "El nombre es requerido"
        }

        if (!user.email.isNullOrBlank() && !isValidEmail(user.email)) {
            errors["email"] = "Email inválido"
        }

        if (!user.phone.isNullOrBlank() && !isValidPhone(user.phone)) {
            errors["phone"] = "Teléfono inválido"
        }

        return errors
    }

    /**
     * Guarda los cambios del perfil
     */
    fun saveChanges() {
        val userToSave = _uiState.value.editedUser ?: return

        val validationErrors = validateUser(userToSave)
        if (validationErrors.isNotEmpty()) {
            _uiState.update {
                it.copy(validationErrors = validationErrors)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val success = userRepository.updateUser(userToSave)

                if (success) {
                    _uiState.update {
                        it.copy(
                            user = userToSave,
                            isEditing = false,
                            editedUser = null,
                            isSaving = false,
                            validationErrors = emptyMap(),
                            successMessage = "Perfil actualizado correctamente",
                            hasError = false
                        )
                    }
                    // Limpiar mensaje de éxito después de 3 segundos
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(successMessage = null) }
                } else {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            hasError = true,
                            errorMessage = "No se pudo actualizar el perfil"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        hasError = true,
                        errorMessage = e.message ?: "Error al guardar cambios"
                    )
                }
            }
        }
    }

    /**
     * Valida el formato de email
     */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    /**
     * Valida el formato de teléfono (simple: mínimo 9 dígitos)
     */
    private fun isValidPhone(phone: String): Boolean {
        return phone.replace(Regex("[^0-9]"), "").length >= 9
    }

    /**
     * Limpia el estado de error
     */
    fun clearError() {
        _uiState.update {
            it.copy(hasError = false, errorMessage = null)
        }
    }
}

