package cl.duoc.app.utils

// Datos de prueba para ReminderTestDataGenerator
object ReminderTestData {
    data class Reminder(
        val id: String,
        val userId: String,
        val reminderId: String,
        val reservation: String,
        val message: String,
        val time: Long
    )
}

