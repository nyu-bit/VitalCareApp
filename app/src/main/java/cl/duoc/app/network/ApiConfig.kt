package cl.duoc.app.network

import cl.duoc.app.BuildConfig

/**
 * Config según Integracion_Backend.md, sección 9 (BASE_URL)
 */
object ApiConfig {
    private const val DEBUG_BASE_URL: String = "http://10.0.2.2:8080/api/"
    // Integracion_Backend.md sugiere reemplazar por IP pública en release
    private const val RELEASE_BASE_URL: String = "http://tu-ip-publica:8080/api/"

    val BASE_URL: String
        get() = if (BuildConfig.DEBUG) DEBUG_BASE_URL else RELEASE_BASE_URL
}
