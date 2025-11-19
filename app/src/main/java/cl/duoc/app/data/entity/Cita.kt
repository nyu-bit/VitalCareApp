package cl.duoc.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Cita - Representa las citas médicas agendadas
 * Tabla: citas
 * 
 * Foreign Keys:
 * - pacienteId -> pacientes.id (CASCADE on delete)
 * - especialidadId -> especialidades.id (CASCADE on delete)
 */
@Entity(
    tableName = "citas",
    foreignKeys = [
        ForeignKey(
            entity = Paciente::class,
            parentColumns = ["id"],
            childColumns = ["pacienteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Especialidad::class,
            parentColumns = ["id"],
            childColumns = ["especialidadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
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
    val motivo: String,
    val observaciones: String? = null,
    val ubicacion: String? = null // para guardar coordenadas GPS
)

/**
 * Estados posibles de una cita médica
 */
enum class EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    COMPLETADA,
    CANCELADA,
    REAGENDADA
}
