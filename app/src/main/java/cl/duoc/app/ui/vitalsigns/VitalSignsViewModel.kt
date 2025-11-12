package cl.duoc.app.ui.vitalsigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 */
class VitalSignsViewModel(
    private val getRecentVitalSignsUseCase: GetRecentVitalSignsUseCase,
    private val getVitalSignsByDateRangeUseCase: GetVitalSignsByDateRangeUseCase,
    private val calculateRiskLevelUseCase: CalculateRiskLevelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VitalSignsUiState())
    val uiState: StateFlow<VitalSignsUiState> = _uiState.asStateFlow()

    /**
     * Carga los signos vitales recientes del usuario
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
