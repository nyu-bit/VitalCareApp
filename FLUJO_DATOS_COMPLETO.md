# 📊 FLUJO COMPLETO DE DATOS: Backend → App

## 🎯 Arquitectura Layer by Layer

```
┌─────────────────────────────────────────────────────────┐
│              USUARIO / UI (Pantalla)                    │
│          ← VitalesScreen (Compose)                       │
└────────────────────────────┬────────────────────────────┘
                             │
                    collectAsState()
                             │
                             ↓
┌─────────────────────────────────────────────────────────┐
│           VIEWMODEL (Lógica + Estado)                   │
│          → VitalesViewModel                              │
│          • loadAllVitales()                              │
│          • loadByPaciente(id)                            │
│          • createVital(vital)                            │
│          • StateFlow<VitalesUiState>                     │
└────────────────────────────┬────────────────────────────┘
                             │
                    viewModelScope.launch
                             │
                             ↓
┌─────────────────────────────────────────────────────────┐
│          REPOSITORY (Abstracción de datos)              │
│          → VitalesRepository                             │
│          • getAllVitales()                               │
│          • getByPaciente(id)                             │
│          • createVital(vital)                            │
│          • deleteVital(id)                               │
└────────────────────────────┬────────────────────────────┘
                             │
                    suspend fun (Coroutines)
                             │
                             ↓
┌─────────────────────────────────────────────────────────┐
│          API CLIENT (Retrofit Interface)                │
│          → VitalesApi                                    │
│          @GET("/vitales")                               │
│          @GET("/vitales/paciente/{id}")                 │
│          @POST("/vitales")                              │
│          @DELETE("/vitales/{id}")                       │
└────────────────────────────┬────────────────────────────┘
                             │
                    Retrofit HTTP Request
                             │
                             ↓
┌─────────────────────────────────────────────────────────┐
│       MICROSERVICIO BACKEND (Java/Spring)               │
│       → ms-vitales (Puerto 8081)                         │
│       • /vitales (GET, POST)                            │
│       • /vitales/paciente/{id} (GET)                    │
│       • /vitales/{id} (DELETE)                          │
└────────────────────────────┬────────────────────────────┘
                             │
                    JPA/Hibernate
                             │
                             ↓
┌─────────────────────────────────────────────────────────┐
│            BASE DE DATOS (MySQL)                        │
│            → vitales_table                              │
│            • id                                         │
│            • paciente_id                                │
│            • frecuencia_cardiaca                        │
│            • presion_arterial                           │
│            • temperatura                                │
│            • saturacion_oxigeno                         │
│            • fecha                                      │
│            • notas                                      │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo Detallado: Cargar Vitales

### Paso 1: Usuario abre pantalla
```kotlin
VitalesScreen()
└─ LaunchedEffect(Unit) {
    viewModel.loadAllVitales()
}
```

### Paso 2: ViewModel inicia carga
```kotlin
fun loadAllVitales() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        val result = repository.getAllVitales()
        // Procesar resultado
    }
}
```

### Paso 3: Repository llama API
```kotlin
suspend fun getAllVitales(): Result<List<SignosVitalesDto>> {
    return vitalesApi.getAllVitales()
}
```

### Paso 4: Retrofit hace request HTTP
```
GET http://10.0.2.2:8081/vitales
```

### Paso 5: Backend recibe y procesa
```
1. Spring Controller recibe GET
2. JPA Query a MySQL
3. Resultado se serializa a JSON
4. Respuesta: [
     {
       "id": "1",
       "pacienteId": "P123",
       "frecuenciaCardiaca": 72,
       "presionArterial": "120/80",
       "temperatura": 36.5,
       "saturacionOxigeno": 98,
       "fecha": "2025-12-10T10:30:00Z"
     }
   ]
```

### Paso 6: App recibe datos
```kotlin
vitalesApi.getAllVitales()
└─ JSON deserialized a List<SignosVitalesDto>
└─ Result<List<SignosVitalesDto>>
```

### Paso 7: ViewModel actualiza estado
```kotlin
result.onSuccess { vitales ->
    _uiState.update { it.copy(vitales = vitales, isLoading = false) }
}
```

### Paso 8: Compose recompone y muestra datos
```kotlin
Column {
    vitales.forEach { vital ->
        VitalCard(vital)  ← Muestra datos del backend
    }
}
```

---

## 📱 Ejemplo Práctico: Pantalla Vitales

### Datos que llegan del backend:
```json
{
  "id": "vital_001",
  "pacienteId": "paciente_123",
  "frecuenciaCardiaca": 72,
  "presionArterial": "120/80",
  "temperatura": 36.5,
  "saturacionOxigeno": 98,
  "fecha": "2025-12-10T10:30:00Z",
  "notas": "Medición tomada en reposo"
}
```

### Se renderiza como:
```
┌─────────────────────────────────────────┐
│           10 Dic 2025 10:30    [🗑️]    │
├─────────────────────────────────────────┤
│  ❤️ Frecuencia  |  🌡️  Temperatura     │
│    72 bpm       |      36.5 °C          │
├─────────────────────────────────────────┤
│  🔥 Presión     |  💨 O₂                │
│   120/80 mmHg   |      98 %             │
├─────────────────────────────────────────┤
│  Notas: Medición tomada en reposo       │
└─────────────────────────────────────────┘
```

---

## 🔗 Integración de los 4 Microservicios

### ms-vitales (8081)
```
GET  /vitales
GET  /vitales/paciente/{id}
POST /vitales
DELETE /vitales/{id}
↓
SignosVitalesDto
├─ frecuenciaCardiaca
├─ presionArterial
├─ temperatura
├─ saturacionOxigeno
└─ ...
```

### ms-ubicacion (8082)
```
GET  /ubicacion
GET  /ubicacion/paciente/{id}
POST /ubicacion
DELETE /ubicacion/{id}
↓
UbicacionDto
├─ latitud
├─ longitud
├─ direccion
├─ ciudad
└─ país
```

### ms-alertas (8083)
```
GET  /alertas
GET  /alertas/paciente/{id}
POST /alertas
PUT  /alertas/{id}
DELETE /alertas/{id}
↓
AlertaDto
├─ titulo
├─ mensaje
├─ severidad
├─ tipo
└─ leida
```

### Weather API (OpenWeatherMap)
```
GET /data/2.5/weather
├─ lat, lon, appid
↓
WeatherDto
├─ temperatura
├─ humedad
├─ descripcion
└─ ...
```

---

## 🛠️ Manejo de Errores

### En Repository:
```kotlin
suspend fun getAllVitales(): Result<List<SignosVitalesDto>> {
    try {
        val signos = vitalesApi.getAllVitales()
        Result.success(signos)  ← Éxito
    } catch (e: IOException) {
        Result.failure(...)      ← Error de conexión
    } catch (e: HttpException) {
        Result.failure(...)      ← Error HTTP (404, 500, etc)
    } catch (e: Exception) {
        Result.failure(...)      ← Error desconocido
    }
}
```

### En ViewModel:
```kotlin
val result = repository.getAllVitales()

result.onSuccess { vitales ->
    // Actualizar estado con datos
    _uiState.update { it.copy(vitales = vitales, isLoading = false) }
}.onFailure { exception ->
    // Mostrar error al usuario
    _uiState.update { it.copy(error = exception.message, isLoading = false) }
}
```

### En UI:
```kotlin
when {
    uiState.isLoading -> LoadingState()
    uiState.error != null -> ErrorState(uiState.error)
    uiState.vitales.isEmpty() -> EmptyState()
    else -> VitalesListContent(uiState.vitales)
}
```

---

## 📋 Flujo de Creación: POST /vitales

### 1. Usuario llena formulario
```
Frecuencia: 75
Presión: 125/82
Temperatura: 36.8
O₂: 97
```

### 2. ViewModel crea objeto
```kotlin
viewModel.createVital(
    SignosVitalesDto(
        pacienteId = "P123",
        frecuenciaCardiaca = 75,
        presionArterial = "125/82",
        temperatura = 36.8,
        saturacionOxigeno = 97,
        fecha = "2025-12-10T10:30:00Z"
    )
)
```

### 3. Repository envía a API
```kotlin
vitalesApi.createVital(vital)
```

### 4. HTTP POST
```
POST http://10.0.2.2:8081/vitales
Content-Type: application/json

{
  "pacienteId": "P123",
  "frecuenciaCardiaca": 75,
  ...
}
```

### 5. Backend procesa
```
1. Controller recibe POST
2. Valida datos
3. Guarda en MySQL
4. Retorna objeto con ID generado
```

### 6. App recibe respuesta
```json
{
  "id": "vital_999",  ← ID generado
  "pacienteId": "P123",
  ...
}
```

### 7. ViewModel actualiza lista
```kotlin
_uiState.update { state ->
    state.copy(vitales = state.vitales + createdVital)
}
```

### 8. UI muestra nuevo vital
```
Nuevo vital aparece en la lista inmediatamente
```

---

## 🔄 Estados de Carga

```
Inicial
  ↓
  isLoading = true
  (Mostrar spinner)
  ↓
Datos llegan
  ↓
  isLoading = false
  vitales = [...]
  (Mostrar lista)
  
  ─ O SI ERROR ─
  isLoading = false
  error = "Error de conexión"
  (Mostrar error con reintentar)
```

---

## 📝 Logging Completo

### En Repository:
```
D/VitalesRepository: Llamando: GET /vitales
D/VitalesRepository: Éxito: Obtenidas 5 vitales
```

### En ViewModel:
```
D/VitalesViewModel: Cargando todos los vitales...
D/VitalesViewModel: Vitales cargados exitosamente: 5 registros
```

### En Logcat:
```
Network: GET http://10.0.2.2:8081/vitales
Response: 200 OK
Body: [{...}, {...}]
```

---

## ✅ Checklist de Integración

- [x] RetrofitInstance con 4 microservicios
- [x] Interfaces Retrofit (VitalesApi, UbicacionApi, AlertasApi, WeatherApi)
- [x] DTOs para mapeo JSON → Kotlin
- [x] Repositorios remotos con manejo de errores
- [x] ViewModels con StateFlow
- [x] Pantalla Compose que consume datos
- [x] Estados: loading, error, empty, data
- [x] Logging en cada capa
- [x] Try/catch con Result pattern
- [x] Documentación completa

---

## 🚀 Próximos Pasos

1. **Configurar backend local**
   - Descargar y ejecutar ms-vitales, ms-ubicacion, ms-alertas
   - Crear base de datos MySQL

2. **Actualizar URLs** (si no es 10.0.2.2:808X)
   - Cambiar en RetrofitInstance.kt

3. **Agregar tu API key de Weather**
   - En WeatherRepository.kt

4. **Compilar y probar**
   - Abre VitalesScreen
   - Debería cargar datos del backend

---

Esta es la arquitectura profesional estándar: UI → ViewModel → Repository → API → Backend → Database


