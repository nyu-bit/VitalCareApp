package cl.duoc.app.di

import android.content.Context
import cl.duoc.app.data.local.SharedPreferencesManager
import cl.duoc.app.data.local.room.VitalCareDatabase
import cl.duoc.app.data.repository.LocationRepositoryImpl
import cl.duoc.app.data.repository.SOSRepositoryImpl
import cl.duoc.app.domain.repository.LocationRepository
import cl.duoc.app.domain.repository.SOSRepository
import cl.duoc.app.domain.repository.UserRepository
import cl.duoc.app.domain.usecase.*
import cl.duoc.app.ui.screens.map.HealthCenterMapViewModel
import cl.duoc.app.ui.screens.map.PatientLocationMapViewModel
import cl.duoc.app.ui.screens.profile.UserProfileViewModel
import cl.duoc.app.ui.screens.sos.SOSViewModel

/**
 * Service Locator (contenedor de inyección de dependencias manual)
 * Proporciona las instancias de repositorios, casos de uso y ViewModels
 *
 * Nota: En producción, se recomienda usar Hilt o Koin
 */
object ServiceLocator {

    private lateinit var context: Context
    private lateinit var database: VitalCareDatabase
    private lateinit var prefsManager: SharedPreferencesManager
    private lateinit var userRepository: UserRepository

    /**
     * Inicializar el localizador con el contexto de la aplicación
     * Llamar desde Application.onCreate() o MainActivity.onCreate()
     */
    fun initialize(appContext: Context) {
        context = appContext
        database = VitalCareDatabase.getDatabase(context)
        prefsManager = SharedPreferencesManager(context)
        // Supongamos que UserRepository ya está disponible
        // userRepository = UserRepositoryRoomImpl(database, prefsManager)
    }

    // ============================================================================
    // REPOSITORIES
    // ============================================================================

    /**
     * Proporciona el repositorio de ubicaciones
     */
    fun provideLocationRepository(): LocationRepository {
        return LocationRepositoryImpl(
            context = context,
            healthCenterDao = database.healthCenterDao(),
            prefsManager = prefsManager
        )
    }

    /**
     * Proporciona el repositorio de eventos SOS
     */
    fun provideSOSRepository(): SOSRepository {
        return SOSRepositoryImpl(
            sosEventDao = database.sosEventDao()
        )
    }

    /**
     * Proporciona el repositorio de usuarios
     * Asume que ya está inyectado desde otro lugar
     */
    fun provideUserRepository(): UserRepository {
        return userRepository
    }

    // ============================================================================
    // USE CASES - UBICACIÓN
    // ============================================================================

    fun provideGetHealthCenterLocationUseCase(): GetHealthCenterLocationUseCase {
        return GetHealthCenterLocationUseCase(provideLocationRepository())
    }

    fun provideGetAllHealthCentersUseCase(): GetAllHealthCentersUseCase {
        return GetAllHealthCentersUseCase(provideLocationRepository())
    }

    fun provideGetCurrentLocationUseCase(): GetCurrentLocationUseCase {
        return GetCurrentLocationUseCase(provideLocationRepository())
    }

    fun provideGetUserLastLocationUseCase(): GetUserLastLocationUseCase {
        return GetUserLastLocationUseCase(provideLocationRepository())
    }

    // ============================================================================
    // USE CASES - SOS
    // ============================================================================

    fun provideTriggerSOSUseCase(): TriggerSOSUseCase {
        return TriggerSOSUseCase(provideSOSRepository())
    }

    fun provideGetSOSHistoryUseCase(): GetSOSHistoryUseCase {
        return GetSOSHistoryUseCase(provideSOSRepository())
    }

    fun provideGetLatestSOSEventsUseCase(): GetLatestSOSEventsUseCase {
        return GetLatestSOSEventsUseCase(provideSOSRepository())
    }

    fun provideGetActiveSOSEventsUseCase(): GetActiveSOSEventsUseCase {
        return GetActiveSOSEventsUseCase(provideSOSRepository())
    }

    fun provideResolveSOSEventUseCase(): ResolveSOSEventUseCase {
        return ResolveSOSEventUseCase(provideSOSRepository())
    }

    fun provideAcknowledgeSOSEventUseCase(): AcknowledgeSOSEventUseCase {
        return AcknowledgeSOSEventUseCase(provideSOSRepository())
    }

    // ============================================================================
    // VIEW MODELS
    // ============================================================================

    /**
     * Proporciona HealthCenterMapViewModel
     */
    fun provideHealthCenterMapViewModel(): HealthCenterMapViewModel {
        return HealthCenterMapViewModel(
            getHealthCenterLocationUseCase = provideGetHealthCenterLocationUseCase(),
            getCurrentLocationUseCase = provideGetCurrentLocationUseCase()
        )
    }

    /**
     * Proporciona UserProfileViewModel
     */
    fun provideUserProfileViewModel(): UserProfileViewModel {
        return UserProfileViewModel(
            userRepository = provideUserRepository()
        )
    }

    /**
     * Proporciona PatientLocationMapViewModel
     */
    fun providePatientLocationMapViewModel(): PatientLocationMapViewModel {
        return PatientLocationMapViewModel(
            getUserLastLocationUseCase = provideGetUserLastLocationUseCase()
        )
    }

    /**
     * Proporciona SOSViewModel
     */
    fun provideSOSViewModel(): SOSViewModel {
        return SOSViewModel(
            triggerSOSUseCase = provideTriggerSOSUseCase(),
            getLatestSOSEventsUseCase = provideGetLatestSOSEventsUseCase(),
            getCurrentLocationUseCase = provideGetCurrentLocationUseCase(),
            acknowledgeSOSEventUseCase = provideAcknowledgeSOSEventUseCase(),
            resolveSOSEventUseCase = provideResolveSOSEventUseCase()
        )
    }
}

/**
 * Extensiones para hacer más fácil el uso en Compose
 *
 * Ejemplo de uso:
 * @Composable
 * fun MyScreen() {
 *     val viewModel = ServiceLocator.provideHealthCenterMapViewModel()
 *     // ...
 * }
 */

