package cl.duoc.app.data.repository

import cl.duoc.app.data.local.room.AlertDao
import cl.duoc.app.data.local.room.toEntity
import cl.duoc.app.data.local.room.toDomainModel
import cl.duoc.app.model.Alert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de alertas usando Room Database
 * Maneja persistencia de alertas de anomalías y otras notificaciones
 * 
 * HU-04: Alerta Automática por Anomalías (guardado en historial)
 * HU-15: Historial de Alertas (visualización)
 */
class AlertRepositoryRoomImpl(
    private val alertDao: AlertDao
) {

    /**
     * Guarda una nueva alerta en la base de datos
     * Retorna el ID generado por la BD
     */
    suspend fun insertAlert(alert: Alert): Long {
        return alertDao.insertAlert(alert.toEntity())
    }

    /**
     * Guarda múltiples alertas (útil para anomalías simultáneas)
     * Retorna lista de IDs generados
     */
    suspend fun insertAlerts(alerts: List<Alert>): List<Long> {
        return alertDao.insertAlerts(alerts.map { it.toEntity() })
    }

    /**
     * Obtiene una alerta por su ID
     */
    suspend fun getAlertById(alertId: Long): Alert? {
        return alertDao.getAlertById(alertId)?.toDomainModel()
    }

    /**
     * Obtiene todas las alertas de un usuario específico
     * Ordenadas por timestamp descendente (más reciente primero)
     */
    fun getAllAlertsByUser(userId: Long): Flow<List<Alert>> {
        return alertDao.getAlertsByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Obtiene alertas no leídas de un usuario
     */
    fun getUnreadAlerts(userId: Long): Flow<List<Alert>> {
        return alertDao.getUnreadAlertsByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Obtiene alertas por prioridad
     */
    fun getAlertsByPriority(userId: Long, priority: String): Flow<List<Alert>> {
        return alertDao.getAlertsByPriority(userId, priority).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Marca una alerta como leída
     */
    suspend fun markAlertAsRead(alertId: Long) {
        alertDao.markAlertAsRead(alertId)
    }

    /**
     * Actualiza la acción tomada en una alerta
     */
    suspend fun updateAlertAction(alertId: Long, action: String) {
        alertDao.updateAlertAction(alertId, action)
    }

    /**
     * Actualiza una alerta completa
     */
    suspend fun updateAlert(alert: Alert) {
        alertDao.updateAlert(alert.toEntity())
    }

    /**
     * Elimina una alerta
     */
    suspend fun deleteAlert(alert: Alert) {
        alertDao.deleteAlert(alert.toEntity())
    }

    /**
     * Elimina una alerta por ID
     */
    suspend fun deleteAlertById(alertId: Long) {
        alertDao.deleteAlertById(alertId)
    }

    /**
     * Elimina todas las alertas de un usuario
     */
    suspend fun deleteAllAlertsForUser(userId: Long) {
        alertDao.deleteAllAlertsForUser(userId)
    }

    /**
     * Cuenta alertas no leídas de un usuario
     */
    suspend fun getUnreadCount(userId: Long): Int {
        return alertDao.getUnreadCount(userId)
    }

    /**
     * Obtiene estadísticas de alertas por prioridad
     */
    suspend fun getAlertStatsByPriority(userId: Long): Map<String, Int> {
        // Implementación simplificada - en producción usar query agregada
        val alerts = alertDao.getAlertsByUserId(userId)
        return mapOf(
            "Alta" to alerts.count { it.priority == "Alta" },
            "Media" to alerts.count { it.priority == "Media" },
            "Baja" to alerts.count { it.priority == "Baja" }
        )
    }

    /**
     * Elimina alertas antiguas (limpieza de mantenimiento)
     * @param daysOld Eliminar alertas más antiguas que X días
     */
    suspend fun deleteOldAlerts(userId: Long, daysOld: Int) {
        // Calcular timestamp límite
        val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        // Implementar query con timestamp si se agrega campo createdAt
        // Por ahora, manual:
        val allAlerts = alertDao.getAlertsByUserId(userId)
        allAlerts.forEach { alert ->
            // Si tienes timestamp como Long, compara aquí
            // alertDao.deleteAlert(alert)
        }
    }
}
