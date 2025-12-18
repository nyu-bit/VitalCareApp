package cl.duoc.app.network

import retrofit2.HttpException
import java.io.IOException

/**
 * Manejo de errores según Integracion_Backend.md, sección 7.
 */
class ApiException(
    val code: Int,
    override val message: String
) : Exception(message)

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: HttpException) {
        Result.failure(ApiException(e.code(), e.message()))
    } catch (e: IOException) {
        Result.failure(Exception("Network error: ${e.message}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
