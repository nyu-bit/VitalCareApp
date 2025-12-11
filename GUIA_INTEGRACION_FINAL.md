# 📱 GUÍA DE INTEGRACIÓN FINAL - AlertasScreen

## ⚡ INICIO RÁPIDO

### Opción 1: Uso más simple
```kotlin
@Composable
fun MiPantalla() {
    val viewModel: AlertsViewModel = viewModel()
    AlertasScreen(viewModel = viewModel)
}
```

### Opción 2: Con navegación
```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController, startDestination = "home") {
        composable("alertas") {
            val viewModel: AlertsViewModel = viewModel()
            AlertasScreen(viewModel = viewModel)
        }
    }
}
```

### Opción 3: Con parámetros
```kotlin
composable(
    route = "alertas/{pacienteId}",
    arguments = listOf(navArgument("pacienteId") { type = NavType.StringType })
) { backStackEntry ->
    val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: "user123"
    val viewModel: AlertsViewModel = viewModel()
    
    LaunchedEffect(pacienteId) {
        viewModel.loadAlerts(pacienteId)
    }
    
    AlertasScreen(viewModel = viewModel)
}
```

---

## 🎮 INTERACCIÓN DEL USUARIO

### Cargar Alertas
```
1. Al abrir pantalla → loadAlerts() automáticamente
2. O manualmente → viewModel.refresh()
```

### Crear Alerta
```
1. Click en botón FAB o ícono + en TopAppBar
2. Se abre formulario con AnimatedVisibility
3. Llenar: Título, Mensaje, Severidad, Tipo
4. Click "Crear" → createAlerta() con validación
5. Nueva alerta aparece en lista
```

### Marcar como Atendida
```
1. Click botón "Atendida" en tarjeta
2. Se actualiza en el servidor (PUT /alertas/{id})
3. Botón se convierte en Chip deshabilitado
4. Alerta se tacha/marca como leída
```

### Eliminar Alerta
```
1. Click ícono papelera roja en tarjeta
2. Llamada DELETE /alertas/{id}
3. Alerta se remueve de la lista
```

### Filtrar
```
1. Los filtros aplican en tiempo real
2. Sin necesidad de re-query al servidor
3. Usa propiedades computadas de estado
```

---

## 🔧 CONFIGURACIÓN AVANZADA

### Cambiar Paciente Dinámicamente
```kotlin
val viewModel: AlertsViewModel = viewModel()
var currentPacienteId by remember { mutableStateOf("user123") }

LaunchedEffect(currentPacienteId) {
    viewModel.loadAlerts(currentPacienteId)
}

AlertasScreen(viewModel = viewModel)

// Cambiar paciente
currentPacienteId = "paciente456"  // Recarga automáticamente
```

### Manejo Manual de Errores
```kotlin
val uiState by viewModel.uiState.collectAsState()

if (uiState.error != null) {
    // Tu lógica de error personalizada
    ShowCustomErrorDialog(
        message = uiState.error!!,
        onRetry = { viewModel.refresh() },
        onDismiss = { viewModel.clearError() }
    )
}
```

### Acceder a Estadísticas
```kotlin
val uiState by viewModel.uiState.collectAsState()

Text("Alertas no leídas: ${uiState.unreadCount}")
Text("Críticas: ${uiState.criticalCount}")
Text("Altas: ${uiState.highCount}")
Text("Vitales: ${uiState.vitalSignsCount}")
```

---

## 🎨 CUSTOMIZACIÓN

### Cambiar Colores de Severidad
En `AlertCard()`, modifica:
```kotlin
val backgroundColor = when (alert.severity) {
    "Crítico" -> Color(0xFFFFEBEE)  // Cambiar aquí
    "Alto" -> Color(0xFFFFE0B2)
    "Medio" -> Color(0xFFFFF9C4)
    else -> Color(0xFFE8F5E9)
}
```

### Cambiar Idioma
En formulario y estados, reemplaza strings:
```kotlin
"Nueva Alerta" → tu idioma
"Título" → tu idioma
"Crear" → tu idioma
etc.
```

### Agregar Más Campos
En `CreateAlertForm()`:
```kotlin
var nuevoCampo by remember { mutableStateOf("") }

TextField(
    value = nuevocampo,
    onValueChange = { nuevocampo = it },
    label = { Text("Mi Campo") },
    // ...
)

// Al crear:
viewModel.createAlerta(
    pacienteId = "user123",
    titulo = titulo,
    // ... 
    // Pasar campo adicional si la API lo soporta
)
```

---

## 🧪 TESTING

### Test Básico
```kotlin
@Test
fun testLoadAlerts() {
    val viewModel = AlertsViewModel()
    
    // Simular carga
    viewModel.loadAlerts("test-patient")
    
    // Esperar corrutinas
    runBlocking {
        delay(1000)
    }
    
    // Verificar estado
    val state = viewModel.uiState.value
    assertEquals(false, state.isLoading)
}
```

### Test de Creación
```kotlin
@Test
fun testCreateAlerta() {
    val viewModel = AlertsViewModel()
    val initialCount = viewModel.uiState.value.allAlerts.size
    
    viewModel.createAlerta(
        pacienteId = "test",
        titulo = "Test",
        mensaje = "Test message",
        severidad = "Medio",
        tipo = "Sistema"
    )
    
    runBlocking { delay(1000) }
    
    val finalCount = viewModel.uiState.value.allAlerts.size
    assertTrue(finalCount > initialCount)
}
```

---

## 🐛 DEBUGGING

### Ver Estado Actual
```kotlin
val uiState by viewModel.uiState.collectAsState()
Log.d("Alertas", "State: $uiState")
Log.d("Alertas", "Loading: ${uiState.isLoading}")
Log.d("Alertas", "Error: ${uiState.error}")
Log.d("Alertas", "Count: ${uiState.allAlerts.size}")
```

### Habilitar Logs Detallados
```kotlin
// En ErrorHandler
fun logError(...) {
    Log.e(tag, message)
    // Agregar
    if (BuildConfig.DEBUG) {
        Log.d("DEBUG", "Full trace: ${throwable?.stackTraceToString()}")
    }
}
```

---

## 📡 VERIFICAR CONEXIÓN

### Test de API
```kotlin
// En algún composable o actividad
LaunchedEffect(Unit) {
    val alertasApi = RetrofitInstance.getAlertasApi()
    try {
        val alertas = alertasApi.getAllAlertas()
        Log.d("API", "Conexión exitosa: ${alertas.size} alertas")
    } catch (e: Exception) {
        Log.e("API", "Error: ${e.message}")
    }
}
```

### Verificar Configuración
```kotlin
// Asegurar que en RetrofitInstance:
// - baseUrl = "http://10.0.2.2:8083/"
// - GsonConverterFactory activado
// - Timeouts configurados
```

---

## ⚠️ SOLUCIÓN DE PROBLEMAS

### Problema: No aparecen alertas
**Solución:**
1. Verificar servidor está corriendo en `http://10.0.2.2:8083/`
2. Verificar `pacienteId` es correcto
3. Revisar logs: `viewModel.uiState.value.error`
4. Llamar `viewModel.refresh()` manualmente

### Problema: Crear alerta no funciona
**Solución:**
1. Verificar formulario tiene todos los campos
2. Validar `pacienteId` en el formulario
3. Revisar respuesta del servidor
4. Verificar endpoint POST está disponible

### Problema: Actualizar no funciona
**Solución:**
1. Verificar alerta tiene `id` válido
2. Revisar permisos en servidor
3. Verificar endpoint PUT: `/alertas/{id}`
4. Revisar status HTTP en logs

### Problema: Compilación falla
**Solución:**
1. `./gradlew clean`
2. Invalidar cache: File → Invalidate Caches
3. Compilar nuevamente
4. Verificar imports están correctos

---

## 🚀 DEPLOYMENT

### Antes de Producción
- [ ] Cambiar baseUrl a servidor real (no 10.0.2.2)
- [ ] Agregar SSL/TLS
- [ ] Aumentar timeouts para red lenta
- [ ] Agregar retry logic
- [ ] Customizar mensajes de error
- [ ] Testear en dispositivo real
- [ ] Revisar permisos de red en AndroidManifest.xml

### En AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### En Network Security Config
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">your-production-domain.com</domain>
    </domain-config>
</network-security-config>
```

---

## 📈 OPTIMIZACIONES

### Caché de Alertas
```kotlin
// Agregar en Repository si necesario
private val cachedAlerts = mutableMapOf<String, List<AlertaDto>>()

suspend fun getByPacienteWithCache(pacienteId: String): Result<List<AlertaDto>> {
    return cachedAlerts[pacienteId]?.let { Result.success(it) }
        ?: getByPaciente(pacienteId).onSuccess { 
            cachedAlerts[pacienteId] = it 
        }
}
```

### Paginación (si hay muchas alertas)
```kotlin
// Modificar en API
@GET("alertas/paciente/{id}")
suspend fun getAlertasByPaciente(
    @Path("id") pacienteId: String,
    @Query("page") page: Int = 0,
    @Query("size") size: Int = 20
): List<AlertaDto>
```

---

## 📚 REFERENCIAS

- Material Design 3: https://m3.material.io/
- Compose: https://developer.android.com/jetpack/compose
- Retrofit: https://square.github.io/retrofit/
- Coroutines: https://kotlinlang.org/docs/coroutines-overview.html

---

**¡Listo para usar!** ✅ Copia el código en tu proyecto y disfruta.

