# 🚀 EJEMPLOS AVANZADOS - Navegación

## Ejemplo 1: Navegar desde una pantalla

```kotlin
// En DashboardScreen.kt
@Composable
fun DashboardScreen(
    onNavigateToAlertas: () -> Unit,
    onNavigateToVitales: () -> Unit,
    onNavigateToUbicacion: () -> Unit
) {
    Column {
        // ... contenido ...
        
        Button(onClick = onNavigateToAlertas) {
            Text("Ir a Alertas")
        }
        
        Button(onClick = onNavigateToVitales) {
            Text("Ver mis Vitales")
        }
    }
}
```

## Ejemplo 2: Detectar ruta actual y cambiar UI

```kotlin
@Composable
fun MiPantalla(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Cambiar comportamiento según ruta actual
    when (currentRoute) {
        "dashboard" -> {
            Text("Estamos en Dashboard")
        }
        "alertas" -> {
            Text("Estamos en Alertas")
        }
        else -> {
            Text("Ruta desconocida")
        }
    }
}
```

## Ejemplo 3: Navegar con argumentos (futuro)

```kotlin
// En NavHost:
composable(
    route = "detalle/{id}",
    arguments = listOf(navArgument("id") { type = NavType.StringType })
) { backStackEntry ->
    val id = backStackEntry.arguments?.getString("id")
    DetalleScreen(id = id)
}

// Navegar:
navController.navigate("detalle/123")
```

## Ejemplo 4: Condicional basado en estado

```kotlin
@Composable
fun MiBoton(navController: NavHostController, mostrarAlertas: Boolean) {
    Button(
        onClick = {
            if (mostrarAlertas) {
                navController.navigate("alertas")
            } else {
                navController.navigate("dashboard")
            }
        }
    ) {
        Text("Navegar")
    }
}
```

## Ejemplo 5: Back stack con popUpTo

```kotlin
// Navegar y eliminar el dashboard del back stack
navController.navigate("alertas") {
    popUpTo("dashboard") {
        inclusive = true  // Elimina también dashboard
    }
}

// Después, presionar back NO vuelve a dashboard
```

## Ejemplo 6: LazyColumn con navegación

```kotlin
@Composable
fun ListaScreen(navController: NavHostController) {
    LazyColumn {
        items(100) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("detalle/$index")
                    }
            ) {
                Text("Item $index")
            }
        }
    }
}
```

## Ejemplo 7: Condición para mostrar BottomBar

```kotlin
@Composable
fun VitalCareNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // No mostrar BottomBar en login
    val mostrarBottomBar = currentRoute != "login"
    
    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // ... rutas ...
        }
        
        if (mostrarBottomBar) {
            VitalCareBottomNavigationBar(navController = navController)
        }
    }
}
```

## Ejemplo 8: Pasar ViewModel a través de navegación

```kotlin
@Composable
fun VitalCareNavigation() {
    val navController = rememberNavController()
    val alertsViewModel: AlertsViewModel = viewModel()  // ← Global
    
    NavHost(navController, "dashboard") {
        composable("alertas") {
            // Reutilizar el mismo ViewModel
            AlertasScreen(viewModel = alertsViewModel)
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToAlertas = { 
                    navController.navigate("alertas") 
                }
            )
        }
    }
}
```

## Ejemplo 9: Animaciones en transiciones

```kotlin
composable(
    route = "alertas",
    enterTransition = {
        slideInVertically(initialOffsetY = { 1000 })
    },
    exitTransition = {
        slideOutVertically(targetOffsetY = { 1000 })
    }
) {
    AlertasScreen(viewModel = viewModel())
}
```

## Ejemplo 10: Nested Navigation (SubNavHost)

```kotlin
// Para agrupar rutas relacionadas
fun NavGraphBuilder.dashboardGraph(navController: NavHostController) {
    navigation(
        startDestination = "dashboard_home",
        route = "dashboard"
    ) {
        composable("dashboard_home") {
            DashboardScreen()
        }
        
        composable("dashboard_settings") {
            SettingsScreen()
        }
    }
}

// En NavHost:
dashboardGraph(navController)
```

---

## 🎯 Patrones Recomendados

### Pattern 1: Single ViewModel Global
```kotlin
val alertsViewModel: AlertsViewModel = viewModel()

NavHost {
    composable("alertas") { AlertasScreen(alertsViewModel) }
    composable("dashboard") { 
        DashboardScreen(
            onNavigate = { navController.navigate("alertas") }
        )
    }
}
```

### Pattern 2: ViewModel por Ruta
```kotlin
composable("alertas") {
    val viewModel: AlertsViewModel = viewModel()
    AlertasScreen(viewModel)
}

composable("vitales") {
    val viewModel: VitalesViewModel = viewModel()
    VitalesScreen(viewModel)
}
```

### Pattern 3: Callbacks para Navegación
```kotlin
DashboardScreen(
    onNavigateToAlertas = { navController.navigate("alertas") },
    onNavigateToVitales = { navController.navigate("vitales") }
)
```

---

## 🧪 Tests de Navegación

```kotlin
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testNavigationToDashboard() {
        composeTestRule.setContent {
            VitalCareNavigation()
        }
        
        // Encontrar y hacer click en Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .performClick()
        
        // Verificar que estamos en Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsSelected()  // RequiereandroidX
    }
}
```

---

## ⚠️ Errores Comunes

### Error 1: navController no disponible
```kotlin
// ❌ Incorrecto
@Composable
fun MiScreen() {
    // navController no está disponible aquí
    navController.navigate("alertas")
}

// ✅ Correcto
@Composable
fun MiScreen(navController: NavHostController) {
    Button(onClick = { navController.navigate("alertas") })
}
```

### Error 2: Múltiples NavControllers
```kotlin
// ❌ Incorrecto - crear nuevo cada vez
@Composable
fun MyContent() {
    val navController = rememberNavController()  // Nuevo cada recomposición
}

// ✅ Correcto - crear una sola vez
@Composable
fun VitalCareNavigation() {
    val navController = rememberNavController()  // Una sola vez
    
    NavHost(navController, "dashboard") {
        // ...
    }
}
```

### Error 3: Back stack infinito
```kotlin
// ❌ Incorrecto - agrega infinitas veces
Button(onClick = { navController.navigate("alertas") })

// ✅ Correcto - reemplaza la pantalla
Button(onClick = { 
    navController.navigate("alertas") {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
    }
})
```

---

## 📚 Referencias

- Material Design Navigation: https://m3.material.io/
- Compose Navigation: https://developer.android.com/jetpack/compose/navigation
- NavController: https://developer.android.com/reference/androidx/navigation/NavController

---

**¡Utiliza estos ejemplos para potenciar tu navegación!** 🚀


