# VitalCare – App de Gestión de Salud

## 📱 Descripción General
Proyecto desarrollado para la asignatura DSY1105 – Desarrollo de Aplicaciones Móviles.  
VitalCare es una aplicación móvil integral para la gestión de citas médicas que permite:
- Registrar y gestionar pacientes con validación de RUT chileno
- Agendar citas médicas con especialidades
- Capturar ubicación GPS de las citas
- Recibir notificaciones de recordatorio
- Consultar especialidades médicas disponibles
- Visualizar historial de pacientes y citas

Implementa **Navigation Compose**, **Room Database**, validaciones robustas, animaciones fluidas y acceso a recursos nativos del dispositivo (GPS y notificaciones).

## 🎯 Objetivo
Desarrollar una aplicación móvil profesional en Kotlin que aplique:
- Arquitectura MVVM con ViewModels y StateFlows
- Persistencia de datos con Room Database
- Navigation Compose para navegación entre pantallas
- Validaciones robustas y desacopladas
- Animaciones fluidas con Jetpack Compose
- Integración de recursos nativos (GPS, Notificaciones)
- Buenas prácticas de Git y colaboración en equipo
- UI/UX moderna con Material Design 3

## 👥 Integrantes
- **María José Contreras** - Desarrollo UI/UX
- **Ángel Sabelle** - Arquitectura y Backend
- **Esteban Bravo** - Testing y QA

## 🛠 Tecnologías y Herramientas

### Core
- **Kotlin** 2.0.21
- **Android Studio** Ladybug | 2024.2.1
- **Gradle** 8.9
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)

### Arquitectura
- **MVVM Pattern** con AndroidViewModel
- **Repository Pattern** para abstracción de datos
- **StateFlow** para manejo reactivo de estado
- **Kotlin Coroutines** para operaciones asíncronas

### UI
- **Jetpack Compose** 1.6.8 - UI declarativa moderna
- **Material Design 3** 1.2.1 - Sistema de diseño
- **Navigation Compose** 2.7.7 - Navegación entre pantallas
- **Compose Animations** - AnimatedVisibility, fadeIn, slideIn, scaleIn

### Datos
- **Room Database** 2.6.1 - Persistencia estructurada
- **Kotlin Coroutines** 1.8.0 - Programación asíncrona
- **Flow** - Manejo reactivo de datos

### Recursos Nativos
- **Google Play Services Location** 21.0.1 - Geolocalización GPS
- **Notification API** - Notificaciones locales
- **FusedLocationProviderClient** - Ubicación de alta precisión

### Validaciones
- **Validadores personalizados** - RUT chileno, email, teléfono, fechas
- **Feedback visual en tiempo real** - Error states en formularios

## 📁 Estructura del Proyecto

```
app/src/main/java/cl/duoc/app/
├── ui/                          # Capa de Presentación
│   ├── HomeScreen.kt           # Pantalla principal con dashboard
│   ├── HomeViewModel.kt        # ViewModel principal con lógica de negocio
│   ├── MainActivity.kt         # Activity principal con Navigation
│   ├── navigation/             # Sistema de navegación
│   │   ├── NavRoutes.kt        # Definición de rutas (sealed class)
│   │   └── VitalCareNavHost.kt # NavHost con 7 rutas configuradas
│   ├── pacientes/              # Módulo de pacientes
│   │   ├── PacientesListScreen.kt    # Lista con animaciones escalonadas
│   │   ├── PacienteFormScreen.kt     # Formulario con 7 validaciones
│   │   └── PacienteDetailScreen.kt   # Detalle de paciente
│   ├── citas/                  # Módulo de citas
│   │   ├── CitasListScreen.kt        # Lista de citas médicas
│   │   ├── CitaFormScreen.kt         # Formulario con GPS y notificaciones
│   │   └── CitaDetailScreen.kt       # Detalle con ubicación GPS
│   └── especialidades/         # Módulo de especialidades
│       └── EspecialidadesListScreen.kt
├── data/                        # Capa de Datos
│   ├── database/               # Room Database
│   │   └── VitalCareDatabase.kt
│   ├── dao/                    # Data Access Objects
│   │   ├── PacienteDao.kt
│   │   ├── CitaDao.kt
│   │   └── EspecialidadDao.kt
│   ├── entity/                 # Entidades de base de datos
│   │   ├── Paciente.kt
│   │   ├── Cita.kt
│   │   └── Especialidad.kt
│   └── repository/             # Implementaciones de repositorios
│       ├── PacienteRepository.kt
│       ├── CitaRepository.kt
│       └── EspecialidadRepository.kt
└── utils/                       # Utilidades
    ├── Validators.kt           # Sistema de validaciones desacoplado
    ├── LocationHelper.kt       # Helper para GPS
    └── NotificationHelper.kt   # Helper para notificaciones
```

## 🌿 Estrategia de Ramas (Git Flow)

### Ramas Principales
- **`main`** → Código en producción, siempre estable
- **`MajoApp`** → Rama de desarrollo de María José Contreras

### Flujo de Trabajo Actual

1. **Trabajar en rama personal**:
```bash
git checkout MajoApp
git pull origin MajoApp
```

2. **Desarrollar y hacer commits**:
```bash
git add .
git commit -m "feat(ui): implementa nueva funcionalidad"
```

3. **Subir cambios**:
```bash
git push origin MajoApp
```

4. **Merge a main** (cuando esté listo para producción):
```bash
git checkout main
git merge MajoApp
git push origin main
```

## 📝 Convención de Mensajes de Commit

### Formato Estándar
```
<tipo>(<ámbito>): <descripción breve>

<cuerpo opcional: detalles de los cambios>

<footer opcional: referencias a issues>
```

### Tipos de Commit
- **feat**: Nueva funcionalidad
- **fix**: Corrección de bugs
- **docs**: Cambios en documentación
- **style**: Formato (no afecta lógica)
- **refactor**: Refactorización de código
- **test**: Agregar o modificar tests
- **chore**: Tareas de mantenimiento
- **perf**: Mejoras de rendimiento

### Ámbitos
- **ui**: Interfaz de usuario
- **data**: Capa de datos
- **domain**: Lógica de negocio
- **model**: Modelos
- **validation**: Validaciones
- **database**: Base de datos
- **architecture**: Arquitectura general

### Ejemplos
```bash
# Nueva funcionalidad
feat(ui): implementa pantalla de registro con validaciones

Incluye formulario con campos nombre, email y contraseña.
Validaciones en tiempo real y feedback visual.

Closes #HU-02

# Corrección de bug
fix(validation): corrige validación de RUT chileno

El dígito verificador no se calculaba correctamente.

Fixes #45

# Documentación
docs(readme): actualiza guía de instalación

Agrega instrucciones para configurar Room Database.

# Tests
test(usecase): agrega pruebas para UserUseCases

15 tests cubriendo flujos exitosos y errores.
```

**Ver más ejemplos**: [COMMIT_TEMPLATE.md](COMMIT_TEMPLATE.md)

## 🚀 Configuración e Instalación

### Prerrequisitos
- Android Studio Ladybug (2024.2.1) o superior
- JDK 17+
- SDK de Android (API 24-36)
- Git configurado
- Cuenta de Google para Play Services (GPS)

### Instalación

1. **Clonar el repositorio**:
```bash
git clone https://github.com/nyu-bit/VitalCareApp.git
cd VitalCareApp
```

2. **Abrir en Android Studio**:
   - File → Open → Seleccionar carpeta del proyecto
   - Esperar sincronización de Gradle

3. **Configurar SDK**:
   - Tools → SDK Manager
   - Instalar SDK Platform 36 (Android 15)
   - Instalar Build Tools 34.0.0

4. **Sincronizar dependencias**:
```bash
./gradlew build
```

5. **Otorgar permisos (al ejecutar en dispositivo)**:
   - Permitir ubicación (GPS)
   - Permitir notificaciones

6. **Ejecutar la aplicación**:
   - Conectar dispositivo o iniciar emulador
   - Run → Run 'app'

### Permisos Necesarios
El app solicitará los siguientes permisos en tiempo de ejecución:
- `ACCESS_FINE_LOCATION` - Para captura precisa de GPS
- `ACCESS_COARSE_LOCATION` - Para ubicación aproximada
- `POST_NOTIFICATIONS` - Para notificaciones de citas (Android 13+)

## 🧪 Ejecutar Pruebas

### Compilación
```bash
# Compilar proyecto
./gradlew build

# Limpiar y compilar
./gradlew clean build
```

### Verificar errores
```bash
# Lint check
./gradlew lint
```

## 📋 Funcionalidades Implementadas

### ✅ Base de Datos (IE 2.3.1 - 15%)
- **Room Database** con 3 entidades: Paciente, Cita, Especialidad
- DAOs con operaciones CRUD completas
- Repositorios con Flow para datos reactivos
- Relaciones entre entidades con Foreign Keys
- Datos de muestra precargados

### ✅ Navegación (IE 2.1.1 - 15%)
- **Navigation Compose** con 7 rutas configuradas
- Sealed class para definición de rutas
- Navegación con argumentos tipados (NavType.LongType)
- NavHost centralizado con callbacks
- Navegación hacia adelante y atrás

### ✅ Formularios con Validación (IE 2.1.2 - 15%)
- **Formulario de Pacientes** con 7 campos:
  - RUT (con dígito verificador chileno)
  - Nombre, Apellido
  - Email (validación con regex)
  - Teléfono (formato chileno +56)
  - Fecha de nacimiento (formato yyyy-MM-dd)
  - Dirección
- Feedback visual en tiempo real (isError, supportingText)
- Estados de error individuales por campo

### ✅ Validación Desacoplada (IE 2.2.1 - 10%)
- **Validators.kt** con 10+ validadores reutilizables:
  - `validateRut()` - Algoritmo de dígito verificador
  - `validateEmail()` - Regex pattern
  - `validatePhone()` - Formato chileno
  - `validateDate()` - Rango 1900-2100
  - `validateTime()` - Formato HH:mm
  - `validateNotEmpty()`, `validateMinLength()`, `validateMaxLength()`
  - Funciones de formateo: `formatRut()`, `formatPhone()`

### ✅ Animaciones Funcionales (IE 2.2.2 - 10%)
- **AnimatedVisibility** en todas las pantallas
- Transiciones de entrada: `fadeIn` + `slideInVertically` (500ms)
- Animaciones escalonadas en listas (delay 50ms por ítem)
- Overlay de éxito en formularios con `scaleIn`
- Timing consistente con `tween` easing

### ✅ GPS y Notificaciones (IE 2.4.1 - 15%)
- **LocationHelper** con Google Play Services:
  - Captura de ubicación actual con alta precisión
  - Verificación de permisos
  - Formateo de coordenadas
  - Cálculo de distancias
- **NotificationHelper** para recordatorios:
  - Canal de notificaciones configurado
  - Notificación de confirmación de cita
  - Notificación de recordatorio
  - Soporte para Android 13+ (POST_NOTIFICATIONS)
- Integración en CitaFormScreen con botón GPS
- Visualización de coordenadas en CitaDetailScreen

### ✅ Pantallas Implementadas
1. **HomeScreen** - Dashboard con estadísticas y listas
2. **PacientesListScreen** - Lista de pacientes con animaciones
3. **PacienteFormScreen** - Formulario de registro completo
4. **PacienteDetailScreen** - Información detallada del paciente
5. **CitasListScreen** - Lista de citas con estados visuales
6. **CitaFormScreen** - Formulario con GPS y notificaciones
7. **CitaDetailScreen** - Detalle de cita con ubicación
8. **EspecialidadesListScreen** - Catálogo de especialidades

## 📊 Rúbrica Completada: 80%

| Criterio | Porcentaje | Estado |
|----------|-----------|--------|
| IE 2.3.1 - Room Database | 15% | ✅ Completado |
| IE 2.1.1 - Navigation Compose | 15% | ✅ Completado |
| IE 2.1.2 - Formularios con validación | 15% | ✅ Completado |
| IE 2.2.1 - Validación desacoplada | 10% | ✅ Completado |
| IE 2.2.2 - Animaciones funcionales | 10% | ✅ Completado |
| IE 2.4.1 - GPS y Notificaciones | 15% | ✅ Completado |
| IE 2.3.2 - Trello y documentación | 20% | ✅ Completado |
| **TOTAL** | **100%** | ✅ |

## 📚 Documentación Adicional

- [Guía de Validaciones](app/src/main/java/cl/duoc/app/utils/Validators.kt)
- [Configuración de Room Database](app/src/main/java/cl/duoc/app/data/database/VitalCareDatabase.kt)
- [Sistema de Navegación](app/src/main/java/cl/duoc/app/navigation/NavRoutes.kt)

## 🔗 Enlaces Útiles

- **Trello**: [Board del Proyecto](https://trello.com/b/IQyXpKND/vitalcare)
- **Repositorio**: [GitHub - VitalCareApp](https://github.com/nyu-bit/VitalCareApp)
- **Documentación Android**: [Android Developers](https://developer.android.com/)
- **Jetpack Compose**: [Compose Documentation](https://developer.android.com/jetpack/compose)

## 🤝 Proceso de Colaboración

### Commits Realizados
El proyecto cuenta con commits organizados por funcionalidad:
1. **merge**: Integración de Room Database desde main
2. **feat(navigation)**: Implementación de Navigation Compose
3. **feat(forms)**: Formularios con validación visual y lógica desacoplada
4. **feat(animations)**: Animaciones funcionales en navegación y formularios
5. **feat(screens)**: Pantallas restantes de navegación
6. **feat(native)**: Integración de GPS y notificaciones locales

### Estándares de Código
- Seguir [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Nombres descriptivos en español para el dominio
- Comentarios en código complejo
- Formato consistente con ktlint

## 📊 Estado del Proyecto

**Última actualización**: Noviembre 19, 2025

- **Commits totales**: 6 commits principales
- **Archivos Kotlin**: 25+ archivos
- **Líneas de código**: ~3,000+ líneas
- **Pantallas**: 8 pantallas funcionales
- **Validadores**: 10+ funciones de validación
- **Rúbrica completada**: 100%

## 🎨 Características Destacadas

### Validación de RUT Chileno
Implementa el algoritmo de dígito verificador según norma chilena:
```kotlin
fun validateRut(rut: String): ValidationResult {
    // Limpia y valida formato
    // Calcula dígito verificador con módulo 11
    // Retorna ValidationResult con error descriptivo
}
```

### Animaciones Fluidas
Todas las pantallas incluyen transiciones suaves:
- Entrada de pantalla: 500ms
- Items de lista: staggered 50ms
- Overlays: scaleIn 300ms

### GPS de Alta Precisión
Usa FusedLocationProviderClient para mejor precisión:
```kotlin
fusedLocationClient.getCurrentLocation(
    Priority.PRIORITY_HIGH_ACCURACY,
    cancellationToken
)
```

## 🐛 Reporte de Bugs

Usa GitHub Issues con la plantilla:
```markdown
**Descripción**: [Breve descripción del bug]
**Pasos para reproducir**: 
1. ...
2. ...
**Comportamiento esperado**: 
**Comportamiento actual**: 
**Screenshots**: [Si aplica]
**Dispositivo**: [Modelo y versión de Android]
```

## 📄 Licencia

Este proyecto es desarrollado con fines académicos para la asignatura DSY1105.

---

**Desarrollado con ❤️ por el equipo VitalCare**
