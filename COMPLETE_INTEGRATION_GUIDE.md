# Integración Completa - Interfaces Retrofit, DTOs, Repositories y ViewModels

## 📚 Flujo de Datos Completo

```
┌─────────────────────────────────────────────────────────────┐
│                    UI Layer (Composables)                   │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                ViewModel Layer (StateFlow)                   │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ VitalesViewModel, UbicacionViewModel, AlertasViewModel │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│               Repository Layer (Business Logic)             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ VitalesRepository, UbicacionRepository, AlertasRepository
│  │ PacienteDataRepository (para datos consolidados)       │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              API Layer (Retrofit Interfaces)                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ VitalesApi, UbicacionApi, AlertasApi                  │ │
│  │ (con DTOs: SignosVitalesDto, UbicacionDto, AlertaDto) │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                 Network Layer (Retrofit)                     │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ RetrofitInstance (Singleton)                           │ │
│  │ - buildClient(baseUrl) - método privado              │ │
│  │ - getVitalesApi(), getUbicacionApi(), getAlertasApi()│ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    Microservicios                            │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ http://10.0.2.2:8081/ - Vitales                       │ │
│  │ http://10.0.2.2:8082/ - Ubicación                     │ │
│  │ http://10.0.2.2:8083/ - Alertas                       │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo de Ejecución - Ejemplo Práctico

### Paso 1: Crear ViewModel
```kotlin
class MiScreen {
    private val viewModel = VitalesViewModel()
}
```

### Paso 2: Disparar acción en UI
```kotlin
Button(onClick = { viewModel.loadVitalesByPaciente("paciente123") })
```

### Paso 3: ViewModel ejecuta Repository
```kotlin
fun loadVitalesByPaciente(pacienteId: String) {
    viewModelScope.launch {
        _uiState.value = VitalesUiState.Loading
        val result = repository.getVitalesByPaciente(pacienteId)
        // ...
    }
}
```

### Paso 4: Repository llama a API
```kotlin
suspend fun getVitalesByPaciente(pacienteId: String): Result<List<SignosVitalesDto>> =
    withContext(Dispatchers.IO) {
        try {
            val signos = vitalesApi.getVitalesByPaciente(pacienteId)
            Result.success(signos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
```

### Paso 5: RetrofitInstance obtiene la interfaz
```kotlin
val vitalesApi = RetrofitInstance.getVitalesApi()
// Retorna: vitalesRetrofit.create(VitalesApi::class.java)
```

### Paso 6: Retrofit realiza la llamada HTTP
```kotlin
@GET("vitales/paciente/{id}")
suspend fun getVitalesByPaciente(@Path("id") pacienteId: String): List<SignosVitalesDto>
// GET /vitales/paciente/paciente123
```

### Paso 7: Respuesta regresa a través de las capas
```
JSON → GsonConverterFactory → SignosVitalesDto (DTO)
    → Result<List<SignosVitalesDto>>
    → Repository
    → ViewModel
    → StateFlow<VitalesUiState.Success>
    → Composable (re-composición)
```

---

## 📂 Estructura de Archivos Generados

### Carpeta `data/api/`
```
api/
├── VitalesApi.kt              # Interfaz + SignosVitalesDto
├── UbicacionApi.kt            # Interfaz + UbicacionDto
├── AlertasApi.kt              # Interfaz + AlertaDto
├── API_USAGE_GUIDE.kt         # Guía de uso con ejemplos
├── ExamplesAndPatterns.kt     # Patrones avanzados
└── README.md                  # Documentación de la carpeta
```

### Carpeta `data/repository/`
```
repository/
├── ApiRepositories.kt         # VitalesRepository, UbicacionRepository, AlertasRepository
├── ViewModels.kt              # VitalesViewModel, UbicacionViewModel, AlertasViewModel
└── ... (otros repositories existentes)
```

### Archivo `data/`
```
data/
└── RetrofitInstance.kt        # Singleton (actualizado con nuevos métodos)
```

---

## 🎯 Casos de Uso Prácticos

### Caso 1: Cargar y mostrar signos vitales de un paciente
```kotlin
@Composable
fun VitalesScreen(pacienteId: String) {
    val viewModel = VitalesViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pacienteId) {
        viewModel.loadVitalesByPaciente(pacienteId)
    }

    when (uiState) {
        is VitalesUiState.Loading -> {
            CircularProgressIndicator()
        }
        is VitalesUiState.Success -> {
            val signos = (uiState as VitalesUiState.Success).signos
            LazyColumn {
                items(signos) { signo ->
                    VitalSignCard(signo)
                }
            }
        }
        is VitalesUiState.Error -> {
            Text("Error: ${(uiState as VitalesUiState.Error).message}")
        }
    }
}
```

### Caso 2: Crear nuevo registro de signos vitales
```kotlin
fun crearNuevoVital(pacienteId: String) {
    val nuevoSigno = SignosVitalesDto(
        pacienteId = pacienteId,
        frecuenciaCardiaca = 72,
        presionArterialSistolica = 120,
        presionArterialDiastolica = 80,
        saturacionOxigeno = 98,
        temperatura = 37.5
    )
    viewModel.saveVitales(nuevoSigno)
}
```

### Caso 3: Obtener datos consolidados de un paciente
```kotlin
val viewModel = PacienteDataViewModel()
viewModel.loadPacienteData("paciente123")

// Obtendrá en paralelo:
// - Todos los signos vitales del paciente
// - Todas las ubicaciones del paciente
// - Todas las alertas del paciente
```

---

## ✅ Características Implementadas

| Característica | Implementado | Detalles |
|---|---|---|
| **Interfaces Retrofit** | ✅ | 3 interfaces (Vitales, Ubicación, Alertas) |
| **DTOs** | ✅ | Incluidos en cada interfaz con valores por defecto |
| **Suspend Functions** | ✅ | Todas las funciones soportan corrutinas |
| **Repositories** | ✅ | Repository por servicio + combinado |
| **ViewModels** | ✅ | ViewModel por servicio con StateFlow |
| **Error Handling** | ✅ | Uso de Result<T> en repositories |
| **Type Safety** | ✅ | Genéricos donde aplica |
| **Singleton Pattern** | ✅ | RetrofitInstance como singleton |
| **Lazy Initialization** | ✅ | Instancias creadas solo cuando se usan |
| **Documentación** | ✅ | JavaDoc + README + Ejemplos |

---

## 🚀 Cómo Usar - Resumen Rápido

### 1. En un ViewModel
```kotlin
class MiViewModel : ViewModel() {
    private val vitalesRepository = VitalesRepository()

    fun cargarDatos() {
        viewModelScope.launch {
            val result = vitalesRepository.getVitalesByPaciente("paciente123")
            // Manejar resultado
        }
    }
}
```

### 2. En un Composable (con ViewModel)
```kotlin
@Composable
fun MiPantalla() {
    val viewModel = VitalesViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVitalesByPaciente("paciente123")
    }

    when (uiState) {
        is VitalesUiState.Loading -> { /* ... */ }
        is VitalesUiState.Success -> { /* ... */ }
        is VitalesUiState.Error -> { /* ... */ }
    }
}
```

### 3. Directamente (menos recomendado)
```kotlin
val vitalesApi = RetrofitInstance.getVitalesApi()
val signos = vitalesApi.getVitalesByPaciente("paciente123")
```

---

## 📚 Archivos de Referencia

- **RETROFIT_APIS_SUMMARY.md** - Resumen general
- **data/api/README.md** - Documentación de APIs
- **data/api/API_USAGE_GUIDE.kt** - Ejemplos básicos
- **data/api/ExamplesAndPatterns.kt** - Patrones avanzados

---

## 🔗 Dependencias Requeridas

Las siguientes dependencias ya fueron agregadas a `build.gradle.kts`:

```gradle
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
implementation("com.google.code.gson:gson:2.10.1")
```

---

## 🔐 Seguridad y Mejores Prácticas

### Agregadas por defecto:
- ✅ Corrutinas para operaciones no-bloqueantes
- ✅ Dispatchers.IO para llamadas HTTP
- ✅ Try-catch para manejo de errores
- ✅ Result<T> para resultados explícitos

### Recomendadas para agregar:
- [ ] Interceptor de autenticación (Token/OAuth)
- [ ] Caché local con Room
- [ ] Logging con OkHttp Logging Interceptor (solo debug)
- [ ] Encriptación de datos sensibles
- [ ] Retry logic con exponential backoff

---

## ✨ Próximos Pasos

1. ✅ Crear interfaces Retrofit
2. ✅ Crear DTOs
3. ✅ Crear Repositories
4. ✅ Crear ViewModels
5. ⏳ Integrar en Composables
6. ⏳ Agregar caché local
7. ⏳ Agregar autenticación
8. ⏳ Agregar logging
9. ⏳ Testing

---

**Generado para:** VitalCare App  
**Fecha:** 2025  
**Versión:** 1.0

