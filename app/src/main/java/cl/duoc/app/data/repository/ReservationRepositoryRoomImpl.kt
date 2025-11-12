package cl.duoc.app.data.repository

import cl.duoc.app.data.local.room.*
import cl.duoc.app.domain.repository.ReservationRepository
import cl.duoc.app.model.Reservation
import cl.duoc.app.model.ReservationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de reservas usando Room Database
 * Esta implementación persiste los datos localmente
 * 
 * @property reservationDao DAO de reservas de Room
 */
class ReservationRepositoryRoomImpl(
    private val reservationDao: ReservationDao
) : ReservationRepository {
    
    /**
     * Obtiene una reserva por su ID desde la base de datos local
     */
    override suspend fun getReservationById(reservationId: String): Reservation? {
        return reservationDao.getReservationById(reservationId)?.toDomain()
    }
    
    /**
     * Obtiene todas las reservas de un usuario desde la base de datos local
     */
    override suspend fun getReservationsByUserId(userId: String): List<Reservation> {
        return reservationDao.getReservationsByUserId(userId).toReservationDomainList()
    }
    
    /**
     * Obtiene reservas filtradas por estado desde la base de datos local
     */
    override suspend fun getReservationsByStatus(
        userId: String,
        status: ReservationStatus
    ): List<Reservation> {
        return reservationDao.getReservationsByStatus(userId, status.name)
            .toReservationDomainList()
    }
    
    /**
     * Crea una nueva reserva en la base de datos local
     */
    override suspend fun createReservation(reservation: Reservation): Boolean {
        return try {
            reservationDao.insertReservation(reservation.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Actualiza una reserva en la base de datos local
     */
    override suspend fun updateReservation(reservation: Reservation): Boolean {
        return try {
            reservationDao.updateReservation(reservation.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Cancela una reserva en la base de datos local
     */
    override suspend fun cancelReservation(reservationId: String): Boolean {
        return try {
            val reservation = reservationDao.getReservationById(reservationId)
            if (reservation != null) {
                val cancelledReservation = reservation.copy(
                    status = ReservationStatus.CANCELLED.name
                )
                reservationDao.updateReservation(cancelledReservation)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina una reserva de la base de datos local
     */
    override suspend fun deleteReservation(reservationId: String): Boolean {
        return try {
            reservationDao.deleteReservationById(reservationId)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Observa cambios en las reservas de un usuario
     */
    override fun observeReservations(userId: String): Flow<List<Reservation>> {
        return reservationDao.observeReservations(userId)
            .map { it.toReservationDomainList() }
    }
}
