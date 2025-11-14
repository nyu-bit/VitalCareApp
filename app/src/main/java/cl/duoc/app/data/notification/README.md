# Módulo de Recordatorios de Citas

## 📋 Descripción

Módulo que implementa notificaciones de recordatorio para citas médicas. Los usuarios recibirán una notificación 1 hora antes de su cita, con información del profesional y especialidad.

**Características principales:**
- ✅ Notificaciones incluso cuando la app está cerrada (WorkManager)
- ✅ Recordatorio automático 1 hora antes de la cita
- ✅ Información completa: Doctor, especialidad, fecha y hora
- ✅ Permisos configurados para Android 13+
- ✅ Arquitectura limpia (Data, Domain, UI)

## 🏗️ Arquitectura

### Data Layer

#### `ReminderRepositoryImpl.kt`
Implementación concreta del repositorio de recordatorios. Gestiona:
- Almacenamiento en memoria de recordatorios
- CRUD de recordatorios
- Obtención de recordatorios pendientes

#### `ReminderNotificationManager.kt`
Gestor de notificaciones que:
- Crea canales de notificación (Android 8.0+)
- Construye notificaciones con información formateada
- Envía notificaciones al usuario

#### `AppointmentReminderWorker.kt`
Worker de WorkManager que:
- Se ejecuta periódicamente en background
- Verifica recordatorios pendientes
- Envía notificaciones automáticas
- Funciona incluso con la app cerrada

### Domain Layer

#### `ReminderRepository.kt` (Interfaz)
Define el contrato de operaciones de recordatorios:
- Crear, actualizar, eliminar recordatorios
- Obtener recordatorios por usuario o reserva
- Marcar recordatorios como notificados

#### `ReminderUseCases.kt`
Casos de uso de negocio:

1. **ScheduleAppointmentReminderUseCase**
   - Valida la reserva
   - Calcula tiempo de recordatorio (1 hora antes)
   - Programa el worker periódico

2. **CancelReminderUseCase**
   - Cancela un recordatorio específico

3. **GetUpcomingRemindersUseCase**
   - Obtiene recordatorios próximos del usuario

4. **CancelReminderByReservationUseCase**
   - Cancela recordatorio por ID de reserva

### UI Layer

#### `RemindersViewModel.kt`
ViewModel que:
- Coordina los casos de uso
- Gestiona estados de carga, éxito y error
- Expone StateFlow para observar cambios

#### `RemindersScreen.kt`
Pantalla Compose que:
- Muestra lista de recordatorios próximos
- Permite cancelar recordatorios
- Muestra estados de carga y errores
- Información completa de cada cita

## 📦 Dependencias Agregadas

En `gradle/libs.versions.toml`:
```toml
workRuntime = "2.8.1"
notificationCompat = "1.6.1"
```

En `app/build.gradle.kts`:
```kotlin
implementation(libs.androidx.work.runtime)
implementation(libs.androidx.core.notification)
```

## 🔐 Permisos Configurados

En `AndroidManifest.xml`:
```xml
<!-- Permisos para notificaciones -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

**Nota:** Para Android 13+, se requiere solicitar `POST_NOTIFICATIONS` en tiempo de ejecución.

## 🔄 Flujo de Funcionamiento

### 1. Programar Recordatorio
```
Usuario crea reserva
    ↓
scheduleReminder(reservationId)
    ↓
Valida reserva futura
    ↓
Calcula: reminderTime = appointmentTime - 1 hora
    ↓
Crea AppointmentReminder
    ↓
Programa worker periódico
```

### 2. Envío de Notificación (Background)
```
WorkManager ejecuta AppointmentReminderWorker cada 15 min
    ↓
Verifica recordatorios pendientes (reminderTime <= ahora)
    ↓
Obtiene info de la reserva
    ↓
ReminderNotificationManager muestra notificación
    ↓
Marca recordatorio como notificado
```

## 📱 Cómo Usar

### 1. Solicitar permiso de notificaciones (Android 13+)

```kotlin
// En MainActivity o donde sea apropiado
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissions(
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        REQUEST_CODE
    )
}
```

### 2. Programar un recordatorio

```kotlin
val viewModel = RemindersViewModel(workManager)

// Cuando usuario crea una reserva
viewModel.scheduleReminder(reservationId)
```

### 3. Mostrar pantalla de recordatorios

```kotlin
RemindersScreen(
    userId = currentUserId,
    viewModel = viewModel
)
```

### 4. Cancelar recordatorio

```kotlin
viewModel.cancelReminder(reminderId)
// O por ID de reserva
viewModel.cancelReminderByReservation(reservationId)
```

## 🧪 Ejemplo de Entidad

```kotlin
// Crear una reserva
val reservation = Reservation(
    id = "res_001",
    userId = "user_001",
    specialty = "Cardiología",
    doctorName = "Juan Pérez",
    date = System.currentTimeMillis() + (2 * 60 * 60 * 1000), // 2 horas después
    status = ReservationStatus.CONFIRMED
)

// Programar recordatorio (se enviará en 1 hora)
viewModel.scheduleReminder(reservation.id)
```

## 🔧 Configuración de WorkManager

El worker se programa con:
- **Intervalo:** 15 minutos (verificar cada 15 min si hay recordatorios)
- **Estrategia:** Verificación única y continua con `ExistingPeriodicWorkPolicy.KEEP`
- **Reintentos:** Exponential backoff (reintenta con delays crecientes)

## ⚠️ Consideraciones Importantes

1. **Permisos en Runtime:** Para Android 13+, solicitar `POST_NOTIFICATIONS` dinámicamente
2. **Battery Optimization:** WorkManager respeta optimizaciones de batería del sistema
3. **Pruebas:** Usar adb para acelerar WorkManager en desarrollo:
   ```bash
   adb shell cmd jobscheduler run -u 0 -j 999 org.example.app
   ```

4. **Zona Horaria:** Los timestamps se almacenan en UTC internamente

## 📝 Entidades del Modelo

```kotlin
data class AppointmentReminder(
    val id: String,
    val reservationId: String,
    val userId: String,
    val reminderTime: Long,
    val workerId: String? = null,
    val isNotified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

## 🚀 Próximas Mejoras Sugeridas

1. Persistencia en Room Database
2. Integración con API remota
3. Múltiples recordatorios por cita (30 min, 1 hora, etc.)
4. Preferencias de notificación por usuario
5. Historial de notificaciones enviadas
6. Tests unitarios y de integración

## 📂 Estructura de Archivos

```
app/src/main/java/cl/duoc/app/
├── data/
│   ├── notification/
│   │   ├── ReminderNotificationManager.kt
│   │   └── AppointmentReminderWorker.kt
│   └── repository/
│       └── ReminderRepositoryImpl.kt
├── domain/
│   ├── repository/
│   │   └── ReminderRepository.kt
│   └── usecase/
│       └── ReminderUseCases.kt
├── model/
│   └── Entities.kt (con AppointmentReminder)
└── ui/
    ├── RemindersViewModel.kt
    └── reminders/
        └── RemindersScreen.kt
```

---

**Criterios de Aceptación Implementados:**
- ✅ Permisos de notificación configurados
- ✅ Mensaje con fecha, hora y profesional
- ✅ Notificación funcional incluso si app está cerrada

