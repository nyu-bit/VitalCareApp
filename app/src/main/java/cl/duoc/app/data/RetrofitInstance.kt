package cl.duoc.app.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson

/**
 * Singleton para la configuración centralizada de clientes Retrofit.
 * Proporciona 4 clientes API para diferentes microservicios.
 */
object RetrofitInstance {

    // URLs base para cada servicio
    private const val VITALES_BASE_URL = "http://10.0.2.2:8081/"
    private const val UBICACION_BASE_URL = "http://10.0.2.2:8082/"
    private const val ALERTAS_BASE_URL = "http://10.0.2.2:8083/"
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/"

    // Instancias lazy de Retrofit
    private val vitalesRetrofit by lazy { buildClient(VITALES_BASE_URL) }
    private val ubicacionRetrofit by lazy { buildClient(UBICACION_BASE_URL) }
    private val alertasRetrofit by lazy { buildClient(ALERTAS_BASE_URL) }
    private val weatherRetrofit by lazy { buildClient(WEATHER_BASE_URL) }

    /**
     * Construye una instancia de Retrofit con una URL base específica.
     * Configura Gson como convertidor de JSON.
     *
     * @param baseUrl La URL base del servicio API
     * @return Una instancia configurada de Retrofit
     */
    private fun buildClient(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    /**
     * Obtiene el cliente API para Vitales Signos.
     * Base URL: http://10.0.2.2:8081/
     */
    fun <T> getVitalesApi(serviceClass: Class<T>): T {
        return vitalesRetrofit.create(serviceClass)
    }

    /**
     * Obtiene la interfaz VitalesApi.
     */
    fun getVitalesApi(): cl.duoc.app.data.api.VitalesApi {
        return vitalesRetrofit.create(cl.duoc.app.data.api.VitalesApi::class.java)
    }

    /**
     * Obtiene el cliente API para Ubicación.
     * Base URL: http://10.0.2.2:8082/
     */
    fun <T> getUbicacionApi(serviceClass: Class<T>): T {
        return ubicacionRetrofit.create(serviceClass)
    }

    /**
     * Obtiene la interfaz UbicacionApi.
     */
    fun getUbicacionApi(): cl.duoc.app.data.api.UbicacionApi {
        return ubicacionRetrofit.create(cl.duoc.app.data.api.UbicacionApi::class.java)
    }

    /**
     * Obtiene el cliente API para Alertas.
     * Base URL: http://10.0.2.2:8083/
     */
    fun <T> getAlertasApi(serviceClass: Class<T>): T {
        return alertasRetrofit.create(serviceClass)
    }

    /**
     * Obtiene la interfaz AlertasApi.
     */
    fun getAlertasApi(): cl.duoc.app.data.api.AlertasApi {
        return alertasRetrofit.create(cl.duoc.app.data.api.AlertasApi::class.java)
    }

    /**
     * Obtiene el cliente API para Weather (OpenWeatherMap).
     * Base URL: https://api.openweathermap.org/
     */
    fun <T> getWeatherApi(serviceClass: Class<T>): T {
        return weatherRetrofit.create(serviceClass)
    }
}

