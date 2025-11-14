package cl.duoc.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.usecase.GetUserLastLocationUseCase
import cl.duoc.app.model.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado UI para la pantalla del mapa de ubicación del paciente
 */
data class PatientLocationMapUiState(
    val patientName: String = "Paciente",
    val patientLocation: LocationData? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val mapZoom: Float = 16f,
    val isSimulated: Boolean = false // Indica si la ubicación es simulada
)

/**
 * ViewModel para la pantalla del mapa de ubicación del paciente (para tutores)
 * Gestiona la visualización de la ubicación del paciente en tiempo real
 */
class PatientLocationMapViewModel(
    private val getUserLastLocationUseCase: GetUserLastLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientLocationMapUiState())
    val uiState: StateFlow<PatientLocationMapUiState> = _uiState.asStateFlow()

    /**
     * Carga la ubicación del paciente
     */
    fun loadPatientLocation(patientId: String, patientName: String = "Paciente") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, patientName = patientName) }
            try {
                val location = getUserLastLocationUseCase.execute(patientId)

                if (location != null) {
                    _uiState.update {
                        it.copy(
                            patientLocation = location,
                            isLoading = false,
                            hasError = false,
                            isSimulated = false
                        )
                    }
                } else {
                    // Si no hay ubicación real, simular una ubicación
                    simulatePatientLocation()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = e.message ?: "Error al cargar ubicación del paciente"
                    )
                }
            }
        }
    }

    /**
     * Simula una ubicación del paciente (para testing)
     * En producción, esto sería reemplazado con datos reales
     */
    private fun simulatePatientLocation() {
        val simulatedLocation = LocationData(
            latitude = -33.8688,  // Santiago, Chile
            longitude = -51.2093,
            accuracy = 15f,
            timestamp = System.currentTimeMillis()
        )

        _uiState.update {
            it.copy(
                patientLocation = simulatedLocation,
                isLoading = false,
                hasError = false,
                isSimulated = true
            )
        }
    }

    /**
     * Centra el mapa en la ubicación del paciente
     */
    fun centerOnPatient() {
        _uiState.update { it.copy(mapZoom = 17f) }
    }

    /**
     * Aleja el zoom del mapa
     */
    fun zoomOut() {
        _uiState.update { it.copy(mapZoom = maxOf(10f, it.mapZoom - 2f)) }
    }

    /**
     * Acerca el zoom del mapa
     */
    fun zoomIn() {
        _uiState.update { it.copy(mapZoom = minOf(21f, it.mapZoom + 2f)) }
    }

    /**
     * Recarga la ubicación del paciente
     */
    fun refreshLocation(patientId: String) {
        viewModelScope.launch {
            try {
                val location = getUserLastLocationUseCase.execute(patientId)
                _uiState.update {
                    it.copy(
                        patientLocation = location,
                        isSimulated = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        hasError = true,
                        errorMessage = "Error al actualizar ubicación"
                    )
                }
            }
        }
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

