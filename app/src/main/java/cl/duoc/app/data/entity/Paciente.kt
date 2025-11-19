package cl.duoc.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Paciente - Representa a los pacientes registrados
 * Tabla: pacientes
 */
@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val rut: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val fechaNacimiento: String, // formato: "yyyy-MM-dd"
    val direccion: String,
    val activo: Boolean = true
)
