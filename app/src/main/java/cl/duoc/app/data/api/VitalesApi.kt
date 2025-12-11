package cl.duoc.app.data.api

import retrofit2.http.*

/**
 * Interfaz Retrofit para el servicio de Signos Vitales
 * Base URL: http://10.0.2.2:8081/
 */
interface VitalesApi {

    /**
     * Obtiene todos los registros de signos vitales
     * GET /vitales
     */
    @GET("vitales")
    suspend fun getAllVitales(): List<SignosVitalesDto>

    /**
     * Obtiene los signos vitales de un paciente específico
     * GET /vitales/paciente/{id}
     *
     * @param pacienteId ID del paciente
     */
    @GET("vitales/paciente/{id}")
    suspend fun getVitalesByPaciente(@Path("id") pacienteId: String): List<SignosVitalesDto>

    /**
     * Crea un nuevo registro de signos vitales
     * POST /vitales
     *
     * @param signos Datos del registro de signos vitales
     */
    @POST("vitales")
    suspend fun createVitales(@Body signos: SignosVitalesDto): SignosVitalesDto

    /**
     * Elimina un registro de signos vitales
     * DELETE /vitales/{id}
     *
     * @param vitalesId ID del registro a eliminar
     */
    @DELETE("vitales/{id}")
    suspend fun deleteVitales(@Path("id") vitalesId: String): Void?
}

/**
 * DTO para Signos Vitales
 */
data class SignosVitalesDto(
    val id: String? = null,
    val pacienteId: String,
    val frecuenciaCardiaca: Int? = null,
    val presionArterialSistolica: Int? = null,
    val presionArterialDiastolica: Int? = null,
    val saturacionOxigeno: Int? = null,
    val temperatura: Double? = null,
    val notas: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

