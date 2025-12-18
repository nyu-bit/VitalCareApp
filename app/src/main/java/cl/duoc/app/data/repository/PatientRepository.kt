package cl.duoc.app.data.repository

import cl.duoc.app.data.model.Alert
import cl.duoc.app.data.model.CreateAlertRequest
import cl.duoc.app.data.model.CreateAlertResponse
import cl.duoc.app.data.model.PatientsResponse
import cl.duoc.app.data.model.UpdateVitalsRequest
import cl.duoc.app.data.model.UpdateVitalsResponse
import cl.duoc.app.data.model.Patient
import cl.duoc.app.data.remote.PatientService
import cl.duoc.app.network.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Repository de pacientes/alertas/vitales basado en Integracion_Backend.md:
 * - Sección 2.2: GET /api/tutors/{tutorId}/patients -> {patients:[...]}
 * - Sección 2.3: PUT /api/patients/{patientId}/vitals
 * - Sección 2.4: GET /api/patients/{patientId}/alerts -> {alerts:[...]}
 * - Sección 2.5: POST /api/patients/{patientId}/alerts
 * - Sección 4.4: patrón de Flow + emit(emptyList()) en error
 */
class PatientRepository(
    private val api: PatientService
) {

    fun getPatients(tutorId: String): Flow<List<Patient>> = flow {
        val response: PatientsResponse = api.getPatients(tutorId)
        emit(response.patients)
    }.catch { e ->
        // Integracion_Backend.md 4.4: emit(emptyList()) y luego propagar error para UI
        emit(emptyList())
        throw e
    }

    suspend fun updatePatientVitals(
        patientId: String,
        vitals: UpdateVitalsRequest
    ): Result<UpdateVitalsResponse> {
        return safeApiCall { api.updateVitals(patientId, vitals) }
    }

    suspend fun getAlerts(patientId: String): Result<List<Alert>> {
        return safeApiCall { api.getAlerts(patientId).alerts }
    }

    suspend fun createAlert(
        patientId: String,
        request: CreateAlertRequest
    ): Result<CreateAlertResponse> {
        return safeApiCall { api.createAlert(patientId, request) }
    }
}
