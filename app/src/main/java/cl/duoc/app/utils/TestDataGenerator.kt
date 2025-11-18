package cl.duoc.app.utils

import kotlin.random.Random

/**
 * Utilidades para generación de datos de prueba
 */
object TestDataGenerator {
    
    /**
     * Genera un ID único
     */
    fun generateId(): String {
        return "id_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
    }
    
    /**
     * Genera signos vitales aleatorios dentro de rangos realistas
     */
    fun generateRandomVitalSigns(
        userId: String,
        isHealthy: Boolean = true
    ): cl.duoc.app.model.VitalSigns {
        val (heartRateRange, systolicRange, diastolicRange, oxygenValue) = if (isHealthy) {
            intArrayOf(
                Random.nextInt(60, 100),
                Random.nextInt(110, 130),
                Random.nextInt(70, 85),
                Random.nextInt(95, 100)
            )
        } else {
            intArrayOf(
                Random.nextInt(120, 150),
                Random.nextInt(150, 180),
                Random.nextInt(95, 110),
                Random.nextInt(85, 94)
            )
        }
        
        return cl.duoc.app.model.VitalSigns(
            id = generateId(),
            userId = userId,
            heartRate = heartRateRange,
            bloodPressureSystolic = systolicRange,
            bloodPressureDiastolic = diastolicRange,
            oxygenSaturation = oxygenValue
        )
    }
    
    /**
     * Genera un usuario de prueba
     */
    fun generateTestUser(
        id: String = generateId(),
        name: String = "Usuario ${Random.nextInt(1, 100)}",
        email: String = "user${Random.nextInt(1, 1000)}@test.com"
    ): cl.duoc.app.model.User {
        return cl.duoc.app.model.User(
            id = id,
            name = name,
            email = email,
            phone = "9${Random.nextInt(10000000, 99999999)}",
            createdAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Genera una reserva de prueba
     */
    fun generateTestReservation(
        userId: String,
        daysFromNow: Int = 7
    ): cl.duoc.app.model.Reservation {
        val specialties = listOf("Cardiología", "Neurología", "Pediatría", "Psiquiatría")
        val doctors = listOf("Dr. Smith", "Dra. García", "Dr. López", "Dra. Martínez")
        
        return cl.duoc.app.model.Reservation(
            id = generateId(),
            userId = userId,
            specialty = specialties.random(),
            doctorName = doctors.random(),
            date = System.currentTimeMillis() + (daysFromNow * 24 * 60 * 60 * 1000L),
            status = cl.duoc.app.model.ReservationStatus.values().random()
        )
    }

    /**
     * Genera un recordatorio de prueba para una reserva
     */
    fun generateTestReminder(
        reservationId: String,
        userId: String,
        appointmentTimeMs: Long
    ): cl.duoc.app.model.AppointmentReminder {
        // Recordatorio 1 hora antes de la cita
        val reminderTime = appointmentTimeMs - (60 * 60 * 1000)

        return cl.duoc.app.model.AppointmentReminder(
            id = generateId(),
            reservationId = reservationId,
            userId = userId,
            reminderTime = reminderTime,
            isNotified = false
        )
    }

    /**
     * Genera múltiples recordatorios de prueba
     */
    fun generateTestReminders(
        userId: String,
        count: Int = 3
    ): List<cl.duoc.app.model.AppointmentReminder> {
        return (1..count).map { i ->
            val appointmentTime = System.currentTimeMillis() + (i * 3 * 60 * 60 * 1000L)
            generateTestReminder(
                reservationId = "res_test_$i",
                userId = userId,
                appointmentTimeMs = appointmentTime
            )
        }
    }
}

// Class para soportar destructuración de 4 elementos
class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D) {
    operator fun component1() = first
    operator fun component2() = second
    operator fun component3() = third
    operator fun component4() = fourth
}
