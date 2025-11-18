package cl.duoc.app.ui.dashboard

import androidx.lifecycle.ViewModel
import cl.duoc.app.model.VitalSigns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Thermostat

/**
 * Estado de UI del Dashboard
 */
data class DashboardUiState(
    val vitalSigns: List<VitalSignData> = emptyList(),
    val patientName: String = "Paciente",
    val lastUpdate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para el Dashboard
 * Maneja la lógica de obtención y actualización de signos vitales
 */
class DashboardViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadVitalSigns()
    }
    
    /**
     * Carga los signos vitales (simulados o desde repositorio)
     */
    fun loadVitalSigns() {
        _uiState.update { 
            it.copy(
                vitalSigns = generateSimulatedVitalSigns(),
                lastUpdate = System.currentTimeMillis(),
                isLoading = false
            ) 
        }
    }
    
    /**
     * Actualiza el nombre del paciente
     */
    fun setPatientName(name: String) {
        _uiState.update { it.copy(patientName = name) }
    }
    
    /**
     * Recarga los signos vitales
     */
    fun refreshVitalSigns() {
        _uiState.update { it.copy(isLoading = true) }
        // Simular delay de red
        loadVitalSigns()
    }
    
    /**
     * Convierte VitalSigns del modelo a VitalSignData para UI
     */
    fun convertToVitalSignData(vitalSigns: cl.duoc.app.model.VitalSigns): List<VitalSignData> {
        val result = mutableListOf<VitalSignData>()
        
        // Frecuencia cardíaca
        vitalSigns.heartRate?.let { hr ->
            val alertLevel = when {
                hr in 60..100 -> AlertLevel.NORMAL
                hr in 45..60 || hr in 100..120 -> AlertLevel.WARNING
                else -> AlertLevel.DANGER
            }
            result.add(
                VitalSignData(
                    title = "Frecuencia Cardíaca",
                    value = hr.toString(),
                    unit = "bpm",
                    icon = androidx.compose.material.icons.Icons.Default.Favorite,
                    alertLevel = alertLevel
                )
            )
        }
        
        // Presión arterial
        if (vitalSigns.bloodPressureSystolic != null && vitalSigns.bloodPressureDiastolic != null) {
            val sys = vitalSigns.bloodPressureSystolic
            val dia = vitalSigns.bloodPressureDiastolic
            val alertLevel = when {
                sys in 90..120 && dia in 60..80 -> AlertLevel.NORMAL
                sys in 80..90 || sys in 120..140 -> AlertLevel.WARNING
                else -> AlertLevel.DANGER
            }
            result.add(
                VitalSignData(
                    title = "Presión Arterial",
                    value = "$sys/$dia",
                    unit = "mmHg",
                    icon = androidx.compose.material.icons.Icons.Default.MonitorHeart,
                    alertLevel = alertLevel
                )
            )
        }
        
        // Temperatura
        vitalSigns.temperature?.let { temp ->
            val alertLevel = when {
                temp in 36.1..37.2 -> AlertLevel.NORMAL
                temp in 35.5..36.0 || temp in 37.3..38.0 -> AlertLevel.WARNING
                else -> AlertLevel.DANGER
            }
            result.add(
                VitalSignData(
                    title = "Temperatura",
                    value = String.format("%.1f", temp),
                    unit = "°C",
                    icon = androidx.compose.material.icons.Icons.Default.Thermostat,
                    alertLevel = alertLevel
                )
            )
        }
        
        // Oxígeno
        vitalSigns.oxygenSaturation?.let { o2 ->
            val alertLevel = when {
                o2 in 95..100 -> AlertLevel.NORMAL
                o2 in 90..94 -> AlertLevel.WARNING
                else -> AlertLevel.DANGER
            }
            result.add(
                VitalSignData(
                    title = "Nivel de Oxígeno",
                    value = o2.toString(),
                    unit = "%",
                    icon = androidx.compose.material.icons.Icons.Default.Air,
                    alertLevel = alertLevel
                )
            )
        }
        
        return result
    }
}
