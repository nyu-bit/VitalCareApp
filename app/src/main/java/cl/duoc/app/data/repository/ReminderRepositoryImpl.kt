package cl.duoc.app.data.repository

import cl.duoc.app.domain.repository.ReminderRepository
import cl.duoc.app.model.AppointmentReminder
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

/**
 * Implementación concreta del repositorio de Recordatorios
 * Usa almacenamiento en memoria (Map) para esta demostración
 * En producción, esto debería usar Room o una API remota
 */
class ReminderRepositoryImpl : ReminderRepository {

    // Almacenamiento en memoria simulado
    private val reminders = MutableStateFlow<Map<String, AppointmentReminder>>(emptyMap())

    /**
     * Obtiene un recordatorio por su ID
     */
    override suspend fun getReminderById(reminderId: String): AppointmentReminder? {
        return reminders.value[reminderId]
    }

    /**
     * Obtiene todos los recordatorios de un usuario
     */
    override suspend fun getRemindersByUserId(userId: String): List<AppointmentReminder> {
        return reminders.value.values
            .filter { it.userId == userId }
            .sortedBy { it.reminderTime }
    }

    /**
     * Obtiene recordatorio por ID de reserva
     */
    override suspend fun getReminderByReservationId(reservationId: String): AppointmentReminder? {
        return reminders.value.values.find { it.reservationId == reservationId }
    }

    /**
     * Crea un nuevo recordatorio
     */
    override suspend fun createReminder(reminder: AppointmentReminder): Boolean {
        return try {
            val currentReminders = reminders.value.toMutableMap()
            currentReminders[reminder.id] = reminder
            reminders.emit(currentReminders)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Actualiza un recordatorio existente
     */
    override suspend fun updateReminder(reminder: AppointmentReminder): Boolean {
        return try {
            val currentReminders = reminders.value.toMutableMap()
            if (currentReminders.containsKey(reminder.id)) {
                currentReminders[reminder.id] = reminder
                reminders.emit(currentReminders)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Elimina un recordatorio
     */
    override suspend fun deleteReminder(reminderId: String): Boolean {
        return try {
            val currentReminders = reminders.value.toMutableMap()
            if (currentReminders.containsKey(reminderId)) {
                currentReminders.remove(reminderId)
                reminders.emit(currentReminders)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtiene recordatorios pendientes de enviar
     */
    override suspend fun getPendingReminders(): List<AppointmentReminder> {
        val currentTime = System.currentTimeMillis()
        return reminders.value.values
            .filter { !it.isNotified && it.reminderTime <= currentTime }
            .sortedBy { it.reminderTime }
    }

    /**
     * Marca un recordatorio como notificado
     */
    override suspend fun markReminderAsNotified(reminderId: String): Boolean {
        return try {
            val reminder = reminders.value[reminderId]
            if (reminder != null) {
                val updatedReminder = reminder.copy(isNotified = true)
                updateReminder(updatedReminder)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

