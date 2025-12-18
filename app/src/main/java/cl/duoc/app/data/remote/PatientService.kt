package cl.duoc.app.data.remote

import cl.duoc.app.data.model.AlertsResponse
import cl.duoc.app.data.model.CreateAlertRequest
import cl.duoc.app.data.model.CreateAlertResponse
import cl.duoc.app.data.model.LoginRequest
import cl.duoc.app.data.model.LoginResponse
import cl.duoc.app.data.model.PatientsResponse
import cl.duoc.app.data.model.UpdateVitalsRequest
import cl.duoc.app.data.model.UpdateVitalsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit service interface (Integracion_Backend.md)
 *
 * Base URL sugerida: http://10.0.2.2:8080/api
 * (ver sección 5.1 y 9)
 */
interface PatientService {

    /** POST /api/auth/login (sección 2.1) */
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    /** GET /api/tutors/{tutorId}/patients (sección 2.2) */
    @GET("tutors/{tutorId}/patients")
    suspend fun getPatients(
        @Path("tutorId") tutorId: String
    ): PatientsResponse

    /** PUT /api/patients/{patientId}/vitals (sección 2.3) */
    @PUT("patients/{patientId}/vitals")
    suspend fun updateVitals(
        @Path("patientId") patientId: String,
        @Body vitals: UpdateVitalsRequest
    ): UpdateVitalsResponse

    /** GET /api/patients/{patientId}/alerts (sección 2.4) */
    @GET("patients/{patientId}/alerts")
    suspend fun getAlerts(
        @Path("patientId") patientId: String
    ): AlertsResponse

    /** POST /api/patients/{patientId}/alerts (sección 2.5) */
    @POST("patients/{patientId}/alerts")
    suspend fun createAlert(
        @Path("patientId") patientId: String,
        @Body alert: CreateAlertRequest
    ): CreateAlertResponse
}
