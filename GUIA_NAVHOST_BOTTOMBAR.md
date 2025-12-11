# 📱 GUÍA - NavHost con BottomNavigationBar

## ✅ Lo que hemos generado

### Estructura de Navegación
```
VitalCareNavigation()
├── NavHost (5 rutas)
│   ├── dashboard
│   ├── vitales
│   ├── ubicacion
│   ├── alertas
│   └── clima
└── BottomNavigationBar (5 items)
```

### Archivos Creados
- ✅ **VitalCareNavigation.kt** - NavHost completo
- ✅ **MainActivity.kt** - Actualizado

---

## 🎯 Rutas Disponibles

### 1. Dashboard (Inicio)
```
Ruta: "dashboard"
Pantalla: DashboardScreen()
Ícono: Dashboard
```

### 2. Vitales
```
Ruta: "vitales"
Pantalla: VitalesScreen()
Ícono: Favorite (corazón)
```

### 3. Ubicación
```
Ruta: "ubicacion"
Pantalla: UbicacionScreenPlaceholder() [reemplazar con UbicacionScreen]
Ícono: LocationOn (pin)
```

### 4. Alertas
```
Ruta: "alertas"
Pantalla: AlertasScreen()
Ícono: Notifications (campana)
```

### 5. Clima
```
Ruta: "clima"
Pantalla: ClimaScreenPlaceholder() [reemplazar con ClimaScreen]
Ícono: Cloud (nube)
```

---

## 📊 BottomNavigationBar Profesional

### Material 3 Completo
- ✅ Material3 NavigationBar
- ✅ Material3 NavigationBarItem
- ✅ Colores dinámicos según tema
- ✅ Animaciones suaves
- ✅ Elevation y tonalElevation

### Características
- ✅ 5 items principales
- ✅ Íconos descriptivos
- ✅ Labels para cada item
- ✅ Selección visual clara
- ✅ Altura optimizada (80.dp)

### Estados
- ✅ **Selected**: Ícono y texto en color primary
- ✅ **Unselected**: Ícono y texto en gris
- ✅ **Transiciones** suaves entre estados

---

## 🔄 Cómo Funciona la Navegación

### Navegación Automática
```kotlin
// Hacer click en item → Navega automáticamente
NavigationBarItem(
    onClick = { navigateTo(navController, "alertas") }
)
```

### Preservación de Estado
```kotlin
// Al navegar, guarda el estado
navController.navigate(route) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true  // ← Guarda estado
    }
    launchSingleTop = true
    restoreState = true   // ← Restaura estado
}
```

### Detección de Ruta Actual
```kotlin
// Detecta automáticamente qué ruta está activa
val currentDestination = navBackStackEntry?.destination
val isSelected = currentDestination?.hierarchy?.any { 
    it.route == "alertas" 
} == true
```

---

## 🛠️ Funciones Auxiliares

### `navigateTo(navController, route)`
Función reutilizable para navegar con estado preservado

```kotlin
private fun navigateTo(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
```

---

## 📱 Cómo se ve

```
┌─────────────────────────────┐
│         Dashboard           │ (o cualquier pantalla)
│                             │
│                             │
│                             │
│                             │
│                             │
├─────────────────────────────┤
│ 📊  ❤️  📍  🔔  ☁️           │ ← BottomNavigationBar
│ Dashboard Vitales Ubicación  │    (5 items)
│          Alertas  Clima      │
└─────────────────────────────┘
```

### Estados del BottomBar

**Estado Normal:**
```
📊 Dashboard  ❤️ Vitales  📍 Ubicación  🔔 Alertas  ☁️ Clima
(texto gris)  (texto gris) (texto gris) (texto gris) (texto gris)
```

**Item Seleccionado (Dashboard):**
```
📊 Dashboard  ❤️ Vitales  📍 Ubicación  🔔 Alertas  ☁️ Clima
(azul)        (gris)      (gris)        (gris)       (gris)
(ícono+texto)
```

---

## 🎨 Customización

### Cambiar Íconos
En `VitalCareBottomNavigationBar()`:

```kotlin
NavigationBarItem(
    icon = {
        Icon(
            imageVector = Icons.Default.Favorite,  // ← Cambiar aquí
            contentDescription = "Vitales"
        )
    },
    // ...
)
```

### Cambiar Colores
```kotlin
colors = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,  // ← Cambiar
    selectedTextColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)
```

### Cambiar Altura
```kotlin
NavigationBar(
    modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)  // ← Cambiar aquí
    // ...
)
```

### Agregar Labels Siempre Visibles
```kotlin
NavigationBarItem(
    // ...
    alwaysShowLabel = true  // ← Cambiar a true
)
```

---

## 🔌 Agregar Nuevas Rutas

### Paso 1: Agregar composable en NavHost
```kotlin
composable(route = "nueva-ruta") {
    NuevaPantallaScreen()
}
```

### Paso 2: Agregar item en BottomNavigationBar
```kotlin
NavigationBarItem(
    icon = { Icon(Icons.Default.Favorite, "Nueva") },
    label = { Text("Nueva") },
    selected = currentDestination?.hierarchy?.any { 
        it.route == "nueva-ruta" 
    } == true,
    onClick = { navigateTo(navController, "nueva-ruta") }
)
```

### Paso 3: Navegar desde cualquier lado
```kotlin
// Desde una pantalla
onClick = {
    navController.navigate("nueva-ruta")
}
```

---

## ⚡ Casos de Uso Comunes

### Ir a Alertas desde Dashboard
```kotlin
// En DashboardScreen
onNavigateToAlertas = { navigateTo(navController, "alertas") }
```

### Ir al Dashboard desde cualquier pantalla
```kotlin
// Usar el BottomNavigationBar (automático)
// O programáticamente:
navController.navigate("dashboard")
```

### Detectar ruta actual
```kotlin
val isOnAlertas = currentDestination?.route == "alertas"
val isOnVitales = currentDestination?.hierarchy?.any { 
    it.route == "vitales" 
} == true
```

---

## 🧪 Testing

### Verificar navegación funciona
```kotlin
@Test
fun testNavigationToDashboard() {
    composeTestRule.onNodeWithText("Dashboard").performClick()
    // Verificar que Dashboard está visible
}
```

### Verificar BottomBar items
```kotlin
@Test
fun testBottomBarItems() {
    composeTestRule
        .onNodeWithContentDescription("Dashboard")
        .assertExists()
}
```

---

## 📋 Checklist de Integración

- [x] VitalCareNavigation.kt creado
- [x] MainActivity.kt actualizado
- [x] 5 rutas configuradas
- [x] BottomNavigationBar implementado
- [x] Placeholders para Ubicación y Clima
- [x] Navegación con estado preservado
- [x] Íconos Material 3
- [x] Colores y estilos

---

## 🚀 Próximos Pasos

1. **Compilar**
   ```
   ./gradlew assembleDebug
   ```

2. **Probar navegación**
   - Hacer click en cada item
   - Verificar transiciones suaves

3. **Reemplazar placeholders**
   - Crear UbicacionScreen real
   - Crear ClimaScreen real

4. **Customizar si necesitas**
   - Cambiar íconos
   - Cambiar colores
   - Agregar más rutas

---

## 📞 Referencia Rápida

| Componente | Función |
|-----------|---------|
| `VitalCareNavigation()` | NavHost + BottomBar |
| `navigateTo()` | Navegar con estado |
| `NavigationBar` | Material 3 bar |
| `NavigationBarItem` | Item individual |
| `currentDestination` | Detectar ruta actual |

---

## ✅ ¡LISTO!

Tu app ahora tiene:
- ✅ 5 rutas principales
- ✅ BottomNavigationBar profesional
- ✅ Navegación fluida
- ✅ Material Design 3
- ✅ Estado preservado

**¡A disfrutar de tu app!** 🎉


