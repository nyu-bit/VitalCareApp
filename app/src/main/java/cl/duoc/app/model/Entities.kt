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
 * @property temperature Temperatura corporal (°C)
 * @property notes Notas adicionales
 * @property timestamp Momento del registro
 */
data class VitalSigns(
    val id: String,
    val userId: String,
    val heartRate: Int?,
    val bloodPressureSystolic: Int?,
    val bloodPressureDiastolic: Int?,
    val oxygenSaturation: Int?,
    val temperature: Double? = null,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Entidad de dominio que representa una Alerta médica
 * 
 * @property id Identificador único de la alerta
 * @property userId ID del usuario al que pertenece
 * @property title Título de la alerta
 * @property message Mensaje descriptivo
 * @property severity Nivel de severidad (Crítico, Alto, Medio, Bajo)
 * @property type Tipo de alerta (Signos Vitales, Medicamento, Cita, etc.)
 * @property isRead Si la alerta ha sido leída
 * @property timestamp Momento de creación de la alerta
 * @property relatedId ID relacionado (ej: ID de signos vitales)
 */
data class Alert(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val severity: String, // Crítico, Alto, Medio, Bajo
    val type: String, // Signos Vitales, Medicamento, Cita, Sistema
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedId: String? = null
)

/**
 * Nivel de riesgo para signos vitales
 */
enum class RiskLevel {
    NORMAL,
    WARNING,
    DANGER
}

/**
 * Entidad de dominio que representa un Recordatorio de Cita
 *
 * @property id Identificador único del recordatorio
 * @property reservationId ID de la reserva asociada
 * @property userId ID del usuario
 * @property reminderTime Tiempo en el que se debe enviar el recordatorio (timestamp)
 * @property workerId ID del trabajo programado en WorkManager
 * @property isNotified Si la notificación ya fue enviada
 * @property createdAt Fecha de creación del recordatorio
 */
data class AppointmentReminder(
    val id: String,
    val reservationId: String,
    val userId: String,
    val reminderTime: Long, // 1 hora antes de la cita
    val workerId: String? = null,
    val isNotified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad de dominio que representa una Ubicación geográfica
 *
 * @property latitude Latitud de la ubicación
 * @property longitude Longitud de la ubicación
 * @property accuracy Precisión de la ubicación en metros
 * @property timestamp Momento en que se obtuvo la ubicación
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Entidad de dominio que representa un Centro de Salud Mental
 *
 * @property id Identificador único del centro
 * @property name Nombre del centro
 * @property address Dirección física
 * @property latitude Latitud del centro
 * @property longitude Longitud del centro
 * @property phone Teléfono de contacto
 * @property email Correo de contacto
 * @property schedule Horario de atención
 */
data class HealthCenter(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val email: String? = null,
    val schedule: String? = null
)

/**
 * Entidad de dominio que representa un Evento de SOS
 *
 * @property id Identificador único del evento
 * @property userId ID del usuario que activó el SOS
 * @property location Ubicación donde se activó el SOS
 * @property timestamp Momento en que se activó el SOS
 * @property status Estado del SOS (TRIGGERED, ACKNOWLEDGED, RESOLVED)
 * @property tutorNotified Si se ha notificado al tutor
 */
data class SOSEvent(
    val id: String,
    val userId: String,
    val location: LocationData,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "TRIGGERED", // TRIGGERED, ACKNOWLEDGED, RESOLVED
    val tutorNotified: Boolean = false
)
