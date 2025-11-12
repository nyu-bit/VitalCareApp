# Pantalla de Perfil Editable - VitalCare

## Descripción
Implementación de una pantalla de perfil completa y editable para VitalCare – App Tutor usando Jetpack Compose, Material3 y persistencia local con SharedPreferences.

**Historia de Usuario**: HU-14 (Perfil del Tutor y Paciente)

## Estructura de Archivos

### 1. ProfileScreen.kt
Composable que implementa la UI completa del perfil editable.

### 2. ProfileViewModel.kt
ViewModel que maneja el estado y la lógica de negocio:
- Estado con `StateFlow` y `MutableStateFlow`
- Funciones para actualizar cada campo
- Funciones `loadProfileData()` y `saveProfileData()`
- Integración con `SharedPreferencesManager`

### 3. SharedPreferencesManager.kt (existente)
Manager genérico para persistencia local que ya existía en el proyecto.

## Características Implementadas

### ✅ Campos Editables

#### Datos del Tutor:
- **Nombre del tutor**: Campo de texto libre
- **Edad**: Campo numérico (solo acepta dígitos)
- **Teléfono de contacto**: Campo de texto para número telefónico

#### Datos del Paciente:
- **Nombre del paciente**: Campo de texto libre
- **Contacto de emergencia**: Campo de texto para número telefónico

### ✅ Persistencia de Datos
- Usa `SharedPreferences` a través de `SharedPreferencesManager`
- Los datos se guardan localmente en el dispositivo
- Carga automática al abrir la pantalla
- Keys específicas para cada campo del perfil

### ✅ Validación y Feedback
- Toast con mensaje "Datos actualizados correctamente" al guardar
- Indicador de carga (CircularProgressIndicator) durante el guardado
- Botón deshabilitado mientras está guardando

### ✅ Diseño Material3
- TopAppBar con color primario
- OutlinedTextField para todos los campos
- Diseño vertical con scroll
- Padding y espaciado limpio
- Dividers para separar secciones
- Iconografía (Person icon)
- Responsive y adaptable

### ✅ Arquitectura
- Patrón MVVM (Model-View-ViewModel)
- Separación de responsabilidades
- Estado reactivo con StateFlow
- LaunchedEffect para carga y observación
- Preview incluido

## Componentes Utilizados

### UI Components
- `Scaffold` - Estructura principal con TopAppBar
- `TopAppBar` - Barra superior con título
- `OutlinedTextField` - Campos de entrada estilizados
- `Button` - Botón de guardar
- `CircularProgressIndicator` - Indicador de carga
- `Divider` - Separadores visuales
- `Icon` - Ícono de perfil
- `Column` - Layout vertical scrolleable

### State Management
- `StateFlow` - Estado inmutable observable
- `MutableStateFlow` - Estado mutable interno
- `collectAsState()` - Recolección de estado en Composable
- `LaunchedEffect` - Efectos secundarios

## Flujo de Datos

```
Usuario → UI (ProfileScreen) 
         ↓
    ViewModel (ProfileViewModel)
         ↓
    SharedPreferencesManager
         ↓
    SharedPreferences (Android)
```

### Carga de Datos
1. `LaunchedEffect(Unit)` se ejecuta al iniciar
2. `viewModel.loadProfileData(context)` lee datos
3. `SharedPreferencesManager.getString()` obtiene valores
4. Estado se actualiza con `_uiState.update()`
5. UI se re-renderiza automáticamente

### Guardado de Datos
1. Usuario presiona "Guardar cambios"
2. `viewModel.saveProfileData(context)` se ejecuta
3. Estado se marca como `isLoading = true`
4. `SharedPreferencesManager.saveString()` persiste datos
5. Estado se actualiza: `isLoading = false, isSaved = true`
6. `LaunchedEffect(uiState.isSaved)` detecta cambio
7. Toast se muestra al usuario
8. Estado se resetea con `resetSavedState()`

## Cómo Usar

### Integración en la App
La pantalla está integrada en `VitalCareApp.kt` con navegación básica:

```kotlin
@Composable
fun VitalCareApp() {
    var currentScreen by remember { mutableStateOf("login") }
    
    when (currentScreen) {
        "login" -> LoginScreen(
            onLoginSuccess = { currentScreen = "profile" }
        )
        "profile" -> ProfileScreen()
    }
}
```

### Vista Previa
El archivo incluye un `@Preview`:
```kotlin
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview()
```

### Navegación
- Después de login exitoso → Navega a Profile
- Los datos persisten entre sesiones

## Keys de SharedPreferences

| Key | Descripción | Tipo |
|-----|-------------|------|
| `profile_tutor_name` | Nombre del tutor | String |
| `profile_tutor_age` | Edad del tutor | String |
| `profile_tutor_phone` | Teléfono del tutor | String |
| `profile_patient_name` | Nombre del paciente | String |
| `profile_emergency_contact` | Contacto de emergencia | String |

## Estado de UI (ProfileUiState)

```kotlin
data class ProfileUiState(
    val tutorName: String = "",
    val tutorAge: String = "",
    val tutorPhone: String = "",
    val patientName: String = "",
    val emergencyContact: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
```

## Próximos Pasos (TODO)

- [ ] Agregar validaciones de formato (teléfono, edad máxima/mínima)
- [ ] Implementar botón "Cancelar" para descartar cambios
- [ ] Agregar campo de foto de perfil
- [ ] Implementar edición de email
- [ ] Agregar más campos (dirección, ciudad, etc.)
- [ ] Sincronización con backend/API
- [ ] Historial de cambios
- [ ] Confirmación antes de guardar
- [ ] Navegación a otras pantallas desde el perfil

## Tecnologías

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - Framework UI moderno
- **Material3** - Sistema de diseño
- **StateFlow** - Gestión de estado reactivo
- **SharedPreferences** - Persistencia local
- **ViewModel** - Arquitectura MVVM
- **Coroutines** - Programación asíncrona

## Pruebas

### Manual Testing
1. Abrir la app y hacer login
2. Navegar a la pantalla de perfil
3. Llenar los campos con datos de prueba
4. Presionar "Guardar cambios"
5. Verificar el Toast de confirmación
6. Cerrar y reabrir la app
7. Verificar que los datos persisten

### Casos de Prueba
- ✅ Guardar datos completos
- ✅ Guardar con campos vacíos
- ✅ Persistencia entre sesiones
- ✅ Validación numérica en edad
- ✅ Indicador de carga funciona
- ✅ Toast se muestra correctamente

---

**Autor**: Implementado para VitalCare - MajoApp branch  
**Fecha**: Noviembre 2025  
**Historia de Usuario**: HU-14 (Perfil del Tutor y Paciente)
