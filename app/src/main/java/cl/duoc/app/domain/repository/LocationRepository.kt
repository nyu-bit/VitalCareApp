package cl.duoc.app.domain.repository

import cl.duoc.app.model.HealthCenter
import cl.duoc.app.model.LocationData
import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) del repositorio de ubicaciones
 * Define las operaciones disponibles para gestionar ubicaciones y centros de salud
 *
 * Esta interfaz debe ser implementada por la capa de datos (data layer)
 * siguiendo el principio de inversión de dependencias de Clean Architecture
 */
interface LocationRepository {

    /**
     * Obtiene la ubicación actual del usuario
     * Requiere permisos de ubicación solicitados
     *
     * @return LocationData con la ubicación actual o null si no se puede obtener
     */
    suspend fun getCurrentLocation(): LocationData?

    /**
     * Obtiene la ubicación del centro de salud mental principal
     *
     * @return HealthCenter con la información del centro
     */
    suspend fun getHealthCenterLocation(): HealthCenter?

    /**
     * Obtiene todos los centros de salud disponibles
     *
     * @return Lista de centros de salud
     */
    suspend fun getAllHealthCenters(): List<HealthCenter>

    /**
     * Observa cambios en los centros de salud
     *
     * @return Flow que emite actualizaciones de centros de salud
     */
    fun observeHealthCenters(): Flow<List<HealthCenter>>

    /**
     * Guarda la ubicación de un usuario
     *
     * @param userId ID del usuario
     * @param location Ubicación a guardar
     * @return true si la operación fue exitosa
     */
    suspend fun saveUserLocation(userId: String, location: LocationData): Boolean

    /**
     * Obtiene la última ubicación conocida de un usuario
     *
     * @param userId ID del usuario
     * @return LocationData con la última ubicación conocida
     */
    suspend fun getUserLastLocation(userId: String): LocationData?
}

