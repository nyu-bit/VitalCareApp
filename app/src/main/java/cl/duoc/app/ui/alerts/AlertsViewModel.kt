package cl.duoc.app.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * - Marcar alertas como leídas
 * - Proporcionar estadísticas de alertas
 */
class AlertsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts()
    }

    /**
     * Carga las alertas del usuario
     */
    fun loadAlerts(userId: String = "user123") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // TODO: Implementar con repositorio real
                val mockAlerts = generateMockAlerts(userId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allAlerts = mockAlerts,
                        error = null
                    )
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
     * Marca una alerta como leída
     */
    fun markAsRead(alertId: String) {
        _uiState.update { currentState ->
            val updatedAlerts = currentState.allAlerts.map { alert ->
                if (alert.id == alertId) alert.copy(isRead = true) else alert
            }
            currentState.copy(allAlerts = updatedAlerts)
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
    fun deleteAlert(alertId: String) {
        _uiState.update { currentState ->
            val updatedAlerts = currentState.allAlerts.filter { it.id != alertId }
            currentState.copy(allAlerts = updatedAlerts)
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
    fun refresh(userId: String = "user123") {
        loadAlerts(userId)
    }

    // Generador de datos mock (temporal)
    private fun generateMockAlerts(userId: String): List<Alert> {
        val now = System.currentTimeMillis()
        return listOf(
            Alert(
                id = "1",
                userId = userId,
                title = "Presión Arterial Elevada",
                message = "Tu presión arterial registró 145/95 mmHg. Se recomienda consultar con tu médico.",
                severity = "Alto",
                type = "Signos Vitales",
                isRead = false,
                timestamp = now - 3600000,
                relatedId = "vital_001"
            ),
            Alert(
                id = "2",
                userId = userId,
                title = "Frecuencia Cardíaca Baja",
                message = "Se detectó una frecuencia cardíaca de 52 bpm. Considera hacer seguimiento.",
                severity = "Medio",
                type = "Signos Vitales",
                isRead = false,
                timestamp = now - 7200000,
                relatedId = "vital_002"
            ),
            Alert(
                id = "3",
                userId = userId,
                title = "Cita Médica Próxima",
                message = "Recuerda tu cita con el Dr. González mañana a las 10:00 AM.",
                severity = "Bajo",
                type = "Cita",
                isRead = true,
                timestamp = now - 86400000
            ),
            Alert(
                id = "4",
                userId = userId,
                title = "Oxigenación Crítica",
                message = "Saturación de oxígeno de 88%. ¡Busca atención médica inmediata!",
                severity = "Crítico",
                type = "Signos Vitales",
                isRead = false,
                timestamp = now - 1800000,
                relatedId = "vital_003"
            ),
            Alert(
                id = "5",
                userId = userId,
                title = "Recordatorio de Medicamento",
                message = "Es hora de tomar tu medicamento para la presión arterial.",
                severity = "Medio",
                type = "Medicamento",
                isRead = true,
                timestamp = now - 10800000
            ),
            Alert(
                id = "6",
                userId = userId,
                title = "Actualización del Sistema",
                message = "Nueva versión disponible con mejoras de seguridad.",
                severity = "Bajo",
                type = "Sistema",
                isRead = true,
                timestamp = now - 172800000
            )
        )
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
