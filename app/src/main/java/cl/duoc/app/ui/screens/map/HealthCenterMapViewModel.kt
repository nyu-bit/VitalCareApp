package cl.duoc.app.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.usecase.GetHealthCenterLocationUseCase
import cl.duoc.app.domain.usecase.GetCurrentLocationUseCase
import cl.duoc.app.model.HealthCenter
import cl.duoc.app.model.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado UI para la pantalla del mapa del centro de salud
 */
data class HealthCenterMapUiState(
    val healthCenter: HealthCenter? = null,
    val userLocation: LocationData? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val mapZoom: Float = 15f
)

/**
 * ViewModel para la pantalla del mapa del centro de salud mental
 * Gestiona la obtención de ubicaciones y centros de salud
 */
class HealthCenterMapViewModel(
    private val getHealthCenterLocationUseCase: GetHealthCenterLocationUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthCenterMapUiState())
    val uiState: StateFlow<HealthCenterMapUiState> = _uiState.asStateFlow()

    init {
        loadHealthCenterLocation()
    }

    /**
     * Carga la ubicación del centro de salud
     */
    fun loadHealthCenterLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val healthCenter = getHealthCenterLocationUseCase.execute()
                if (healthCenter != null) {
                    _uiState.update {
                        it.copy(
                            healthCenter = healthCenter,
                            isLoading = false,
                            hasError = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasError = true,
                            errorMessage = "Centro de salud no encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    /**
     * Carga la ubicación actual del usuario
     */
    fun loadUserLocation() {
        viewModelScope.launch {
            try {
                val location = getCurrentLocationUseCase.execute()
                _uiState.update {
                    it.copy(userLocation = location)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        hasError = true,
                        errorMessage = "No se pudo obtener ubicación: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Centra el mapa en el centro de salud
     */
    fun centerOnHealthCenter() {
        _uiState.update { it.copy(mapZoom = 17f) }
    }

    /**
     * Centra el mapa en la ubicación del usuario
     */
    fun centerOnUserLocation() {
        _uiState.update { it.copy(mapZoom = 16f) }
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

