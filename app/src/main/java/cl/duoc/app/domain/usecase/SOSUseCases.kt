package cl.duoc.app.domain.usecase

import cl.duoc.app.domain.repository.SOSRepository
import cl.duoc.app.model.LocationData
import cl.duoc.app.model.SOSEvent

/**
 * Caso de uso para disparar un evento SOS
 *
 * Responsabilidad: Encapsula la lógica de negocio para crear
 * un evento de emergencia SOS con ubicación actual
 */
class TriggerSOSUseCase(
    private val sosRepository: SOSRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @param userId ID del usuario que activa el SOS
     * @param location Ubicación donde se activa el SOS
     * @return SOSEvent creado
     */
    suspend fun execute(userId: String, location: LocationData): SOSEvent {
        return sosRepository.triggerSOSEvent(userId, location)
    }
}

/**
 * Caso de uso para obtener el historial de eventos SOS
 */
class GetSOSHistoryUseCase(
    private val sosRepository: SOSRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @param userId ID del usuario
     * @return Lista de eventos SOS ordenados por fecha descendente
     */
    suspend fun execute(userId: String): List<SOSEvent> {
        return sosRepository.getSOSHistory(userId)
    }
}

/**
 * Caso de uso para obtener los últimos eventos SOS
 */
class GetLatestSOSEventsUseCase(
    private val sosRepository: SOSRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @param userId ID del usuario
     * @param limit Número máximo de eventos a obtener (por defecto 50)
     * @return Lista de eventos SOS
     */
    suspend fun execute(userId: String, limit: Int = 50): List<SOSEvent> {
        return sosRepository.getLatestSOSEvents(userId, limit)
    }
}

/**
 * Caso de uso para obtener eventos SOS activos
 */
class GetActiveSOSEventsUseCase(
    private val sosRepository: SOSRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @param userId ID del usuario
     * @return Lista de eventos SOS no resueltos
     */
    suspend fun execute(userId: String): List<SOSEvent> {
        return sosRepository.getActiveSOSEvents(userId)
    }
}

/**
 * Caso de uso para resolver un evento SOS
 */
class ResolveSOSEventUseCase(
    private val sosRepository: SOSRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @param eventId ID del evento SOS
     * @return true si la operación fue exitosa
     */
    suspend fun execute(eventId: String): Boolean {
        return sosRepository.resolveSOSEvent(eventId)
    }
}

/**
 * Caso de uso para reconocer un evento SOS
 */
class AcknowledgeSOSEventUseCase(
    private val sosRepository: SOSRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @param eventId ID del evento SOS
     * @return true si la operación fue exitosa
     */
    suspend fun execute(eventId: String): Boolean {
        return sosRepository.acknowledgeSOSEvent(eventId)
    }
}

