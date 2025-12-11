package cl.duoc.app.utils

import android.util.Log

/**
 * Clase para manejo consistente de errores en toda la aplicación
 * Proporciona métodos para logging, clasificación y mensajes de error user-friendly
 */
object ErrorHandler {

    private const val TAG = "VitalCareApp"

    /**
     * Tipos de error reconocidos en la aplicación
     */
    enum class ErrorType {
        NETWORK,
        DATABASE,
        VALIDATION,
        AUTHENTICATION,
        UNKNOWN
    }

    /**
     * Resultado de operación que puede fallar
     */
    sealed class Result<T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error<T>(val message: String, val type: ErrorType) : Result<T>()
    }

    /**
     * Registra un error en el log del sistema
     * @param tag Etiqueta para identificar el origen del error
     * @param message Mensaje descriptivo del error
     * @param throwable Excepción opcional asociada
     */
    fun logError(tag: String = TAG, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    /**
     * Registra una advertencia en el log del sistema
     */
    fun logWarning(tag: String = TAG, message: String) {
        Log.w(tag, message)
    }

    /**
     * Registra información en el log del sistema
     */
    fun logInfo(tag: String = TAG, message: String) {
        Log.i(tag, message)
    }

    /**
     * Clasifica una excepción en un tipo de error conocido
     */
    fun classifyException(exception: Exception): ErrorType {
        return when (exception) {
            is java.net.UnknownHostException,
            is java.net.SocketTimeoutException,
            is java.io.IOException -> ErrorType.NETWORK
            
            is android.database.SQLException -> ErrorType.DATABASE

            is IllegalArgumentException,
            is IllegalStateException -> ErrorType.VALIDATION
            
            else -> ErrorType.UNKNOWN
        }
    }

    /**
     * Obtiene un mensaje user-friendly basado en el tipo de error
     */
    fun getUserFriendlyMessage(errorType: ErrorType): String {
        return when (errorType) {
            ErrorType.NETWORK -> "Error de conexión. Por favor, verifica tu conexión a internet."
            ErrorType.DATABASE -> "Error al acceder a los datos. Por favor, intenta nuevamente."
            ErrorType.VALIDATION -> "Los datos ingresados no son válidos. Por favor, revisa la información."
            ErrorType.AUTHENTICATION -> "Error de autenticación. Por favor, inicia sesión nuevamente."
            ErrorType.UNKNOWN -> "Ha ocurrido un error inesperado. Por favor, intenta nuevamente."
        }
    }

    /**
     * Maneja una excepción de forma segura, registrando y retornando mensaje apropiado
     */
    fun handleException(
        exception: Throwable,
        tag: String = TAG,
        customMessage: String? = null
    ): String {
        val errorType = if (exception is Exception) classifyException(exception) else ErrorType.UNKNOWN
        logError(tag, customMessage ?: exception.message ?: "Unknown error", exception)
        return customMessage ?: getUserFriendlyMessage(errorType)
    }

    /**
     * Ejecuta un bloque de código de forma segura, capturando y manejando excepciones
     */
    fun <T> safeCall(
        tag: String = TAG,
        customErrorMessage: String? = null,
        block: () -> T
    ): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: Exception) {
            val errorType = classifyException(e)
            val message = customErrorMessage ?: getUserFriendlyMessage(errorType)
            logError(tag, message, e)
            Result.Error(message, errorType)
        }
    }
}
