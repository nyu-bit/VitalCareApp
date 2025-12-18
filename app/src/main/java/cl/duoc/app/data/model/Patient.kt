package cl.duoc.app.data.model

data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val condition: String,
    val lastVitals: Vitals?,
    val riskLevel: String
)

data class Vitals(
    val heartRate: Int,
    val bloodPressure: String,
    val temperature: Double,
    val oxygenLevel: Int? = null,
    val timestamp: String
)

data class Alert(
    val id: String,
    val type: String,
    val severity: String,
    val message: String,
    val timestamp: String,
    val read: Boolean
)

data class Location(
    val latitude: Double,
    val longitude: Double
)
