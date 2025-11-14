package cl.duoc.app.domain.repository

import cl.duoc.app.model.LocationData
import cl.duoc.app.model.SOSEvent
import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) del repositorio de eventos SOS
 * Define las operaciones disponibles para gestionar eventos de emergencia SOS
 *
 * Esta interfaz debe ser implementada por la capa de datos (data layer)
 */
interface SOSRepository {

    /**
     * Dispara un evento SOS con la ubicación actual
     *
     * @param userId ID del usuario que activa el SOS
     * @param location Ubicación donde se activa el SOS
     * @return SOSEvent creado
     */
    suspend fun triggerSOSEvent(userId: String, location: LocationData): SOSEvent

    /**
     * Obtiene el historial de eventos SOS de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de eventos SOS ordenados por fecha descendente
     */
    suspend fun getSOSHistory(userId: String): List<SOSEvent>

    /**
     * Obtiene los últimos N eventos SOS de un usuario
     *
     * @param userId ID del usuario
     * @param limit Número máximo de eventos a obtener
     * @return Lista de eventos SOS
     */
    suspend fun getLatestSOSEvents(userId: String, limit: Int = 50): List<SOSEvent>

    /**
     * Obtiene un evento SOS específico por ID
     *
     * @param eventId ID del evento SOS
     * @return SOSEvent encontrado o null
     */
    suspend fun getSOSEventById(eventId: String): SOSEvent?

    /**
     * Obtiene los eventos SOS activos (no resueltos) de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de eventos SOS activos
     */
    suspend fun getActiveSOSEvents(userId: String): List<SOSEvent>

    /**
     * Observa cambios en los eventos SOS de un usuario en tiempo real
     *
     * @param userId ID del usuario
     * @return Flow que emite cambios en eventos SOS
     */
    fun observeSOSEvents(userId: String): Flow<List<SOSEvent>>

    /**
     * Observa el último evento SOS de un usuario
     *
     * @param userId ID del usuario
     * @return Flow que emite el último evento SOS
     */
    fun observeLatestSOSEvent(userId: String): Flow<SOSEvent?>

    /**
     * Actualiza el estado de un evento SOS
     *
     * @param eventId ID del evento
     * @param newStatus Nuevo estado (TRIGGERED, ACKNOWLEDGED, RESOLVED)
     * @param tutorNotified Si el tutor fue notificado
     * @return true si la actualización fue exitosa
     */
    suspend fun updateSOSEventStatus(eventId: String, newStatus: String, tutorNotified: Boolean): Boolean

    /**
     * Marca un evento SOS como resuelte
     *
     * @param eventId ID del evento
     * @return true si la operación fue exitosa
     */
    suspend fun resolveSOSEvent(eventId: String): Boolean

    /**
     * Marca un evento SOS como reconocido por el tutor
     *
     * @param eventId ID del evento
     * @return true si la operación fue exitosa
     */
    suspend fun acknowledgeSOSEvent(eventId: String): Boolean
}

