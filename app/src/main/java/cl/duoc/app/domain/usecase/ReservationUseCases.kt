package cl.duoc.app.domain.usecase

import cl.duoc.app.domain.repository.ReservationRepository
import cl.duoc.app.model.Reservation
import cl.duoc.app.model.ReservationStatus
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso: Obtener reservas de un usuario
 * 
 * @property reservationRepository Repositorio de reservas
 */
class GetUserReservationsUseCase(
    private val reservationRepository: ReservationRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario
     * @return Lista de reservas del usuario ordenadas por fecha
     */
    suspend operator fun invoke(userId: String): List<Reservation> {
        require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
        return reservationRepository.getReservationsByUserId(userId)
    }
}

/**
 * Caso de uso: Crear una nueva reserva
 * 
 * Valida que la fecha sea futura y que no haya conflictos
 * 
 * @property reservationRepository Repositorio de reservas
 */
class CreateReservationUseCase(
    private val reservationRepository: ReservationRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param reservation Reserva a crear
     * @return Result<Boolean> indicando éxito o error
     */
    suspend operator fun invoke(reservation: Reservation): Result<Boolean> {
        return try {
            // Validaciones de negocio
            require(reservation.userId.isNotBlank()) { "El ID del usuario es requerido" }
            require(reservation.specialty.isNotBlank()) { "La especialidad es requerida" }
            require(reservation.doctorName.isNotBlank()) { "El nombre del médico es requerido" }
            require(reservation.date > System.currentTimeMillis()) { 
                "La fecha de la reserva debe ser futura" 
            }
            
            val success = reservationRepository.createReservation(reservation)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al crear la reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Caso de uso: Cancelar una reserva
 * 
 * Solo permite cancelar reservas que estén en estado PENDING o CONFIRMED
 * 
 * @property reservationRepository Repositorio de reservas
 */
class CancelReservationUseCase(
    private val reservationRepository: ReservationRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param reservationId ID de la reserva a cancelar
     * @return Result<Boolean> indicando éxito o error
     */
    suspend operator fun invoke(reservationId: String): Result<Boolean> {
        return try {
            require(reservationId.isNotBlank()) { "El ID de la reserva no puede estar vacío" }
            
            // Verificar que la reserva existe y puede cancelarse
            val reservation = reservationRepository.getReservationById(reservationId)
            if (reservation == null) {
                return Result.failure(Exception("Reserva no encontrada"))
            }
            
            if (reservation.status == ReservationStatus.CANCELLED) {
                return Result.failure(Exception("La reserva ya está cancelada"))
            }
            
            if (reservation.status == ReservationStatus.COMPLETED) {
                return Result.failure(Exception("No se puede cancelar una reserva completada"))
            }
            
            val success = reservationRepository.cancelReservation(reservationId)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al cancelar la reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Caso de uso: Obtener próximas reservas
 * 
 * Retorna solo las reservas pendientes o confirmadas que son futuras
 * 
 * @property reservationRepository Repositorio de reservas
 */
class GetUpcomingReservationsUseCase(
    private val reservationRepository: ReservationRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario
     * @return Lista de próximas reservas
     */
    suspend operator fun invoke(userId: String): List<Reservation> {
        require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
        
        val allReservations = reservationRepository.getReservationsByUserId(userId)
        val currentTime = System.currentTimeMillis()
        
        return allReservations.filter { reservation ->
            reservation.date > currentTime &&
            (reservation.status == ReservationStatus.PENDING || 
             reservation.status == ReservationStatus.CONFIRMED)
        }
    }
}

/**
 * Caso de uso: Observar cambios en reservas
 * 
 * @property reservationRepository Repositorio de reservas
 */
class ObserveReservationsUseCase(
    private val reservationRepository: ReservationRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario
     * @return Flow que emite la lista de reservas cuando cambian
     */
    operator fun invoke(userId: String): Flow<List<Reservation>> {
        require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
        return reservationRepository.observeReservations(userId)
    }
}
