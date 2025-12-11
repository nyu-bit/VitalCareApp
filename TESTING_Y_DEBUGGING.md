# 🧪 TESTING Y DEBUGGING - Flujo de Datos

## 🔍 Cómo Debuggear el Flujo Completo

### 1. Verificar RetrofitInstance
```kotlin
// En MainActivity o Activity
val vitalesApi = RetrofitInstance.getVitalesApi()
Log.d("DEBUG", "VitalesApi: $vitalesApi")
```

### 2. Verificar conexión a la API
```kotlin
viewModelScope.launch {
    val result = VitalesRepository().getAllVitales()
    result.onSuccess { vitales ->
        Log.d("DEBUG", "Éxito: ${vitales.size} vitales")
    }.onFailure { exception ->
        Log.e("DEBUG", "Error: ${exception.message}", exception)
    }
}
```

### 3. Verificar estado del ViewModel
```kotlin
val uiState by viewModel.uiState.collectAsState()
Log.d("DEBUG", "Estado actual: loading=${uiState.isLoading}, " +
              "vitales=${uiState.vitales.size}, " +
              "error=${uiState.error}")
```

---

## 🧪 Unit Tests

### Test 1: Repository getAllVitales
```kotlin
@Test
fun testGetAllVitales() = runBlocking {
    // Arrange
    val repository = VitalesRepository()
    
    // Act
    val result = repository.getAllVitales()
    
    // Assert
    assertTrue(result.isSuccess)
    result.onSuccess { vitales ->
        assertTrue(vitales.isNotEmpty())
    }
}
```

### Test 2: Repository getByPaciente
```kotlin
@Test
fun testGetByPaciente() = runBlocking {
    // Arrange
    val repository = VitalesRepository()
    val pacienteId = "P123"
    
    // Act
    val result = repository.getByPaciente(pacienteId)
    
    // Assert
    result.onSuccess { vitales ->
        vitales.forEach { vital ->
            assertEquals(pacienteId, vital.pacienteId)
        }
    }
}
```

### Test 3: ViewModel loadAllVitales
```kotlin
@Test
fun testLoadAllVitales() = runBlocking {
    // Arrange
    val viewModel = VitalesViewModel()
    
    // Act
    viewModel.loadAllVitales()
    advanceUntilIdle()
    
    // Assert
    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertTrue(state.vitales.isNotEmpty())
}
```

### Test 4: Error Handling
```kotlin
@Test
fun testErrorHandling() = runBlocking {
    // Arrange
    val viewModel = VitalesViewModel()
    
    // Act
    viewModel.loadByPaciente("")  // ID inválido
    
    // Assert
    val state = viewModel.uiState.value
    assertNotNull(state.error)
    assertTrue(state.error?.contains("inválido") == true)
}
```

---

## 📊 Logging en Cada Capa

### API Layer (Retrofit)
```kotlin
// Agregar OkHttp Logging Interceptor
fun buildClient(baseUrl: String): Retrofit {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
    
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

// Logs:
// --> GET /vitales
// <-- 200 OK (123ms)
// [{"id":"1",...}]
```

### Repository Layer
```kotlin
Log.d("VitalesRepository", "Llamando: GET /vitales")
Log.d("VitalesRepository", "Éxito: Obtenidas 5 vitales")
Log.e("VitalesRepository", "Error: ${exception.message}", exception)
```

### ViewModel Layer
```kotlin
Log.d("VitalesViewModel", "Cargando todos los vitales...")
Log.d("VitalesViewModel", "Estado actualizado: isLoading=false, vitales=5")
```

### UI Layer
```kotlin
Log.d("VitalesScreen", "Renderizando lista con ${vitales.size} items")
```

---

## 🔴 Errores Comunes y Soluciones

### Error 1: "Failed to connect to 10.0.2.2"
```
CAUSA: Backend no está corriendo
SOLUCIÓN:
1. Ejecutar ms-vitales en Puerto 8081
2. Verificar URL en RetrofitInstance
3. Usar adb reverse si está en dispositivo real
   adb reverse tcp:8081 tcp:8081
```

### Error 2: "JSON parsing error"
```
CAUSA: DTO no mapea correctamente JSON
SOLUCIÓN:
1. Verificar nombres de campos en JSON
2. Usar @SerializedName("campo_json") en DTO
3. Log del JSON: Log.d("JSON", GsonBuilder().setPrettyPrinting().create().toJson(response))
```

### Error 3: "Timeout"
```
CAUSA: API está lenta o Backend no responde
SOLUCIÓN:
1. Aumentar timeout en Retrofit
2. Verificar que el Backend está corriendo
3. Revisar logs del Backend
```

### Error 4: "java.lang.IllegalStateException: Expected BEGIN_OBJECT"
```
CAUSA: API devuelve un array pero DTO espera objeto
SOLUCIÓN:
1. Revisar si endpoint devuelve array o objeto
2. Cambiar suspend fun () -> List<Dto> o -> Dto
```

---

## 🎯 Flujos de Test Completos

### Test Flujo Completo: Carga de Vitales

```kotlin
@Test
fun testCompleteVitalsFlow() = runBlocking {
    // 1. Crear ViewModel
    val viewModel = VitalesViewModel()
    assert(!viewModel.uiState.value.isLoading)
    
    // 2. Cargar vitales
    viewModel.loadAllVitales()
    assert(viewModel.uiState.value.isLoading)
    
    // 3. Esperar a que termine
    advanceUntilIdle()
    
    // 4. Verificar resultados
    val state = viewModel.uiState.value
    assert(!state.isLoading)
    assert(state.error == null)
    assert(state.vitales.isNotEmpty())
    
    // 5. Verificar que los datos son correctos
    val firstVital = state.vitales.first()
    assert(firstVital.id != null)
    assert(firstVital.pacienteId.isNotEmpty())
    assert(firstVital.frecuenciaCardiaca > 0)
}
```

---

## 📈 Monitoreo en Producción

### Agregar Crashlytics
```kotlin
// En ViewModel
result.onFailure { exception ->
    FirebaseCrashlytics.getInstance().recordException(exception)
    _uiState.update { it.copy(error = exception.message) }
}
```

### Agregar Analytics
```kotlin
// Cuando se cargan datos
Log.d("Analytics", "Vitals loaded: ${vitales.size}")
FirebaseAnalytics.getInstance().logEvent("vitals_loaded", bundleOf(
    "count" to vitales.size
))
```

---

## 🔗 Mock Testing (Sin Backend)

### Crear Mock Repository
```kotlin
class MockVitalesRepository : VitalesRepository {
    override suspend fun getAllVitales(): Result<List<SignosVitalesDto>> {
        return Result.success(listOf(
            SignosVitalesDto(
                id = "1",
                pacienteId = "P123",
                frecuenciaCardiaca = 72,
                presionArterial = "120/80",
                temperatura = 36.5,
                saturacionOxigeno = 98,
                fecha = "2025-12-10T10:30:00Z"
            )
        ))
    }
}
```

### Usar en Test
```kotlin
@Test
fun testWithMockRepository() = runBlocking {
    // Usar MockRepository en lugar de real
    val mockRepository = MockVitalesRepository()
    val result = mockRepository.getAllVitales()
    
    assertTrue(result.isSuccess)
    result.onSuccess { vitales ->
        assertEquals(1, vitales.size)
    }
}
```

---

## 📱 Testear en Emulador

### 1. Iniciar emulador
```bash
emulator -avd AVD_Name
```

### 2. Verificar conectividad
```bash
adb shell
ping 10.0.2.2
```

### 3. Ver logs en tiempo real
```bash
adb logcat | grep VitalesRepository
```

### 4. Hacer HTTP request desde Android
```kotlin
// En una Activity o Fragment
viewModelScope.launch {
    val result = VitalesRepository().getAllVitales()
    runOnUiThread {
        Log.d("DEBUG", "Resultado: $result")
    }
}
```

---

## 🔍 Verificar Datos en Backend

### Hacer curl request
```bash
curl http://10.0.2.2:8081/vitales
```

### Respuesta esperada
```json
[
  {
    "id": "1",
    "pacienteId": "P123",
    "frecuenciaCardiaca": 72,
    "presionArterial": "120/80",
    "temperatura": 36.5,
    "saturacionOxigeno": 98,
    "fecha": "2025-12-10T10:30:00Z",
    "notas": null
  }
]
```

---

## ✅ Checklist de Testing

- [ ] Verificar RetrofitInstance construye correctamente
- [ ] Test Repository.getAllVitales()
- [ ] Test Repository.getByPaciente(id)
- [ ] Test ViewModel.loadAllVitales()
- [ ] Test ViewModel manejo de errores
- [ ] Test UI rendering con datos
- [ ] Test UI loading state
- [ ] Test UI error state
- [ ] Test UI empty state
- [ ] Verificar logs en Logcat
- [ ] Mock testing sin backend
- [ ] Test en dispositivo real

---

**Usa estos tests y métodos de debug para validar todo el flujo end-to-end!**


