package cl.duoc.app.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import cl.duoc.app.data.local.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado de la UI del perfil
 */
data class ProfileUiState(
    val tutorName: String = "",
    val tutorAge: String = "",
    val tutorPhone: String = "",
    val patientName: String = "",
    val emergencyContact: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

/**
 * ViewModel para la pantalla de perfil
 * Maneja la lógica de carga y guardado de datos del perfil
 */
class ProfileViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    companion object {
        // Keys para SharedPreferences
        private const val KEY_TUTOR_NAME = "profile_tutor_name"
        private const val KEY_TUTOR_AGE = "profile_tutor_age"
        private const val KEY_TUTOR_PHONE = "profile_tutor_phone"
        private const val KEY_PATIENT_NAME = "profile_patient_name"
        private const val KEY_EMERGENCY_CONTACT = "profile_emergency_contact"
    }
    
    /**
     * Actualiza el nombre del tutor
     */
    fun updateTutorName(name: String) {
        _uiState.update { it.copy(tutorName = name) }
    }
    
    /**
     * Actualiza la edad del tutor
     */
    fun updateTutorAge(age: String) {
        _uiState.update { it.copy(tutorAge = age) }
    }
    
    /**
     * Actualiza el teléfono del tutor
     */
    fun updateTutorPhone(phone: String) {
        _uiState.update { it.copy(tutorPhone = phone) }
    }
    
    /**
     * Actualiza el nombre del paciente
     */
    fun updatePatientName(name: String) {
        _uiState.update { it.copy(patientName = name) }
    }
    
    /**
     * Actualiza el contacto de emergencia
     */
    fun updateEmergencyContact(contact: String) {
        _uiState.update { it.copy(emergencyContact = contact) }
    }
    
    /**
     * Carga los datos del perfil desde SharedPreferences
     */
    fun loadProfileData(context: Context) {
        val prefsManager = SharedPreferencesManager(context)
        
        _uiState.update {
            it.copy(
                tutorName = prefsManager.getString(KEY_TUTOR_NAME, ""),
                tutorAge = prefsManager.getString(KEY_TUTOR_AGE, ""),
                tutorPhone = prefsManager.getString(KEY_TUTOR_PHONE, ""),
                patientName = prefsManager.getString(KEY_PATIENT_NAME, ""),
                emergencyContact = prefsManager.getString(KEY_EMERGENCY_CONTACT, "")
            )
        }
    }
    
    /**
     * Guarda los datos del perfil en SharedPreferences
     */
    fun saveProfileData(context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        
        val prefsManager = SharedPreferencesManager(context)
        val currentState = _uiState.value
        
        // Guardar cada campo
        prefsManager.saveString(KEY_TUTOR_NAME, currentState.tutorName)
        prefsManager.saveString(KEY_TUTOR_AGE, currentState.tutorAge)
        prefsManager.saveString(KEY_TUTOR_PHONE, currentState.tutorPhone)
        prefsManager.saveString(KEY_PATIENT_NAME, currentState.patientName)
        prefsManager.saveString(KEY_EMERGENCY_CONTACT, currentState.emergencyContact)
        
        // Actualizar estado
        _uiState.update { 
            it.copy(
                isLoading = false,
                isSaved = true
            ) 
        }
    }
    
    /**
     * Resetea el estado de guardado
     */
    fun resetSavedState() {
        _uiState.update { it.copy(isSaved = false) }
    }
}
