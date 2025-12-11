package cl.duoc.app.ui.vitalsigns

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.api.SignosVitalesDto
import cl.duoc.app.data.repository.VitalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ============================================================
 * VIEWMODEL - VITALES
 * ============================================================
 *
 * Patrón MVVM: Maneja la lógica de negocio y estado
 * Responsabilidad: Conectar con Repository y actualizar UI
 *
 * Flujo: UI → VitalesViewModel → VitalesRepository → API
 */
data class VitalesUiState(
    val vitales: List<SignosVitalesDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedVital: SignosVitalesDto? = null
)

class VitalesViewModel : ViewModel() {
    private val repository = VitalesRepository()
    private val TAG = "VitalesViewModel"

    private val _uiState = MutableStateFlow(VitalesUiState())
    val uiState: StateFlow<VitalesUiState> = _uiState.asStateFlow()

    /**
     * Carga todos los signos vitales
     *
     * Flujo:
     * 1. Actualiza loading = true
     * 2. Llama repository.getAllVitales()
     * 3. Repository llama API (Retrofit)
     * 4. Mapea resultado a UI State
     */
    fun loadAllVitales() {
        viewModelScope.launch {
            Log.d(TAG, "Cargando todos los vitales...")
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getAllVitales()

            result.onSuccess { vitales ->
                Log.d(TAG, "Vitales cargados exitosamente: ${vitales.size} registros")
                _uiState.update {
                    it.copy(
                        vitales = vitales,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Error desconocido"
                Log.e(TAG, "Error al cargar vitales: $errorMessage", exception)
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
     * Carga signos vitales de un paciente específico
     *
     * @param pacienteId ID del paciente
     */
    fun loadByPaciente(pacienteId: String) {
        if (pacienteId.isBlank()) {
            _uiState.update { it.copy(error = "ID de paciente inválido") }
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "Cargando vitales del paciente: $pacienteId")
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getByPaciente(pacienteId)

            result.onSuccess { vitales ->
                Log.d(TAG, "Vitales del paciente cargados: ${vitales.size} registros")
                _uiState.update {
                    it.copy(
                        vitales = vitales,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Error desconocido"
                Log.e(TAG, "Error al cargar vitales: $errorMessage", exception)
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
     * Crea un nuevo signo vital
     *
     * @param vital Datos del vital a crear
     */
    fun createVital(vital: SignosVitalesDto) {
        viewModelScope.launch {
            Log.d(TAG, "Creando nuevo vital...")
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.createVital(vital)

            result.onSuccess { createdVital ->
                Log.d(TAG, "Vital creado exitosamente con ID: ${createdVital.id}")
                // Agregar a la lista existente
                _uiState.update { state ->
                    state.copy(
                        vitales = state.vitales + createdVital,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Error desconocido"
                Log.e(TAG, "Error al crear vital: $errorMessage", exception)
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
     * Elimina un signo vital
     *
     * @param id ID del vital a eliminar
     */
    fun deleteVital(id: String) {
        viewModelScope.launch {
            Log.d(TAG, "Eliminando vital con ID: $id")
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.deleteVital(id)

            result.onSuccess {
                Log.d(TAG, "Vital eliminado exitosamente")
                // Remover de la lista
                _uiState.update { state ->
                    state.copy(
                        vitales = state.vitales.filter { it.id != id },
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Error desconocido"
                Log.e(TAG, "Error al eliminar vital: $errorMessage", exception)
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
     * Selecciona un vital para mostrar detalles
     */
    fun selectVital(vital: SignosVitalesDto) {
        _uiState.update { it.copy(selectedVital = vital) }
    }

    /**
     * Limpia el error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Recarga los vitales
     */
    fun refresh(pacienteId: String = "") {
        if (pacienteId.isNotEmpty()) {
            loadByPaciente(pacienteId)
        } else {
            loadAllVitales()
        }
    }
}

