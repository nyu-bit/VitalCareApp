package cl.duoc.app.domain.usecase

import cl.duoc.app.domain.repository.LocationRepository
import cl.duoc.app.model.HealthCenter

/**
 * Caso de uso para obtener la ubicación del centro de salud mental
 *
 * Responsabilidad: Encapsula la lógica de negocio para obtener
 * la información del centro de salud (ubicación, contacto, etc.)
 */
class GetHealthCenterLocationUseCase(
    private val locationRepository: LocationRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @return HealthCenter con la información del centro o null
     */
    suspend fun execute(): HealthCenter? {
        return locationRepository.getHealthCenterLocation()
    }
}

/**
 * Caso de uso para obtener todos los centros de salud disponibles
 */
class GetAllHealthCentersUseCase(
    private val locationRepository: LocationRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @return Lista de centros de salud
     */
    suspend fun execute(): List<HealthCenter> {
        return locationRepository.getAllHealthCenters()
    }
}

/**
 * Caso de uso para obtener la ubicación actual del usuario
 */
class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @return LocationData con la ubicación actual o null
     */
    suspend fun execute() = locationRepository.getCurrentLocation()
}

/**
 * Caso de uso para obtener la última ubicación conocida de un usuario
 */
class GetUserLastLocationUseCase(
    private val locationRepository: LocationRepository
) {

    /**
     * Ejecuta el caso de uso
     *
     * @param userId ID del usuario
     * @return LocationData con la última ubicación o null
     */
    suspend fun execute(userId: String) = locationRepository.getUserLastLocation(userId)
}

