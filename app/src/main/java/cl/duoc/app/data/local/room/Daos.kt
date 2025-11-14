package cl.duoc.app.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones de Usuario
 * Define las consultas SQL para la tabla de usuarios
 */
@Dao
interface UserDao {
    
    /**
     * Obtiene todos los usuarios
     */
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>
    
    /**
     * Obtiene un usuario por ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    /**
     * Obtiene un usuario por email
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    /**
     * Observa cambios en un usuario específico
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>
    
    /**
     * Inserta un usuario (reemplaza si ya existe)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    /**
     * Inserta múltiples usuarios
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    /**
     * Actualiza un usuario
     */
    @Update
    suspend fun updateUser(user: UserEntity)
    
    /**
     * Elimina un usuario
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    /**
     * Elimina un usuario por ID
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
    
    /**
     * Elimina todos los usuarios
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

/**
 * DAO (Data Access Object) para operaciones de Reserva
 * Define las consultas SQL para la tabla de reservas
 */
@Dao
interface ReservationDao {
    
    /**
     * Obtiene todas las reservas de un usuario ordenadas por fecha descendente
     */
    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY date DESC")
    suspend fun getReservationsByUserId(userId: String): List<ReservationEntity>
    
    /**
     * Obtiene una reserva por ID
     */
    @Query("SELECT * FROM reservations WHERE id = :reservationId")
    suspend fun getReservationById(reservationId: String): ReservationEntity?
    
    /**
     * Obtiene reservas filtradas por estado
     */
    @Query("SELECT * FROM reservations WHERE userId = :userId AND status = :status ORDER BY date DESC")
    suspend fun getReservationsByStatus(userId: String, status: String): List<ReservationEntity>
    
    /**
     * Observa cambios en las reservas de un usuario
     */
    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY date DESC")
    fun observeReservations(userId: String): Flow<List<ReservationEntity>>
    
    /**
     * Inserta una reserva
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)
    
    /**
     * Actualiza una reserva
     */
    @Update
    suspend fun updateReservation(reservation: ReservationEntity)
    
    /**
     * Elimina una reserva
     */
    @Delete
    suspend fun deleteReservation(reservation: ReservationEntity)
    
    /**
     * Elimina una reserva por ID
     */
    @Query("DELETE FROM reservations WHERE id = :reservationId")
    suspend fun deleteReservationById(reservationId: String)
    
    /**
     * Elimina todas las reservas de un usuario
     */
    @Query("DELETE FROM reservations WHERE userId = :userId")
    suspend fun deleteAllReservationsByUserId(userId: String)
    
    /**
     * Elimina todas las reservas
     */
    @Query("DELETE FROM reservations")
    suspend fun deleteAllReservations()
}

/**
 * DAO (Data Access Object) para operaciones de Signos Vitales
 * Define las consultas SQL para la tabla de signos vitales
 */
@Dao
interface VitalSignsDao {
    
    /**
     * Obtiene todos los signos vitales de un usuario ordenados por fecha descendente
     */
    @Query("SELECT * FROM vital_signs WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getVitalSignsByUserId(userId: String): List<VitalSignsEntity>
    
    /**
     * Obtiene un registro por ID
     */
    @Query("SELECT * FROM vital_signs WHERE id = :vitalSignsId")
    suspend fun getVitalSignsById(vitalSignsId: String): VitalSignsEntity?
    
    /**
     * Obtiene los últimos N registros de un usuario
     */
    @Query("SELECT * FROM vital_signs WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLatestVitalSigns(userId: String, limit: Int): List<VitalSignsEntity>
    
    /**
     * Observa cambios en los signos vitales de un usuario
     */
    @Query("SELECT * FROM vital_signs WHERE userId = :userId ORDER BY timestamp DESC")
    fun observeVitalSigns(userId: String): Flow<List<VitalSignsEntity>>
    
    /**
     * Inserta un registro de signos vitales
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalSigns(vitalSigns: VitalSignsEntity)
    
    /**
     * Inserta múltiples registros
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalSignsList(vitalSignsList: List<VitalSignsEntity>)
    
    /**
     * Elimina un registro
     */
    @Delete
    suspend fun deleteVitalSigns(vitalSigns: VitalSignsEntity)
    
    /**
     * Elimina un registro por ID
     */
    @Query("DELETE FROM vital_signs WHERE id = :vitalSignsId")
    suspend fun deleteVitalSignsById(vitalSignsId: String)
    
    /**
     * Elimina todos los registros de un usuario
     */
    @Query("DELETE FROM vital_signs WHERE userId = :userId")
    suspend fun deleteAllVitalSignsByUserId(userId: String)

    /**
     * Elimina todos los registros
     */
    @Query("DELETE FROM vital_signs")
    suspend fun deleteAllVitalSigns()
}

/**
 * DAO (Data Access Object) para operaciones de Evento SOS
 * Define las consultas SQL para la tabla de eventos SOS
 */
@Dao
interface SOSEventDao {

    /**
     * Obtiene todos los eventos SOS de un usuario ordenados por timestamp descendente
     */
    @Query("SELECT * FROM sos_events WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getSOSEventsByUserId(userId: String): List<SOSEventEntity>

    /**
     * Obtiene un evento SOS por ID
     */
    @Query("SELECT * FROM sos_events WHERE id = :eventId")
    suspend fun getSOSEventById(eventId: String): SOSEventEntity?

    /**
     * Obtiene los últimos eventos SOS de un usuario
     */
    @Query("SELECT * FROM sos_events WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLatestSOSEvents(userId: String, limit: Int): List<SOSEventEntity>

    /**
     * Obtiene eventos SOS no resuelte
     */
    @Query("SELECT * FROM sos_events WHERE userId = :userId AND status = 'TRIGGERED' ORDER BY timestamp DESC")
    suspend fun getActiveSOSEvents(userId: String): List<SOSEventEntity>

    /**
     * Observa cambios en los eventos SOS de un usuario
     */
    @Query("SELECT * FROM sos_events WHERE userId = :userId ORDER BY timestamp DESC")
    fun observeSOSEvents(userId: String): Flow<List<SOSEventEntity>>

    /**
     * Observa el último evento SOS
     */
    @Query("SELECT * FROM sos_events WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun observeLatestSOSEvent(userId: String): Flow<SOSEventEntity?>

    /**
     * Inserta un evento SOS
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSOSEvent(event: SOSEventEntity)

    /**
     * Actualiza un evento SOS
     */
    @Update
    suspend fun updateSOSEvent(event: SOSEventEntity)

    /**
     * Actualiza el estado de un evento SOS
     */
    @Query("UPDATE sos_events SET status = :status, tutorNotified = :tutorNotified WHERE id = :eventId")
    suspend fun updateSOSEventStatus(eventId: String, status: String, tutorNotified: Boolean)

    /**
     * Elimina un evento SOS
     */
    @Delete
    suspend fun deleteSOSEvent(event: SOSEventEntity)

    /**
     * Elimina un evento SOS por ID
     */
    @Query("DELETE FROM sos_events WHERE id = :eventId")
    suspend fun deleteSOSEventById(eventId: String)

    /**
     * Elimina todos los eventos SOS de un usuario
     */
    @Query("DELETE FROM sos_events WHERE userId = :userId")
    suspend fun deleteAllSOSEventsByUserId(userId: String)

    /**
     * Elimina todos los eventos SOS
     */
    @Query("DELETE FROM sos_events")
    suspend fun deleteAllSOSEvents()
}

/**
 * DAO (Data Access Object) para operaciones de Centro de Salud
 * Define las consultas SQL para la tabla de centros de salud
 */
@Dao
interface HealthCenterDao {

    /**
     * Obtiene todos los centros de salud
     */
    @Query("SELECT * FROM health_centers")
    suspend fun getAllHealthCenters(): List<HealthCenterEntity>

    /**
     * Obtiene un centro de salud por ID
     */
    @Query("SELECT * FROM health_centers WHERE id = :centerId")
    suspend fun getHealthCenterById(centerId: String): HealthCenterEntity?

    /**
     * Observa cambios en los centros de salud
     */
    @Query("SELECT * FROM health_centers")
    fun observeHealthCenters(): Flow<List<HealthCenterEntity>>

    /**
     * Inserta un centro de salud
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthCenter(center: HealthCenterEntity)

    /**
     * Inserta múltiples centros de salud
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthCenters(centers: List<HealthCenterEntity>)

    /**
     * Actualiza un centro de salud
     */
    @Update
    suspend fun updateHealthCenter(center: HealthCenterEntity)

    /**
     * Elimina un centro de salud
     */
    @Delete
    suspend fun deleteHealthCenter(center: HealthCenterEntity)

    /**
     * Elimina un centro de salud por ID
     */
    @Query("DELETE FROM health_centers WHERE id = :centerId")
    suspend fun deleteHealthCenterById(centerId: String)

    /**
     * Elimina todos los centros de salud
     */
    @Query("DELETE FROM health_centers")
    suspend fun deleteAllHealthCenters()
}
