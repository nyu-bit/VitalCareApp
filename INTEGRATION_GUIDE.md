# Guía de Integración de Nuevas Características

## Requisitos Previos

Antes de ejecutar la aplicación con las nuevas características, asegúrese de tener:

1. **Android Studio** versión 2023.1 o superior
2. **Gradle** 8.0+
3. **Emulador de Android** con API 24+ o dispositivo físico

---

## 1. Configurar Google Maps API

### Paso 1: Obtener API Key

1. Ir a [Google Cloud Console](https://console.cloud.google.com/)
2. Crear un nuevo proyecto o seleccionar uno existente
3. Habilitar las APIs:
   - **Maps SDK for Android**
   - **Location Services**
4. Crear credenciales de tipo "API Key"
5. Copiar la clave

### Paso 2: Agregar la clave en AndroidManifest.xml

Abrir `app/src/main/AndroidManifest.xml` y agregar dentro de `<application>`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
```

---

## 2. Inicializar Centros de Salud en la BD

Crear una clase para inicializar datos al arranque:

```kotlin
// app/src/main/java/cl/duoc/app/di/DataInitializer.kt

class DataInitializer(
    private val context: Context,
    private val healthCenterDao: HealthCenterDao
) {
    
    suspend fun initializeHealthCenters() {
        val existingCenters = healthCenterDao.getAllHealthCenters()
        if (existingCenters.isEmpty()) {
            val centers = TestDataGenerator.generateHealthCenters()
            healthCenterDao.insertHealthCenters(
                centers.map { it.toEntity() }
            )
        }
    }
}
```

Llamar en `MainActivity`:

```kotlin
// En MainActivity.onCreate()
viewModelScope.launch {
    dataInitializer.initializeHealthCenters()
}
```

---

## 3. Configurar Inyección de Dependencias

Crear un contenedor manual (sin Hilt por ahora):

```kotlin
// app/src/main/java/cl/duoc/app/di/ServiceLocator.kt

object ServiceLocator {
    private lateinit var context: Context
    private lateinit var database: VitalCareDatabase
    private lateinit var prefsManager: SharedPreferencesManager
    
    fun initialize(context: Context) {
        this.context = context
        this.database = VitalCareDatabase.getInstance(context)
        this.prefsManager = SharedPreferencesManager(context)
    }
    
    // Repositories
    fun provideLocationRepository(): LocationRepository {
        return LocationRepositoryImpl(
            context = context,
            healthCenterDao = database.healthCenterDao(),
            prefsManager = prefsManager
        )
    }
    
    fun provideSOSRepository(): SOSRepository {
        return SOSRepositoryImpl(
            sosEventDao = database.sosEventDao()
        )
    }
    
    // ViewModels (factories)
    fun provideHealthCenterMapViewModel(): HealthCenterMapViewModel {
        return HealthCenterMapViewModel(
            getHealthCenterLocationUseCase = GetHealthCenterLocationUseCase(
                provideLocationRepository()
            ),
            getCurrentLocationUseCase = GetCurrentLocationUseCase(
                provideLocationRepository()
            )
        )
    }
    
    fun provideSOSViewModel(): SOSViewModel {
        return SOSViewModel(
            triggerSOSUseCase = TriggerSOSUseCase(provideSOSRepository()),
            getLatestSOSEventsUseCase = GetLatestSOSEventsUseCase(provideSOSRepository()),
            getCurrentLocationUseCase = GetCurrentLocationUseCase(provideLocationRepository()),
            acknowledgeSOSEventUseCase = AcknowledgeSOSEventUseCase(provideSOSRepository()),
            resolveSOSEventUseCase = ResolveSOSEventUseCase(provideSOSRepository())
        )
    }
    
    // ... más providers
}
```

---

## 4. Agregar Pantallas a Navegación

Si está usando `Navigation Compose`:

```kotlin
// En su NavGraph

NavHost(...) {
    composable("home") {
        HomeScreen()
    }
    
    composable("health_center_map") {
        HealthCenterMapScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
    
    composable("profile/{userId}") { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: ""
        UserProfileScreen(
            userId = userId,
            onBackClick = { navController.popBackStack() }
        )
    }
    
    composable("patient_location/{patientId}/{patientName}") { backStackEntry ->
        val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
        val patientName = backStackEntry.arguments?.getString("patientName") ?: ""
        PatientLocationMapScreen(
            patientId = patientId,
            patientName = patientName,
            onBackClick = { navController.popBackStack() }
        )
    }
    
    composable("sos/{userId}") { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: ""
        SOSScreen(
            userId = userId,
            onBackClick = { navController.popBackStack() }
        )
    }
}
```

---

## 5. Solicitar Permisos en Tiempo de Ejecución

La aplicación solicita permisos automáticamente con `LocationPermissionHandler`, pero también puede hacerlo manualmente:

```kotlin
// Agregar en AndroidManifest.xml (ya está)
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

// En su Activity o Composable
val permissionState = rememberMultiplePermissionsState(
    permissions = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.POST_NOTIFICATIONS
    )
)

LaunchedEffect(Unit) {
    if (!permissionState.allPermissionsGranted) {
        permissionState.launchMultiplePermissionRequest()
    }
}
```

---

## 6. Probar en Emulador

### Simular Ubicación GPS:

1. Abrir **Extended Controls** en el emulador
2. Ir a **Location**
3. Ingresar coordenadas (ejemplo: Santiago)
   - Latitude: `-33.8688`
   - Longitude: `-70.2093`
4. Hacer clic en **Send**

### Probar SOS:

1. Navegar a la pantalla de SOS
2. Presionar el botón "SOS" rojo
3. Debería aparecer una notificación local
4. El evento se guardará en el historial

---

## 7. Compilar y Ejecutar

```bash
# Limpiar proyecto
./gradlew clean

# Compilar
./gradlew build

# Ejecutar en emulador
./gradlew installDebug

# O en Android Studio:
# Run > Run 'app'
```

---

## 8. Solución de Problemas

### Error: "Could not find method kaptTest()"

**Solución**: Agregar en `build.gradle.kts`:
```kotlin
plugins {
    // ... otros plugins
    id("kotlin-kapt")
}
```

### Google Maps no muestra nada

**Solución**: 
- Verificar que la API Key esté correctamente en `AndroidManifest.xml`
- Verificar que Google Maps SDK esté habilitada en Cloud Console
- En emulador, asegurarse de que tenga Play Services instalado

### Permisos no solicitados

**Solución**:
- Si es API 31+, el permiso `POST_NOTIFICATIONS` se solicita en tiempo de ejecución
- Usar `LocationPermissionHandler` o `rememberMultiplePermissionsState`

### SOSNotificationManager crash

**Solución**:
- Los canales de notificación se crean en `NotificationManager.init()`
- Se llama automáticamente al instanciar el manager
- Asegurarse de llamarlo en Application o MainActivity

---

## 9. Datos de Prueba

La aplicación incluye datos simulados para testing:

```kotlin
// Obtener centros de salud de prueba
val centers = TestDataGenerator.generateHealthCenters()

// Obtener usuarios de prueba
val users = TestDataGenerator.generateTestUsers()
```

---

## 10. API Reference Rápida

### LocationRepository
```kotlin
getCurrentLocation(): LocationData?
getHealthCenterLocation(): HealthCenter?
getAllHealthCenters(): List<HealthCenter>
getUserLastLocation(userId: String): LocationData?
```

### SOSRepository
```kotlin
triggerSOSEvent(userId: String, location: LocationData): SOSEvent
getSOSHistory(userId: String): List<SOSEvent>
getActiveSOSEvents(userId: String): List<SOSEvent>
resolveSOSEvent(eventId: String): Boolean
acknowledgeSOSEvent(eventId: String): Boolean
```

---

## Contacto y Soporte

Para reportar bugs o solicitar características, crear un issue en el repositorio.

Versión: 1.0.0
Última actualización: 2024

