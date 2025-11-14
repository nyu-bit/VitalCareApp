# Implementación de Historias de Usuario - VitalCare

## Resumen

Se han implementado exitosamente **4 historias de usuario** siguiendo Clean Architecture y las mejores prácticas del proyecto existente.

---

## Historia de Usuario 1: Visualizar Centro de Salud Mental en Mapa

**Criterios de Aceptación:**
- ✅ Uso de Google Maps API
- ✅ Permisos de ubicación solicitados y gestionados
- ✅ Mapa visible e integrado en UI

**Archivos Creados/Modificados:**
- `HealthCenterMapViewModel.kt` - Gestiona estado del mapa
- `HealthCenterMapScreen.kt` - UI con Google Maps integrado
- `LocationRepository.kt` - Interfaz de datos
- `LocationRepositoryImpl.kt` - Implementación con FusedLocationProvider
- `LocationUseCases.kt` - Casos de uso
- `PermissionComponents.kt` - Manejo de permisos de ubicación

**Características:**
- Obtiene ubicación del centro de salud desde Room DB
- Solicita y gestiona permisos de ubicación en runtime
- Muestra marcador del centro en el mapa
- Muestra marcador de ubicación del usuario
- Panel de información con detalles del centro
- Botones para obtener dirección y llamar

---

## Historia de Usuario 2: Visualizar y Actualizar Datos Personales

**Criterios de Aceptación:**
- ✅ Mostrar datos registrados
- ✅ Permitir edición y validación
- ✅ Actualización reflejada en persistencia local

**Archivos Creados/Modificados:**
- `UserProfileViewModel.kt` - Gestiona perfil y modo edición
- `UserProfileScreen.kt` - UI con formulario de edición
- `Entities.kt` - Modelos extendidos con ubicación

**Características:**
- Carga datos del usuario desde Room DB
- Modo lectura y modo edición alternables
- Validación de email, teléfono y RUT
- Guardado persistente en SharedPreferences y Room
- Mensajes de éxito y error
- Campos editables: nombre, email, teléfono, RUT, fecha de nacimiento, dirección

---

## Historia de Usuario 3: Ver Ubicación del Paciente (Para Tutores)

**Criterios de Aceptación:**
- ✅ Mapa integrado con marcador simulado
- ✅ Puede centrarse la vista en el paciente
- ✅ Usa permisos de GPS locales

**Archivos Creados/Modificados:**
- `PatientLocationMapViewModel.kt` - Gestiona ubicación del paciente
- `PatientLocationMapScreen.kt` - UI con mapa y controles de zoom
- `LocationRepository.kt` & `LocationRepositoryImpl.kt` - Obtiene ubicación

**Características:**
- Obtiene última ubicación conocida del paciente
- Simula ubicación si no hay datos reales (para testing)
- Mapa con marcador del paciente
- Controles de zoom in/out
- Botón para centrar vista en el paciente
- Panel que muestra coordenadas, precisión y timestamp
- Función de actualizar/refrescar ubicación

---

## Historia de Usuario 4: Recibir Notificación SOS

**Criterios de Aceptación:**
- ✅ Se genera evento "SOS activado" (simulado)
- ✅ Se muestra notificación local destacada
- ✅ Se registra en historial

**Archivos Creados/Modificados:**
- `SOSViewModel.kt` - Gestiona eventos SOS
- `SOSScreen.kt` - UI con botón SOS y historial
- `SOSRepository.kt` & `SOSRepositoryImpl.kt` - Datos SOS
- `SOSUseCases.kt` - Casos de uso
- `Entities.kt` - Entidad SOSEvent
- `NotificationManager.kt` - Gestiona notificaciones locales
- `Daos.kt` & `Entities.kt` (Room) - Persistencia de SOS

**Características:**
- Botón SOS grande y destacado (rojo) en la pantalla
- Captura ubicación actual al activar SOS
- Crea evento SOS con timestamp y ubicación
- Muestra notificación local en pantalla completa
- Notificación con vibración y luces LED
- Historial de eventos SOS ordenados por fecha
- Estadosde SOS: TRIGGERED, ACKNOWLEDGED, RESOLVED
- Funciones para reconocer y resolver eventos

---

## Cambios en Archivos Existentes

### `gradle/libs.versions.toml`
- Agregadas versiones para: Google Maps, Room, Location Services, Accompanist Permissions, Gson

### `app/build.gradle.kts`
- Agregadas dependencias: Play Services Maps, Maps Compose, Room, Accompanist Permissions

### `AndroidManifest.xml`
- Permisos: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `POST_NOTIFICATIONS`, `INTERNET`

### `model/Entities.kt`
- Agregadas entidades: `LocationData`, `HealthCenter`, `SOSEvent`

### `data/local/room/Entities.kt`
- Agregadas entidades Room: `SOSEventEntity`, `HealthCenterEntity`

### `data/local/room/Daos.kt`
- Agregados DAOs: `SOSEventDao`, `HealthCenterDao`

### `data/local/room/Mappers.kt`
- Agregados mappers para `SOSEvent` y `HealthCenter`

### `data/local/room/VitalCareDatabase.kt`
- Agregadas nuevas entidades y DAOs a la BD

---

## Estructura de Carpetas Creada

```
app/src/main/java/cl/duoc/app/
├── domain/
│   ├── repository/
│   │   ├── LocationRepository.kt (NUEVO)
│   │   └── SOSRepository.kt (NUEVO)
│   └── usecase/
│       ├── LocationUseCases.kt (NUEVO)
│       └── SOSUseCases.kt (NUEVO)
├── data/
│   ├── repository/
│   │   ├── LocationRepositoryImpl.kt (NUEVO)
│   │   └── SOSRepositoryImpl.kt (NUEVO)
│   ├── notification/
│   │   └── NotificationManager.kt (NUEVO)
│   └── TestDataGenerator.kt (NUEVO)
└── ui/
    ├── components/
    │   └── PermissionComponents.kt (NUEVO)
    └── screens/
        ├── profile/
        │   ├── UserProfileViewModel.kt (NUEVO)
        │   └── UserProfileScreen.kt (NUEVO)
        ├── map/
        │   ├── HealthCenterMapViewModel.kt (NUEVO)
        │   ├── HealthCenterMapScreen.kt (NUEVO)
        ├── map/
        │   ├── PatientLocationMapViewModel.kt (NUEVO)
        │   ├── PatientLocationMapScreen.kt (NUEVO)
        └── sos/
            ├── SOSViewModel.kt (NUEVO)
            └── SOSScreen.kt (NUEVO)
```

---

## Próximos Pasos

### Para Integración en Producción:

1. **Configurar Google Maps API Key**
   - Obtener API Key desde Google Cloud Console
   - Agregar en `AndroidManifest.xml`:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="TU_API_KEY_AQUI" />
     ```

2. **Implementar Navegación**
   - Integrar con `Navigation Compose`
   - Agregar rutas a las nuevas pantallas

3. **Inicializar Centros de Salud en BD**
   - Usar `TestDataGenerator.generateHealthCenters()`
   - Insertar en Room en el inicializador de la app

4. **Pruebas Unitarias e Integración**
   - Crear tests para ViewModels
   - Crear tests para Repositorios

5. **Configurar WorkManager para Notificaciones**
   - Implementar tareas periódicas para reminders
   - Configurar canales de notificación

---

## Consideraciones Técnicas

- ✅ **Clean Architecture**: Separación clara de capas (domain, data, ui)
- ✅ **StateFlow & ViewModel**: Gestión reactiva del estado
- ✅ **Room Database**: Persistencia local confiable
- ✅ **Coroutines**: Operaciones asincrónicas
- ✅ **Jetpack Compose**: UI moderna declarativa
- ✅ **Permisos Runtime**: Solicitud segura de permisos
- ✅ **Notificaciones Canales**: Cumple con Android 8+

---

## Notas de Testing

Para probar las funcionalidades sin GPS real:

1. **Usar emulador de Android Studio** con GPS simulado
2. **LocationRepositoryImpl** retorna `null` si no hay permisos - se activa manejo graceful
3. **PatientLocationMapViewModel** simula ubicación si no hay datos reales
4. **SOSViewModel** captura ubicación actual o simula con Santiago, Chile

---

## Licencia

Mismo que el proyecto VitalCareApp

