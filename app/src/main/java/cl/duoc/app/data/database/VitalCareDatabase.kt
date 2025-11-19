package cl.duoc.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.duoc.app.data.dao.CitaDao
import cl.duoc.app.data.dao.EspecialidadDao
import cl.duoc.app.data.dao.PacienteDao
import cl.duoc.app.data.entity.Cita
import cl.duoc.app.data.entity.Especialidad
import cl.duoc.app.data.entity.Paciente

/**
 * VitalCare Database - SQLite a través de Room
 * 
 * Base de datos principal de la aplicación VitalCare
 * Versión 1 - Esquema inicial
 * 
 * Tablas:
 * - pacientes: Información de pacientes registrados
 * - especialidades: Especialidades médicas disponibles
 * - citas: Citas médicas agendadas (con FK a pacientes y especialidades)
 */
@Database(
    entities = [
        Paciente::class,
        Especialidad::class,
        Cita::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class VitalCareDatabase : RoomDatabase() {
    
    // DAOs
    abstract fun pacienteDao(): PacienteDao
    abstract fun especialidadDao(): EspecialidadDao
    abstract fun citaDao(): CitaDao
    
    companion object {
        @Volatile
        private var INSTANCE: VitalCareDatabase? = null
        
        /**
         * Obtiene la instancia única de la base de datos (Singleton)
         * 
         * @param context Contexto de la aplicación
         * @return Instancia de VitalCareDatabase
         */
        fun getDatabase(context: Context): VitalCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitalCareDatabase::class.java,
                    "vitalcare_database.db"
                )
                    // Destruir y recrear la base de datos si cambia la versión
                    // NOTA: En producción, usar migraciones en vez de fallbackToDestructiveMigration
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Limpia la instancia de la base de datos (útil para testing)
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
