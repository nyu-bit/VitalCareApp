package cl.duoc.app.ui.vitalsigns

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.anomaly.AnomalyDetectionService
import cl.duoc.app.data.anomaly.AnomalyNotificationManager
import cl.duoc.app.data.repository.AlertRepositoryRoomImpl
import cl.duoc.app.model.VitalSigns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar detección y monitoreo de anomalías en signos vitales
 * 
 * HU-04: Alerta Automática por Anomalías
 * - Detecta valores anormales en tiempo real
 * - Genera notificaciones locales con sonido/vibración
 * - Guarda alertas en el historial
 */
class AnomalyMonitoringViewModel(
    private val anomalyDetectionService: AnomalyDetectionService,
    private val alertRepository: AlertRepositoryRoomImpl,
    private val context: Context
) : ViewModel() {

    private val notificationManager = AnomalyNotificationManager(context)

    private val _monitoringState = MutableStateFlow<AnomalyMonitoringState>(AnomalyMonitoringState.Idle)
    val monitoringState: StateFlow<AnomalyMonitoringState> = _monitoringState.asStateFlow()

    private val _lastAnomaliesDetected = MutableStateFlow<List<AnomalyDetectionService.AnomalyResult>>(emptyList())
    val lastAnomaliesDetected: StateFlow<List<AnomalyDetectionService.AnomalyResult>> = _lastAnomaliesDetected.asStateFlow()

    /**
     * Monitorea signos vitales y detecta anomalías
     * Este método se llama cada vez que se ingresan o actualizan signos vitales
     */
    fun monitorVitalSigns(vitalSigns: VitalSigns, userId: String) {
        viewModelScope.launch {
            try {
                _monitoringState.update { AnomalyMonitoringState.Analyzing }

                // 1. Detectar anomalías
                val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

                _lastAnomaliesDetected.value = anomalies

                if (anomalies.isNotEmpty()) {
                    // 2. Guardar alertas en base de datos
                    val alerts = anomalyDetectionService.createAlertsFromAnomalies(
                        userId = userId,
                        vitalSigns = vitalSigns,
                        anomalies = anomalies
                    )
                    
                    val alertIds = alertRepository.insertAlerts(alerts)

                    // 3. Mostrar notificaciones para anomalías que requieren atención inmediata
                    anomalies.forEach { anomaly ->
                        if (anomalyDetectionService.requiresImmediateNotification(anomaly)) {
                            notificationManager.showAnomalyNotification(anomaly)
                        }
                    }

                    _monitoringState.update {
                        AnomalyMonitoringState.AnomaliesDetected(
                            count = anomalies.size,
                            highPriorityCount = anomalies.count { it.priority == "Alta" },
                            alertIds = alertIds
                        )
                    }
                } else {
                    _monitoringState.update { AnomalyMonitoringState.Normal }
                }

            } catch (e: Exception) {
                _monitoringState.update {
                    AnomalyMonitoringState.Error("Error al monitorear signos vitales: ${e.message}")
                }
            }
        }
    }

    /**
     * Analiza signos vitales sin guardar (para preview)
     */
    fun previewAnomalies(vitalSigns: VitalSigns) {
        viewModelScope.launch {
            try {
                val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)
                _lastAnomaliesDetected.value = anomalies
            } catch (e: Exception) {
                _monitoringState.update {
                    AnomalyMonitoringState.Error("Error al analizar: ${e.message}")
                }
            }
        }
    }

    /**
     * Limpia el estado de monitoreo
     */
    fun clearMonitoringState() {
        _monitoringState.value = AnomalyMonitoringState.Idle
        _lastAnomaliesDetected.value = emptyList()
    }

    /**
     * Verifica si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * Obtiene resumen de la última detección
     */
    fun getLastDetectionSummary(): String {
        val anomalies = _lastAnomaliesDetected.value
        return when {
            anomalies.isEmpty() -> "✅ Todos los signos vitales están en rango normal"
            else -> {
                val highCount = anomalies.count { it.priority == "Alta" }
                val mediumCount = anomalies.count { it.priority == "Media" }
                val lowCount = anomalies.count { it.priority == "Baja" }
                
                buildString {
                    append("⚠️ Se detectaron ${anomalies.size} anomalía(s):\n")
                    if (highCount > 0) append("• $highCount de prioridad ALTA\n")
                    if (mediumCount > 0) append("• $mediumCount de prioridad MEDIA\n")
                    if (lowCount > 0) append("• $lowCount de prioridad BAJA")
                }
            }
        }
    }

    /**
     * Obtiene texto descriptivo del estado actual
     */
    fun getStateDescription(): String {
        return when (val state = _monitoringState.value) {
            is AnomalyMonitoringState.Idle -> "En espera de signos vitales"
            is AnomalyMonitoringState.Analyzing -> "Analizando signos vitales..."
            is AnomalyMonitoringState.Normal -> "✅ Signos vitales normales"
            is AnomalyMonitoringState.AnomaliesDetected -> {
                "⚠️ ${state.count} anomalía(s) detectada(s)" +
                    if (state.highPriorityCount > 0) " (${state.highPriorityCount} de alta prioridad)" else ""
            }
            is AnomalyMonitoringState.Error -> "❌ ${state.message}"
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Limpiar recursos si es necesario
    }
}

/**
 * Estados del monitoreo de anomalías
 */
sealed class AnomalyMonitoringState {
    object Idle : AnomalyMonitoringState()
    object Analyzing : AnomalyMonitoringState()
    object Normal : AnomalyMonitoringState()
    data class AnomaliesDetected(
        val count: Int,
        val highPriorityCount: Int,
        val alertIds: List<Long>
    ) : AnomalyMonitoringState()
    data class Error(val message: String) : AnomalyMonitoringState()
}
