package cl.duoc.app.ui.examples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.domain.repository.UserRepository
import cl.duoc.app.domain.repository.VitalSignsRepository
import cl.duoc.app.domain.usecase.GetUserUseCase
import cl.duoc.app.domain.usecase.SaveUserUseCase
import cl.duoc.app.domain.usecase.CalculateRiskLevelUseCase
import cl.duoc.app.domain.usecase.GetRecentVitalSignsUseCase
import cl.duoc.app.model.User
import cl.duoc.app.model.VitalSigns
import cl.duoc.app.model.RiskLevel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

/**
 * EJEMPLO COMPLETO: Interacción entre capas usando Clean Architecture
 * 
 * Este ViewModel demuestra:
 * 1. UI Layer -> Domain Layer (ViewModel -> Use Cases)
 * 2. Domain Layer -> Data Layer (Use Cases -> Repository)
 * 3. Data Layer -> Domain Layer (Repository -> Use Cases)
 * 4. Domain Layer -> UI Layer (Use Cases -> ViewModel -> UI)
 * 
 * FLUJO COMPLETO:
 * UI (Compose) -> ViewModel -> UseCase -> Repository -> DataSource -> Repository -> UseCase -> ViewModel -> UI
 */
class LayerInteractionViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val getRecentVitalSignsUseCase: GetRecentVitalSignsUseCase,
    private val calculateRiskLevelUseCase: CalculateRiskLevelUseCase,
    private val userRepository: UserRepository,
    private val vitalSignsRepository: VitalSignsRepository
) : ViewModel() {

    // Estado que la UI observará
    private val _uiState = MutableStateFlow(InteractionUiState())
    val uiState: StateFlow<InteractionUiState> = _uiState.asStateFlow()

    /**
     * EJEMPLO 1: Flujo Simple
     * UI -> ViewModel -> UseCase -> Repository -> Room/DataSource
     * 
     * Flujo de datos:
     * 1. UI llama a loadUser()
     * 2. ViewModel llama al UseCase
     * 3. UseCase llama al Repository
     * 4. Repository obtiene datos de Room
     * 5. Datos regresan a través de las capas
     * 6. ViewModel actualiza el estado
     * 7. UI reacciona al cambio de estado
     */
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // CAPA UI -> CAPA DOMAIN
                // El ViewModel llama al Use Case (lógica de negocio)
                val user = getUserUseCase(userId)

                // CAPA DOMAIN devuelve resultado a CAPA UI
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar usuario: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * EJEMPLO 2: Flujo con Transformación de Datos
     * El UseCase aplica lógica de negocio antes de devolver datos
     * 
     * Flujo:
     * 1. UI solicita signos vitales recientes
     * 2. UseCase obtiene TODOS los signos vitales del Repository
     * 3. UseCase FILTRA y ordena (lógica de negocio)
     * 4. UseCase CALCULA nivel de riesgo (más lógica de negocio)
     * 5. Datos procesados llegan a ViewModel
     * 6. ViewModel actualiza UI
     */
    fun loadRecentVitalSigns(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingVitalSigns = true) }
            
            try {
                // El UseCase encapsula lógica de negocio compleja
                val recentVitalSigns = getRecentVitalSignsUseCase(
                    userId = userId,
                    limit = 10 // Obtener últimos 10 registros
                )
                
                // Calcular nivel de riesgo usando otro UseCase
                val riskLevels = recentVitalSigns.map { vitalSigns ->
                    vitalSigns to calculateRiskLevelUseCase(vitalSigns)
                }

                _uiState.update {
                    it.copy(
                        isLoadingVitalSigns = false,
                        recentVitalSigns = recentVitalSigns
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoadingVitalSigns = false,
                        error = "Error al cargar signos vitales: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * EJEMPLO 3: Flujo con Flow (Reactive)
     * Observación continua de cambios en la base de datos
     * 
     * Flujo reactivo:
     * 1. UI se suscribe al Flow del ViewModel
     * 2. ViewModel se suscribe al Flow del Repository
     * 3. Repository expone Flow de Room
     * 4. Cuando hay cambios en Room, se propagan automáticamente
     * 5. UI se actualiza automáticamente sin polling
     */
    fun observeUserVitalSigns(userId: String) {
        viewModelScope.launch {
            // IMPORTANTE: collect es terminal y mantiene la suscripción activa
            try {
                vitalSignsRepository.observeVitalSigns(userId)
                    .collect { vitalSignsList ->
                        // Cada vez que cambia la BD, esto se ejecuta
                        _uiState.update {
                            it.copy(
                                recentVitalSigns = vitalSignsList.take(10),
                                totalVitalSignsCount = vitalSignsList.size
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error observando signos: ${e.message}")
                }
            }
        }
    }

    /**
     * EJEMPLO 4: Flujo de Escritura (Create/Update)
     * UI -> ViewModel -> UseCase -> Repository -> Room
     * 
     * Flujo de guardado:
     * 1. UI envía datos del formulario
     * 2. ViewModel valida datos básicos
     * 3. UseCase aplica validaciones de negocio
     * 4. Repository persiste en Room
     * 5. Resultado regresa a UI
     */
    fun saveUser(name: String, email: String, phone: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                // Crear entidad de dominio
                val user = User(
                    id = System.currentTimeMillis().toString(),
                    name = name,
                    email = email,
                    phone = phone
                )
                
                // CAPA UI -> CAPA DOMAIN
                // El UseCase valida y guarda
                val result = saveUserUseCase(user)

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            currentUser = user,
                            saveSuccess = true,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            error = "No se pudo guardar el usuario"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        error = "Error al guardar: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * EJEMPLO 5: Flujo con Múltiples Fuentes de Datos
     * Combina datos de diferentes repositorios
     * 
     * Flujo complejo:
     * 1. Obtener usuario de UserRepository
     * 2. Obtener signos vitales de VitalSignsRepository
     * 3. Combinar información en el ViewModel
     * 4. Presentar vista unificada a la UI
     */
    fun loadUserDashboard(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Llamadas en paralelo usando async
                val userDeferred = async { getUserUseCase(userId) }
                val vitalSignsDeferred = async {
                    getRecentVitalSignsUseCase(userId, 5)
                }
                
                // Esperar ambos resultados
                val user = userDeferred.await()
                val vitalSigns = vitalSignsDeferred.await()
                
                // Calcular estadísticas
                val riskLevels = vitalSigns.map { 
                    calculateRiskLevelUseCase(it)
                }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        recentVitalSigns = vitalSigns,
                        averageRiskLevel = calculateAverageRisk(riskLevels),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar dashboard: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * EJEMPLO 6: Acceso Directo al Repository (cuando NO se necesita UseCase)
     * En algunos casos simples, el ViewModel puede llamar directamente al Repository
     * Esto es aceptable cuando NO hay lógica de negocio que aplicar
     */
    fun getAllUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Acceso directo al Repository (sin UseCase)
                // Válido para operaciones CRUD simples sin lógica de negocio
                val users = userRepository.getAllUsers()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allUsers = users
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar usuarios: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * EJEMPLO 7: Manejo de Estados Complejos
     * Demuestra cómo manejar múltiples estados simultáneos
     */
    fun refreshAllData(userId: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isRefreshing = true,
                    refreshError = null
                )
            }
            
            try {
                // Paso 1: Cargar usuario
                val user = getUserUseCase(userId)
                _uiState.update { it.copy(currentUser = user) }
                
                // Paso 2: Cargar signos vitales
                val vitalSigns = getRecentVitalSignsUseCase(userId, 10)
                _uiState.update { it.copy(recentVitalSigns = vitalSigns) }
                
                // Paso 3: Calcular estadísticas
                val stats = calculateStatistics(vitalSigns)
                
                _uiState.update { 
                    it.copy(
                        isRefreshing = false,
                        statistics = stats,
                        lastRefreshTime = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRefreshing = false,
                        refreshError = "Error al refrescar: ${e.message}"
                    )
                }
            }
        }
    }

    // Funciones auxiliares privadas (lógica de UI, no de negocio)
    private fun calculateAverageRisk(riskLevels: List<RiskLevel>): String {
        // Lógica simple de presentación
        return when {
            riskLevels.any { it.name == "DANGER" } -> "Crítico"
            riskLevels.count { it.name == "WARNING" } > riskLevels.size / 2 -> "Alto"
            else -> "Normal"
        }
    }

    private fun calculateStatistics(vitalSigns: List<VitalSigns>): VitalSignsStatistics {
        // Lógica de presentación/agregación
        return VitalSignsStatistics(
            totalRecords = vitalSigns.size,
            averageHeartRate = vitalSigns.mapNotNull { it.heartRate }.average().toInt(),
            averageOxygenSaturation = vitalSigns.mapNotNull { it.oxygenSaturation }.average().toInt()
        )
    }
}

/**
 * Estado de UI que expone el ViewModel
 * Contiene TODA la información que la UI necesita mostrar
 */
data class InteractionUiState(
    // Estados de carga
    val isLoading: Boolean = false,
    val isLoadingVitalSigns: Boolean = false,
    val isSaving: Boolean = false,
    val isRefreshing: Boolean = false,
    
    // Datos
    val currentUser: User? = null,
    val allUsers: List<User> = emptyList(),
    val recentVitalSigns: List<VitalSigns> = emptyList(),
    val riskLevels: Map<VitalSigns, String> = emptyMap(),
    val statistics: VitalSignsStatistics? = null,
    
    // Contadores
    val totalVitalSignsCount: Int = 0,
    
    // Metadatos
    val averageRiskLevel: String? = null,
    val lastRefreshTime: Long? = null,
    
    // Estados de éxito/error
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val refreshError: String? = null
)

/**
 * Modelo de presentación para estadísticas
 */
data class VitalSignsStatistics(
    val totalRecords: Int,
    val averageHeartRate: Int,
    val averageOxygenSaturation: Int
)
