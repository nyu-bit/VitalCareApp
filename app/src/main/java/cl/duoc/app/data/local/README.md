# Configuración de Almacenamiento Local

## Descripción
Este proyecto incluye dos opciones de almacenamiento local:
1. **SharedPreferences** - Para datos simples y configuraciones
2. **Room Database** - Para datos estructurados y complejos (recomendado)

## Dependencias Necesarias

### Para usar Room, agregar en `build.gradle.kts` (módulo app):

```kotlin
dependencies {
    // Room
    val room_version = "2.6.0"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    
    // Gson para SharedPreferences
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Coroutines (si no están ya)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### Configurar KSP en `build.gradle.kts` (módulo app):

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}
```

## Estructura de Archivos

```
data/
├── local/
│   ├── SharedPreferencesManager.kt        # Manager para SharedPreferences
│   └── room/
│       ├── Entities.kt                    # Entidades Room (UserEntity, etc.)
│       ├── Daos.kt                        # DAOs (UserDao, ReservationDao, etc.)
│       ├── VitalCareDatabase.kt           # Configuración de Room Database
│       └── Mappers.kt                     # Mappers entre Entity y Domain
└── repository/
    ├── UserRepositoryImpl.kt              # Implementación en memoria
    ├── UserRepositoryRoomImpl.kt          # Implementación con Room
    ├── ReservationRepositoryRoomImpl.kt   # Implementación con Room
    └── VitalSignsRepositoryRoomImpl.kt    # Implementación con Room
```

## Uso de SharedPreferences

### Inicialización:
```kotlin
val prefsManager = SharedPreferencesManager(context)
```

### Operaciones:
```kotlin
// Guardar datos simples
prefsManager.saveString("key", "value")
prefsManager.saveBoolean("is_logged_in", true)
prefsManager.saveInt("counter", 10)

// Leer datos simples
val value = prefsManager.getString("key")
val isLoggedIn = prefsManager.getBoolean("is_logged_in")

// Guardar objetos
val user = User(...)
prefsManager.saveObject("current_user", user)

// Leer objetos
val user = prefsManager.getObject<User>("current_user")

// Guardar listas
prefsManager.saveList("users", listOf(user1, user2))

// Leer listas
val users = prefsManager.getList<User>("users")
```

## Uso de Room Database

### 1. Obtener instancia de la base de datos:
```kotlin
val database = VitalCareDatabase.getDatabase(context)
```

### 2. Obtener DAOs:
```kotlin
val userDao = database.userDao()
val reservationDao = database.reservationDao()
val vitalSignsDao = database.vitalSignsDao()
```

### 3. Usar con repositorios:
```kotlin
// Crear repositorio con Room
val userRepository = UserRepositoryRoomImpl(userDao)

// Usar en casos de uso
val getUserUseCase = GetUserUseCase(userRepository)
```

### 4. Operaciones típicas:
```kotlin
// En un ViewModel o caso de uso
viewModelScope.launch {
    // Insertar
    val user = User(...)
    userRepository.saveUser(user)
    
    // Consultar
    val user = userRepository.getUserById("123")
    
    // Observar cambios
    userRepository.observeUser("123")
        .collect { user ->
            // Actualizar UI
        }
}
```

## Entidades Room

### UserEntity
- id (PrimaryKey)
- name
- email
- phone
- createdAt

### ReservationEntity
- id (PrimaryKey)
- userId
- specialty
- doctorName
- date
- status

### VitalSignsEntity
- id (PrimaryKey)
- userId
- heartRate
- bloodPressureSystolic
- bloodPressureDiastolic
- oxygenSaturation
- timestamp

## DAOs Disponibles

### UserDao
- `getAllUsers()`: Obtiene todos los usuarios
- `getUserById()`: Obtiene usuario por ID
- `getUserByEmail()`: Busca por email
- `observeUser()`: Observa cambios en un usuario
- `insertUser()`: Inserta o actualiza
- `updateUser()`: Actualiza
- `deleteUser()`: Elimina

### ReservationDao
- `getReservationsByUserId()`: Reservas de un usuario
- `getReservationById()`: Reserva por ID
- `getReservationsByStatus()`: Filtra por estado
- `observeReservations()`: Observa cambios
- `insertReservation()`: Inserta
- `updateReservation()`: Actualiza
- `deleteReservation()`: Elimina

### VitalSignsDao
- `getVitalSignsByUserId()`: Registros de un usuario
- `getLatestVitalSigns()`: Últimos N registros
- `observeVitalSigns()`: Observa cambios
- `insertVitalSigns()`: Inserta registro
- `deleteVitalSigns()`: Elimina registro
- `deleteAllVitalSignsByUserId()`: Limpia historial

## Mappers

Los mappers convierten entre entidades Room y modelos de dominio:

```kotlin
// Entity -> Domain
val user = userEntity.toDomain()
val users = userEntitiesList.toDomainList()

// Domain -> Entity
val userEntity = user.toEntity()
```

## Inyección de Dependencias (Preparación)

Para usar Room con DI, crear un módulo:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VitalCareDatabase {
        return VitalCareDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideUserDao(database: VitalCareDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryRoomImpl(userDao)
    }
}
```

## Migraciones

Si cambias el esquema de la base de datos, incrementa la versión en `@Database`:

```kotlin
@Database(
    entities = [...],
    version = 2,  // Incrementar
    exportSchema = true
)
```

## Testing

Para testing, usa una base de datos en memoria:

```kotlin
@Before
fun setup() {
    database = Room.inMemoryDatabaseBuilder(
        context,
        VitalCareDatabase::class.java
    ).build()
}

@After
fun tearDown() {
    database.close()
}
```

## Recomendaciones

✅ **Usar Room** para datos estructurados y relaciones complejas
✅ **Usar SharedPreferences** solo para configuraciones simples
✅ **Implementar mappers** para separar capas
✅ **Usar Flow** para observar cambios reactivos
✅ **Implementar migraciones** en producción
✅ **No acceder a Room desde el hilo principal** (usar suspend o Flow)

## Errores Comunes

❌ Acceder a Room desde el hilo principal → Usar `suspend` o Flow
❌ No incrementar version al cambiar esquema → Crash al abrir DB
❌ No usar mappers → Acoplamiento entre capas
❌ Olvidar agregar dependencias → Errores de compilación
