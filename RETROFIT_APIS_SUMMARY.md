# Resumen de Interfaces Retrofit y DTOs Generados

## 📋 Archivos Creados

### 1. **VitalesApi.kt** (`data/api/`)
Interfaz Retrofit para el microservicio de Signos Vitales

**Métodos:**
- ✅ `getAllVitales()` - GET /vitales
- ✅ `getVitalesByPaciente(id)` - GET /vitales/paciente/{id}
- ✅ `createVitales(signos)` - POST /vitales
- ✅ `deleteVitales(id)` - DELETE /vitales/{id}

**DTO incluido:** `SignosVitalesDto`

---

### 2. **UbicacionApi.kt** (`data/api/`)
Interfaz Retrofit para el microservicio de Ubicación

**Métodos:**
- ✅ `getAllUbicaciones()` - GET /ubicacion
- ✅ `getUbicacionesByPaciente(id)` - GET /ubicacion/paciente/{id}
- ✅ `createUbicacion(ubicacion)` - POST /ubicacion

**DTO incluido:** `UbicacionDto`

---

### 3. **AlertasApi.kt** (`data/api/`)
Interfaz Retrofit para el microservicio de Alertas

**Métodos:**
- ✅ `getAllAlertas()` - GET /alertas
- ✅ `getAlertasByPaciente(id)` - GET /alertas/paciente/{id}
- ✅ `createAlerta(alerta)` - POST /alertas
- ✅ `updateAlerta(id, alerta)` - PUT /alertas/{id}
- ✅ `deleteAlerta(id)` - DELETE /alertas/{id}

**DTO incluido:** `AlertaDto`

---

### 4. **RetrofitInstance.kt** (Actualizado - `data/`)
Singleton que gestiona todas las instancias de Retrofit

**Nuevos métodos agregados:**
- `getVitalesApi()` - Retorna VitalesApi
- `getUbicacionApi()` - Retorna UbicacionApi
- `getAlertasApi()` - Retorna AlertasApi

---

### 5. **Archivos de Documentación y Ejemplos**

#### **API_USAGE_GUIDE.kt**
Guía completa con ejemplos de uso para cada interfaz

#### **README.md**
Documentación general de la carpeta `api/`

#### **ExamplesAndPatterns.kt**
Patrones recomendados y ejemplos avanzados:
- Repository pattern
- ViewModel pattern
- Uso en Composables
- Manejo de errores
- Operaciones concurrentes

---

## 🔧 Características Implementadas

✅ **Suspend Functions:** Todos los métodos son `suspend fun` para uso con corrutinas  
✅ **Retrofit + Gson:** Integración completa con GsonConverterFactory  
✅ **DTOs Completos:** Todos los DTOs incluyen:
   - Valores por defecto apropiados
   - Timestamps automáticos
   - Campos opcionales donde corresponde

✅ **Documentación:** JavaDoc completo en todas las interfaces  
✅ **Singleton Pattern:** RetrofitInstance garantiza instancias únicas  
✅ **Lazy Initialization:** Las instancias de Retrofit se crean solo cuando se usan  

---

## 📦 DTOs Generados

### SignosVitalesDto
```kotlin
data class SignosVitalesDto(
    val id: String? = null,
    val pacienteId: String,
    val frecuenciaCardiaca: Int? = null,
    val presionArterialSistolica: Int? = null,
    val presionArterialDiastolica: Int? = null,
    val saturacionOxigeno: Int? = null,
    val temperatura: Double? = null,
    val notas: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
```

### UbicacionDto
```kotlin
data class UbicacionDto(
    val id: String? = null,
    val pacienteId: String,
    val latitud: Double,
    val longitud: Double,
    val direccion: String? = null,
    val ciudad: String? = null,
    val pais: String? = null,
    val precision: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
)
```

### AlertaDto
```kotlin
data class AlertaDto(
    val id: String? = null,
    val pacienteId: String,
    val titulo: String,
    val mensaje: String,
    val severidad: String,
    val tipo: String,
    val leida: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val idRelacionado: String? = null
)
```

---

## 🚀 Uso Rápido

```kotlin
// Obtener la interfaz
val vitalesApi = RetrofitInstance.getVitalesApi()

// Usar en una corrutina
viewModelScope.launch {
    try {
        val signos = vitalesApi.getAllVitales()
        // Procesar datos
    } catch (e: Exception) {
        // Manejar error
    }
}
```

---

## 📍 URLs Base Configuradas

| Servicio | Base URL |
|----------|----------|
| Vitales | `http://10.0.2.2:8081/` |
| Ubicación | `http://10.0.2.2:8082/` |
| Alertas | `http://10.0.2.2:8083/` |
| Weather | `https://api.openweathermap.org/` |

---

## ✅ Dependencias Agregadas

Se agregaron al `build.gradle.kts`:
- `com.squareup.retrofit2:retrofit:2.11.0`
- `com.squareup.retrofit2:converter-gson:2.11.0`
- `com.squareup.okhttp3:okhttp:4.11.0`
- `com.squareup.okhttp3:logging-interceptor:4.11.0`

---

## 📚 Próximos Pasos Recomendados

1. Implementar Repositories que usen estas interfaces
2. Crear ViewModels que consuman los Repositories
3. Agregar interceptores de OAuth/Token si es necesario
4. Implementar caché local con Room para datos offline
5. Agregar logging con OkHttp Logging Interceptor en debug

Para más información, consultar los archivos de documentación en la carpeta `api/`.

