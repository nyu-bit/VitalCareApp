package cl.duoc.app.data.api

import retrofit2.http.*

/**
 * Interfaz Retrofit para el servicio de Signos Vitales
 * Base URL: http://10.0.2.2:8081/
 *
 * Endpoints:
 * GET    /vitales
 * GET    /vitales/paciente/{id}
 * POST   /vitales
 * DELETE /vitales/{id}
 */
interface VitalesApi {

    /**
     * Obtiene todos los signos vitales
     * GET /vitales
     *
     * @return Lista de SignosVitalesDto
     */
    @GET("vitales")
    suspend fun getAllVitales(): List<SignosVitalesDto>

    /**
     * Obtiene los signos vitales de un paciente específico
     * GET /vitales/paciente/{id}
     *
     * @param pacienteId ID del paciente
     * @return Lista de SignosVitalesDto del paciente
     */
    @GET("vitales/paciente/{id}")
    suspend fun getVitalesByPaciente(@Path("id") pacienteId: String): List<SignosVitalesDto>

    /**
     * Crea un nuevo registro de signos vitales
     * POST /vitales
     *
     * @param signoVital Datos del signo vital a crear
     * @return SignosVitalesDto creado con ID
     */
    @POST("vitales")
    suspend fun createVital(@Body signoVital: SignosVitalesDto): SignosVitalesDto

    /**
     * Elimina un registro de signos vitales
     * DELETE /vitales/{id}
     *
     * @param id ID del vital a eliminar
     * @return Respuesta void
     */
    @DELETE("vitales/{id}")
    suspend fun deleteVital(@Path("id") id: String): Void?
}

/**
 * DTO para Signos Vitales
 * Mapea datos JSON desde la API a objetos Kotlin
 */
data class SignosVitalesDto(
    val id: String? = null,
    val pacienteId: String,
    val frecuenciaCardiaca: Int,
    val presionArterial: String,  // Formato: "120/80"
    val temperatura: Double,
    val saturacionOxigeno: Int,
    val fecha: String,  // ISO 8601: "2025-12-10T10:30:00Z"
    val notas: String? = null
)

/**
 * Interfaz Retrofit para el servicio de Ubicación
 * Base URL: http://10.0.2.2:8082/
 */
interface UbicacionApi {

    /**
     * Obtiene todas las ubicaciones
     * GET /ubicacion
     */
    @GET("ubicacion")
    suspend fun getAllUbicaciones(): List<UbicacionDto>

    /**
     * Obtiene las ubicaciones de un paciente
     * GET /ubicacion/paciente/{id}
     */
    @GET("ubicacion/paciente/{id}")
    suspend fun getUbicacionesByPaciente(@Path("id") pacienteId: String): List<UbicacionDto>

    /**
     * Guarda una nueva ubicación
     * POST /ubicacion
     */
    @POST("ubicacion")
    suspend fun saveUbicacion(@Body ubicacion: UbicacionDto): UbicacionDto

    /**
     * Elimina una ubicación
     * DELETE /ubicacion/{id}
     */
    @DELETE("ubicacion/{id}")
    suspend fun deleteUbicacion(@Path("id") id: String): Void?
}

/**
 * DTO para Ubicación
 */
data class UbicacionDto(
    val id: String? = null,
    val pacienteId: String,
    val latitud: Double,
    val longitud: Double,
    val direccion: String,
    val ciudad: String,
    val pais: String,
    val fecha: String  // ISO 8601
)

/**
 * Interfaz Retrofit para el servicio de Alertas
 * Base URL: http://10.0.2.2:8083/
 */
interface AlertasApi {

    /**
     * Obtiene todas las alertas
     * GET /alertas
     */
    @GET("alertas")
    suspend fun getAllAlertas(): List<AlertaDto>

    /**
     * Obtiene las alertas de un paciente
     * GET /alertas/paciente/{id}
     */
    @GET("alertas/paciente/{id}")
    suspend fun getAlertasByPaciente(@Path("id") pacienteId: String): List<AlertaDto>

    /**
     * Crea una nueva alerta
     * POST /alertas
     */
    @POST("alertas")
    suspend fun createAlerta(@Body alerta: AlertaDto): AlertaDto

    /**
     * Actualiza una alerta
     * PUT /alertas/{id}
     */
    @PUT("alertas/{id}")
    suspend fun updateAlerta(
        @Path("id") id: String,
        @Body alerta: AlertaDto
    ): AlertaDto

    /**
     * Elimina una alerta
     * DELETE /alertas/{id}
     */
    @DELETE("alertas/{id}")
    suspend fun deleteAlerta(@Path("id") id: String): Void?
}

/**
 * DTO para Alertas
 */
data class AlertaDto(
    val id: String? = null,
    val pacienteId: String,
    val titulo: String,
    val mensaje: String,
    val severidad: String,  // "Crítico", "Alto", "Medio", "Bajo"
    val tipo: String,       // "Signos Vitales", "Medicamento", "Cita", "Sistema"
    val leida: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val idRelacionado: String? = null  // ID del vital o evento relacionado
)

/**
 * Interfaz Retrofit para el servicio de Clima (OpenWeatherMap)
 * Base URL: https://api.openweathermap.org/
 */
interface WeatherApi {

    /**
     * Obtiene el clima actual de una ubicación
     * GET /data/2.5/weather
     *
     * @param latitude Latitud
     * @param longitude Longitud
     * @param apiKey Clave de API de OpenWeatherMap
     * @param units Unidades (metric para Celsius)
     */
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherDto
}

/**
 * DTO para Clima
 */
data class WeatherDto(
    val id: Long,
    val name: String,
    val main: WeatherMain,
    val weather: List<WeatherInfo>,
    val wind: WindInfo,
    val clouds: CloudInfo,
    val sys: SystemInfo,
    val dt: Long
)

data class WeatherMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class WeatherInfo(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class WindInfo(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

data class CloudInfo(
    val all: Int
)

data class SystemInfo(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

