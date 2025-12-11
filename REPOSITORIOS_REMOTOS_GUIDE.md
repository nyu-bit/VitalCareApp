# 📡 REPOSITORIOS REMOTOS - DOCUMENTACIÓN

## 🎯 Overview

Se han generado **3 Repositorios Remotos** que consumen las APIs Retrofit de VitalCare:

1. **VitalesRepository** - Para operaciones con Signos Vitales
2. **UbicacionRepository** - Para operaciones con Ubicaciones
3. **AlertasRepository** - Para operaciones con Alertas
4. **PacienteDataRepository** - Para operaciones consolidadas

---

## 🏥 VitalesRepository

**Ubicación:** `data/repository/ApiRepositories.kt`

### Métodos Disponibles

#### `getAllVitales(): Result<List<SignosVitalesDto>>`
```kotlin
// Obtiene todos los signos vitales
val repository = VitalesRepository()
val result = repository.getAllVitales()
result.fold(
    onSuccess = { signos -> println("${signos.size} vitales encontrados") },
    onFailure = { error -> println("Error: ${error.message}") }
)
```

#### `getByPaciente(pacienteId: String): Result<List<SignosVitalesDto>>`
```kotlin
// Obtiene vitales de un paciente específico
val repository = VitalesRepository()
val result = repository.getByPaciente("paciente123")
```

#### `createVital(signoVital: SignosVitalesDto): Result<SignosVitalesDto>`
```kotlin
// Crea un nuevo registro de vital
val nuevoVital = SignosVitalesDto(
    pacienteId = "pac123",
    frecuenciaCardiaca = 72,
    temperatura = 37.5
)
val result = repository.createVital(nuevoVital)
```

#### `deleteVitales(vitalesId: String): Result<Unit>`
```kotlin
// Elimina un registro de vital
val result = repository.deleteVitales("vital123")
```

---

## 📍 UbicacionRepository

**Ubicación:** `data/repository/ApiRepositories.kt`

### Métodos Disponibles

#### `getAll(): Result<List<UbicacionDto>>`
```kotlin
// Obtiene todas las ubicaciones
val repository = UbicacionRepository()
val result = repository.getAll()
```

#### `getByPaciente(pacienteId: String): Result<List<UbicacionDto>>`
```kotlin
// Obtiene ubicaciones de un paciente
val result = repository.getByPaciente("paciente123")
```

#### `saveUbicacion(ubicacion: UbicacionDto): Result<UbicacionDto>`
```kotlin
// Guarda una nueva ubicación
val nuevaUbicacion = UbicacionDto(
    pacienteId = "pac123",
    latitud = -33.8688,
    longitud = -71.5203,
    direccion = "Calle Principal 123"
)
val result = repository.saveUbicacion(nuevaUbicacion)
```

---

## 🚨 AlertasRepository

**Ubicación:** `data/repository/ApiRepositories.kt`

### Métodos Disponibles

#### `getAll(): Result<List<AlertaDto>>`
```kotlin
// Obtiene todas las alertas
val repository = AlertasRepository()
val result = repository.getAll()
```

#### `getByPaciente(pacienteId: String): Result<List<AlertaDto>>`
```kotlin
// Obtiene alertas de un paciente
val result = repository.getByPaciente("paciente123")
```

#### `createAlerta(alerta: AlertaDto): Result<AlertaDto>`
```kotlin
// Crea una nueva alerta
val nuevaAlerta = AlertaDto(
    pacienteId = "pac123",
    titulo = "Presión Alta",
    mensaje = "Presión arterial elevada",
    severidad = "Alto",
    tipo = "Signos Vitales"
)
val result = repository.createAlerta(nuevaAlerta)
```

#### `markAsAttended(alerta: AlertaDto): Result<AlertaDto>`
```kotlin
// Marca una alerta como atendida/leída
val result = repository.markAsAttended(alerta)
// Esto automáticamente establece leida = true
```

#### `deleteAlerta(alertaId: String): Result<Unit>`
```kotlin
// Elimina una alerta
val result = repository.deleteAlerta("alerta123")
```

---

## 👥 PacienteDataRepository

**Ubicación:** `data/repository/ApiRepositories.kt`

### Propósito
Permite obtener datos consolidados de un paciente desde múltiples APIs simultáneamente.

### Método Disponible

#### `getPacienteCompleteData(pacienteId: String): Result<PacienteCompleteData>`
```kotlin
// Obtiene todos los datos de un paciente
val repository = PacienteDataRepository()
val result = repository.getPacienteCompleteData("paciente123")

result.fold(
    onSuccess = { data ->
        val vitales = data.vitales
        val ubicaciones = data.ubicaciones
        val alertas = data.alertas
        // Procesar datos consolidados
    },
    onFailure = { error ->
        println("Error: ${error.message}")
    }
)
```

### Estructura de `PacienteCompleteData`
```kotlin
data class PacienteCompleteData(
    val vitales: List<SignosVitalesDto>,
    val ubicaciones: List<UbicacionDto>,
    val alertas: List<AlertaDto>
)
```

---

## 🛡️ Manejo de Excepciones

### Tipos de Errores Capturados

Todos los repositorios manejan 3 tipos de excepciones:

#### 1. **IOException** - Errores de Conexión
```kotlin
catch (e: IOException) {
    // Error de conexión (sin internet, timeout, etc.)
    Result.failure(Exception("Error de conexión: ${e.message}", e))
}
```

#### 2. **HttpException** - Errores HTTP
```kotlin
catch (e: retrofit2.HttpException) {
    // Error HTTP (404, 500, 401, etc.)
    Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
}
```

#### 3. **Exception** - Otros Errores
```kotlin
catch (e: Exception) {
    // Error desconocido
    Result.failure(Exception("Error desconocido: ${e.message}", e))
}
```

### Validaciones de Entrada

Los repositorios validan automáticamente:

```kotlin
// Validar que el ID no esté vacío
if (pacienteId.isBlank()) {
    return@withContext Result.failure(Exception("ID de paciente no puede estar vacío"))
}

// Validar que exista ID requerido
if (signoVital.pacienteId.isBlank()) {
    return@withContext Result.failure(Exception("ID de paciente requerido"))
}
```

---

## 📊 Patrón Result<T>

Todos los métodos retornan `Result<T>` para manejo explícito de errores:

```kotlin
// Opción 1: Usar fold
result.fold(
    onSuccess = { datos -> /* Procesar datos */ },
    onFailure = { error -> /* Manejar error */ }
)

// Opción 2: Usar getOrNull
val datos = result.getOrNull()
if (datos != null) {
    // Usar datos
}

// Opción 3: Usar getOrElse
val datos = result.getOrElse { emptyList() }

// Opción 4: Usar isSuccess
if (result.isSuccess) {
    val datos = result.getOrNull()
}
```

---

## 🔄 Execution Context

Todos los métodos usan `withContext(Dispatchers.IO)` para:

✅ No bloquear el thread principal  
✅ Ejecutar operaciones HTTP en background  
✅ Mantener la UI responsiva  

---

## 📝 Logging

Se incluyen funciones de logging integradas:

### logSuccess(operacion: String, mensaje: Any?)
```kotlin
// Registra operaciones exitosas
logSuccess("VitalesRepository.getAllVitales()", 5) 
// Output: ✅ [2025-12-10 14:30:45] SUCCESS - VitalesRepository.getAllVitales(): 5
```

### logError(operacion: String, tipoError: String, excepcion: Exception)
```kotlin
// Registra errores
logError("VitalesRepository.getByPaciente()", "Error de conexión", exception)
// Output: ❌ [2025-12-10 14:30:45] ERROR - VitalesRepository.getByPaciente() | Error de conexión: ...
```

---

## 📌 Mejores Prácticas

### 1. Siempre usar en Coroutine
```kotlin
viewModelScope.launch {
    val result = repository.getAllVitales()
    // Procesar resultado
}
```

### 2. Manejar errores explícitamente
```kotlin
result.fold(
    onSuccess = { datos -> /* Actualizar UI */ },
    onFailure = { error -> /* Mostrar error al usuario */ }
)
```

### 3. Validar datos antes de enviar
```kotlin
if (signoVital.pacienteId.isBlank()) {
    // Mostrar error al usuario
    return
}
val result = repository.createVital(signoVital)
```

### 4. No ignorar errores
```kotlin
// ❌ Mal
repository.getAllVitales()

// ✅ Bien
val result = repository.getAllVitales()
result.onFailure { error -> 
    showError(error.message)
}
```

---

## 🧪 Testing

Para testear estos repositorios, mockear las APIs:

```kotlin
class VitalesRepositoryTest {
    
    @Test
    fun testGetAllVitales() = runTest {
        // Mock del API
        val mockApi = mockk<VitalesApi>()
        coEvery { mockApi.getAllVitales() } returns listOf(
            SignosVitalesDto(pacienteId = "pac1", frecuenciaCardiaca = 72)
        )
        
        // Test
        val repository = VitalesRepository() // Con inyección de dependencia
        val result = repository.getAllVitales()
        
        // Assert
        assertTrue(result.isSuccess)
    }
}
```

---

## 📂 Estructura del Archivo

```
ApiRepositories.kt
├── VitalesRepository
│   ├── getAllVitales()
│   ├── getByPaciente()
│   ├── createVital()
│   └── deleteVitales()
├── UbicacionRepository
│   ├── getAll()
│   ├── getByPaciente()
│   └── saveUbicacion()
├── AlertasRepository
│   ├── getAll()
│   ├── getByPaciente()
│   ├── createAlerta()
│   ├── markAsAttended()
│   └── deleteAlerta()
├── PacienteDataRepository
│   └── getPacienteCompleteData()
├── PacienteCompleteData (Data Class)
├── logSuccess() (Función auxiliar)
└── logError() (Función auxiliar)
```

---

## 🎯 Resumen

| Repositorio | Métodos | Descripción |
|---|---|---|
| **VitalesRepository** | 4 | Operaciones CRUD de signos vitales |
| **UbicacionRepository** | 3 | Operaciones CRUD de ubicaciones |
| **AlertasRepository** | 5 | Operaciones CRUD de alertas + marcar como leída |
| **PacienteDataRepository** | 1 | Datos consolidados de paciente |

**Total: 13 métodos** con manejo robusto de excepciones y logging integrado.

---

**Generado:** Diciembre 2025  
**Estado:** ✅ COMPLETO  
**Próximo Paso:** Integrar en ViewModels

