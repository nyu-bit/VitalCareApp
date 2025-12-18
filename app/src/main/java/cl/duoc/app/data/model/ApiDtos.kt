package cl.duoc.app.data.model

/**
 * Wrappers según Integracion_Backend.md (sección 2.x: responses con {patients:[...]}, {alerts:[...]}).
 */
data class PatientsResponse(
    val patients: List<Patient>
)

data class AlertsResponse(
    val alerts: List<Alert>
)

data class UpdateVitalsRequest(
    val heartRate: Int,
    val bloodPressure: String,
    val temperature: Double,
    val oxygenLevel: Int,
    val timestamp: String
)

data class UpdateVitalsResponse(
    val success: Boolean,
    val message: String
)

data class CreateAlertRequest(
    val type: String,
    val severity: String,
    val message: String,
    val location: Location? = null
)

data class CreateAlertResponse(
    val id: String,
    val created: Boolean
)
