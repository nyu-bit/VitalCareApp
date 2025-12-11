# 💡 EJEMPLO COMPLETO - AlertasScreen en tu App

## Escenario: Integrar AlertasScreen en MainActivity

### 1. Estructura de Navegación

**File: `ui/navigation/AppNavigation.kt`**

```kotlin
package cl.duoc.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.app.ui.alerts.AlertasScreen
import cl.duoc.app.ui.alerts.AlertsViewModel
import cl.duoc.app.ui.home.HomeScreen
import cl.duoc.app.ui.home.HomeViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Pantalla de inicio
        composable(route = "home") {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAlertas = { pacienteId ->
                    navController.navigate("alertas/$pacienteId")
                }
            )
        }

        // Pantalla de alertas
        composable(
            route = "alertas/{pacienteId}",
            arguments = listOf(
                navArgument("pacienteId") { 
                    type = NavType.StringType 
                    defaultValue = "user123"
                }
            )
        ) { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: "user123"
            val viewModel: AlertsViewModel = viewModel()
            
            AlertasScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
```

### 2. MainActivity Actualizada

**File: `MainActivity.kt`**

```kotlin
package cl.duoc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

import cl.duoc.app.ui.navigation.AppNavigation
import cl.duoc.app.ui.theme.VitalCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VitalCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VitalCareApp()
                }
            }
        }
    }
}

@Composable
fun VitalCareApp() {
    val navController = rememberNavController()
    
    AppNavigation(navController = navController)
}
```

### 3. HomeScreen que Navega a Alertas

**File: `ui/home/HomeScreen.kt`** (Fragmento)

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAlertas: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Card para ir a Alertas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNavigateToAlertas("paciente123")
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Alertas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Ver todas tus alertas",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Alertas",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

---

## 📊 Caso de Uso Real: Ciclo Completo

### Escenario
Usuario abre la app, ve dashboard, navega a alertas, crea una alerta, la marca como atendida y la elimina.

### Flujo

```
MainActivity 
    ↓
setContent { VitalCareApp() }
    ↓
AppNavigation { NavHost ... }
    ↓
HomeScreen (ruta: "home")
    ↓ [Click en Card Alertas]
AlertasScreen (ruta: "alertas/paciente123")
    ↓
AlertsViewModel.loadAlerts("paciente123")
    ↓
AlertasRepository.getByPaciente("paciente123")
    ↓
AlertasApi.getAlertasByPaciente("paciente123")
    ↓ [GET /alertas/paciente/paciente123]
Response: List<AlertaDto>
    ↓
Mostrar lista en LazyColumn
    ↓ [Click FAB + para crear]
CreateAlertForm se abre
    ↓ [Llenar y hacer click "Crear"]
AlertsViewModel.createAlerta(...)
    ↓
AlertasRepository.createAlerta(AlertaDto)
    ↓
AlertasApi.createAlerta(AlertaDto)
    ↓ [POST /alertas]
Response: AlertaDto creada
    ↓
Nueva alerta aparece en lista
    ↓ [Click botón "Atendida"]
AlertsViewModel.markAsAttended(alertId)
    ↓
AlertasRepository.markAsAttended(AlertaDto)
    ↓
AlertasApi.updateAlerta(id, AlertaDto)
    ↓ [PUT /alertas/{id}]
Response: AlertaDto actualizada
    ↓
Botón cambia a Chip deshabilitado
    ↓ [Click ícono papelera]
AlertsViewModel.deleteAlerta(alertId)
    ↓
AlertasRepository.deleteAlerta(alertId)
    ↓
AlertasApi.deleteAlerta(alertId)
    ↓ [DELETE /alertas/{id}]
Response: Void
    ↓
Alerta se elimina de la lista
```

---

## 🔄 Estados Durante la Ejecución

### 1. Carga Inicial
```
uiState = AlertsUiState(
    isLoading = true,
    allAlerts = [],
    error = null
)
↓
Muestra: LoadingState con CircularProgressIndicator
```

### 2. Carga Exitosa
```
uiState = AlertsUiState(
    isLoading = false,
    allAlerts = [Alert1, Alert2, Alert3, ...],
    error = null
)
↓
Muestra: AlertsList con LazyColumn
```

### 3. Si hay Error
```
uiState = AlertsUiState(
    isLoading = false,
    allAlerts = [],
    error = "Error de conexión: timeout"
)
↓
Muestra: ErrorState con botón Reintentar
```

### 4. Si está vacío
```
uiState = AlertsUiState(
    isLoading = false,
    allAlerts = [],
    error = null
)
↓
Muestra: EmptyState
```

---

## 🎯 Interacciones Principales

### Crear Alerta
```kotlin
// Usuario abre formulario
showCreateForm = true  // Estado local en AlertasScreen

// Usuario llena datos y hace click "Crear"
viewModel.createAlerta(
    pacienteId = "user123",
    titulo = "Presión Elevada",
    mensaje = "Tu presión está en 145/95",
    severidad = "Alto",
    tipo = "Signos Vitales"
)

// ViewModel actualiza estado
_uiState.update { state ->
    state.copy(
        isLoading = false,
        allAlerts = state.allAlerts + newAlert,
        error = null
    )
}

// Composable recibe nuevo estado
// LazyColumn se re-renderiza con nueva alerta
```

### Marcar como Atendida
```kotlin
// Usuario hace click en botón "Atendida"
viewModel.markAsAttended("alert-id-123")

// ViewModel busca alerta
val alertToUpdate = _uiState.value.allAlerts.find { 
    it.id == "alert-id-123" 
}

// Crea DTO con leida = true
val alertaDto = AlertaDto(
    id = "alert-id-123",
    // ... otros campos
    leida = true  // ← Cambio importante
)

// Envía al servidor (PUT)
alertasRepository.markAsAttended(alertaDto)

// Actualiza lista local
_uiState.update { state ->
    val updated = state.allAlerts.map { alert ->
        if (alert.id == "alert-id-123") 
            alert.copy(isRead = true)  // ← Marca como leída
        else 
            alert
    }
    state.copy(allAlerts = updated)
}

// Composable muestra cambio
// Botón "Atendida" → Chip deshabilitado
```

### Eliminar Alerta
```kotlin
// Usuario hace click en papelera
viewModel.deleteAlerta("alert-id-123")

// Repository llama API
alertasRepository.deleteAlerta("alert-id-123")

// API: DELETE /alertas/alert-id-123

// ViewModel filtra la alerta
_uiState.update { state ->
    val updated = state.allAlerts.filter { 
        it.id != "alert-id-123"  // ← Elimina
    }
    state.copy(allAlerts = updated)
}

// LazyColumn se re-renderiza sin esa alerta
```

---

## 🧪 Testing el Flujo Completo

```kotlin
@Test
fun testAletasScreenCompleteFlow() {
    // Setup
    val viewModel = AlertsViewModel()
    
    // 1. Cargar alertas
    viewModel.loadAlerts("test-patient")
    advanceUntilIdle()
    assert(viewModel.uiState.value.isLoading == false)
    
    val initialCount = viewModel.uiState.value.allAlerts.size
    
    // 2. Crear alerta
    viewModel.createAlerta(
        pacienteId = "test-patient",
        titulo = "Test Alert",
        mensaje = "Test message",
        severidad = "Medio",
        tipo = "Sistema"
    )
    advanceUntilIdle()
    
    val afterCreate = viewModel.uiState.value.allAlerts.size
    assertEquals(initialCount + 1, afterCreate)
    
    // 3. Marcar como atendida
    val alertId = viewModel.uiState.value.allAlerts.first().id
    viewModel.markAsAttended(alertId)
    advanceUntilIdle()
    
    val updated = viewModel.uiState.value.allAlerts.find { 
        it.id == alertId 
    }
    assertTrue(updated?.isRead == true)
    
    // 4. Eliminar
    viewModel.deleteAlerta(alertId)
    advanceUntilIdle()
    
    val afterDelete = viewModel.uiState.value.allAlerts.size
    assertEquals(afterCreate - 1, afterDelete)
}
```

---

## 📈 Monitoreo en Producción

```kotlin
// Agregar en ViewModel para logging
private fun logState(context: String) {
    val state = _uiState.value
    Log.d("AlertsViewModel", "$context: " +
        "loading=${state.isLoading}, " +
        "count=${state.allAlerts.size}, " +
        "error=${state.error}"
    )
}

// Llamar en cada operación
fun loadAlerts(...) {
    // ...
    logState("loadAlerts")
}
```

---

## ✅ Checklist de Integración

- [ ] Importar AlertasScreen en AppNavigation
- [ ] Importar AlertsViewModel
- [ ] Agregar ruta en NavHost
- [ ] Agregar navegación desde HomeScreen
- [ ] Verificar RetrofitInstance tiene baseUrl correcto
- [ ] Verificar servidor está corriendo
- [ ] Compilar sin errores
- [ ] Probar en dispositivo/emulador
- [ ] Verificar cada operación CRUD
- [ ] Revisar logs de Logcat

---

**¡Listo!** Ahora tu app tiene una pantalla de alertas completamente funcional. 🎉


