package cl.duoc.app.domain.usecase

import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cl.duoc.app.data.notification.AppointmentReminderWorker
import cl.duoc.app.domain.repository.ReminderRepository
import cl.duoc.app.domain.repository.ReservationRepository
import cl.duoc.app.model.AppointmentReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Caso de uso: Programar un recordatorio para una cita
 *
 * Crea un recordatorio que se activará 1 hora antes de la cita
 *
 * @property reminderRepository Repositorio de recordatorios
 * @property reservationRepository Repositorio de reservas
 * @property workManager WorkManager para tareas en background
 */
class ScheduleAppointmentReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val reservationRepository: ReservationRepository,
    private val workManager: WorkManager
) {
    companion object {
        const val WORK_NAME = "appointment_reminder_worker"
        const val REMINDER_INTERVAL_MINUTES = 15L // Verificar cada 15 minutos
    }

    /**
     * Ejecuta el caso de uso
     *
     * @param reservationId ID de la reserva
     * @return Result indicando éxito o error
     */
    suspend operator fun invoke(reservationId: String): Result<Boolean> {
        return try {
            withContext(Dispatchers.Default) {
                // Validar que el ID no esté vacío
                require(reservationId.isNotBlank()) { "El ID de la reserva no puede estar vacío" }

                // Obtener la reserva
                val reservation = reservationRepository.getReservationById(reservationId)
                    ?: throw IllegalArgumentException("La reserva no existe")

                // Validar que la cita sea futura
                val currentTime = System.currentTimeMillis()
                require(reservation.date > currentTime) { "La cita debe ser futura" }

                // Verificar si ya existe un recordatorio para esta reserva
                val existingReminder = reminderRepository.getReminderByReservationId(reservationId)
                if (existingReminder != null) {
                    return@withContext Result.failure(Exception("Ya existe un recordatorio para esta cita"))
                }

                // Calcular tiempo del recordatorio (1 hora antes)
                val reminderTime = reservation.date - (60 * 60 * 1000)

                // Validar que el recordatorio sea en el futuro
                require(reminderTime > currentTime) { "El tiempo del recordatorio debe ser futuro" }

                // Crear el recordatorio
                val reminder = AppointmentReminder(
                    id = UUID.randomUUID().toString(),
                    reservationId = reservationId,
                    userId = reservation.userId,
                    reminderTime = reminderTime,
                    isNotified = false
                )

                // Guardar en repositorio
                val created = reminderRepository.createReminder(reminder)

                if (created) {
                    // Programar el worker periódico
                    schedulePeriodicReminder()
                    Result.success(true)
                } else {
                    Result.failure(Exception("Error al crear el recordatorio"))
                }
            }
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Programa el worker periódico si no está ya programado
     */
    private fun schedulePeriodicReminder() {
        val reminderWork = PeriodicWorkRequestBuilder<AppointmentReminderWorker>(
            REMINDER_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setBackoffPolicy(
                BackoffPolicy.EXPONENTIAL,
                15,
                TimeUnit.MINUTES
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }
}

/**
 * Caso de uso: Cancelar un recordatorio
 *
 * @property reminderRepository Repositorio de recordatorios
 */
class CancelReminderUseCase(
    private val reminderRepository: ReminderRepository
) {
    /**
     * Ejecuta el caso de uso
     *
     * @param reminderId ID del recordatorio a cancelar
     * @return Result indicando éxito o error
     */
    suspend operator fun invoke(reminderId: String): Result<Boolean> {
        return try {
            require(reminderId.isNotBlank()) { "El ID del recordatorio no puede estar vacío" }

            val deleted = reminderRepository.deleteReminder(reminderId)
            if (deleted) {
                Result.success(true)
            } else {
                Result.failure(Exception("El recordatorio no existe"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Caso de uso: Obtener recordatorios pendientes de un usuario
 *
 * @property reminderRepository Repositorio de recordatorios
 */
class GetUpcomingRemindersUseCase(
    private val reminderRepository: ReminderRepository,
    private val reservationRepository: ReservationRepository
) {

    /**
     * Datos que devuelve sobre un recordatorio próximo
     */
    data class UpcomingReminder(
        val reminderId: String,
        val reservationId: String,
        val doctorName: String,
        val specialty: String,
        val appointmentTime: Long,
        val reminderTime: Long
    )

    /**
     * Ejecuta el caso de uso
     *
     * @param userId ID del usuario
     * @return Result con lista de recordatorios próximos
     */
    suspend operator fun invoke(userId: String): Result<List<UpcomingReminder>> {
        return try {
            require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }

            withContext(Dispatchers.Default) {
                val reminders = reminderRepository.getRemindersByUserId(userId)

                val upcomingReminders = reminders.mapNotNull { reminder ->
                    val reservation = reservationRepository.getReservationById(reminder.reservationId)
                    if (reservation != null) {
                        UpcomingReminder(
                            reminderId = reminder.id,
                            reservationId = reminder.reservationId,
                            doctorName = reservation.doctorName,
                            specialty = reservation.specialty,
                            appointmentTime = reservation.date,
                            reminderTime = reminder.reminderTime
                        )
                    } else {
                        null
                    }
                }

                Result.success(upcomingReminders)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Caso de uso: Cancelar recordatorio por ID de reserva
 *
 * @property reminderRepository Repositorio de recordatorios
 */
class CancelReminderByReservationUseCase(
    private val reminderRepository: ReminderRepository
) {
    /**
     * Ejecuta el caso de uso
     *
     * @param reservationId ID de la reserva
     * @return Result indicando éxito o error
     */
    suspend operator fun invoke(reservationId: String): Result<Boolean> {
        return try {
            require(reservationId.isNotBlank()) { "El ID de la reserva no puede estar vacío" }

            val reminder = reminderRepository.getReminderByReservationId(reservationId)

            if (reminder != null) {
                val deleted = reminderRepository.deleteReminder(reminder.id)
                if (deleted) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Error al cancelar el recordatorio"))
                }
            } else {
                Result.failure(Exception("No existe recordatorio para esta reserva"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

