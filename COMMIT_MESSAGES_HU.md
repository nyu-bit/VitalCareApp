# Mensajes de Commit por Historia de Usuario

Este archivo contiene mensajes de commit específicos para cada Historia de Usuario (HU) del proyecto VitalCareApp.

## HU-02: Registro de Usuario con Validaciones Visuales

### Commit Principal
```
feat(ui): pantalla de registro con validaciones visuales

Implementación:
- FormScreen con Jetpack Compose
- Campos: nombre, correo, contraseña, confirmación
- FormViewModel con patrón MVVM
- FormUiState para estado consolidado
- FormUiEvent para manejo de eventos

Validaciones:
- Todos los campos completos
- Email con formato válido (regex)
- Coincidencia de contraseñas
- Mensajes de error bajo cada campo
- Validación en tiempo real

Características:
- Estados de loading durante procesamiento
- Feedback visual inmediato
- Limpieza de errores al editar campos
- Visibilidad de contraseñas (toggle)

Tecnologías:
- Jetpack Compose para UI
- StateFlow para estado reactivo
- Coroutines para operaciones asíncronas

Closes #HU-02
```

## HU-03: Visualización de Signos Vitales

### Commit Principal
```
feat(ui): visualización en tiempo real de signos vitales

Pantalla VitalSignsScreen:
- Muestra frecuencia cardíaca (bpm)
- Muestra presión arterial (sistólica/diastólica)
- Muestra oxigenación (%)
- Actualización periódica cada 5 segundos

Indicadores de Riesgo:
- Verde: Valores normales
- Amarillo: Valores de advertencia
- Rojo: Valores peligrosos

VitalSignsViewModel:
- Generación de datos simulados
- Cálculo de nivel de riesgo con CalculateRiskLevelUseCase
- Exposición de estado con StateFlow
- Timer para actualización automática

Lógica de Riesgo:
- NORMAL: Todos los valores en rango saludable
- WARNING: Algún valor en rango de precaución
- DANGER: Algún valor crítico

Closes #HU-03
```

### Commit de Tests
```
test(vitalsigns): pruebas para cálculo de nivel de riesgo

Tests incluidos:
- Valores normales retornan NORMAL
- Valores borderline retornan WARNING
- Valores críticos retornan DANGER
- Oxígeno bajo marca DANGER inmediatamente
- Validación de rangos en SaveVitalSignsUseCase

Cobertura completa de lógica de negocio
para evaluación de signos vitales.

Related to #HU-03
```

## HU-05: Persistencia Local de Datos

### Commit Principal
```
feat(data): persistencia local con Room de usuario y reservas

Room Database:
- VitalCareDatabase con patrón Singleton
- UserEntity, ReservationEntity, VitalSignsEntity
- DAOs con operaciones CRUD completas
- Mappers entre entidades Room y dominio

SharedPreferences:
- SharedPreferencesManager para configs simples
- Serialización JSON con Gson
- Operaciones para tipos primitivos y objetos

Repositorios:
- UserRepositoryRoomImpl
- ReservationRepositoryRoomImpl
- VitalSignsRepositoryRoomImpl
- Soporte para Flow reactivo

Características:
- Datos persisten al cerrar la app
- Recuperación automática al abrir
- Observación de cambios en tiempo real
- Inicialización en Application

Separación de capas:
- data/ contiene implementaciones
- domain/ solo conoce interfaces
- Inversión de dependencias respetada

Closes #HU-05
```

### Commit de Configuración
```
chore(data): configura dependencias de Room y Gson

build.gradle.kts:
- Room 2.6.0 con KSP
- Room KTX para coroutines
- Gson 2.10.1 para serialización

plugins:
- KSP para procesamiento de anotaciones Room

Incluye README con instrucciones de setup
y ejemplos de uso de Room y SharedPreferences.

Related to #HU-05
```

## HU-10: Documentación y Control de Versiones

### Commit Principal
```
docs(process): guía de control de versiones y colaboración

COMMIT_TEMPLATE.md:
- Formato estándar de commits
- Tipos: feat, fix, docs, test, refactor, etc.
- Ámbitos por capa de la arquitectura
- 10+ ejemplos reales del proyecto
- Reglas de buenos commits

Convenciones:
- Mensajes descriptivos en imperativo
- Cuerpo explicando el "por qué"
- Footer con referencias a issues
- Formato: tipo(ámbito): descripción

Ejemplos por HU:
- HU-02: Registro de usuario
- HU-03: Signos vitales
- HU-05: Persistencia
- HU-10: Documentación
- HU-15: Historial de alertas

Flujo de trabajo:
- Branching strategy (main, feature branches)
- Proceso de PR y code review
- Versionado semántico
- Comandos Git útiles

Closes #HU-10
```

### Commit de READMEs
```
docs(architecture): documenta estructura y responsabilidades

README.md en cada capa:
- ui/: Responsabilidades de la capa de presentación
- data/: Implementaciones y fuentes de datos
- domain/: Lógica de negocio pura
- model/: Entidades del dominio
- di/: Inyección de dependencias

Guías específicas:
- data/local/README.md: Configuración de Room
- ui/form/VALIDATIONS_GUIDE.md: Sistema de validaciones

Cada README incluye:
- Descripción de la capa
- Responsabilidades
- Ejemplos de uso
- Mejores prácticas

Related to #HU-10
```

## HU-15: Historial de Alertas

### Commit Principal
```
feat(alerts): historial de alertas con almacenamiento local

AlertHistoryScreen:
- Lista de alertas con tipo, fecha y hora
- Diseño en LazyColumn para performance
- Iconos por tipo de alerta
- Colores según severidad

AlertEntity (Room):
- id, userId, type, message, timestamp
- Índice en timestamp para queries rápidas

AlertDao:
- getAlertsByUserId ordenadas por fecha DESC
- deleteAllAlerts para limpieza
- observeAlerts con Flow reactivo

AlertHistoryViewModel:
- Carga de historial desde repositorio
- Opción de limpiar todo el historial
- Estados: loading, success, empty
- Confirmación antes de borrar

Funcionalidades:
- Ver historial completo
- Filtrar por tipo (opcional)
- Limpiar historial con confirmación
- Persistencia con Room

Closes #HU-15
```

### Commit de UI
```
feat(ui): diseño de tarjetas para historial de alertas

AlertCard composable:
- Tipo de alerta con icono
- Mensaje descriptivo
- Fecha y hora formateadas
- Color según severidad (Error, Warning, Info)

AlertEmptyState:
- Mensaje cuando no hay alertas
- Icono ilustrativo
- Texto explicativo

Mejoras visuales:
- Material 3 cards
- Animaciones de entrada
- Swipe to dismiss (opcional)
- Pull to refresh

Related to #HU-15
```

## Commits de Mantenimiento

### Actualizar Dependencias
```
chore(deps): actualiza dependencias del proyecto

- Compose BOM 2024.01.00
- Kotlin 1.9.20
- Room 2.6.1
- Coroutines 1.7.3

Cambios de migración:
- Actualiza imports de Compose
- Ajusta compileSdk a 34

Todas las pruebas pasan correctamente.

Related to #maintenance
```

### Refactorización
```
refactor(repository): extrae interfaz común de repositorios

BaseRepository interface:
- Operaciones CRUD genéricas
- Métodos comunes a todos los repositorios

Beneficios:
- Reduce duplicación de código
- Facilita testing con mocks
- Mejora mantenibilidad

No hay cambios en funcionalidad.
```

### Fix de Bug
```
fix(validation): corrige validación de email con caracteres especiales

El validador rechazaba emails válidos con '+' en el nombre.
Actualiza regex para permitir caracteres especiales estándar.

Antes: user+tag@example.com → Rechazado
Ahora: user+tag@example.com → Aceptado

Fixes #156
```

## Comandos Git para Verificar

```bash
# Ver historial de commits
git log --oneline --graph --all

# Ver commits de una HU específica
git log --grep="HU-02"

# Ver estadísticas del commit
git show --stat <commit-hash>

# Ver cambios de archivos específicos
git log --follow -- path/to/file
```
