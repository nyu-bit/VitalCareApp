package cl.duoc.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Especialidad - Representa las especialidades médicas disponibles
 * Tabla: especialidades
 */
@Entity(tableName = "especialidades")
data class Especialidad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val nombre: String,
    val descripcion: String,
    val duracionConsulta: Int, // duración en minutos
    val activa: Boolean = true
)
