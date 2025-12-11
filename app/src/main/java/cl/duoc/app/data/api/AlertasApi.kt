package cl.duoc.app.data.api

import retrofit2.http.*

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
     * Obtiene las alertas de un paciente específico
     * GET /alertas/paciente/{id}
     *
     * @param pacienteId ID del paciente
     */
    @GET("alertas/paciente/{id}")
    suspend fun getAlertasByPaciente(@Path("id") pacienteId: String): List<AlertaDto>

    /**
     * Crea una nueva alerta
     * POST /alertas
     *
     * @param alerta Datos de la alerta
     */
    @POST("alertas")
    suspend fun createAlerta(@Body alerta: AlertaDto): AlertaDto

    /**
     * Actualiza una alerta existente
     * PUT /alertas/{id}
     *
     * @param alertaId ID de la alerta a actualizar
     * @param alerta Datos actualizados de la alerta
     */
    @PUT("alertas/{id}")
    suspend fun updateAlerta(
        @Path("id") alertaId: String,
        @Body alerta: AlertaDto
    ): AlertaDto

    /**
     * Elimina una alerta
     * DELETE /alertas/{id}
     *
     * @param alertaId ID de la alerta a eliminar
     */
    @DELETE("alertas/{id}")
    suspend fun deleteAlerta(@Path("id") alertaId: String): Void?
}

/**
 * DTO para Alertas
 */
data class AlertaDto(
    val id: String? = null,
    val pacienteId: String,
    val titulo: String,
    val mensaje: String,
    val severidad: String, // Crítico, Alto, Medio, Bajo
    val tipo: String, // Signos Vitales, Medicamento, Cita, Sistema
    val leida: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val idRelacionado: String? = null
)

