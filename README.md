# VitalCare – App de Gestión de Salud

## 📱 Descripción General
Proyecto desarrollado para la asignatura DSY1105 – Desarrollo de Aplicaciones Móviles.  
VitalCare es una aplicación móvil integral que permite a los usuarios:
- Registrarse y gestionar su perfil
- Reservar horas médicas
- Monitorear signos vitales en tiempo real
- Recibir alertas de salud
- Acceder a su historial médico

Implementa **Clean Architecture**, **MVVM**, validaciones robustas, persistencia local y acceso a recursos nativos del dispositivo.

## 🎯 Objetivo
Desarrollar una aplicación móvil profesional en Kotlin que aplique:
- Principios de Clean Architecture y SOLID
- Patrones de diseño (MVVM, Repository, Use Cases)
- Testing exhaustivo (Unit Tests)
- Buenas prácticas de Git y colaboración en equipo
- UI/UX moderna con Jetpack Compose

## 👥 Integrantes
- **María José Contreras** - Desarrollo UI/UX
- **Ángel Sabelle** - Arquitectura y Backend
- **Esteban Bravo** - Testing y QA

## 🛠 Tecnologías y Herramientas

### Core
- **Kotlin** 1.9.20+
- **Android Studio** Hedgehog | 2023.1.1+
- **Gradle** 8.2+
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Arquitectura
- **Clean Architecture** (UI, Domain, Data, Model)
- **MVVM Pattern** con ViewModels
- **Repository Pattern** para abstracción de datos
- **Use Cases** para lógica de negocio

### UI
- **Jetpack Compose** - UI declarativa moderna
- **Material Design 3** - Sistema de diseño
- **Navigation Compose** - Navegación entre pantallas
- **Accompanist** - Utilidades adicionales

### Datos
- **Room Database** - Persistencia estructurada
- **SharedPreferences** - Configuraciones simples
- **Gson** - Serialización JSON
- **Kotlin Coroutines** - Programación asíncrona
- **Flow** - Manejo reactivo de datos

### Testing
- **JUnit 4** - Framework de testing
- **Coroutines Test** - Testing de coroutines
- **Truth** - Assertions legibles
- **MockK** - Mocking para Kotlin

### Otros
- **Google Maps SDK** - Geolocalización
- **WorkManager** - Tareas en background
- **Notification API** - Notificaciones locales

## 📁 Estructura del Proyecto

```
app/src/main/java/cl/duoc/app/
├── ui/                          # Capa de Presentación
│   ├── HomeScreen.kt           # Pantalla principal
│   ├── HomeViewModel.kt        # ViewModel de Home
│   ├── form/                   # Módulo de formularios
│   │   ├── FormScreen.kt       # Pantalla de formulario
│   │   ├── FormViewModel.kt    # ViewModel con validaciones
│   │   ├── FormValidators.kt   # Sistema de validaciones
│   │   └── FormUiState.kt      # Estados de UI
│   └── ...
├── domain/                      # Capa de Dominio (Lógica de Negocio)
│   ├── repository/             # Contratos de repositorios
│   │   ├── UserRepository.kt
│   │   ├── ReservationRepository.kt
│   │   └── VitalSignsRepository.kt
│   └── usecase/                # Casos de uso
│       ├── UserUseCases.kt
│       ├── ReservationUseCases.kt
│       └── VitalSignsUseCases.kt
├── data/                        # Capa de Datos
│   ├── repository/             # Implementaciones de repositorios
│   │   ├── UserRepositoryImpl.kt
│   │   └── UserRepositoryRoomImpl.kt
│   └── local/                  # Fuentes de datos locales
│       ├── SharedPreferencesManager.kt
│       └── room/               # Room Database
│           ├── VitalCareDatabase.kt
│           ├── Daos.kt
│           ├── Entities.kt
│           └── Mappers.kt
├── model/                       # Modelos del Dominio
│   └── Entities.kt             # User, Reservation, VitalSigns
└── di/                          # Inyección de Dependencias
    └── AppModule.kt

app/src/test/                    # Pruebas Unitarias
├── ui/
│   ├── HomeViewModelTest.kt
│   └── form/
│       ├── FormViewModelTest.kt
│       └── FormValidatorsTest.kt
├── domain/usecase/
│   ├── UserUseCasesTest.kt
│   └── VitalSignsUseCasesTest.kt
└── data/repository/
    ├── UserRepositoryImplTest.kt
    └── ReservationRepositoryImplTest.kt
```

## 🌿 Estrategia de Ramas (Git Flow)

### Ramas Principales
- **`main`** → Código en producción, siempre estable
- **`dev`** → Rama de desarrollo, integración continua
- **`qa`** → Rama de pruebas y validación antes de producción

### Ramas de Trabajo (Feature Branches)
Formato: `feature/descripcion-breve` o `nombre-dev`

Ejemplos:
```bash
feature/login-screen
feature/vital-signs-monitoring
AngelApp
MariaApp
EstebanApp
```

### Ramas de Corrección
```bash
fix/bug-descripcion
hotfix/critical-issue
```

### Flujo de Trabajo

1. **Crear rama de trabajo desde `dev`**:
```bash
git checkout dev
git pull origin dev
git checkout -b feature/nueva-funcionalidad
```

2. **Desarrollar y hacer commits**:
```bash
git add .
git commit -m "feat(ui): implementa nueva funcionalidad"
```

3. **Actualizar con cambios de dev**:
```bash
git checkout dev
git pull origin dev
git checkout feature/nueva-funcionalidad
git merge dev
```

4. **Subir cambios**:
```bash
git push origin feature/nueva-funcionalidad
```

5. **Crear Pull Request**:
   - Desde `feature/nueva-funcionalidad` hacia `dev`
   - Asignar reviewers
   - Esperar aprobación
   - Merge con squash (opcional)

6. **Después del merge**:
```bash
git checkout dev
git pull origin dev
git branch -d feature/nueva-funcionalidad
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
- Android Studio Hedgehog o superior
- JDK 17+
- SDK de Android (API 24-34)
- Git configurado

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
   - Instalar SDK Platform 34
   - Instalar Build Tools 34.0.0

4. **Sincronizar dependencias**:
```bash
./gradlew build
```

5. **Ejecutar la aplicación**:
   - Conectar dispositivo o iniciar emulador
   - Run → Run 'app'

### Variables de Entorno (Opcional)
Crear archivo `local.properties`:
```properties
sdk.dir=/path/to/Android/sdk
MAPS_API_KEY=your_google_maps_key
```

## 🧪 Ejecutar Pruebas

### Pruebas Unitarias
```bash
# Todas las pruebas
./gradlew test

# Tests específicos
./gradlew testDebugUnitTest

# Con reporte de cobertura
./gradlew testDebugUnitTest jacocoTestReport
```

### Pruebas de Instrumentación
```bash
./gradlew connectedAndroidTest
```

### Ver Reportes
Los reportes se generan en:
- `app/build/reports/tests/testDebugUnitTest/index.html`
- `app/build/reports/coverage/`

## 📋 Historias de Usuario Implementadas

- ✅ **HU-02**: Registro de Usuario con Validaciones Visuales
- ✅ **HU-05**: Persistencia Local de Datos
- ✅ **HU-10**: Documentación y Control de Versiones
- 🔄 **HU-03**: Visualización de Signos Vitales (En progreso)
- 🔄 **HU-15**: Historial de Alertas (En progreso)

## 📚 Documentación Adicional

- [Plantilla de Commits](COMMIT_TEMPLATE.md)
- [Mensajes de Commit por HU](COMMIT_MESSAGES_HU.md)
- [Guía de Validaciones](app/src/main/java/cl/duoc/app/ui/form/VALIDATIONS_GUIDE.md)
- [Configuración de Room](app/src/main/java/cl/duoc/app/data/local/README.md)

## 🔗 Enlaces Útiles

- **Trello**: [Board del Proyecto](https://trello.com/b/IQyXpKND/mind-clinic)
- **Repositorio**: [GitHub - VitalCareApp](https://github.com/nyu-bit/VitalCareApp)
- **Documentación Android**: [Android Developers](https://developer.android.com/)
- **Jetpack Compose**: [Compose Documentation](https://developer.android.com/jetpack/compose)

## 🤝 Proceso de Colaboración

### Code Review
1. Todo código debe pasar por Pull Request
2. Mínimo 1 aprobación requerida
3. Tests deben pasar
4. Sin conflictos con rama base

### Estándares de Código
- Seguir [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Documentar funciones públicas
- Máximo 300 líneas por archivo
- Nombres descriptivos en español para dominio

### Meetings
- **Daily Standup**: Lunes, Miércoles, Viernes 10:00 AM
- **Planning**: Inicio de cada sprint
- **Retrospective**: Fin de cada sprint

## 📊 Estado del Proyecto

**Última actualización**: Noviembre 8, 2025

- **Commits totales**: 10+
- **Tests**: 100+ pruebas unitarias
- **Cobertura**: ~85%
- **Líneas de código**: ~5,500+

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
