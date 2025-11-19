package cl.duoc.app.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.app.MainActivity
import cl.duoc.app.R

/**
 * Helper para gestionar notificaciones locales
 * Proporciona métodos para crear y mostrar notificaciones de citas
 */
class NotificationHelper(private val context: Context) {
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Crea el canal de notificaciones (requerido para Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Verifica si los permisos de notificación están otorgados
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No requiere permiso en versiones anteriores
        }
    }
    
    /**
     * Muestra notificación de recordatorio de cita
     */
    fun showCitaReminderNotification(
        citaId: Long,
        fecha: String,
        hora: String,
        especialidad: String
    ) {
        if (!hasNotificationPermission()) {
            return
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("citaId", citaId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            citaId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio de Cita Médica")
            .setContentText("$especialidad - $fecha a las $hora")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tienes una cita de $especialidad programada para el $fecha a las $hora")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        try {
            notificationManager.notify(citaId.toInt(), notification)
        } catch (e: SecurityException) {
            // Permiso denegado
        }
    }
    
    /**
     * Muestra notificación de confirmación de cita
     */
    fun showCitaConfirmationNotification(
        citaId: Long,
        fecha: String,
        hora: String
    ) {
        if (!hasNotificationPermission()) {
            return
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Cita Confirmada")
            .setContentText("Tu cita del $fecha a las $hora ha sido confirmada")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        try {
            notificationManager.notify(citaId.toInt() + 10000, notification)
        } catch (e: SecurityException) {
            // Permiso denegado
        }
    }
    
    /**
     * Muestra notificación de cita próxima (1 hora antes)
     */
    fun showUpcomingCitaNotification(
        citaId: Long,
        especialidad: String,
        hora: String,
        ubicacion: String?
    ) {
        if (!hasNotificationPermission()) {
            return
        }
        
        val locationText = ubicacion?.let { "\n📍 Ubicación guardada" } ?: ""
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ Cita en 1 hora")
            .setContentText("$especialidad a las $hora$locationText")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tu cita de $especialidad es en 1 hora (a las $hora).$locationText")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
        
        try {
            notificationManager.notify(citaId.toInt() + 20000, notification)
        } catch (e: SecurityException) {
            // Permiso denegado
        }
    }
    
    /**
     * Cancela una notificación específica
     */
    fun cancelNotification(citaId: Long) {
        notificationManager.cancel(citaId.toInt())
        notificationManager.cancel(citaId.toInt() + 10000)
        notificationManager.cancel(citaId.toInt() + 20000)
    }
    
    /**
     * Cancela todas las notificaciones
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
    
    companion object {
        private const val CHANNEL_ID = "citas_channel"
        private const val CHANNEL_NAME = "Recordatorios de Citas"
        private const val CHANNEL_DESCRIPTION = "Notificaciones para recordatorios de citas médicas"
        
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
        
        /**
         * Permisos necesarios para notificaciones (Android 13+)
         */
        val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
    }
}
