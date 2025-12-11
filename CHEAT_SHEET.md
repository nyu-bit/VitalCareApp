# 🎓 CHEAT SHEET - REFERENCIA RÁPIDA

## 🚀 5 MINUTOS PARA EMPEZAR

### Paso 1: Copiar Composable (1 minuto)
```kotlin
// De: ui/screens/examples/ApiExamplesComposables.kt
VitalesScreenExample(pacienteId = "pac123")
```

### Paso 2: Usar en tu navegación (1 minuto)
```kotlin
NavHost(navController, startDestination = "vitales/{id}") {
    composable("vitales/{id}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id") ?: ""
        VitalesScreenExample(pacienteId = id)
    }
}
```

### Paso 3: Probar (1 minuto)
```
Ejecuta la app y navega a esa pantalla
¡Deberías ver los datos!
```

### Paso 4: Repetir para otros APIs (2 minutos)
```
Copia UbicacionScreenExample
Copia AlertasScreenExample
¡Listo!
```

---

## 📍 UBICACIÓN DE ARCHIVOS - COPIA RÁPIDA

```
API Interface
  └─ data/api/VitalesApi.kt

DTO
  └─ Dentro de VitalesApi.kt (SignosVitalesDto)

Repository
  └─ data/repository/ApiRepositories.kt (VitalesRepository)

ViewModel
  └─ data/repository/ViewModels.kt (VitalesViewModel)

Composable
  └─ ui/screens/examples/ApiExamplesComposables.kt (VitalesScreenExample)

Configuración
  └─ data/RetrofitInstance.kt
```

---

## 🎯 SNIPPETS DE CÓDIGO - COPIAR Y PEGAR

### Usar ViewModel en Composable
```kotlin
@Composable
fun MiPantalla(pacienteId: String) {
    val viewModel = VitalesViewModel()
    val state by viewModel.uiState.collectAsState()
    
    LaunchedEffect(pacienteId) {
        viewModel.loadVitalesByPaciente(pacienteId)
    }
    
    when (state) {
        is VitalesUiState.Loading -> CircularProgressIndicator()
        is VitalesUiState.Success -> {
            // Mostrar datos
        }
        is VitalesUiState.Error -> {
            // Mostrar error
        }
    }
}
```

### Crear Nuevo Vital
```kotlin
val nuevoSigno = SignosVitalesDto(
    pacienteId = "pac123",
    frecuenciaCardiaca = 72,
    temperatura = 37.5
)
viewModel.saveVitales(nuevoSigno)
```

### Llamar API Directamente
```kotlin
val api = RetrofitInstance.getVitalesApi()
val signos = api.getAllVitales()
```

---

## 🔗 URLS Y CONFIGURACIÓN

```
Vitales:    http://10.0.2.2:8081/
Ubicación:  http://10.0.2.2:8082/
Alertas:    http://10.0.2.2:8083/
```

---

## 📊 MÉTODOS POR API

### VitalesApi
```
getAllVitales()
getVitalesByPaciente(id)
createVitales(dto)
deleteVitales(id)
```

### UbicacionApi
```
getAllUbicaciones()
getUbicacionesByPaciente(id)
createUbicacion(dto)
```

### AlertasApi
```
getAllAlertas()
getAlertasByPaciente(id)
createAlerta(dto)
updateAlerta(id, dto)
deleteAlerta(id)
```

---

## 🎨 COMPOSABLES LISTOS

```
VitalesScreenExample
UbicacionScreenExample
AlertasScreenExample
PacienteDetailScreenExample
CreateVitalSignFormExample
```

---

## 🐛 TROUBLESHOOTING RÁPIDO

| Problema | Solución |
|----------|----------|
| Cannot resolve VitalesApi | Verificar import: `import cl.duoc.app.data.api.VitalesApi` |
| No se conecta | Servidor debe estar activo en http://10.0.2.2:8081/ |
| JSON no deserializa | Verificar DTO campos coincidan con JSON |
| UI no actualiza | Usar `collectAsState()` + `LaunchedEffect` |
| Error en compilación | Build → Clean Build Folder |

---

## 📚 DOCUMENTACIÓN MÁS IMPORTANTE

```
1. ONE_PAGE_GUIDE.md           ← Leer primero
2. QUICK_START_INTEGRATION.md  ← Seguir pasos
3. API_USAGE_GUIDE.kt          ← Ver ejemplos
4. ApiExamplesComposables.kt   ← Copiar Composables
```

---

## ✅ CHECKLIST DE INTEGRACIÓN

```
[ ] Verificar dependencias en build.gradle.kts
[ ] Copiar un Composable a tu proyecto
[ ] Probar en emulador
[ ] Adaptarlo a tu diseño
[ ] Integrar los 3 APIs
[ ] Conectar navegación
[ ] Agregar caché (opcional)
[ ] Tests (opcional)
```

---

## 🚀 COMANDOS ÚTILES

```bash
# Limpiar y construir
gradlew clean build

# Ejecutar tests
gradlew test

# Ver dependencias
gradlew dependencies
```

---

## 💡 TIPS IMPORTANTES

✅ Todos los Composables están listos para copiar  
✅ Los ViewModels ya manejan todos los estados  
✅ Los Repositories manejan errores con Result<T>  
✅ Las suspend functions funcionan con coroutines  
✅ DTOs tienen valores por defecto  
✅ Timestamps se generan automáticamente  

---

## 📱 ESTRUCTURA GENERAL

```
UI (Composable)
    ↓ viewModel.load()
ViewModel (StateFlow)
    ↓ repository.get()
Repository (Result<T>)
    ↓ api.call()
API (Retrofit)
    ↓ HTTP
Microservicio
```

---

## 🎯 TODO EN UN VISTAZO

```
✅ 3 APIs listos
✅ 3 DTOs listos
✅ 4 Repositories listos
✅ 4 ViewModels listos
✅ 8 Composables listos
✅ Documentación completa
✅ Ejemplos abundantes
✅ Listo para usar
```

---

## 🔥 ACCIONES RÁPIDAS

### Quiero usar VitalesApi
1. Copiar `VitalesScreenExample` de `ApiExamplesComposables.kt`
2. Pegarlo en tu app
3. Listo ✅

### Quiero crear un nuevo vital
1. Crear `SignosVitalesDto` con datos
2. Llamar `viewModel.saveVitales(dto)`
3. Listo ✅

### Quiero mostrar lista de ubicaciones
1. Copiar `UbicacionScreenExample`
2. Pegarlo
3. Listo ✅

---

## 📊 ESTADÍSTICAS RÁPIDAS

```
APIs:           3
Métodos:       12
Repositories:   4
ViewModels:     4
Composables:    8
Documentos:    12
Líneas:      2800+
Estado:     ✅ OK
```

---

## 🎁 BONUS

```
ExamplesAndPatterns.kt    → Patrones avanzados
API_USAGE_GUIDE.kt        → Ejemplos básicos
IMPLEMENTATION_CHECKLIST  → Paso a paso
MENTAL_MAP.md             → Diagrama visual
```

---

**¡LISTO PARA USAR! Comienza con ONE_PAGE_GUIDE.md**

