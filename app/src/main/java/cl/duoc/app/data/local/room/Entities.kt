package cl.duoc.app.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para Usuario
 * Representa la tabla de usuarios en la base de datos local
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val createdAt: Long
)

/**
 * Entidad Room para Reserva
 * Representa la tabla de reservas en la base de datos local
 */
@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val specialty: String,
    val doctorName: String,
    val date: Long,
    val status: String // Guardamos el enum como String
)

/**
 * Entidad Room para Signos Vitales
 * Representa la tabla de signos vitales en la base de datos local
 */
@Entity(tableName = "vital_signs")
data class VitalSignsEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val heartRate: Int,
    val bloodPressureSystolic: Int,
    val bloodPressureDiastolic: Int,
    val oxygenSaturation: Int,
    val timestamp: Long
)
