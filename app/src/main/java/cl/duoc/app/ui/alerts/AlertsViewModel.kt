package cl.duoc.app.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.api.AlertaDto
import cl.duoc.app.data.repository.AlertasRepository
import cl.duoc.app.model.Alert
import cl.duoc.app.utils.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de historial de alertas (HU-15)
 * 
 * Responsabilidades:
 * - Gestionar lista de alertas
 * - Filtrar por tipo y severidad
 * - Marcar alertas como leídas/atendidas
 * - Crear, actualizar y eliminar alertas
 * - Proporcionar estadísticas de alertas
 */
class AlertsViewModel : ViewModel() {

    private val alertasRepository = AlertasRepository()

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts()
    }

    /**
     * Carga las alertas del usuario desde la API remota
     */
    fun loadAlerts(pacienteId: String = "user123") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = alertasRepository.getByPaciente(pacienteId)

                result.onSuccess { alertaDtos ->
                    val alerts = alertaDtos.map { dto ->
                        Alert(
                            id = dto.id ?: "",
                            userId = dto.pacienteId,
                            title = dto.titulo,
                            message = dto.mensaje,
                            severity = dto.severidad,
                            type = dto.tipo,
                            isRead = dto.leida,
                            timestamp = dto.timestamp,
                            relatedId = dto.idRelacionado
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allAlerts = alerts,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    val errorMessage = ErrorHandler.handleException(
                        exception = exception,
                        tag = "AlertsViewModel"
                    )
                    _uiState.update {
                        it.copy(isLoading = false, error = errorMessage)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "AlertsViewModel"
                )
                _uiState.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }

    /**
     * Crea una nueva alerta
     */
    fun createAlerta(
        pacienteId: String,
        titulo: String,
        mensaje: String,
        severidad: String,
        tipo: String,
        idRelacionado: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val alertaDto = AlertaDto(
                    pacienteId = pacienteId,
                    titulo = titulo,
                    mensaje = mensaje,
                    severidad = severidad,
                    tipo = tipo,
                    leida = false,
                    timestamp = System.currentTimeMillis(),
                    idRelacionado = idRelacionado
                )

                val result = alertasRepository.create(alertaDto)

                result.onSuccess { createdDto ->
                    val newAlert = Alert(
                        id = createdDto.id ?: "",
                        userId = createdDto.pacienteId,
                        title = createdDto.titulo,
                        message = createdDto.mensaje,
                        severity = createdDto.severidad,
                        type = createdDto.tipo,
                        isRead = createdDto.leida,
                        timestamp = createdDto.timestamp,
                        relatedId = createdDto.idRelacionado
                    )

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            allAlerts = state.allAlerts + newAlert,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    val errorMessage = ErrorHandler.handleException(
                        exception = exception,
                        tag = "AlertsViewModel.createAlerta"
                    )
                    _uiState.update {
                        it.copy(isLoading = false, error = errorMessage)
                    }
                }
            } catch (e: Throwable) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "AlertsViewModel.createAlerta"
                )
                _uiState.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }

    /**
     * Marca una alerta como atendida (actualiza en la API)
     */
    fun markAsAttended(alertId: String) {
        viewModelScope.launch {
            try {
                // Encontrar la alerta actual
                val alertToUpdate = _uiState.value.allAlerts.find { it.id == alertId }
                if (alertToUpdate != null) {
                    // Crear DTO con leida = true
                    val alertaDto = AlertaDto(
                        id = alertId,
                        pacienteId = alertToUpdate.userId,
                        titulo = alertToUpdate.title,
                        mensaje = alertToUpdate.message,
                        severidad = alertToUpdate.severity,
                        tipo = alertToUpdate.type,
                        leida = true,
                        timestamp = alertToUpdate.timestamp,
                        idRelacionado = alertToUpdate.relatedId
                    )

                    // Actualizar en la API
                    val result = alertasRepository.markAsAttended(alertId, alertaDto)

                    result.onSuccess {
                        // Actualizar estado local
                        _uiState.update { state ->
                            val updated = state.allAlerts.map { alert ->
                                if (alert.id == alertId) alert.copy(isRead = true) else alert
                            }
                            state.copy(allAlerts = updated, error = null)
                        }
                    }.onFailure { exception ->
                        val errorMessage = ErrorHandler.handleException(
                            exception = exception,
                            tag = "AlertsViewModel.markAsAttended"
                        )
                        _uiState.update {
                            it.copy(error = errorMessage)
                        }
                    }
                }
            } catch (e: Throwable) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "AlertsViewModel.markAsAttended"
                )
                _uiState.update {
                    it.copy(error = errorMessage)
                }
            }
        }
    }

    /**
     * Marca todas las alertas como leídas
     */
    fun markAllAsRead() {
        _uiState.update { currentState ->
            val updatedAlerts = currentState.allAlerts.map { it.copy(isRead = true) }
            currentState.copy(allAlerts = updatedAlerts)
        }
    }

    /**
     * Elimina una alerta
     */
    fun deleteAlerta(alertId: String) {
        viewModelScope.launch {
            try {
                val result = alertasRepository.delete(alertId)

                result.onSuccess {
                    _uiState.update { state ->
                        val updated = state.allAlerts.filter { it.id != alertId }
                        state.copy(allAlerts = updated, error = null)
                    }
                }.onFailure { exception ->
                    val errorMessage = ErrorHandler.handleException(
                        exception = exception,
                        tag = "AlertsViewModel.deleteAlerta"
                    )
                    _uiState.update {
                        it.copy(error = errorMessage)
                    }
                }
            } catch (e: Throwable) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "AlertsViewModel.deleteAlerta"
                )
                _uiState.update {
                    it.copy(error = errorMessage)
                }
            }
        }
    }

    /**
     * Filtra alertas por severidad
     */
    fun filterBySeverity(severity: String?) {
        _uiState.update { it.copy(selectedSeverityFilter = severity) }
    }

    /**
     * Filtra alertas por tipo
     */
    fun filterByType(type: String?) {
        _uiState.update { it.copy(selectedTypeFilter = type) }
    }

    /**
     * Filtra alertas por estado de lectura
     */
    fun filterByReadStatus(showOnlyUnread: Boolean) {
        _uiState.update { it.copy(showOnlyUnread = showOnlyUnread) }
    }

    /**
     * Limpia todos los filtros
     */
    fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedSeverityFilter = null,
                selectedTypeFilter = null,
                showOnlyUnread = false
            )
        }
    }

    /**
     * Refresca las alertas
     */
    fun refresh(pacienteId: String = "user123") {
        loadAlerts(pacienteId)
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * Estado de la UI para el historial de alertas
 */
data class AlertsUiState(
    val isLoading: Boolean = false,
    val allAlerts: List<Alert> = emptyList(),
    val selectedSeverityFilter: String? = null,
    val selectedTypeFilter: String? = null,
    val showOnlyUnread: Boolean = false,
    val error: String? = null
) {
    /**
     * Lista filtrada de alertas
     */
    val filteredAlerts: List<Alert>
        get() {
            var filtered = allAlerts

            // Filtrar por estado de lectura
            if (showOnlyUnread) {
                filtered = filtered.filter { !it.isRead }
            }

            // Filtrar por severidad
            selectedSeverityFilter?.let { severity ->
                filtered = filtered.filter { it.severity == severity }
            }

            // Filtrar por tipo
            selectedTypeFilter?.let { type ->
                filtered = filtered.filter { it.type == type }
            }

            return filtered.sortedByDescending { it.timestamp }
        }

    /**
     * Contador de alertas no leídas
     */
    val unreadCount: Int
        get() = allAlerts.count { !it.isRead }

    /**
     * Contadores por severidad
     */
    val criticalCount: Int
        get() = allAlerts.count { it.severity == "Crítico" && !it.isRead }

    val highCount: Int
        get() = allAlerts.count { it.severity == "Alto" && !it.isRead }

    val mediumCount: Int
        get() = allAlerts.count { it.severity == "Medio" && !it.isRead }

    /**
     * Contadores por tipo
     */
    val vitalSignsCount: Int
        get() = allAlerts.count { it.type == "Signos Vitales" }

    val appointmentCount: Int
        get() = allAlerts.count { it.type == "Cita" }

    val medicationCount: Int
        get() = allAlerts.count { it.type == "Medicamento" }

    val systemCount: Int
        get() = allAlerts.count { it.type == "Sistema" }
}
