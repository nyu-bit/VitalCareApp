package cl.duoc.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.repository.UserRepository
import cl.duoc.app.model.User
import cl.duoc.app.utils.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de ejemplo que demuestra el uso correcto de ErrorHandler
 * para manejo consistente de errores en toda la aplicación
 */
class ErrorHandlingViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ErrorHandlingState())
    val uiState: StateFlow<ErrorHandlingState> = _uiState.asStateFlow()

    /**
     * Ejemplo 1: Manejo básico de error con try-catch
     */
    fun loadUserBasic(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val user = userRepository.getUserById(userId)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        user = user,
                        error = null
                    )
                }
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "ErrorHandlingViewModel",
                    customMessage = "No se pudo cargar el usuario"
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            }
        }
    }

    /**
     * Ejemplo 2: Uso directo de try-catch con ErrorHandler
     */
    fun loadUserWithSafeCall(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val user = userRepository.getUserById(userId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        error = null
                    )
                }
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.handleException(e, "ErrorHandlingViewModel")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            }
        }
    }

    /**
     * Ejemplo 3: Guardar usuario con manejo de errores
     */
    fun saveUserWithErrorHandling(user: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                userRepository.saveUser(user)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedSuccessfully = true,
                        error = null
                    )
                }
                ErrorHandler.logInfo("ErrorHandlingViewModel", "Usuario guardado exitosamente")
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.handleException(e, "ErrorHandlingViewModel")
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = errorMessage
                    )
                }
            }
        }
    }

    /**
     * Ejemplo 4: Manejo de múltiples tipos de error
     */
    fun performComplexOperation(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Paso 1: Validar entrada
                if (userId.isBlank()) {
                    throw IllegalArgumentException("ID de usuario no puede estar vacío")
                }
                
                // Paso 2: Cargar usuario
                val user = userRepository.getUserById(userId)
                    ?: throw NoSuchElementException("Usuario no encontrado")
                
                // Paso 3: Realizar operación
                val updated = processUser(user)
                
                // Paso 4: Guardar
                userRepository.saveUser(updated)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        user = updated,
                        error = null
                    )
                }
                
                ErrorHandler.logInfo("ErrorHandlingViewModel", "Operación completada exitosamente")
                
            } catch (e: IllegalArgumentException) {
                // Error de validación
                val errorType = ErrorHandler.classifyException(e)
                ErrorHandler.logWarning("ErrorHandlingViewModel", "Validación falló: ${e.message}")
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Datos inválidos",
                        errorType = errorType
                    )
                }
                
            } catch (e: NoSuchElementException) {
                // Error de datos no encontrados
                ErrorHandler.logError("ErrorHandlingViewModel", "Usuario no encontrado", e)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Usuario no encontrado. Verifica el ID.",
                        errorType = ErrorHandler.ErrorType.DATABASE
                    )
                }
                
            } catch (e: Exception) {
                // Cualquier otro error
                val errorMessage = ErrorHandler.handleException(e, "ErrorHandlingViewModel")
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = errorMessage,
                        errorType = ErrorHandler.classifyException(e)
                    )
                }
            }
        }
    }

    /**
     * Ejemplo 5: Logging detallado para debugging
     */
    fun debugOperation(operation: String) {
        ErrorHandler.logInfo("ErrorHandlingViewModel", "Iniciando operación: $operation")
        
        viewModelScope.launch {
            try {
                ErrorHandler.logInfo("ErrorHandlingViewModel", "Ejecutando paso 1")
                // ... operación paso 1
                
                ErrorHandler.logInfo("ErrorHandlingViewModel", "Ejecutando paso 2")
                // ... operación paso 2
                
                ErrorHandler.logInfo("ErrorHandlingViewModel", "Operación completada")
            } catch (e: Exception) {
                ErrorHandler.logError(
                    tag = "ErrorHandlingViewModel",
                    message = "Error en $operation",
                    throwable = e
                )
            }
        }
    }

    /**
     * Función auxiliar para procesar usuario
     */
    private fun processUser(user: User): User {
        // Lógica de negocio
        return user.copy(
            name = user.name.trim()
        )
    }

    /**
     * Limpia el error del estado
     */
    fun clearError() {
        _uiState.update { it.copy(error = null, errorType = null) }
    }
}

/**
 * Estado del UI para demostración de manejo de errores
 */
data class ErrorHandlingState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val errorType: ErrorHandler.ErrorType? = null,
    val savedSuccessfully: Boolean = false
)
