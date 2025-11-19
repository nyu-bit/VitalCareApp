package cl.duoc.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Cita - Representa las citas médicas agendadas
 * Tabla: citas
 * 
 * Referencias:
 * - pacienteId: ID del paciente (referencia a Paciente.id)
 * - especialidadId: ID de la especialidad (referencia a Especialidad.id)
 */
@Entity(
    tableName = "citas",
    indices = [
        Index(value = ["pacienteId"]),
        Index(value = ["especialidadId"])
    ]
)
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val pacienteId: Long,
    val especialidadId: Long,
    val fecha: String, // formato: "yyyy-MM-dd"
    val hora: String, // formato: "HH:mm"
    val estado: EstadoCita = EstadoCita.PENDIENTE,
    val motivo: String = "", // motivo de la cita
    val observaciones: String = "",
    val ubicacion: String? = null // coordenadas GPS formato: "lat,lon"
)
