package cl.duoc.app.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import cl.duoc.app.R
import cl.duoc.app.model.SOSEvent

/**
 * Manager para gestionar notificaciones locales
 * Crea y muestra notificaciones de SOS y otros eventos
 */
class NotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val SOS_NOTIFICATION_ID = 101
        const val REMINDER_NOTIFICATION_ID = 102
        const val HEALTH_ALERT_NOTIFICATION_ID = 103

        const val SOS_CHANNEL_ID = "sos_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val HEALTH_CHANNEL_ID = "health_channel"
    }

    init {
        createNotificationChannels()
    }

    /**
     * Crea los canales de notificación (requerido para Android 8+)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal para notificaciones de SOS
            val sosChannel = NotificationChannel(
                SOS_CHANNEL_ID,
                "Notificaciones de SOS",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Alertas de SOS del paciente"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(sosChannel)

            // Canal para recordatorios
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de citas médicas"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(reminderChannel)

            // Canal para alertas de salud
            val healthChannel = NotificationChannel(
                HEALTH_CHANNEL_ID,
                "Alertas de Salud",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas sobre signos vitales"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(healthChannel)
        }
    }

    /**
     * Muestra una notificación de SOS destacada
     */
    fun showSOSNotification(event: SOSEvent, tutorName: String = "Tutor") {
        val notification = NotificationCompat.Builder(context, SOS_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Reemplazar con ícono personalizado
            .setContentTitle("¡SOS ACTIVADO!")
            .setContentText("El paciente ha activado una alerta de emergencia")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("El paciente ha presionado el botón SOS.\n" +
                    "Ubicación: ${String.format("%.4f, %.4f", event.location.latitude, event.location.longitude)}\n" +
                    "Hora: ${formatTime(event.timestamp)}")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(null, true) // Activar pantalla completa
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setLights(0xFFFF0000.toInt(), 1000, 1000)
            .build()

        notificationManager.notify(SOS_NOTIFICATION_ID, notification)
    }

    /**
     * Muestra una notificación de recordatorio de cita
     */
    fun showReminderNotification(
        appointmentTitle: String,
        doctorName: String,
        timeMinutesUntil: Int
    ) {
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio de Cita")
            .setContentText("$appointmentTitle con $doctorName en $timeMinutesUntil minutos")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Recuerda tu cita de $appointmentTitle con $doctorName.\n" +
                    "Faltan $timeMinutesUntil minutos para la cita.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        notificationManager.notify(REMINDER_NOTIFICATION_ID, notification)
    }

    /**
     * Muestra una notificación de alerta de salud
     */
    fun showHealthAlertNotification(
        title: String,
        message: String,
        severity: String = "Alto"
    ) {
        val importance = when (severity) {
            "Crítico" -> NotificationCompat.PRIORITY_MAX
            "Alto" -> NotificationCompat.PRIORITY_HIGH
            "Medio" -> NotificationCompat.PRIORITY_DEFAULT
            else -> NotificationCompat.PRIORITY_LOW
        }

        val notification = NotificationCompat.Builder(context, HEALTH_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Alerta de Salud: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(importance)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 250))
            .build()

        notificationManager.notify(HEALTH_ALERT_NOTIFICATION_ID, notification)
    }

    /**
     * Cancela la notificación de SOS
     */
    fun cancelSOSNotification() {
        notificationManager.cancel(SOS_NOTIFICATION_ID)
    }

    /**
     * Cancela la notificación de recordatorio
     */
    fun cancelReminderNotification() {
        notificationManager.cancel(REMINDER_NOTIFICATION_ID)
    }

    /**
     * Cancela la notificación de alerta de salud
     */
    fun cancelHealthAlertNotification() {
        notificationManager.cancel(HEALTH_ALERT_NOTIFICATION_ID)
    }

    /**
     * Formatea un timestamp a hora legible
     */
    private fun formatTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}

