# 🔧 GUÍA DE INYECCIÓN DE VIEWMODELS - CORRECCIÓN

Después de corregir los archivos, los ViewModels ahora son **parámetros requeridos**. Aquí te muestro cómo inyectarlos correctamente.

---

## 📋 Cambio de Paradigma

**ANTES** (Incorrecto):
```kotlin
fun HealthCenterMapScreen(
    viewModel: HealthCenterMapViewModel = viewModel()  // ❌ Incorrecto
)
```

**DESPUÉS** (Correcto):
```kotlin
fun HealthCenterMapScreen(
    viewModel: HealthCenterMapViewModel  // ✅ Requerido
)
```

---

## 🎯 Cómo Inyectar ViewModels

### Opción 1: Usar ServiceLocator (Recomendado)

```kotlin
// En tu Navigation o Activity

HealthCenterMapScreen(
    viewModel = ServiceLocator.provideHealthCenterMapViewModel(),
    onBackClick = { navController.popBackStack() }
)

PatientLocationMapScreen(
    patientId = "patient_123",
    patientName = "Juan García",
    viewModel = ServiceLocator.providePatientLocationMapViewModel(),
    onBackClick = { navController.popBackStack() }
)

UserProfileScreen(
    userId = "user_1",
    viewModel = ServiceLocator.provideUserProfileViewModel(),
    onBackClick = { navController.popBackStack() }
)

SOSScreen(
    userId = "user_1",
    viewModel = ServiceLocator.provideSOSViewModel(),
    onBackClick = { navController.popBackStack() }
)
```

### Opción 2: Inyectar Manualmente

```kotlin
// En tu Navigation o Activity

// 1. Obtener las dependencias
val locationRepository = LocationRepositoryImpl(context, db.healthCenterDao(), prefsManager)
val sosRepository = SOSRepositoryImpl(db.sosEventDao())

// 2. Crear los ViewModels
val healthCenterVM = HealthCenterMapViewModel(
    getHealthCenterLocationUseCase = GetHealthCenterLocationUseCase(locationRepository),
    getCurrentLocationUseCase = GetCurrentLocationUseCase(locationRepository)
)

val patientLocationVM = PatientLocationMapViewModel(
    getUserLastLocationUseCase = GetUserLastLocationUseCase(locationRepository)
)

val userProfileVM = UserProfileViewModel(
    userRepository = userRepository
)

val sosVM = SOSViewModel(
    triggerSOSUseCase = TriggerSOSUseCase(sosRepository),
    getLatestSOSEventsUseCase = GetLatestSOSEventsUseCase(sosRepository),
    getCurrentLocationUseCase = GetCurrentLocationUseCase(locationRepository),
    acknowledgeSOSEventUseCase = AcknowledgeSOSEventUseCase(sosRepository),
    resolveSOSEventUseCase = ResolveSOSEventUseCase(sosRepository)
)

// 3. Pasar a las pantallas
HealthCenterMapScreen(viewModel = healthCenterVM, onBackClick = { })
PatientLocationMapScreen(patientId = "123", viewModel = patientLocationVM, onBackClick = { })
UserProfileScreen(userId = "1", viewModel = userProfileVM, onBackClick = { })
SOSScreen(userId = "1", viewModel = sosVM, onBackClick = { })
```

### Opción 3: Usar Hilt (Para Futuros Desarrollos)

Si quieres usar Hilt para inyección automática, puedes preparar los ViewModels de la siguiente forma:

```kotlin
// Agregar en build.gradle.kts
plugins {
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.46")
    kapt("com.google.dagger:hilt-compiler:2.46")
}

// En los ViewModels
@HiltViewModel
class HealthCenterMapViewModel @Inject constructor(
    private val getHealthCenterLocationUseCase: GetHealthCenterLocationUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {
    // ...
}

// En las pantallas
@Composable
fun HealthCenterMapScreen(
    viewModel: HealthCenterMapViewModel = hiltViewModel(),  // ✅ Hilt se encarga
    onBackClick: () -> Unit = {}
) {
    // ...
}
```

---

## 📱 Ejemplo Completo en Navigation Compose

```kotlin
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        
        composable("home") {
            HomeScreen(
                onNavigateToHealthMap = { navController.navigate("health_map") }
            )
        }
        
        composable("health_map") {
            HealthCenterMapScreen(
                viewModel = ServiceLocator.provideHealthCenterMapViewModel(),
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable("patient_map/{patientId}/{patientName}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            val patientName = backStackEntry.arguments?.getString("patientName") ?: ""
            
            PatientLocationMapScreen(
                patientId = patientId,
                patientName = patientName,
                viewModel = ServiceLocator.providePatientLocationMapViewModel(),
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            
            UserProfileScreen(
                userId = userId,
                viewModel = ServiceLocator.provideUserProfileViewModel(),
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable("sos/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            
            SOSScreen(
                userId = userId,
                viewModel = ServiceLocator.provideSOSViewModel(),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
```

---

## ✅ Checklist de Implementación

Para cada pantalla, asegúrate de:

- [ ] Obtener el ViewModel usando ServiceLocator
- [ ] Pasar el ViewModel como parámetro
- [ ] Pasar los parámetros requeridos (userId, patientId, etc.)
- [ ] Implementar onBackClick correctamente
- [ ] Probar la pantalla en el emulador

---

## 🚀 Recomendación Final

**Usa ServiceLocator** (Opción 1) porque:
- ✅ Es simple de implementar
- ✅ Todas las dependencias están centralizadas
- ✅ Fácil de debuggear
- ✅ Funciona sin Hilt
- ✅ Está lista para usar

Si en el futuro quieres mejorar, migra a **Hilt** (Opción 3).

---

**¡Ya está todo configurado y listo para inyectar!** 🎉

