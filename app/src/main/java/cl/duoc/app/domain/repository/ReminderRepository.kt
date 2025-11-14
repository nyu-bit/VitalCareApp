package cl.duoc.app.domain.repository

import cl.duoc.app.model.AppointmentReminder
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de Recordatorios
 * Define las operaciones de negocio para gestionar recordatorios de citas
 */
interface ReminderRepository {

    /**
     * Obtiene un recordatorio por su ID
     *
     * @param reminderId ID del recordatorio
     * @return Recordatorio o null si no existe
     */
    suspend fun getReminderById(reminderId: String): AppointmentReminder?

    /**
     * Obtiene todos los recordatorios de un usuario
     *
     * @param userId ID del usuario
     * @return Lista de recordatorios del usuario
     */
    suspend fun getRemindersByUserId(userId: String): List<AppointmentReminder>

    /**
     * Obtiene recordatorios pendientes de una reserva específica
     *
     * @param reservationId ID de la reserva
     * @return Recordatorio de la reserva o null
     */
    suspend fun getReminderByReservationId(reservationId: String): AppointmentReminder?

    /**
     * Crea un nuevo recordatorio
     *
     * @param reminder Recordatorio a crear
     * @return true si se creó exitosamente
     */
    suspend fun createReminder(reminder: AppointmentReminder): Boolean

    /**
     * Actualiza un recordatorio existente
     *
     * @param reminder Recordatorio con datos actualizados
     * @return true si se actualizó exitosamente
     */
    suspend fun updateReminder(reminder: AppointmentReminder): Boolean

    /**
     * Elimina un recordatorio
     *
     * @param reminderId ID del recordatorio a eliminar
     * @return true si se eliminó exitosamente
     */
    suspend fun deleteReminder(reminderId: String): Boolean

    /**
     * Obtiene recordatorios pendientes de enviar
     * (Recordatorios que aún no han sido notificados)
     *
     * @return Lista de recordatorios pendientes
     */
    suspend fun getPendingReminders(): List<AppointmentReminder>

    /**
     * Marca un recordatorio como notificado
     *
     * @param reminderId ID del recordatorio
     * @return true si se actualizó exitosamente
     */
    suspend fun markReminderAsNotified(reminderId: String): Boolean
}

