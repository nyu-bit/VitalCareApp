package cl.duoc.app.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor según Integracion_Backend.md, sección 6.
 * Inyecta Authorization: Bearer {token} cuando exista.
 */
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider()

        val request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
