package cl.duoc.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.model.Patient
import cl.duoc.app.data.model.UpdateVitalsRequest
import cl.duoc.app.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel basado en Integracion_Backend.md sección 4.5 (loadPatients + updateVitals)
 * usando un estado de UI sellado (mejor para Compose que LiveData).
 */
class PatientMonitorViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PatientUiState>(PatientUiState.Idle)
    val uiState: StateFlow<PatientUiState> = _uiState.asStateFlow()

    fun loadPatients(tutorId: String) {
        viewModelScope.launch {
            _uiState.value = PatientUiState.Loading

            repository.getPatients(tutorId)
                .catch { e ->
                    _uiState.value = PatientUiState.Error(e.message ?: "Unknown error")
                }
                .collect { patients ->
                    _uiState.value = PatientUiState.Success(patients)
                }
        }
    }

    fun updateVitals(patientId: String, vitals: UpdateVitalsRequest) {
        viewModelScope.launch {
            val result = repository.updatePatientVitals(patientId, vitals)
            if (result.isFailure) {
                _uiState.value = PatientUiState.Error(
                    "Error updating vitals: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }
}

sealed class PatientUiState {
    data object Idle : PatientUiState()
    data object Loading : PatientUiState()
    data class Success(val patients: List<Patient>) : PatientUiState()
    data class Error(val message: String) : PatientUiState()
}
