package cl.duoc.app.data.database

import androidx.room.TypeConverter
import cl.duoc.app.data.entity.EstadoCita

/**
 * Converters para tipos personalizados en Room
 */
class Converters {
    
    @TypeConverter
    fun fromEstadoCita(value: EstadoCita): String {
        return value.name
    }
    
    @TypeConverter
    fun toEstadoCita(value: String): EstadoCita {
        return try {
            EstadoCita.valueOf(value)
        } catch (e: IllegalArgumentException) {
            EstadoCita.PENDIENTE
        }
    }
}
