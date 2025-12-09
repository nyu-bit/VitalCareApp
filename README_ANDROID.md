# VitalCareApp - Aplicación de Gestión de Citas Médicas

## 📱 Descripción General

VitalCareApp es una aplicación Android moderna desarrollada en **Kotlin** con **Jetpack Compose** para gestionar citas médicas, pacientes y especialidades. La aplicación implementa una arquitectura limpia con separación clara de responsabilidades (Repository Pattern, MVVM).

**Stack Tecnológico:**
- Kotlin 2.0.0
- Jetpack Compose (UI moderna)
- Room Database (persistencia local)
- Coroutines & Flow (programación asincrónica)
- Navigation Compose (navegación)
- MockK + JUnit (testing)

## 🏗️ Arquitectura

### Capas de Aplicación

```
┌─────────────────────────────────────────┐
│         Presentation Layer (UI)         │
│  ├─ Composables (Screens)               │
│  ├─ ViewModels                          │
│  └─ Navigation                          │
├─────────────────────────────────────────┤
│         Domain Layer (Business)         │
│  ├─ Use Cases (Business Logic)          │
│  └─ Validators                          │
├─────────────────────────────────────────┤
│         Data Layer (Persistence)        │
│  ├─ Repositories                        │
│  ├─ DAOs (Room Database)                │
│  ├─ Entities (Data Models)              │
│  └─ Local Storage                       │
└─────────────────────────────────────────┘
```

### Estructura de Directorios

```
app/src/
├── main/
│   ├── java/cl/duoc/app/
│   │   ├── data/
│   │   │   ├── dao/              # Data Access Objects
│   │   │   │   ├── CitaDao
│   │   │   │   ├── EspecialidadDao
│   │   │   │   └── PacienteDao
│   │   │   ├── entity/           # Entidades de Room
│   │   │   │   ├── Cita
│   │   │   │   ├── Especialidad
│   │   │   │   └── Paciente
│   │   │   ├── repository/       # Repositories (Abstracción)
│   │   │   │   ├── CitaRepository
│   │   │   │   ├── EspecialidadRepository
│   │   │   │   └── PacienteRepository
│   │   │   └── database/         # Room Database
│   │   │       └── AppDatabase
│   │   │
│   │   ├── domain/               # Lógica de negocio
│   │   │   ├── usecases/        # Casos de uso
│   │   │   └── models/          # Modelos de dominio
│   │   │
│   │   ├── ui/
│   │   │   ├── screens/         # Pantallas (Composables)
│   │   │   │   ├── CitasScreen
│   │   │   │   ├── PacientesScreen
│   │   │   │   └── EspecialidadesScreen
│   │   │   ├── viewmodels/      # ViewModels (Estado)
│   │   │   ├── components/      # Componentes reutilizables
│   │   │   └── navigation/      # Navegación
│   │   │
│   │   └── utils/                # Utilidades
│   │       ├── Validators       # Sistema de validación
│   │       ├── Constants
│   │       └── Extensions
│   │
│   └── res/
│       ├── values/              # Strings, colores, estilos
│       ├── drawable/            # Recursos gráficos
│       └── raw/                 # Recursos raw
│
└── test/
    └── java/cl/duoc/app/
        ├── data/repository/     # Tests de Repository
        ├── utils/               # Tests de Validators
        └── ExampleUnitTest
```

## 📊 Entidades Principales

### Cita
```kotlin
@Entity(tableName = "citas")
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pacienteId: Long,           // FK a Paciente
    val especialidadId: Long,       // FK a Especialidad
    val fecha: String,              // "2024-12-25"
    val hora: String,               // "14:30"
    val estado: EstadoCita = EstadoCita.PENDIENTE
)

enum class EstadoCita {
    PENDIENTE,      // Cita creada, sin confirmar
    CONFIRMADA,     // Paciente confirmó asistencia
    CANCELADA,      // Cita cancelada
    COMPLETADA      // Cita realizada
}
```

### Paciente
```kotlin
@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rut: String,                // Ej: "12345678-5"
    val nombre: String,
    val apellido: String,
    val email: String,              // Ej: "paciente@example.com"
    val telefono: String,           // Ej: "+56912345678"
    val fechaNacimiento: String,    // "1990-01-15"
    val direccion: String,
    val activo: Boolean = true
)
```

### Especialidad
```kotlin
@Entity(tableName = "especialidades")
data class Especialidad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,             // Ej: "Cardiología"
    val descripcion: String,        // "Especialista en corazón"
    val duracionCita: Int,          // Minutos: 30, 45, 60
    val activa: Boolean = true
)
```

## 🔄 Flujo de Datos

### Obtener Citas de un Paciente

```
UI (Composable)
    ↓ (recolecta Click)
ViewModel
    ↓ (llama a repository)
CitaRepository
    ↓ (delega a DAO)
CitaDao
    ↓ (consulta Room DB)
SQLite Database
    ↓ (retorna Flow<List<Cita>>)
ViewModel (recolecta Flow)
    ↓ (actualiza State)
UI (recompone con nuevos datos)
    ↓ (muestra citas)
Pantalla actualizada
```

### Validación de Datos

```
Usuario ingresa datos
    ↓
Composable invoca validator
    ↓
Validators.validateEmail() / RUT / etc
    ↓
ValidationResult(isValid, errorMessage)
    ↓
UI muestra error o continúa
    ↓
Si válido: Repository.insert() o update()
```

## 🛠️ Repository Pattern

Los repositories abstraen el acceso a datos:

```kotlin
class CitaRepository(private val citaDao: CitaDao) {
    
    // Lectura - Retorna Flow para suscripción en tiempo real
    fun getCitasByPaciente(pacienteId: Long): Flow<List<Cita>> {
        return citaDao.getCitasByPaciente(pacienteId)
    }
    
    // Lectura sincrónica
    suspend fun getCitaByIdSync(id: Long): Cita? {
        return citaDao.getCitaById(id)
    }
    
    // Escritura
    suspend fun insert(cita: Cita): Long {
        return citaDao.insert(cita)
    }
    
    // Acciones de negocio
    suspend fun confirmarCita(citaId: Long) {
        citaDao.updateEstado(citaId, EstadoCita.CONFIRMADA)
    }
}
```

**Ventajas:**
- Lógica de negocio centralizada
- Fácil de testear (mockear DAO)
- Cambios de fuente de datos no afectan UI
- Reutilizable desde ViewModels

## ✅ Sistema de Validación

Validadores desacoplados de UI en `utils/Validators.kt`:

```kotlin
object Validators {
    
    fun validateEmail(email: String): ValidationResult {
        // Valida formato de email
    }
    
    fun validateRut(rut: String): ValidationResult {
        // Valida RUT chileno con dígito verificador
    }
    
    fun validatePhone(phone: String): ValidationResult {
        // Valida teléfono chileno
    }
    
    fun validateDate(date: String): ValidationResult {
        // Valida fecha en formato yyyy-MM-dd
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
```

**Uso en UI:**
```kotlin
val emailValidation = Validators.validateEmail(email)
if (!emailValidation.isValid) {
    Text(emailValidation.errorMessage, color = Color.Red)
}
```

## 📡 Gestión de Dependencias

### Inyección de Dependencias (Manual)

Actualmente se usa inyección manual:

```kotlin
val database = AppDatabase.getInstance(context)
val citaRepository = CitaRepository(database.citaDao())
val pacienteRepository = PacienteRepository(database.pacienteDao())
```

**Mejora Futura:** Integrar Hilt para inyección automática.

## 🎨 Jetpack Compose

La UI está completamente construida con Compose:

```kotlin
@Composable
fun CitasScreen(
    viewModel: CitasViewModel = hiltViewModel()
) {
    val citas by viewModel.citas.collectAsState()
    
    LazyColumn {
        items(citas) { cita ->
            CitaItem(cita = cita)
        }
    }
}

@Composable
fun CitaItem(cita: Cita) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Paciente: ${cita.pacienteId}")
            Text("Fecha: ${cita.fecha} ${cita.hora}")
            Text("Estado: ${cita.estado}")
        }
    }
}
```

## 📚 Testing

Ver `TESTS_DOCUMENTATION.md` para detalles completos.

**Resumen:**
- 159 tests unitarios
- MockK para mocking de dependencias
- Coroutines Test para código asincrónico
- 100% de tests pasando

Ejecutar tests:
```bash
./gradlew testDebugUnitTest
```

## 🚀 Funcionalidades Principales

### Gestión de Citas
- ✅ Crear cita (validación de datos)
- ✅ Listar citas por paciente
- ✅ Filtrar citas por estado/fecha
- ✅ Cambiar estado (pendiente → confirmada → completada)
- ✅ Cancelar cita

### Gestión de Pacientes
- ✅ Registrar paciente (con validación RUT)
- ✅ Ver datos del paciente
- ✅ Actualizar información
- ✅ Buscar paciente por RUT/email
- ✅ Listar pacientes activos

### Gestión de Especialidades
- ✅ Ver especialidades disponibles
- ✅ Filtrar activas/inactivas
- ✅ Ver duración estándar de cita

## 🔐 Validaciones Implementadas

| Campo | Validación | Ejemplo Válido |
|-------|-----------|----------------|
| Email | RFC 5322 simplificado | usuario@example.com |
| RUT | Algoritmo chileno + DV | 12345678-5 |
| Teléfono | 9+ dígitos chilenos | +56912345678 |
| Fecha | yyyy-MM-dd válido | 2024-12-25 |
| Hora | HH:mm (00:00-23:59) | 14:30 |
| No Vacío | Solo espacios rechazados | "Juan" |

## 📦 Dependencias Principales

```gradle
// Compose
androidx.compose:compose-bom:2024.12.01

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0

// Navigation
androidx.navigation:navigation-compose:2.7.7

// Testing
io.mockk:mockk:1.13.8
org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0
junit:junit:4.13.2
```

## 🛑 Configuración BuildConfig

```gradle
android {
    namespace = "cl.duoc.app"
    compileSdk = 36
    targetSdk = 36
    minSdk = 24
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}
```

## 🐛 Debugging

### Logs
Se recomienda agregar logs en repository:
```kotlin
fun getCitaById(id: Long): Flow<Cita?> {
    Log.d("CitaRepository", "Obteniendo cita $id")
    return citaDao.getCitaByIdFlow(id)
}
```

### Inspeccionar Base de Datos
Usar Android Studio Database Inspector:
1. Run > Logcat
2. Pestaña "Database Inspector"
3. Seleccionar base de datos

## 📈 Posibles Mejoras

1. **Seguridad**
   - Encriptación de datos sensibles
   - Autenticación de usuario
   - Permisos de acceso

2. **Performance**
   - Paginación en listas largas
   - Caché local
   - Sincronización inteligente

3. **Testing**
   - Tests de UI con ComposeTestRule
   - Tests instrumentados
   - Tests de integración

4. **Arquitectura**
   - Implementar Hilt para DI
   - MVVM completo con savedStateHandle
   - Repositorio remoto (API REST)

5. **UX/UI**
   - Animaciones más fluidas
   - Temas oscuro/claro
   - Notificaciones de recordatorio
   - Exportar citas a calendario

## 📝 Convenciones de Código

### Naming
- `Repository` - Patrón repositorio
- `ViewModel` - Mantiene estado de pantalla
- `Screen` - Composable principal de pantalla
- `Item` - Componente para lista
- `validate*` - Función de validación

### Structure
- Un archivo por clase principal
- Métodos públicos primero, luego privados
- Documentación en classes principales
- Tests siguiendo patrón AAA

## 🔗 Recursos Útiles

- [Documentación Room](https://developer.android.com/training/data-storage/room)
- [Jetpack Compose](https://developer.android.com/compose)
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [MockK](https://mockk.io/)

## 👨‍💼 Información del Proyecto

- **Lenguaje:** Kotlin
- **MinSDK:** 24 (Android 7.0)
- **TargetSDK:** 36 (Android 15)
- **Estado:** En desarrollo
- **Última actualización:** Diciembre 2024

---

**Nota:** Para preguntas sobre la arquitectura o implementación, revisar los tests en `app/src/test/` que actúan como documentación ejecutable.

