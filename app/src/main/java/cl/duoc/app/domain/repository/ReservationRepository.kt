package cl.duoc.app.domain.repository

import cl.duoc.app.model.Reservation
import cl.duoc.app.model.ReservationStatus
import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) del repositorio de reservas
 * Define las operaciones disponibles para gestionar reservas médicas
 */
interface ReservationRepository {
    
    /**
     * Obtiene una reserva por su ID
     * 
     * @param reservationId ID de la reserva
     * @return Reserva encontrada o null si no existe
     */
    suspend fun getReservationById(reservationId: String): Reservation?
    
    /**
     * Obtiene todas las reservas de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de reservas del usuario
     */
    suspend fun getReservationsByUserId(userId: String): List<Reservation>
    
    /**
     * Obtiene reservas filtradas por estado
     * 
     * @param userId ID del usuario
     * @param status Estado de las reservas a buscar
     * @return Lista de reservas con el estado especificado
     */
    suspend fun getReservationsByStatus(userId: String, status: ReservationStatus): List<Reservation>
    
    /**
     * Crea una nueva reserva
     * 
     * @param reservation Reserva a crear
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun createReservation(reservation: Reservation): Boolean
    
    /**
     * Actualiza una reserva existente
     * 
     * @param reservation Reserva con información actualizada
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun updateReservation(reservation: Reservation): Boolean
    
    /**
     * Cancela una reserva cambiando su estado a CANCELLED
     * 
     * @param reservationId ID de la reserva a cancelar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun cancelReservation(reservationId: String): Boolean
    
    /**
     * Elimina una reserva
     * 
     * @param reservationId ID de la reserva a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun deleteReservation(reservationId: String): Boolean
    
    /**
     * Observa cambios en las reservas de un usuario
     * 
     * @param userId ID del usuario
     * @return Flow que emite la lista actualizada de reservas
     */
    fun observeReservations(userId: String): Flow<List<Reservation>>
}
