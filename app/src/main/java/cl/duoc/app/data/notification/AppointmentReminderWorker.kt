package cl.duoc.app.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.duoc.app.data.repository.ReservationRepositoryImpl
import cl.duoc.app.data.repository.ReminderRepositoryImpl
import cl.duoc.app.model.ReservationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker que verifica y envía notificaciones de recordatorio de citas
 * Se ejecuta periódicamente en background
 *
 * Responsabilidades:
 * - Verificar citas próximas
 * - Determinar si es hora de enviar recordatorio (1 hora antes)
 * - Enviar notificaciones
 * - Actualizar estado de recordatorios
 */
class AppointmentReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        // Intervalo de verificación: 15 minutos (configurado en ScheduleAppointmentReminderUseCase)
    }

    private val reminderRepository = ReminderRepositoryImpl()
    private val reservationRepository = ReservationRepositoryImpl()
    private val notificationManager = ReminderNotificationManager(appContext)

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.Default) {
                // Obtener recordatorios pendientes
                val pendingReminders = reminderRepository.getPendingReminders()

                for (reminder in pendingReminders) {
                    try {
                        // Obtener información de la reserva
                        val reservation = reservationRepository.getReservationById(reminder.reservationId)

                        if (reservation != null &&
                            (reservation.status == ReservationStatus.CONFIRMED ||
                            reservation.status == ReservationStatus.PENDING)) {

                            // Mostrar notificación
                            notificationManager.showAppointmentReminder(
                                doctorName = reservation.doctorName,
                                appointmentTime = reservation.date,
                                specialty = reservation.specialty
                            )

                            // Marcar recordatorio como notificado
                            reminderRepository.markReminderAsNotified(reminder.id)
                        }
                    } catch (e: Exception) {
                        // Log del error pero continuar con otros recordatorios
                        e.printStackTrace()
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Reintentar la tarea en caso de error
            Result.retry()
        }
    }
}

