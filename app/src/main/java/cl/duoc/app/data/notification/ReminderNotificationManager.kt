package cl.duoc.app.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import cl.duoc.app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gestor de notificaciones para recordatorios de citas
 *
 * Responsabilidades:
 * - Crear canal de notificaciones
 * - Construir y enviar notificaciones
 * - Formatear información de citas
 */
class ReminderNotificationManager(private val context: Context) {

    companion object {
        // Identificadores de canal y notificaciones
        const val REMINDER_CHANNEL_ID = "appointment_reminder_channel"
        const val REMINDER_CHANNEL_NAME = "Recordatorios de Citas"
        const val REMINDER_NOTIFICATION_ID = 101

        // Formato de hora para mostrar en notificaciones
        private const val TIME_FORMAT = "HH:mm"
        private const val DATE_FORMAT = "dd/MM/yyyy"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    /**
     * Crea el canal de notificaciones para recordatorios
     * Necesario para Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(REMINDER_CHANNEL_ID, REMINDER_CHANNEL_NAME, importance).apply {
                description = "Recordatorios de citas médicas"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Construye y envía una notificación de recordatorio de cita
     *
     * @param doctorName Nombre del médico
     * @param appointmentTime Hora de la cita (timestamp)
     * @param specialty Especialidad del médico
     */
    fun showAppointmentReminder(
        doctorName: String,
        appointmentTime: Long,
        specialty: String
    ) {
        val formattedTime = formatTime(appointmentTime)
        val formattedDate = formatDate(appointmentTime)

        val title = "Recordatorio de Cita"
        val message = "Tu cita con el Dr./Dra. $doctorName es hoy a las $formattedTime ($specialty)"

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Puedes cambiar esto por tu logo
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "Dr./Dra. $doctorName\nEspecialidad: $specialty\nFecha: $formattedDate a las $formattedTime"
            ))
            .build()

        notificationManager.notify(REMINDER_NOTIFICATION_ID, notification)
    }

    /**
     * Cancela una notificación de recordatorio
     */
    fun cancelAppointmentReminder() {
        notificationManager.cancel(REMINDER_NOTIFICATION_ID)
    }

    /**
     * Formatea el timestamp a formato de hora (HH:mm)
     */
    private fun formatTime(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            "00:00"
        }
    }

    /**
     * Formatea el timestamp a formato de fecha (dd/MM/yyyy)
     */
    private fun formatDate(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            "01/01/2024"
        }
    }
}

