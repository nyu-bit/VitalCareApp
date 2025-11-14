package cl.duoc.app.data.repository

import cl.duoc.app.data.local.room.SOSEventDao
import cl.duoc.app.data.local.room.toSOSEventDomainList
import cl.duoc.app.domain.repository.SOSRepository
import cl.duoc.app.model.LocationData
import cl.duoc.app.model.SOSEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Implementación del repositorio de eventos SOS
 * Gestiona la persistencia y recuperación de eventos de emergencia
 *
 * @property sosEventDao DAO para operaciones de eventos SOS
 */
class SOSRepositoryImpl(
    private val sosEventDao: SOSEventDao
) : SOSRepository {

    override suspend fun triggerSOSEvent(userId: String, location: LocationData): SOSEvent =
        withContext(Dispatchers.IO) {
        val event = SOSEvent(
            id = UUID.randomUUID().toString(),
            userId = userId,
            location = location,
            timestamp = System.currentTimeMillis(),
            status = "TRIGGERED",
            tutorNotified = false
        )

        sosEventDao.insertSOSEvent(event.toEntity())
        event
    }

    override suspend fun getSOSHistory(userId: String): List<SOSEvent> =
        withContext(Dispatchers.IO) {
        return@withContext sosEventDao.getSOSEventsByUserId(userId).toSOSEventDomainList()
    }

    override suspend fun getLatestSOSEvents(userId: String, limit: Int): List<SOSEvent> =
        withContext(Dispatchers.IO) {
        return@withContext sosEventDao.getLatestSOSEvents(userId, limit).toSOSEventDomainList()
    }

    override suspend fun getSOSEventById(eventId: String): SOSEvent? =
        withContext(Dispatchers.IO) {
        return@withContext sosEventDao.getSOSEventById(eventId)?.toDomain()
    }

    override suspend fun getActiveSOSEvents(userId: String): List<SOSEvent> =
        withContext(Dispatchers.IO) {
        return@withContext sosEventDao.getActiveSOSEvents(userId).toSOSEventDomainList()
    }

    override fun observeSOSEvents(userId: String): Flow<List<SOSEvent>> {
        return sosEventDao.observeSOSEvents(userId).map { entities ->
            entities.toSOSEventDomainList()
        }
    }

    override fun observeLatestSOSEvent(userId: String): Flow<SOSEvent?> {
        return sosEventDao.observeLatestSOSEvent(userId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun updateSOSEventStatus(
        eventId: String,
        newStatus: String,
        tutorNotified: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            sosEventDao.updateSOSEventStatus(eventId, newStatus, tutorNotified)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun resolveSOSEvent(eventId: String): Boolean =
        withContext(Dispatchers.IO) {
        return@withContext updateSOSEventStatus(eventId, "RESOLVED", true)
    }

    override suspend fun acknowledgeSOSEvent(eventId: String): Boolean =
        withContext(Dispatchers.IO) {
        return@withContext updateSOSEventStatus(eventId, "ACKNOWLEDGED", true)
    }

    /**
     * Importar extension toDomain y toEntity de mappers
     */
    private fun cl.duoc.app.data.local.room.SOSEventEntity.toDomain(): SOSEvent {
        return SOSEvent(
            id = this.id,
            userId = this.userId,
            location = LocationData(
                latitude = this.latitude,
                longitude = this.longitude,
                accuracy = this.accuracy,
                timestamp = this.timestamp
            ),
            timestamp = this.timestamp,
            status = this.status,
            tutorNotified = this.tutorNotified
        )
    }

    private fun SOSEvent.toEntity(): cl.duoc.app.data.local.room.SOSEventEntity {
        return cl.duoc.app.data.local.room.SOSEventEntity(
            id = this.id,
            userId = this.userId,
            latitude = this.location.latitude,
            longitude = this.location.longitude,
            accuracy = this.location.accuracy,
            timestamp = this.timestamp,
            status = this.status,
            tutorNotified = this.tutorNotified
        )
    }
}

