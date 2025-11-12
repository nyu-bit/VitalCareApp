package cl.duoc.app.utils

import android.content.Context
import androidx.work.WorkManager
import cl.duoc.app.data.repository.ReservationRepositoryImpl
import cl.duoc.app.data.repository.ReminderRepositoryImpl
import cl.duoc.app.model.Reservation
import cl.duoc.app.model.ReservationStatus
import cl.duoc.app.ui.RemindersViewModel
import java.util.UUID
import java.util.Calendar

/**
 * Clase de utilidad para generar datos de prueba
 * Útil para testing del módulo de recordatorios
 */
object ReminderTestDataGenerator {

    /**
     * Crea una reserva de prueba con fecha futura
     *
     * @param hoursFromNow Horas en el futuro (default 2)
     * @return Reservation lista para pruebas
     */
    fun createTestReservation(hoursFromNow: Long = 2): Reservation {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, hoursFromNow.toInt())

        return Reservation(
            id = "res_test_${UUID.randomUUID().toString().take(8)}",
            userId = "user_test",
            specialty = "Cardiología",
            doctorName = "Dr. Juan Pérez López",
            date = calendar.timeInMillis,
            status = ReservationStatus.CONFIRMED
        )
    }

    /**
     * Crea múltiples reservas de prueba
     *
     * @param count Cantidad de reservas
     * @return Lista de reservas
     */
    fun createMultipleTestReservations(count: Int = 3): List<Reservation> {
        val reservations = mutableListOf<Reservation>()
        for (i in 1..count) {
            reservations.add(createTestReservation(hoursFromNow = (i * 3).toLong()))
        }
        return reservations
    }

    /**
     * Inicializa datos de prueba en el repositorio
     *
     * @param context Context de Android
     * @return RemindersViewModel configurado con datos de prueba
     */
    suspend fun initializeTestData(context: Context): RemindersViewModel {
        val reservationRepository = ReservationRepositoryImpl()
        val reminderRepository = ReminderRepositoryImpl()
        val workManager = WorkManager.getInstance(context)

        // Crear y guardar reservas de prueba
        val testReservations = createMultipleTestReservations(3)
        for (reservation in testReservations) {
            reservationRepository.createReservation(reservation)
        }

        // Crear ViewModel con datos de prueba
        return RemindersViewModel(workManager).apply {
            // Los datos ya están en memoria
        }
    }

    /**
     * Script de prueba para verificar todo el flujo
     * Ejecutar en un Activity o ViewModel
     */
    fun getTestScript(): String {
        return """
            // TEST SCRIPT - Módulo de Recordatorios
            
            // 1. Crear una reserva futura
            val reservation = ReminderTestDataGenerator.createTestReservation(hoursFromNow = 2)
            Log.d("RemindersTest", "Reserva creada: ${reservation.id}")
            
            // 2. Agregar a repositorio
            reservationRepository.createReservation(reservation)
            
            // 3. Crear y mostrar ViewModel
            val viewModel = RemindersViewModel(WorkManager.getInstance(context))
            
            // 4. Programar recordatorio
            viewModel.scheduleReminder(reservation.id)
            // Esperar a que se complete la coroutine
            
            // 5. Cargar recordatorios pendientes
            viewModel.loadUpcomingReminders("user_test")
            
            // 6. Verificar en Logcat:
            // - "Recordatorio programado exitosamente"
            // - Recordatorio visible en pantalla RemindersScreen
            
            // 7. Para ver notificación en background:
            // - Cerrar app completamente
            // - Esperar 15 minutos a que WorkManager se ejecute
            // - O forzar: adb shell cmd jobscheduler run -u 0 -j 999 cl.duoc.app
            
            // 8. Cancelar recordatorio
            val upcomingReminders = viewModel.upcomingReminders.value
            if (upcomingReminders.isNotEmpty()) {
                val reminderId = upcomingReminders[0].reminderId
                viewModel.cancelReminder(reminderId)
                Log.d("RemindersTest", "Recordatorio cancelado: $reminderId")
            }
        """.trimIndent()
    }
}

