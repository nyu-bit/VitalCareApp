package cl.duoc.app.data.repository

import cl.duoc.app.domain.repository.ReservationRepository
import cl.duoc.app.model.Reservation
import cl.duoc.app.model.ReservationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementación concreta del repositorio de reservas
 * Usa almacenamiento en memoria (Map) para esta demostración
 * En producción, esto debería usar Room o una API remota
 */
class ReservationRepositoryImpl : ReservationRepository {
    
    // Almacenamiento en memoria simulado
    private val reservations = MutableStateFlow<Map<String, Reservation>>(emptyMap())
    
    /**
     * Obtiene una reserva por su ID
     */
    override suspend fun getReservationById(reservationId: String): Reservation? {
        return reservations.value[reservationId]
    }
    
    /**
     * Obtiene todas las reservas de un usuario
     */
    override suspend fun getReservationsByUserId(userId: String): List<Reservation> {
        return reservations.value.values
            .filter { it.userId == userId }
            .sortedByDescending { it.date }
    }
    
    /**
     * Obtiene reservas filtradas por estado
     */
    override suspend fun getReservationsByStatus(
        userId: String,
        status: ReservationStatus
    ): List<Reservation> {
        return reservations.value.values
            .filter { it.userId == userId && it.status == status }
            .sortedByDescending { it.date }
    }
    
    /**
     * Crea una nueva reserva
     */
    override suspend fun createReservation(reservation: Reservation): Boolean {
        return try {
            reservations.value = reservations.value + (reservation.id to reservation)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Actualiza una reserva existente
     */
    override suspend fun updateReservation(reservation: Reservation): Boolean {
        return try {
            if (reservations.value.containsKey(reservation.id)) {
                reservations.value = reservations.value + (reservation.id to reservation)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Cancela una reserva cambiando su estado
     */
    override suspend fun cancelReservation(reservationId: String): Boolean {
        return try {
            val reservation = reservations.value[reservationId]
            if (reservation != null) {
                val cancelledReservation = reservation.copy(status = ReservationStatus.CANCELLED)
                reservations.value = reservations.value + (reservationId to cancelledReservation)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina una reserva
     */
    override suspend fun deleteReservation(reservationId: String): Boolean {
        return try {
            reservations.value = reservations.value - reservationId
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Observa cambios en las reservas de un usuario
     * Retorna un Flow reactivo que emite cuando las reservas cambian
     */
    override fun observeReservations(userId: String): Flow<List<Reservation>> {
        return reservations.map { reservationMap ->
            reservationMap.values
                .filter { it.userId == userId }
                .sortedByDescending { it.date }
        }
    }
}
