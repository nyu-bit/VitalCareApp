package cl.duoc.app.data.api

import retrofit2.http.*

/**
 * Interfaz Retrofit para el servicio de Ubicación
 * Base URL: http://10.0.2.2:8082/
 */
interface UbicacionApi {

    /**
     * Obtiene todas las ubicaciones registradas
     * GET /ubicacion
     */
    @GET("ubicacion")
    suspend fun getAllUbicaciones(): List<UbicacionDto>

    /**
     * Obtiene las ubicaciones de un paciente específico
     * GET /ubicacion/paciente/{id}
     *
     * @param pacienteId ID del paciente
     */
    @GET("ubicacion/paciente/{id}")
    suspend fun getUbicacionesByPaciente(@Path("id") pacienteId: String): List<UbicacionDto>

    /**
     * Crea un nuevo registro de ubicación
     * POST /ubicacion
     *
     * @param ubicacion Datos de la ubicación
     */
    @POST("ubicacion")
    suspend fun createUbicacion(@Body ubicacion: UbicacionDto): UbicacionDto

    /**
     * Elimina un registro de ubicación
     * DELETE /ubicacion/{id}
     *
     * @param id ID de la ubicación a eliminar
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
    val direccion: String? = null,
    val ciudad: String? = null,
    val pais: String? = null,
    val precision: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
)

