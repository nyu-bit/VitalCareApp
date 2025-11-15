package cl.duoc.app.ui.vitalsigns

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.anomaly.AnomalyDetectionService
import cl.duoc.app.data.anomaly.AnomalyNotificationManager
import cl.duoc.app.data.repository.AlertRepositoryRoomImpl
import cl.duoc.app.domain.usecase.GetRecentVitalSignsUseCase
import cl.duoc.app.domain.usecase.GetVitalSignsByDateRangeUseCase
import cl.duoc.app.domain.usecase.CalculateRiskLevelUseCase
import cl.duoc.app.model.VitalSigns
import cl.duoc.app.utils.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de visualización de signos vitales (HU-03)
 * 
 * Responsabilidades:
 * - Cargar signos vitales del usuario
 * - Calcular nivel de riesgo
 * - Filtrar por rango de fechas
 * - Proporcionar estadísticas
 * - HU-04: Detectar anomalías y generar alertas automáticas
 */
class VitalSignsViewModel(
    private val getRecentVitalSignsUseCase: GetRecentVitalSignsUseCase,
    private val getVitalSignsByDateRangeUseCase: GetVitalSignsByDateRangeUseCase,
    private val calculateRiskLevelUseCase: CalculateRiskLevelUseCase,
    private val anomalyDetectionService: AnomalyDetectionService,
    private val alertRepository: AlertRepositoryRoomImpl,
    private val context: Context
) : ViewModel() {

    private val notificationManager = AnomalyNotificationManager(context)

    private val _uiState = MutableStateFlow(VitalSignsUiState())
    val uiState: StateFlow<VitalSignsUiState> = _uiState.asStateFlow()

    private val _anomalyDetectionState = MutableStateFlow<AnomalyDetectionState>(AnomalyDetectionState.Idle)
    val anomalyDetectionState: StateFlow<AnomalyDetectionState> = _anomalyDetectionState.asStateFlow()

    /**
     * Carga los signos vitales recientes del usuario
     * HU-04: Ahora también detecta anomalías en cada registro
     */
    fun loadVitalSigns(userId: String, limit: Int = 20) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val vitalSignsList = getRecentVitalSignsUseCase.execute(userId, limit)

                // Calcular nivel de riesgo para cada registro
                val vitalSignsWithRisk = vitalSignsList.map { vitalSigns ->
                    val riskLevel = calculateRiskLevelUseCase.execute(vitalSigns)
                    VitalSignsWithRisk(vitalSigns, riskLevel)
                }

                // Calcular estadísticas
                val stats = if (vitalSignsList.isNotEmpty()) {
                    calculateStatistics(vitalSignsList)
                } else null

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        vitalSignsList = vitalSignsWithRisk,
                        statistics = stats,
                        error = null
                    )
                }
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "VitalSignsViewModel",
                    customMessage = "Error al cargar signos vitales"
                )
                _uiState.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }

    /**
     * Filtra signos vitales por rango de fechas
     */
    fun filterByDateRange(userId: String, startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val vitalSignsList = getVitalSignsByDateRangeUseCase.execute(
                    userId = userId,
                    startDate = startDate,
                    endDate = endDate
                )

                val vitalSignsWithRisk = vitalSignsList.map { vitalSigns ->
                    val riskLevel = calculateRiskLevelUseCase.execute(vitalSigns)
                    VitalSignsWithRisk(vitalSigns, riskLevel)
                }

                val stats = if (vitalSignsList.isNotEmpty()) {
                    calculateStatistics(vitalSignsList)
                } else null

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        vitalSignsList = vitalSignsWithRisk,
                        statistics = stats,
                        error = null
                    )
                }
            } catch (e: Exception) {
                val errorMessage = ErrorHandler.handleException(
                    exception = e,
                    tag = "VitalSignsViewModel"
                )
                _uiState.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }

    /**
     * Filtra por nivel de riesgo
     */
    fun filterByRiskLevel(riskLevel: String?) {
        _uiState.update { currentState ->
            currentState.copy(selectedRiskFilter = riskLevel)
        }
    }

    /**
     * Refresca los datos
     */
    fun refresh(userId: String) {
        loadVitalSigns(userId)
    }

    /**
     * Limpia el error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * HU-04: Detecta anomalías en signos vitales y genera alertas
     * Este método se debe llamar cada vez que se registran nuevos signos vitales
     */
    fun detectAnomaliesInVitalSigns(vitalSigns: VitalSigns, userId: Long) {
        viewModelScope.launch {
            try {
                _anomalyDetectionState.update { AnomalyDetectionState.Analyzing }

                // 1. Detectar anomalías
                val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

                if (anomalies.isNotEmpty()) {
                    // 2. Guardar alertas en base de datos
                    val alerts = anomalyDetectionService.createAlertsFromAnomalies(
                        userId = userId,
                        vitalSigns = vitalSigns,
                        anomalies = anomalies
                    )
                    
                    val alertIds = alertRepository.insertAlerts(alerts)

                    // 3. Mostrar notificaciones para anomalías prioritarias
                    anomalies.forEach { anomaly ->
                        if (anomalyDetectionService.requiresImmediateNotification(anomaly)) {
                            notificationManager.showAnomalyNotification(anomaly)
                        }
                    }

                    _anomalyDetectionState.update {
                        AnomalyDetectionState.AnomaliesDetected(
                            anomalies = anomalies,
                            alertIds = alertIds
                        )
                    }
                } else {
                    _anomalyDetectionState.update { AnomalyDetectionState.Normal }
                }

            } catch (e: Exception) {
                _anomalyDetectionState.update {
                    AnomalyDetectionState.Error("Error al detectar anomalías: ${e.message}")
                }
            }
        }
    }

    /**
     * Limpia el estado de detección de anomalías
     */
    fun clearAnomalyDetectionState() {
        _anomalyDetectionState.value = AnomalyDetectionState.Idle
    }

    /**
     * Verifica si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * Calcula estadísticas de los signos vitales
     */
    private fun calculateStatistics(vitalSignsList: List<VitalSigns>): VitalSignsStatistics {
        val validHeartRates = vitalSignsList.mapNotNull { it.heartRate }
        val validBloodPressures = vitalSignsList.filter { 
            it.bloodPressureSystolic != null && it.bloodPressureDiastolic != null 
        }
        val validOxygenSaturations = vitalSignsList.mapNotNull { it.oxygenSaturation }
        val validTemperatures = vitalSignsList.mapNotNull { it.temperature }

        return VitalSignsStatistics(
            totalRecords = vitalSignsList.size,
            averageHeartRate = if (validHeartRates.isNotEmpty()) 
                validHeartRates.average().toInt() else null,
            minHeartRate = validHeartRates.minOrNull(),
            maxHeartRate = validHeartRates.maxOrNull(),
            averageSystolic = if (validBloodPressures.isNotEmpty())
                validBloodPressures.mapNotNull { it.bloodPressureSystolic }.average().toInt() else null,
            averageDiastolic = if (validBloodPressures.isNotEmpty())
                validBloodPressures.mapNotNull { it.bloodPressureDiastolic }.average().toInt() else null,
            averageOxygenSaturation = if (validOxygenSaturations.isNotEmpty())
                validOxygenSaturations.average().toInt() else null,
            minOxygenSaturation = validOxygenSaturations.minOrNull(),
            averageTemperature = if (validTemperatures.isNotEmpty())
                validTemperatures.average() else null
        )
    }
}

/**
 * Signos vitales con nivel de riesgo calculado
 */
data class VitalSignsWithRisk(
    val vitalSigns: VitalSigns,
    val riskLevel: String
)

/**
 * Estadísticas calculadas de los signos vitales
 */
data class VitalSignsStatistics(
    val totalRecords: Int,
    val averageHeartRate: Int?,
    val minHeartRate: Int?,
    val maxHeartRate: Int?,
    val averageSystolic: Int?,
    val averageDiastolic: Int?,
    val averageOxygenSaturation: Int?,
    val minOxygenSaturation: Int?,
    val averageTemperature: Double?
)

/**
 * Estado de la UI para visualización de signos vitales
 */
data class VitalSignsUiState(
    val isLoading: Boolean = false,
    val vitalSignsList: List<VitalSignsWithRisk> = emptyList(),
    val statistics: VitalSignsStatistics? = null,
    val selectedRiskFilter: String? = null,
    val error: String? = null
) {
    /**
     * Lista filtrada según el filtro de riesgo seleccionado
     */
    val filteredVitalSigns: List<VitalSignsWithRisk>
        get() = if (selectedRiskFilter != null) {
            vitalSignsList.filter { it.riskLevel == selectedRiskFilter }
        } else {
            vitalSignsList
        }
}

/**
 * HU-04: Estado de detección de anomalías
 */
sealed class AnomalyDetectionState {
    object Idle : AnomalyDetectionState()
    object Analyzing : AnomalyDetectionState()
    object Normal : AnomalyDetectionState()
    data class AnomaliesDetected(
        val anomalies: List<AnomalyDetectionService.AnomalyResult>,
        val alertIds: List<Long>
    ) : AnomalyDetectionState()
    data class Error(val message: String) : AnomalyDetectionState()
}
