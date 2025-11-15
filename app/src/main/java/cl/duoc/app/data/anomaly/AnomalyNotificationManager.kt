package cl.duoc.app.data.anomaly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import cl.duoc.app.R
import cl.duoc.app.data.anomaly.AnomalyDetectionService.AnomalyResult
import cl.duoc.app.utils.Constants

/**
 * Manager especializado para notificaciones de anomalías en signos vitales
 * 
 * HU-04: Alerta Automática por Anomalías
 * - Genera notificaciones locales con sonido y vibración
 * - Configura prioridad según severidad de la anomalía
 */
class AnomalyNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val ANOMALY_NOTIFICATION_BASE_ID = 200
        private var notificationIdCounter = ANOMALY_NOTIFICATION_BASE_ID
    }

    init {
        createNotificationChannel()
    }

    /**
     * Crea el canal de notificación para alertas de signos vitales
     * Requerido para Android 8.0 (API 26) y superiores
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.AnomalyDetection.NOTIFICATION_CHANNEL_ID,
                Constants.AnomalyDetection.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = Constants.AnomalyDetection.NOTIFICATION_CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
                lightColor = android.graphics.Color.RED
                vibrationPattern = longArrayOf(0, 500, 300, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra notificación para una anomalía detectada
     * 
     * @param anomaly Resultado de detección de anomalía
     * @return ID de la notificación mostrada
     */
    fun showAnomalyNotification(anomaly: AnomalyResult): Int {
        val notificationId = getNextNotificationId()
        
        val notification = buildNotification(anomaly)
        
        notificationManager.notify(notificationId, notification)
        
        return notificationId
    }

    /**
     * Construye la notificación según la anomalía
     */
    private fun buildNotification(anomaly: AnomalyResult): android.app.Notification {
        val priority = getNotificationPriority(anomaly.priority)
        val icon = getNotificationIcon(anomaly.priority)
        val color = getNotificationColor(anomaly.priority)
        
        val builder = NotificationCompat.Builder(context, Constants.AnomalyDetection.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(anomaly.anomalyType ?: "Alerta de Signos Vitales")
            .setContentText(anomaly.description ?: "Se detectó una anomalía en tus signos vitales")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(buildBigTextContent(anomaly))
            )
            .setPriority(priority)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(color)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
        
        // Configurar sonido y vibración según prioridad
        when (anomaly.priority) {
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> {
                builder.setVibrate(longArrayOf(0, 500, 300, 500, 300, 500)) // Vibración más larga
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_LIGHTS)
            }
            Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM -> {
                builder.setVibrate(longArrayOf(0, 400, 200, 400))
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND)
            }
            else -> {
                builder.setVibrate(longArrayOf(0, 300))
                builder.setDefaults(NotificationCompat.DEFAULT_ALL)
            }
        }
        
        return builder.build()
    }

    /**
     * Construye el texto expandido de la notificación
     */
    private fun buildBigTextContent(anomaly: AnomalyResult): String {
        val content = StringBuilder()
        
        // Título con emoji según prioridad
        val emoji = when (anomaly.priority) {
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> "🚨"
            Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM -> "⚠️"
            else -> "ℹ️"
        }
        content.append("$emoji ${anomaly.anomalyType}\n\n")
        
        // Descripción
        anomaly.description?.let {
            content.append("$it\n\n")
        }
        
        // Acción recomendada
        anomaly.recommendedAction?.let {
            content.append("Recomendación:\n")
            content.append(it)
        }
        
        return content.toString()
    }

    /**
     * Obtiene la prioridad de notificación según severidad
     */
    private fun getNotificationPriority(priority: String?): Int {
        return when (priority) {
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> NotificationCompat.PRIORITY_MAX
            Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM -> NotificationCompat.PRIORITY_HIGH
            Constants.AnomalyDetection.ALERT_PRIORITY_LOW -> NotificationCompat.PRIORITY_DEFAULT
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
    }

    /**
     * Obtiene el ícono según prioridad
     */
    private fun getNotificationIcon(priority: String?): Int {
        return when (priority) {
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> android.R.drawable.ic_dialog_alert
            Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM -> android.R.drawable.stat_notify_error
            else -> android.R.drawable.ic_dialog_info
        }
    }

    /**
     * Obtiene el color según prioridad
     */
    private fun getNotificationColor(priority: String?): Int {
        return when (priority) {
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> 0xFFD32F2F.toInt() // Rojo
            Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM -> 0xFFF57C00.toInt() // Naranja
            Constants.AnomalyDetection.ALERT_PRIORITY_LOW -> 0xFF1976D2.toInt() // Azul
            else -> 0xFF757575.toInt() // Gris
        }
    }

    /**
     * Genera ID único para cada notificación
     */
    private fun getNextNotificationId(): Int {
        return notificationIdCounter++
    }

    /**
     * Cancela todas las notificaciones de anomalías
     */
    fun cancelAllAnomalyNotifications() {
        for (i in ANOMALY_NOTIFICATION_BASE_ID until notificationIdCounter) {
            notificationManager.cancel(i)
        }
        notificationIdCounter = ANOMALY_NOTIFICATION_BASE_ID
    }

    /**
     * Cancela una notificación específica
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Verifica si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else {
            true // Asumir habilitadas en versiones antiguas
        }
    }

    /**
     * Verifica si el canal de notificaciones está habilitado
     */
    fun isChannelEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(
                Constants.AnomalyDetection.NOTIFICATION_CHANNEL_ID
            )
            return channel?.importance != NotificationManager.IMPORTANCE_NONE
        }
        return true
    }
}
