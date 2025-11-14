package cl.duoc.app.ui.screens.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.usecase.TriggerSOSUseCase
import cl.duoc.app.domain.usecase.GetLatestSOSEventsUseCase
import cl.duoc.app.domain.usecase.GetCurrentLocationUseCase
import cl.duoc.app.domain.usecase.AcknowledgeSOSEventUseCase
import cl.duoc.app.domain.usecase.ResolveSOSEventUseCase
import cl.duoc.app.model.LocationData
import cl.duoc.app.model.SOSEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado UI para la pantalla de SOS
 */
data class SOSUiState(
    val sosHistory: List<SOSEvent> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isSosActive: Boolean = false,
    val lastSOSEvent: SOSEvent? = null,
    val successMessage: String? = null,
    val isTriggering: Boolean = false,
    val currentLocation: LocationData? = null
)

/**
 * ViewModel para la pantalla de SOS
 * Gestiona la activación de SOS y el historial de eventos
 */
class SOSViewModel(
    private val triggerSOSUseCase: TriggerSOSUseCase,
    private val getLatestSOSEventsUseCase: GetLatestSOSEventsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val acknowledgeSOSEventUseCase: AcknowledgeSOSEventUseCase,
    private val resolveSOSEventUseCase: ResolveSOSEventUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SOSUiState())
    val uiState: StateFlow<SOSUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        // Se debe llamar a loadSOSHistory con el ID del usuario autenticado
    }

    /**
     * Carga el historial de eventos SOS
     */
    fun loadSOSHistory(userId: String) {
        this.currentUserId = userId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val history = getLatestSOSEventsUseCase.execute(userId, limit = 50)
                val activeEvents = history.filter { it.status == "TRIGGERED" }

                _uiState.update {
                    it.copy(
                        sosHistory = history,
                        isSosActive = activeEvents.isNotEmpty(),
                        lastSOSEvent = history.firstOrNull(),
                        isLoading = false,
                        hasError = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                        errorMessage = e.message ?: "Error al cargar historial de SOS"
                    )
                }
            }
        }
    }

    /**
     * Dispara un evento SOS
     * Obtiene la ubicación actual y crea el evento de emergencia
     */
    fun triggerSOS() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTriggering = true) }
            try {
                // Obtener ubicación actual
                val location = getCurrentLocationUseCase.execute() ?:
                    // Si no hay ubicación real, usar ubicación simulada
                    LocationData(
                        latitude = -33.8688,  // Santiago, Chile
                        longitude = -51.2093,
                        accuracy = 20f,
                        timestamp = System.currentTimeMillis()
                    )

                // Disparar SOS
                val event = triggerSOSUseCase.execute(currentUserId, location)

                _uiState.update {
                    it.copy(
                        lastSOSEvent = event,
                        isSosActive = true,
                        isTriggering = false,
                        successMessage = "¡SOS activado! Se ha notificado a tu tutor",
                        hasError = false,
                        currentLocation = location
                    )
                }

                // Limpiar mensaje de éxito después de 5 segundos
                kotlinx.coroutines.delay(5000)
                _uiState.update { it.copy(successMessage = null) }

                // Recargar historial
                loadSOSHistory(currentUserId)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isTriggering = false,
                        hasError = true,
                        errorMessage = "Error al activar SOS: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Reconoce un evento SOS (indica que el tutor fue notificado)
     */
    fun acknowledgeSOS(eventId: String) {
        viewModelScope.launch {
            try {
                val success = acknowledgeSOSEventUseCase.execute(eventId)
                if (success) {
                    _uiState.update {
                        it.copy(
                            lastSOSEvent = it.lastSOSEvent?.copy(
                                status = "ACKNOWLEDGED",
                                tutorNotified = true
                            )
                        )
                    }
                    loadSOSHistory(currentUserId)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        hasError = true,
                        errorMessage = "Error al reconocer SOS"
                    )
                }
            }
        }
    }

    /**
     * Resuelve un evento SOS (cierra la emergencia)
     */
    fun resolveSOSEvent(eventId: String) {
        viewModelScope.launch {
            try {
                val success = resolveSOSEventUseCase.execute(eventId)
                if (success) {
                    _uiState.update {
                        it.copy(
                            lastSOSEvent = it.lastSOSEvent?.copy(status = "RESOLVED"),
                            isSosActive = false
                        )
                    }
                    loadSOSHistory(currentUserId)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        hasError = true,
                        errorMessage = "Error al resolver SOS"
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

