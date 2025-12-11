# 🎬 VIEWMODELS - DOCUMENTACIÓN COMPLETA

## 📱 Overview

Se han generado **4 ViewModels** que consumen los Repositorios Remotos con gestión de estado completa usando `MutableStateFlow` y `viewModelScope`.

---

## 🏥 VitalesViewModel

**Ubicación:** `data/repository/ViewModels.kt`

### Estados Disponibles

```kotlin
// Estados independientes para mayor flexibilidad
val vitales: StateFlow<List<SignosVitalesDto>>      // Lista de vitales
val loading: StateFlow<Boolean>                      // Estado de carga
val error: StateFlow<String?>                        // Mensaje de error
```

### Métodos Disponibles

#### `loadVitales(): Unit`
Carga todos los signos vitales disponibles
```kotlin
val viewModel = VitalesViewModel()
viewModel.loadVitales()
// Actualiza: vitales, loading, error
```

#### `loadByPaciente(pacienteId: String): Unit`
Carga vitales de un paciente específico
```kotlin
viewModel.loadByPaciente("paciente123")
```

#### `createVital(signoVital: SignosVitalesDto): Unit`
Crea un nuevo registro de signos vitales
```kotlin
val nuevoVital = SignosVitalesDto(
    pacienteId = "pac123",
    frecuenciaCardiaca = 72,
    temperatura = 37.5
)
viewModel.createVital(nuevoVital)
// Se agrega automáticamente a la lista
```

#### `deleteVital(vitalesId: String): Unit`
Elimina un registro de vitales
```kotlin
viewModel.deleteVital("vital123")
// Se remueve automáticamente de la lista
```

#### `clearError(): Unit`
Limpia el mensaje de error
```kotlin
viewModel.clearError()
```

---

## 📍 UbicacionViewModel

**Ubicación:** `data/repository/ViewModels.kt`

### Estados Disponibles

```kotlin
val ubicaciones: StateFlow<List<UbicacionDto>>      // Lista de ubicaciones
val loading: StateFlow<Boolean>                      // Estado de carga
val error: StateFlow<String?>                        // Mensaje de error
```

### Métodos Disponibles

#### `loadUbicacion(): Unit`
Carga todas las ubicaciones disponibles
```kotlin
val viewModel = UbicacionViewModel()
viewModel.loadUbicacion()
```

#### `loadByPaciente(pacienteId: String): Unit`
Carga ubicaciones de un paciente específico
```kotlin
viewModel.loadByPaciente("paciente123")
```

#### `saveUbicacion(ubicacion: UbicacionDto): Unit`
Guarda una nueva ubicación
```kotlin
val nuevaUbicacion = UbicacionDto(
    pacienteId = "pac123",
    latitud = -33.8688,
    longitud = -71.5203,
    direccion = "Calle Principal 123"
)
viewModel.saveUbicacion(nuevaUbicacion)
// Se agrega automáticamente a la lista
```

#### `clearError(): Unit`
Limpia el mensaje de error
```kotlin
viewModel.clearError()
```

---

## 🚨 AlertasViewModel

**Ubicación:** `data/repository/ViewModels.kt`

### Estados Disponibles

```kotlin
val alertas: StateFlow<List<AlertaDto>>              // Lista de alertas
val loading: StateFlow<Boolean>                      // Estado de carga
val error: StateFlow<String?>                        // Mensaje de error
```

### Métodos Disponibles

#### `loadAlertas(): Unit`
Carga todas las alertas disponibles
```kotlin
val viewModel = AlertasViewModel()
viewModel.loadAlertas()
```

#### `loadByPaciente(pacienteId: String): Unit`
Carga alertas de un paciente específico
```kotlin
viewModel.loadByPaciente("paciente123")
```

#### `createAlerta(alerta: AlertaDto): Unit`
Crea una nueva alerta
```kotlin
val nuevaAlerta = AlertaDto(
    pacienteId = "pac123",
    titulo = "Presión Alta",
    mensaje = "Presión arterial elevada",
    severidad = "Alto",
    tipo = "Signos Vitales"
)
viewModel.createAlerta(nuevaAlerta)
// Se agrega automáticamente a la lista
```

#### `markAsAttended(alerta: AlertaDto): Unit`
Marca una alerta como atendida (leída)
```kotlin
viewModel.markAsAttended(alerta)
// Se actualiza en la lista automáticamente
```

#### `deleteAlerta(alertaId: String): Unit`
Elimina una alerta
```kotlin
viewModel.deleteAlerta("alerta123")
// Se remueve automáticamente de la lista
```

#### `clearError(): Unit`
Limpia el mensaje de error
```kotlin
viewModel.clearError()
```

---

## 👥 PacienteDataViewModel

**Ubicación:** `data/repository/ViewModels.kt`

### Estados Disponibles

```kotlin
val pacienteData: StateFlow<PacienteCompleteData?>   // Datos consolidados
val loading: StateFlow<Boolean>                       // Estado de carga
val error: StateFlow<String?>                         // Mensaje de error
```

### Métodos Disponibles

#### `loadPacienteData(pacienteId: String): Unit`
Carga datos consolidados de un paciente
```kotlin
val viewModel = PacienteDataViewModel()
viewModel.loadPacienteData("paciente123")
// Obtiene vitales, ubicaciones y alertas en paralelo
```

#### `clearPacienteData(): Unit`
Limpia los datos del paciente
```kotlin
viewModel.clearPacienteData()
```

#### `clearError(): Unit`
Limpia el mensaje de error
```kotlin
viewModel.clearError()
```

---

## 💡 USO EN COMPOSABLES

### Ejemplo 1: Cargar Vitales

```kotlin
@Composable
fun VitalesScreen() {
    val viewModel: VitalesViewModel = viewModel()
    
    // Recolectar estados
    val vitales by viewModel.vitales.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Cargar datos cuando se monta
    LaunchedEffect(Unit) {
        viewModel.loadVitales()
    }
    
    // Mostrar UI según el estado
    when {
        loading -> CircularProgressIndicator()
        error != null -> Text("Error: $error")
        else -> {
            LazyColumn {
                items(vitales) { vital ->
                    VitalCard(vital)
                }
            }
        }
    }
}
```

### Ejemplo 2: Crear Vital

```kotlin
@Composable
fun CreateVitalScreen(pacienteId: String) {
    val viewModel: VitalesViewModel = viewModel()
    var frecuencia by remember { mutableStateOf("") }
    
    Column {
        OutlinedTextField(
            value = frecuencia,
            onValueChange = { frecuencia = it },
            label = { Text("Frecuencia Cardíaca") }
        )
        
        Button(onClick = {
            val vital = SignosVitalesDto(
                pacienteId = pacienteId,
                frecuenciaCardiaca = frecuencia.toIntOrNull()
            )
            viewModel.createVital(vital)
        }) {
            Text("Guardar")
        }
    }
}
```

### Ejemplo 3: Alertas por Paciente

```kotlin
@Composable
fun AlertasPacienteScreen(pacienteId: String) {
    val viewModel: AlertasViewModel = viewModel()
    
    val alertas by viewModel.alertas.collectAsState()
    val loading by viewModel.loading.collectAsState()
    
    LaunchedEffect(pacienteId) {
        viewModel.loadByPaciente(pacienteId)
    }
    
    if (loading) {
        CircularProgressIndicator()
    } else {
        LazyColumn {
            items(alertas) { alerta ->
                AlertaItem(
                    alerta = alerta,
                    onMark = { viewModel.markAsAttended(alerta) },
                    onDelete = { viewModel.deleteAlerta(alerta.id ?: "") }
                )
            }
        }
    }
}
```

### Ejemplo 4: Datos Consolidados

```kotlin
@Composable
fun PacienteCompleteScreen(pacienteId: String) {
    val viewModel: PacienteDataViewModel = viewModel()
    
    val data by viewModel.pacienteData.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(pacienteId) {
        viewModel.loadPacienteData(pacienteId)
    }
    
    when {
        loading -> CircularProgressIndicator()
        error != null -> Text("Error: $error")
        data != null -> {
            Column {
                Text("Vitales: ${data!!.vitales.size}")
                Text("Ubicaciones: ${data!!.ubicaciones.size}")
                Text("Alertas: ${data!!.alertas.size}")
            }
        }
    }
}
```

---

## 🏗️ ARQUITECTURA

```
Composable
    ↓ (collectAsState())
StateFlow<T>
    ↓ (viewModel.metodo())
ViewModel
    ↓ (viewModelScope.launch)
Repository
    ↓ (getByPaciente(), etc)
API (Retrofit)
    ↓
Microservicio
```

---

## 📊 MANEJO DE ESTADO

Cada ViewModel mantiene 3 estados independientes:

```
┌─────────────────────────────────────────┐
│ vitales: StateFlow<List<SignosVitalesDto>> │ → Datos principales
├─────────────────────────────────────────┤
│ loading: StateFlow<Boolean>              │ → Indicador de carga
├─────────────────────────────────────────┤
│ error: StateFlow<String?>                │ → Mensajes de error
└─────────────────────────────────────────┘
```

### Ventajas

✅ Más granular y flexible que Sealed Classes  
✅ Fácil de observar independientemente  
✅ Mejor para UI reactive  
✅ Simplifica la lógica de composables  

---

## 🔄 FLUJO TÍPICO

```
1. Usuario abre pantalla
   ↓
2. LaunchedEffect carga datos: viewModel.loadVitales()
   ↓
3. ViewModel actualiza loading = true
   ↓
4. Composable muestra CircularProgressIndicator
   ↓
5. Repository llama a API
   ↓
6. ViewModel actualiza vitales con datos
   ↓
7. ViewModel actualiza loading = false
   ↓
8. Composable re-compone y muestra datos
```

---

## 🛡️ VALIDACIONES

Todos los métodos incluyen validaciones de entrada:

```kotlin
if (pacienteId.isBlank()) {
    _error.value = "ID de paciente inválido"
    return
}
```

---

## 🎯 MEJORES PRÁCTICAS

✅ **Validar entrada** antes de llamar repository  
✅ **Actualizar lista** en lugar de recargar  
✅ **Limpiar error** cuando sea necesario  
✅ **Usar LaunchedEffect** para cargas iniciales  
✅ **Observar estados** con collectAsState()  

---

## 📝 EJEMPLO COMPLETO

```kotlin
@Composable
fun VitalesApp() {
    val viewModel: VitalesViewModel = viewModel()
    val vitales by viewModel.vitales.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadVitales()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Text("Signos Vitales", style = MaterialTheme.typography.headlineMedium)
        
        // Error
        error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
            Button(onClick = { viewModel.clearError() }) {
                Text("Descartar")
            }
        }
        
        // Loading
        if (loading) {
            CircularProgressIndicator()
        } else {
            // Content
            LazyColumn {
                items(vitales) { vital ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${vital.frecuenciaCardiaca} bpm")
                            Button(onClick = {
                                viewModel.deleteVital(vital.id ?: "")
                            }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
```

---

**Generado:** Diciembre 2025  
**Estado:** ✅ COMPLETO  
**Próximo:** Integración en Composables

