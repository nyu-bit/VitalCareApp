package cl.duoc.app.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos Room para VitalCare
 * 
 * Define las entidades de la base de datos y proporciona acceso a los DAOs
 * 
 * @property userDao DAO para operaciones de usuarios
 * @property reservationDao DAO para operaciones de reservas
 * @property vitalSignsDao DAO para operaciones de signos vitales
 */
@Database(
    entities = [
        UserEntity::class,
        ReservationEntity::class,
        VitalSignsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class VitalCareDatabase : RoomDatabase() {
    
    /**
     * Proporciona acceso al DAO de usuarios
     */
    abstract fun userDao(): UserDao
    
    /**
     * Proporciona acceso al DAO de reservas
     */
    abstract fun reservationDao(): ReservationDao
    
    /**
     * Proporciona acceso al DAO de signos vitales
     */
    abstract fun vitalSignsDao(): VitalSignsDao
    
    companion object {
        private const val DATABASE_NAME = "vital_care_database"
        
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
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // Elimina y recrea si hay cambios de esquema
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Limpia la instancia de la base de datos (útil para testing)
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
