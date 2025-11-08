# 🔄 Guía de Interacción entre Capas - Clean Architecture

Esta guía explica cómo las diferentes capas de VitalCareApp se comunican entre sí, con ejemplos prácticos de código.

## 📋 Tabla de Contenidos

1. [Resumen de Arquitectura](#resumen-de-arquitectura)
2. [Flujo de Datos: Lectura](#flujo-de-datos-lectura)
3. [Flujo de Datos: Escritura](#flujo-de-datos-escritura)
4. [Flujo Reactivo con Flow](#flujo-reactivo-con-flow)
5. [Ejemplos Prácticos](#ejemplos-prácticos)
6. [Mejores Prácticas](#mejores-prácticas)

---

## Resumen de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                        UI LAYER                             │
│  ┌──────────────┐         ┌─────────────────┐              │
│  │  Composables │ ←────→  │   ViewModels    │              │
│  │  (Screens)   │         │  (State Logic)  │              │
│  └──────────────┘         └─────────────────┘              │
└─────────────────────────────────┬───────────────────────────┘
                                  │
                                  ↓
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  ┌──────────────┐    ┌────────────────┐   ┌──────────┐     │
│  │  Use Cases   │    │  Repositories  │   │  Models  │     │
│  │ (Business    │    │  (Interfaces)  │   │ (Entities)│    │
│  │   Logic)     │    └────────────────┘   └──────────┘     │
│  └──────────────┘                                           │
└─────────────────────────────────┬───────────────────────────┘
                                  │
                                  ↓
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                             │
│  ┌──────────────┐    ┌────────────────┐   ┌──────────┐     │
│  │ Repository   │    │      DAOs      │   │  Mappers │     │
│  │Implementations│   │  (Room/API)    │   │          │     │
│  └──────────────┘    └────────────────┘   └──────────┘     │
│                                                              │
│  ┌──────────────┐    ┌────────────────┐                    │
│  │    Room      │    │ SharedPrefs    │                    │
│  │  Database    │    │                │                    │
│  └──────────────┘    └────────────────┘                    │
└─────────────────────────────────────────────────────────────┘
```

---

## Flujo de Datos: Lectura

### Escenario: Cargar información de un usuario

```kotlin
// 1. UI LAYER - Screen (Compose)
@Composable
fun UserScreen(viewModel: UserViewModel, userId: String) {
    // Observar el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // Cargar datos cuando se monta el componente
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)  // ← Llamada a ViewModel
    }
    
    // Mostrar datos
    when {
        uiState.isLoading -> LoadingView()
        uiState.user != null -> UserContent(uiState.user)
        uiState.error != null -> ErrorView(uiState.error)
    }
}

// 2. UI LAYER - ViewModel
class UserViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // ← Llamada al Use Case (Domain Layer)
                val user = getUserUseCase.execute(userId)
                
                _uiState.update { 
                    it.copy(isLoading = false, user = user) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = e.message) 
                }
            }
        }
    }
}

// 3. DOMAIN LAYER - Use Case
class GetUserUseCase(
    private val userRepository: UserRepository  // ← Interface
) {
    suspend fun execute(userId: String): User? {
        // Aquí va lógica de negocio si es necesaria
        // Por ejemplo: validaciones, transformaciones, etc.
        
        return userRepository.getUserById(userId)  // ← Llamada al Repository
    }
}

// 4. DOMAIN LAYER - Repository Interface
interface UserRepository {
    suspend fun getUserById(id: String): User?
    suspend fun saveUser(user: User): Boolean
    fun getAllUsersFlow(): Flow<List<User>>
}

// 5. DATA LAYER - Repository Implementation
class UserRepositoryRoomImpl(
    private val userDao: UserDao,
    private val userMapper: UserMapper
) : UserRepository {
    
    override suspend fun getUserById(id: String): User? {
        // ← Llamada a Room DAO
        val entity = userDao.getUserById(id)
        
        // Mapear de Entity (Room) a Model (Domain)
        return entity?.let { userMapper.entityToModel(it) }
    }
}

// 6. DATA LAYER - Room DAO
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}
```

### Flujo Visual:

```
┌──────────┐     loadUser()      ┌─────────────┐
│   UI     │ ─────────────────→  │  ViewModel  │
│ (Screen) │                     └──────┬──────┘
└──────────┘                            │
                                        │ execute()
                                        ↓
                              ┌─────────────────┐
                              │    Use Case     │
                              └────────┬────────┘
                                       │
                                       │ getUserById()
                                       ↓
                              ┌─────────────────┐
                              │   Repository    │
                              │ (Implementation)│
                              └────────┬────────┘
                                       │
                                       │ getUserById()
                                       ↓
                              ┌─────────────────┐
                              │   Room DAO      │
                              └────────┬────────┘
                                       │
                                       │ SQL Query
                                       ↓
                              ┌─────────────────┐
                              │   Room DB       │
                              └─────────────────┘

            ← Resultado regresa por el mismo camino ←
```

---

## Flujo de Datos: Escritura

### Escenario: Guardar un nuevo usuario

```kotlin
// 1. UI LAYER - Screen (Compose)
@Composable
fun CreateUserScreen(viewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") }
        )
        
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        
        Button(
            onClick = {
                // ← Llamada al ViewModel con datos del formulario
                viewModel.saveUser(name, email)
            },
            enabled = !uiState.isSaving
        ) {
            Text("Guardar")
        }
        
        if (uiState.saveSuccess) {
            Text("¡Usuario guardado exitosamente!")
        }
    }
}

// 2. UI LAYER - ViewModel
class UserViewModel(
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {
    
    fun saveUser(name: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            try {
                // Crear modelo de dominio
                val user = User(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    email = email
                )
                
                // ← Llamada al Use Case
                val success = saveUserUseCase.execute(user)
                
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        saveSuccess = success
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isSaving = false, error = e.message) 
                }
            }
        }
    }
}

// 3. DOMAIN LAYER - Use Case
class SaveUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(user: User): Boolean {
        // Validaciones de negocio
        require(user.name.isNotBlank()) { "El nombre no puede estar vacío" }
        require(user.email?.contains("@") == true) { "Email inválido" }
        
        // ← Llamada al Repository
        return userRepository.saveUser(user)
    }
}

// 4. DATA LAYER - Repository Implementation
class UserRepositoryRoomImpl(
    private val userDao: UserDao,
    private val userMapper: UserMapper
) : UserRepository {
    
    override suspend fun saveUser(user: User): Boolean {
        return try {
            // Mapear de Model (Domain) a Entity (Room)
            val entity = userMapper.modelToEntity(user)
            
            // ← Guardar en Room
            userDao.insert(entity)
            true
        } catch (e: Exception) {
            false
        }
    }
}

// 5. DATA LAYER - Room DAO
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)
}
```

### Flujo Visual:

```
┌──────────┐   saveUser("Juan", "juan@email.com")   ┌─────────────┐
│   UI     │ ─────────────────────────────────────→ │  ViewModel  │
│ (Button) │                                         └──────┬──────┘
└──────────┘                                                │
                                                            │ execute(User)
                                                            ↓
                                                   ┌─────────────────┐
                                                   │    Use Case     │
                                                   │  (Validación)   │
                                                   └────────┬────────┘
                                                            │
                                                            │ saveUser(User)
                                                            ↓
                                                   ┌─────────────────┐
                                                   │   Repository    │
                                                   │    (Mapper)     │
                                                   └────────┬────────┘
                                                            │
                                                            │ insert(Entity)
                                                            ↓
                                                   ┌─────────────────┐
                                                   │   Room DAO      │
                                                   └────────┬────────┘
                                                            │
                                                            │ INSERT SQL
                                                            ↓
                                                   ┌─────────────────┐
                                                   │   Room DB       │
                                                   └─────────────────┘

                     ← Success/Error regresa ←
```

---

## Flujo Reactivo con Flow

### Escenario: Observar cambios en tiempo real

```kotlin
// 1. UI LAYER - Screen (Compose)
@Composable
fun UserListScreen(viewModel: UserViewModel) {
    // Observar lista de usuarios en tiempo real
    val users by viewModel.users.collectAsState(initial = emptyList())
    
    LazyColumn {
        items(users) { user →
            UserItem(user = user)
        }
    }
}

// 2. UI LAYER - ViewModel
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    // Flow que emite automáticamente cuando hay cambios
    val users: Flow<List<User>> = userRepository
        .getAllUsersFlow()
        .catch { e ->
            // Manejo de errores
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

// 3. DATA LAYER - Repository Implementation
class UserRepositoryRoomImpl(
    private val userDao: UserDao,
    private val userMapper: UserMapper
) : UserRepository {
    
    override fun getAllUsersFlow(): Flow<List<User>> {
        // Flow de Room se transforma a Flow de Domain
        return userDao.getAllUsers()
            .map { entities ->
                entities.map { userMapper.entityToModel(it) }
            }
    }
}

// 4. DATA LAYER - Room DAO
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name")
    fun getAllUsers(): Flow<List<UserEntity>>  // ← Flow emite automáticamente
}
```

### Flujo Visual:

```
┌─────────────────────────────────────────────────────────┐
│                    Flujo Reactivo                       │
└─────────────────────────────────────────────────────────┘

Room DB cambia (INSERT/UPDATE/DELETE)
        │
        ↓
Flow de DAO emite nueva lista
        │
        ↓
Repository mapea EntityList → ModelList
        │
        ↓
ViewModel recibe nueva lista
        │
        ↓
StateFlow actualiza
        │
        ↓
Compose detecta cambio automáticamente
        │
        ↓
UI se recompone con nuevos datos

Todo automático, sin polling ni refresh manual!
```

---

## Ejemplos Prácticos

### Ejemplo 1: Operación Compleja con Múltiples Fuentes

```kotlin
// ViewModel
fun loadDashboard(userId: String) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        try {
            // Obtener de diferentes repositorios en paralelo
            val userDeferred = async { userRepository.getUserById(userId) }
            val vitalSignsDeferred = async { 
                vitalSignsRepository.getRecentVitalSigns(userId, 10) 
            }
            val reservationsDeferred = async { 
                reservationRepository.getUserReservations(userId) 
            }
            
            // Esperar todos los resultados
            val user = userDeferred.await()
            val vitalSigns = vitalSignsDeferred.await()
            val reservations = reservationsDeferred.await()
            
            // Aplicar lógica de negocio
            val riskLevel = calculateRiskUseCase.execute(vitalSigns)
            
            // Actualizar UI con datos combinados
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    user = user,
                    vitalSigns = vitalSigns,
                    reservations = reservations,
                    riskLevel = riskLevel
                )
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(isLoading = false, error = e.message) 
            }
        }
    }
}
```

### Ejemplo 2: Validación en Capas

```kotlin
// UI: Validación básica de formato
@Composable
fun EmailField(email: String, onEmailChange: (String) -> Unit) {
    var error by remember { mutableStateOf<String?>(null) }
    
    TextField(
        value = email,
        onValueChange = { newEmail ->
            // Validación inmediata en UI
            error = if (!newEmail.contains("@")) {
                "Email debe contener @"
            } else null
            
            onEmailChange(newEmail)
        },
        isError = error != null,
        supportingText = error?.let { { Text(it) } }
    )
}

// ViewModel: Validación de lógica
class UserViewModel {
    fun saveUser(email: String) {
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(error = "Email inválido") }
            return
        }
        
        // Continuar con guardado...
    }
}

// UseCase: Validación de reglas de negocio
class SaveUserUseCase {
    suspend fun execute(user: User): Boolean {
        // Reglas de negocio complejas
        require(user.email?.endsWith("@empresa.com") == true) {
            "Solo se permiten emails corporativos"
        }
        
        return repository.saveUser(user)
    }
}
```

---

## Mejores Prácticas

### ✅ DO - Hacer

1. **Inyección de Dependencias**
   ```kotlin
   class UserViewModel(
       private val getUserUseCase: GetUserUseCase,  // Inyectar UseCase
       private val saveUserUseCase: SaveUserUseCase
   ) : ViewModel()
   ```

2. **Usar Flow para datos reactivos**
   ```kotlin
   val users: Flow<List<User>> = repository.getAllUsersFlow()
   ```

3. **Manejo de errores en cada capa**
   ```kotlin
   try {
       val result = useCase.execute()
   } catch (e: Exception) {
       _uiState.update { it.copy(error = e.message) }
   }
   ```

4. **Mapear entre capas**
   ```kotlin
   // Room Entity → Domain Model
   fun entityToModel(entity: UserEntity): User
   
   // Domain Model → Room Entity
   fun modelToEntity(model: User): UserEntity
   ```

### ❌ DON'T - No Hacer

1. **No acceder a Room directamente desde ViewModel**
   ```kotlin
   // ❌ MAL
   class UserViewModel(private val userDao: UserDao)
   
   // ✅ BIEN
   class UserViewModel(private val getUserUseCase: GetUserUseCase)
   ```

2. **No poner lógica de negocio en ViewModel**
   ```kotlin
   // ❌ MAL - Lógica en ViewModel
   class UserViewModel {
       fun isUserAtRisk(vitalSigns: VitalSigns): Boolean {
           return vitalSigns.heartRate > 100 // Regla de negocio
       }
   }
   
   // ✅ BIEN - Lógica en UseCase
   class CalculateRiskUseCase {
       fun execute(vitalSigns: VitalSigns): String {
           // Lógica de negocio compleja aquí
       }
   }
   ```

3. **No exponer tipos de Room en Domain/UI**
   ```kotlin
   // ❌ MAL
   interface UserRepository {
       suspend fun getUser(): UserEntity  // Tipo de Room
   }
   
   // ✅ BIEN
   interface UserRepository {
       suspend fun getUser(): User  // Tipo de Domain
   }
   ```

4. **No bloquear el hilo principal**
   ```kotlin
   // ❌ MAL
   fun loadData() {
       val data = runBlocking { repository.getData() }
   }
   
   // ✅ BIEN
   fun loadData() {
       viewModelScope.launch {
           val data = repository.getData()
       }
   }
   ```

---

## 🎯 Resumen de Responsabilidades

| Capa | Responsabilidad | NO debe contener |
|------|----------------|------------------|
| **UI** | Renderizado, eventos de usuario, estado visual | Lógica de negocio, acceso a BD |
| **ViewModel** | Gestión de estado, orquestación de UseCases | Lógica de negocio, acceso directo a BD |
| **UseCase** | Lógica de negocio, validaciones de dominio | Detalles de implementación de datos |
| **Repository** | Abstracción de fuentes de datos | Lógica de negocio |
| **DataSource** | Acceso a BD/API, persistencia | Lógica de negocio, estado de UI |

---

## 📚 Archivos de Ejemplo

Para ver implementaciones completas, consulta:

- **`LayerInteractionViewModel.kt`** - 7 ejemplos de interacción entre capas
- **`LayerInteractionScreen.kt`** - Pantalla Compose que consume el ViewModel
- **`UserUseCases.kt`** - Ejemplos de Use Cases
- **`UserRepositoryRoomImpl.kt`** - Implementación de Repository con Room

---

**Última actualización:** Noviembre 2025  
**Versión:** 1.0  
**Proyecto:** VitalCareApp - Clean Architecture
