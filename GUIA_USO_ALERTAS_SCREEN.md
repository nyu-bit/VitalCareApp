## 🎬 GUÍA DE USO - AlertasScreen

### 1. Uso Básico en Navigation

```kotlin
// En tu AppNavigation.kt
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController, startDestination = "alertas") {
        composable("alertas") {
            val alertsViewModel: AlertsViewModel = viewModel()
            AlertasScreen(viewModel = alertsViewModel)
        }
    }
}
```

### 2. Uso con Argumentos de Navegación

```kotlin
composable(
    "alertas/{pacienteId}",
    arguments = listOf(navArgument("pacienteId") { type = NavType.StringType })
) { backStackEntry ->
    val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: "user123"
    val alertsViewModel: AlertsViewModel = viewModel()
    
    LaunchedEffect(pacienteId) {
        alertsViewModel.loadAlerts(pacienteId)
    }
    
    AlertasScreen(viewModel = alertsViewModel)
}
```

### 3. Casos de Uso de las Funciones

#### Cargar alertas de un paciente
```kotlin
val viewModel: AlertsViewModel = viewModel()
LaunchedEffect(Unit) {
    viewModel.loadAlerts("paciente123")
}
```

#### Crear una nueva alerta
```kotlin
viewModel.createAlerta(
    pacienteId = "paciente123",
    titulo = "Presión Elevada",
    mensaje = "Tu presión está en 145/95 mmHg",
    severidad = "Alto",
    tipo = "Signos Vitales"
)
```

#### Marcar alerta como atendida
```kotlin
viewModel.markAsAttended("alertaId123")
```

#### Eliminar alerta
```kotlin
viewModel.deleteAlerta("alertaId123")
```

#### Filtrar alertas
```kotlin
// Por severidad
viewModel.filterBySeverity("Alto")

// Por tipo
viewModel.filterByType("Signos Vitales")

// Solo no leídas
viewModel.filterByReadStatus(showOnlyUnread = true)

// Limpiar filtros
viewModel.clearFilters()
```

#### Refrescar datos
```kotlin
viewModel.refresh("paciente123")
```

### 4. Acceder al Estado

```kotlin
val uiState by viewModel.uiState.collectAsState()

// Verificar si está cargando
if (uiState.isLoading) { }

// Acceder a las alertas filtradas
uiState.filteredAlerts.forEach { alert ->
    println("${alert.title}: ${alert.severity}")
}

// Contador de alertas no leídas
val unread = uiState.unreadCount

// Contar por severidad
val critical = uiState.criticalCount
val high = uiState.highCount
```

### 5. Integración en MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VitalCareApp()
        }
    }
}

@Composable
fun VitalCareApp() {
    val alertsViewModel: AlertsViewModel = viewModel()
    
    Column {
        // Otros componentes...
        AlertasScreen(viewModel = alertsViewModel)
    }
}
```

### 6. Combinación con otras Screens

```kotlin
@Composable
fun DashboardScreen() {
    val vitalesViewModel: VitalesViewModel = viewModel()
    val alertsViewModel: AlertsViewModel = viewModel()
    
    Column {
        Text("Dashboard")
        
        // Sección de alertas
        AlertasScreen(viewModel = alertsViewModel)
        
        Divider()
        
        // Otras secciones...
    }
}
```

### 7. Manejo de Errores

```kotlin
val uiState by viewModel.uiState.collectAsState()

// El error aparece automáticamente en ErrorState
// Pero si quieres manejarlo:
if (uiState.error != null) {
    // ErrorState ya lo maneja, pero puedes hacer custom:
    AlertDialog(
        onDismissRequest = { viewModel.clearError() },
        title = { Text("Error") },
        text = { Text(uiState.error!!) },
        confirmButton = {
            Button(onClick = { viewModel.refresh() }) {
                Text("Reintentar")
            }
        }
    )
}
```

### 8. Testing

```kotlin
@Test
fun testLoadAlerts() {
    val viewModel = AlertsViewModel()
    viewModel.loadAlerts("paciente123")
    
    // Esperar a que se carguen
    advanceUntilIdle()
    
    assertEquals(false, viewModel.uiState.value.isLoading)
}

@Test
fun testCreateAlerta() {
    val viewModel = AlertsViewModel()
    viewModel.createAlerta(
        pacienteId = "paciente123",
        titulo = "Test",
        mensaje = "Test message",
        severidad = "Medio",
        tipo = "Sistema"
    )
    
    advanceUntilIdle()
    assertTrue(viewModel.uiState.value.allAlerts.isNotEmpty())
}
```

### 9. Estados Disponibles

```kotlin
// Estado de carga
when {
    uiState.isLoading -> {
        // Mostrar CircularProgressIndicator
    }
    // Error
    uiState.error != null -> {
        // Mostrar ErrorState con opción de reintentar
    }
    // Lista vacía
    uiState.filteredAlerts.isEmpty() -> {
        // Mostrar EmptyState
    }
    // Mostrar datos
    else -> {
        // Mostrar AlertsList con LazyColumn
    }
}
```

### 10. Severidades y Tipos Válidos

```kotlin
// Severidades válidas:
"Crítico"     // Rojo
"Alto"        // Naranja
"Medio"       // Amarillo
"Bajo"        // Verde

// Tipos válidos:
"Signos Vitales"
"Medicamento"
"Cita"
"Sistema"
```

---

## 📱 Preview en Compose

```kotlin
@Preview(showBackground = true)
@Composable
fun AlertasScreenPreview() {
    val viewModel = AlertsViewModel()
    AlertasScreen(viewModel = viewModel)
}
```

---

## ⚙️ Configuración de API

Asegúrate de que tu `RetrofitInstance` tenga:

```kotlin
object RetrofitInstance {
    fun getAlertasApi(): AlertasApi {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8083/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlertasApi::class.java)
    }
}
```

---

## 🔍 Debugging

```kotlin
// Agregar logging en AlertsViewModel
private fun logState() {
    val state = _uiState.value
    Log.d("AlertsViewModel", "Loading: ${state.isLoading}")
    Log.d("AlertsViewModel", "Alerts: ${state.allAlerts.size}")
    Log.d("AlertsViewModel", "Error: ${state.error}")
}
```

---

## 📞 Soporte

Para más información sobre cada componente, ver:
- `AlertasScreen.kt` - Implementación completa
- `AlertsViewModel.kt` - Lógica de negocio
- `AlertasApi.kt` - Interfaz Retrofit

