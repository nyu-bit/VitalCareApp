package cl.duoc.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.utils.FormatUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla Home
 * Representa toda la información que se muestra en la UI
 */
data class HomeUiState(
    val counter: Int = 0,
    val userName: String = "",
    val isLoading: Boolean = false,
    val welcomeMessage: String = "Bienvenido a VitalCare",
    val lastUpdate: String = "",
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel para la pantalla Home
 * Gestiona el estado y la lógica de negocio de la pantalla principal
 * Expone los datos a la UI mediante StateFlow
 */
class HomeViewModel : ViewModel() {
    
    // Estado privado mutable
    private val _uiState = MutableStateFlow(HomeUiState())
    
    // Estado público inmutable expuesto a la UI
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    /**
     * Incrementa el contador
     */
    fun incrementCounter() {
        _uiState.update { currentState ->
            currentState.copy(
                counter = currentState.counter + 1,
                lastUpdate = getCurrentTime()
            )
        }
    }

    /**
     * Decrementa el contador
     */
    fun decrementCounter() {
        _uiState.update { currentState ->
            currentState.copy(
                counter = maxOf(0, currentState.counter - 1),
                lastUpdate = getCurrentTime()
            )
        }
    }

    /**
     * Resetea el contador
     */
    fun resetCounter() {
        _uiState.update { currentState ->
            currentState.copy(
                counter = 0,
                lastUpdate = getCurrentTime()
            )
        }
    }

    /**
     * Carga los datos del usuario
     * Simula una operación asíncrona
     */
    fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            
            try {
                // Simular carga desde repositorio
                delay(1500)
                
                // Simulación de datos cargados
                _uiState.update { currentState ->
                    currentState.copy(
                        userName = "Angel Developer",
                        welcomeMessage = "¡Bienvenido de nuevo, Angel!",
                        isLoading = false,
                        lastUpdate = getCurrentTime()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = "Error al cargar datos: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Actualiza el nombre del usuario
     */
    fun updateUserName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                userName = newName,
                welcomeMessage = "¡Bienvenido, $newName!",
                lastUpdate = getCurrentTime()
            )
        }
    }

    /**
     * Limpia el error
     */
    fun clearError() {
        _uiState.update { it.copy(hasError = false, errorMessage = null) }
    }

    /**
     * Obtiene la hora actual formateada
     */
    private fun getCurrentTime(): String {
        return FormatUtils.formatTime(System.currentTimeMillis())
    }

    /**
     * Se llama cuando el ViewModel es destruido
     */
    override fun onCleared() {
        super.onCleared()
        // Aquí se pueden limpiar recursos si es necesario
    }
}