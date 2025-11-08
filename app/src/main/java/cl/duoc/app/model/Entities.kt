package cl.duoc.app.model

/**
 * Entidad de dominio que representa un Usuario
 * 
 * @property id Identificador único del usuario
 * @property name Nombre completo del usuario
 * @property email Correo electrónico del usuario
 * @property phone Número de teléfono (opcional)
 * @property rut RUT del usuario (opcional)
 * @property birthDate Fecha de nacimiento (opcional)
 * @property address Dirección del usuario (opcional)
 * @property createdAt Fecha de creación de la cuenta
 */
data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val rut: String? = null,
    val birthDate: String? = null,
    val address: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de dominio que representa una Reserva médica
 * 
 * @property id Identificador único de la reserva
 * @property userId ID del usuario que realiza la reserva
 * @property specialty Especialidad médica
 * @property doctorName Nombre del médico
 * @property date Fecha y hora de la cita
 * @property status Estado de la reserva
 */
data class Reservation(
    val id: String,
    val userId: String,
    val specialty: String,
    val doctorName: String,
    val date: Long,
    val status: ReservationStatus = ReservationStatus.PENDING
)

/**
 * Estados posibles de una reserva
 */
enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

/**
 * Entidad de dominio que representa Signos Vitales
 * 
 * @property id Identificador único del registro
 * @property userId ID del usuario
 * @property heartRate Frecuencia cardíaca (latidos por minuto)
 * @property bloodPressureSystolic Presión arterial sistólica
 * @property bloodPressureDiastolic Presión arterial diastólica
 * @property oxygenSaturation Saturación de oxígeno (%)
 * @property timestamp Momento del registro
 */
data class VitalSigns(
    val id: String,
    val userId: String,
    val heartRate: Int,
    val bloodPressureSystolic: Int,
    val bloodPressureDiastolic: Int,
    val oxygenSaturation: Int,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Nivel de riesgo para signos vitales
 */
enum class RiskLevel {
    NORMAL,
    WARNING,
    DANGER
}
