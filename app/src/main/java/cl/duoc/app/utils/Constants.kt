package cl.duoc.app.utils

/**
 * Constantes utilizadas en toda la aplicación
 * Centraliza valores mágicos para facilitar mantenimiento y cambios
 */
object Constants {

    // ========== Validaciones ==========
    object Validation {
        const val MIN_PASSWORD_LENGTH = 8
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 100
        const val MIN_PHONE_LENGTH = 9
        const val MAX_PHONE_LENGTH = 15
        const val RUT_LENGTH_WITH_DASH = 10 // Ej: 12345678-9
        const val RUT_LENGTH_WITHOUT_DASH = 9
    }

    // ========== Signos Vitales ==========
    object VitalSigns {
        // Presión Arterial (mmHg)
        const val MIN_SYSTOLIC_PRESSURE = 70
        const val MAX_SYSTOLIC_PRESSURE = 200
        const val MIN_DIASTOLIC_PRESSURE = 40
        const val MAX_DIASTOLIC_PRESSURE = 130
        const val NORMAL_SYSTOLIC_MIN = 90
        const val NORMAL_SYSTOLIC_MAX = 120
        const val NORMAL_DIASTOLIC_MIN = 60
        const val NORMAL_DIASTOLIC_MAX = 80

        // Frecuencia Cardíaca (bpm)
        const val MIN_HEART_RATE = 40
        const val MAX_HEART_RATE = 200
        const val NORMAL_HEART_RATE_MIN = 60
        const val NORMAL_HEART_RATE_MAX = 100

        // Saturación de Oxígeno (%)
        const val MIN_OXYGEN_SATURATION = 70
        const val MAX_OXYGEN_SATURATION = 100
        const val NORMAL_OXYGEN_SATURATION_MIN = 95
        const val CRITICAL_OXYGEN_SATURATION = 90

        // Temperatura (°C)
        const val MIN_TEMPERATURE = 35.0
        const val MAX_TEMPERATURE = 42.0
        const val NORMAL_TEMPERATURE_MIN = 36.1
        const val NORMAL_TEMPERATURE_MAX = 37.2
    }

    // ========== Niveles de Riesgo ==========
    object RiskLevel {
        const val LOW = "Bajo"
        const val MEDIUM = "Medio"
        const val HIGH = "Alto"
        const val CRITICAL = "Crítico"
    }

    // ========== Base de Datos ==========
    object Database {
        const val DATABASE_NAME = "vital_care_database"
        const val DATABASE_VERSION = 1
        
        // Nombres de tablas
        const val TABLE_USERS = "users"
        const val TABLE_RESERVATIONS = "reservations"
        const val TABLE_VITAL_SIGNS = "vital_signs"
    }

    // ========== SharedPreferences ==========
    object Preferences {
        const val PREFS_NAME = "vital_care_prefs"
        
        // Keys
        const val KEY_USER_ID = "user_id"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_FIRST_TIME = "first_time"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    // ========== Formatos de Fecha/Hora ==========
    object DateFormat {
        const val DATE_PATTERN = "dd/MM/yyyy"
        const val TIME_PATTERN = "HH:mm:ss"
        const val DATETIME_PATTERN = "dd/MM/yyyy HH:mm"
        const val TIME_SHORT_PATTERN = "HH:mm"
        const val DATE_LONG_PATTERN = "EEEE, dd 'de' MMMM 'de' yyyy"
    }

    // ========== UI ==========
    object UI {
        const val DEFAULT_ANIMATION_DURATION = 300
        const val DEBOUNCE_TIME = 500L // milisegundos
        const val SPLASH_SCREEN_DURATION = 2000L
        const val SNACKBAR_DURATION = 3000 // milisegundos
    }

    // ========== Network ==========
    object Network {
        const val TIMEOUT_CONNECT = 30L // segundos
        const val TIMEOUT_READ = 30L
        const val TIMEOUT_WRITE = 30L
        const val MAX_RETRY_ATTEMPTS = 3
    }

    // ========== Límites ==========
    object Limits {
        const val MAX_RESERVATIONS_PER_USER = 5
        const val MAX_VITAL_SIGNS_HISTORY = 100
        const val MAX_UPLOAD_SIZE_MB = 10
    }

    // ========== Mensajes ==========
    object Messages {
        const val GENERIC_ERROR = "Ha ocurrido un error inesperado"
        const val NETWORK_ERROR = "Error de conexión a internet"
        const val VALIDATION_ERROR = "Por favor, verifica los datos ingresados"
        const val SUCCESS_SAVE = "Datos guardados exitosamente"
        const val SUCCESS_DELETE = "Datos eliminados exitosamente"
        const val CONFIRM_DELETE = "¿Estás seguro de que deseas eliminar?"
    }

    // ========== Tipos de Reserva ==========
    object ReservationType {
        const val CONSULTATION = "Consulta Médica"
        const val EMERGENCY = "Urgencia"
        const val PREVENTIVE = "Control Preventivo"
        const val FOLLOW_UP = "Seguimiento"
    }

    // ========== Estados de Reserva ==========
    object ReservationStatus {
        const val PENDING = "Pendiente"
        const val CONFIRMED = "Confirmada"
        const val CANCELLED = "Cancelada"
        const val COMPLETED = "Completada"
    }
}
