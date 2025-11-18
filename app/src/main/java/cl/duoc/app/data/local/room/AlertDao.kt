package cl.duoc.app.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import cl.duoc.app.model.Alert
import cl.duoc.app.data.local.room.AlertEntity

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertEntity>): List<Long>

    @Query("SELECT * FROM alerts WHERE id = :alertId")
    suspend fun getAlertById(alertId: Long): AlertEntity?

    @Query("SELECT * FROM alerts WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAlertsByUserId(userId: Long): List<AlertEntity>

    @Query("SELECT * FROM alerts WHERE userId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadAlertsByUserId(userId: Long): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE userId = :userId AND priority = :priority ORDER BY timestamp DESC")
    fun getAlertsByPriority(userId: Long, priority: String): Flow<List<AlertEntity>>

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :alertId")
    suspend fun markAlertAsRead(alertId: Long)

    @Update
    suspend fun updateAlert(alert: AlertEntity)

    @Query("DELETE FROM alerts WHERE id = :alertId")
    suspend fun deleteAlertById(alertId: Long)

    @Query("DELETE FROM alerts WHERE userId = :userId")
    suspend fun deleteAllAlertsForUser(userId: Long)

    @Query("UPDATE alerts SET actionTaken = :action WHERE id = :alertId")
    suspend fun updateAlertAction(alertId: Long, action: String)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("SELECT COUNT(*) FROM alerts WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadCount(userId: Long): Int
}

// Entidad Room para Alert
@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val priority: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

// Conversión de modelo a entidad
fun Alert.toEntity(): AlertEntity = AlertEntity(
    id = this.id.toLongOrNull() ?: 0L,
    userId = this.userId.toLongOrNull() ?: 0L,
    title = this.title,
    description = this.message,
    priority = this.severity,
    timestamp = this.timestamp,
    isRead = this.isRead
)

// Conversión de entidad a modelo
fun AlertEntity.toDomainModel(): Alert = Alert(
    id = this.id.toString(),
    userId = this.userId.toString(),
    title = this.title,
    message = this.description,
    severity = this.priority,
    type = "Signos Vitales",
    isRead = this.isRead,
    timestamp = this.timestamp,
    relatedId = null
)
