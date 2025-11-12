# Plantilla de Mensajes de Commit

## Formato Estándar

```
<tipo>(<ámbito>): <descripción breve>

<cuerpo opcional: detalles de los cambios>

<footer opcional: referencias a issues>
```

## Tipos de Commit

- **feat**: Nueva funcionalidad
- **fix**: Corrección de bugs
- **docs**: Cambios en documentación
- **style**: Cambios de formato (no afectan la lógica)
- **refactor**: Refactorización de código
- **test**: Agregar o modificar tests
- **chore**: Tareas de mantenimiento
- **perf**: Mejoras de rendimiento
- **ci**: Cambios en CI/CD
- **build**: Cambios en el sistema de build

## Ámbitos Comunes

- **ui**: Interfaz de usuario
- **data**: Capa de datos
- **domain**: Lógica de negocio
- **model**: Modelos de datos
- **di**: Inyección de dependencias
- **validation**: Validaciones
- **repository**: Repositorios
- **usecase**: Casos de uso
- **database**: Base de datos
- **api**: API o servicios remotos

## Ejemplos de Mensajes de Commit

### Ejemplo 1: Nueva Funcionalidad
```
feat(ui): pantalla de registro con validaciones visuales

- Implementa FormScreen con Jetpack Compose
- Agrega FormViewModel con StateFlow
- Incluye validaciones en tiempo real
- Maneja estados de loading y error
- Feedback visual inmediato para el usuario

Closes #HU-02
```

### Ejemplo 2: Corrección de Bug
```
fix(validation): corrige validación de RUT chileno

El dígito verificador no se calculaba correctamente
para RUTs con números menores a 1 millón.

Fixes #123
```

### Ejemplo 3: Refactorización
```
refactor(repository): separa implementaciones en memoria y Room

- Crea UserRepositoryImpl para almacenamiento en memoria
- Crea UserRepositoryRoomImpl para persistencia con Room
- Mantiene misma interfaz para ambas implementaciones
- Facilita testing y cambio entre implementaciones

Related to #45
```

### Ejemplo 4: Agregar Tests
```
test(usecase): agrega pruebas unitarias para casos de uso

- UserUseCasesTest con 15 tests
- VitalSignsUseCasesTest con 12 tests
- Cobertura de flujos exitosos y errores
- Validaciones de lógica de negocio

Closes #78
```

### Ejemplo 5: Documentación
```
docs(architecture): documenta estructura Clean Architecture

Agrega README.md en cada capa explicando:
- Responsabilidades de cada capa
- Separación de concerns
- Flujo de datos entre capas
- Ejemplos de uso

Closes #HU-10
```

### Ejemplo 6: Múltiples Cambios
```
feat(data): implementa persistencia local con Room y SharedPreferences

Base de datos:
- VitalCareDatabase con 3 tablas
- DAOs para User, Reservation, VitalSigns
- Mappers entre entidades Room y dominio

SharedPreferences:
- Manager genérico con serialización JSON
- Operaciones para tipos primitivos y objetos

Repositorios:
- Implementaciones Room para cada entidad
- Soporte para Flow reactivo
- Patrón Singleton para database

Closes #HU-05
```

### Ejemplo 7: Breaking Change
```
refactor(repository)!: cambia interfaz de UserRepository

BREAKING CHANGE: El método saveUser ahora retorna Result<Boolean>
en lugar de Boolean directamente, para mejor manejo de errores.

Migración:
```kotlin
// Antes
val success = repository.saveUser(user)

// Ahora
val result = repository.saveUser(user)
if (result.isSuccess) { ... }
```

Closes #89
```

## Mensajes de Commit para el Proyecto VitalCareApp

### Commit 1: Arquitectura Base
```
feat(architecture): implementa estructura Clean Architecture

Capas:
- ui/: Pantallas, ViewModels y estados de UI
- data/: Repositorios e implementaciones
- domain/: Casos de uso y contratos de repositorios
- model/: Entidades del dominio
- di/: Módulos de inyección de dependencias

Cada capa incluye README.md con documentación
de responsabilidades y estructura.

Closes #001
```

### Commit 2: Sistema de Formularios
```
feat(ui): implementa pantalla de formulario con patrón MVVM

Componentes:
- FormScreen: UI con Jetpack Compose
- FormViewModel: Gestión de estado con StateFlow
- FormUiState: Estado consolidado del formulario
- FormUiEvent: Eventos de usuario (sealed class)

Características:
- Validaciones en tiempo real
- Manejo de errores por campo
- Estados de loading y éxito
- Visibilidad de contraseñas
- Campos: nombre, email, password, confirmPassword

Closes #002
```

### Commit 3: ViewModels
```
feat(ui): desarrolla ViewModels con StateFlow y LiveData

HomeViewModel:
- Gestión de contador con operaciones
- Carga asíncrona de datos de usuario
- Exposición de estado con StateFlow
- Manejo de errores

ExampleViewModel:
- Lista de tareas con CRUD
- Filtrado dinámico
- Uso de StateFlow y LiveData
- Contadores reactivos

Demuestra patrones de arquitectura MVVM y
exposición de datos reactivos a la UI.

Closes #003
```

### Commit 4: Sistema de Validaciones
```
feat(validation): implementa validaciones personalizadas reutilizables

FormValidators:
- Validación de email (básico y estricto)
- Contraseñas con requisitos configurables
- Coincidencia de contraseñas
- Longitud mínima/máxima
- Solo letras, solo números
- Teléfono chileno (formato +56 9XXXXXXXX)
- RUT chileno con dígito verificador
- Rangos numéricos
- Patrones regex personalizados
- Composición de validadores

ReactiveFormViewModel:
- Validaciones en tiempo real mientras se escribe
- Estado consolidado de validez del formulario
- Feedback inmediato al usuario

Incluye guía de uso con ejemplos.

Closes #004
```

### Commit 5: Repositorios y Casos de Uso
```
feat(domain): implementa repositorios y casos de uso

Interfaces (domain/repository/):
- UserRepository
- ReservationRepository
- VitalSignsRepository

Implementaciones (data/repository/):
- UserRepositoryImpl (en memoria)
- ReservationRepositoryImpl (en memoria)
- VitalSignsRepositoryImpl (en memoria)

Casos de Uso (domain/usecase/):
- GetUserUseCase, SaveUserUseCase, DeleteUserUseCase
- CreateReservationUseCase, CancelReservationUseCase
- SaveVitalSignsUseCase, CalculateRiskLevelUseCase
- Validaciones de negocio en cada use case

Modelos (model/):
- User, Reservation, VitalSigns
- Enums: ReservationStatus, RiskLevel

Separación completa entre capas siguiendo
principios SOLID y Clean Architecture.

Closes #005
```

### Commit 6: Persistencia Local
```
feat(data): configura Room Database y SharedPreferences

Room Database:
- VitalCareDatabase con 3 tablas
- Entidades: UserEntity, ReservationEntity, VitalSignsEntity
- DAOs con operaciones CRUD completas
- Soporte para Flow reactivo
- Mappers entre entidades Room y dominio
- Patrón Singleton

SharedPreferences:
- SharedPreferencesManager genérico
- Serialización JSON con Gson
- Operaciones para primitivos y objetos complejos
- Guardar/leer listas

Repositorios Room:
- UserRepositoryRoomImpl
- ReservationRepositoryRoomImpl
- VitalSignsRepositoryRoomImpl

Incluye README con guía de configuración,
dependencias necesarias y ejemplos de uso.

Closes #HU-05
```

### Commit 7: Pruebas Unitarias
```
test: implementa suite completa de pruebas unitarias

Tests de Validadores (20+ tests):
- FormValidatorsTest cubre todos los validadores
- Casos exitosos y errores
- Validaciones especiales (RUT, teléfono)

Tests de ViewModels (27+ tests):
- FormViewModelTest
- HomeViewModelTest
- Manejo de estados y eventos
- Operaciones asíncronas con runTest

Tests de Repositorios (25+ tests):
- UserRepositoryImplTest
- ReservationRepositoryImplTest
- Operaciones CRUD
- Casos de error

Tests de Casos de Uso (27+ tests):
- UserUseCasesTest
- VitalSignsUseCasesTest
- Validaciones de negocio
- Cálculo de niveles de riesgo

Total: 100+ tests unitarios con JUnit
Cobertura de flujos exitosos y manejo de errores.

Closes #007
```

## Reglas para Buenos Commits

✅ **DO:**
- Usa verbos en imperativo (agrega, corrige, implementa)
- Sé específico y descriptivo
- Separa cambios no relacionados en commits diferentes
- Referencia issues con Closes, Fixes, Related to
- Usa el cuerpo para explicar el "por qué" y "cómo"
- Limita la primera línea a 72 caracteres

❌ **DON'T:**
- Commits muy grandes con cambios no relacionados
- Mensajes vagos como "fix bug" o "update code"
- Commits con código comentado o debug
- Mezclar refactoring con nuevas features
- Olvidar referenciar el issue relacionado

## Comandos Git Útiles

```bash
# Commit con mensaje completo
git commit -m "feat(ui): título" -m "Detalles del cambio" -m "Closes #123"

# Ver últimos commits
git log --oneline -10

# Modificar último commit
git commit --amend

# Ver cambios por confirmar
git diff --staged
```
