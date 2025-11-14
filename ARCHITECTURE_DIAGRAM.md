# 🏗️ ARQUITECTURA DEL MÓDULO DE RECORDATORIOS

## 📐 Diagrama General

```
┌─────────────────────────────────────────────────────────────────┐
│                          USUARIO/APP                             │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      UI LAYER (Presentación)                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  RemindersScreen.kt                    RemindersViewModel.kt    │
│  ┌────────────────────┐                 ┌──────────────────┐   │
│  │ - Lista recordatorios  ←─────────────→ - Coordina UCs   │   │
│  │ - Cancelar reminder │                 │ - Maneja estado │   │
│  │ - Mostrar errores   │                 │ - StateFlow     │   │
│  │ - Loading/Success   │                 └──────────────────┘   │
│  └────────────────────┘                                         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   DOMAIN LAYER (Lógica de Negocio)              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ReminderUseCases.kt                                            │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │ • ScheduleAppointmentReminderUseCase                      │ │
│  │   → Valida reserva futura                                │ │
│  │   → Calcula reminderTime = date - 1 hora                 │ │
│  │   → Crea AppointmentReminder                             │ │
│  │   → Programa worker periódico                            │ │
│  │                                                           │ │
│  │ • CancelReminderUseCase                                  │ │
│  │   → Elimina recordatorio específico                      │ │
│  │                                                           │ │
│  │ • GetUpcomingRemindersUseCase                            │ │
│  │   → Obtiene recordatorios próximos                       │ │
│  │   → Combina con info de reservas                         │ │
│  │                                                           │ │
│  │ • CancelReminderByReservationUseCase                     │ │
│  │   → Cancela por ID de reserva                            │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                  │
│  ReminderRepository.kt (Interface)                              │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │ • getReminderById()                                       │ │
│  │ • getRemindersByUserId()                                  │ │
│  │ • getReminderByReservationId()                            │ │
│  │ • createReminder()                                        │ │
│  │ • updateReminder()                                        │ │
│  │ • deleteReminder()                                        │ │
│  │ • getPendingReminders()                                   │ │
│  │ • markReminderAsNotified()                                │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     DATA LAYER (Infraestructura)                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ReminderRepositoryImpl.kt                                       │
│  ┌────────────────────────────┐                                │
│  │ Almacenamiento:            │                                │
│  │ - MutableStateFlow          │  ← En memoria                │
│  │ - Map<String, Reminder>     │  ← Futuro: Room DB          │
│  │                             │                                │
│  │ Operaciones CRUD            │                                │
│  │ - Create, Read, Update      │                                │
│  │ - Delete, List              │                                │
│  └────────────────────────────┘                                │
│                                                                  │
│  ReminderNotificationManager.kt                                 │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ • createNotificationChannel()                              ││
│  │   → Android 8.0+ (NotificationChannel)                    ││
│  │   → Importancia: HIGH                                      ││
│  │                                                             ││
│  │ • showAppointmentReminder()                                ││
│  │   → Formatea mensaje con doctor, hora, fecha              ││
│  │   → Crea NotificationCompat.Builder                        ││
│  │   → Envía notificación (incluso app cerrada)              ││
│  │                                                             ││
│  │ • cancelAppointmentReminder()                              ││
│  │   → Cancela notificación                                   ││
│  │                                                             ││
│  │ • Formato de fecha: SimpleDateFormat                       ││
│  │   → Hora: HH:mm                                            ││
│  │   → Fecha: dd/MM/yyyy                                      ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
│  AppointmentReminderWorker.kt (Background Service)              │
│  ┌────────────────────────────────────────────────────────────┐│
│  │ extends CoroutineWorker                                    ││
│  │                                                             ││
│  │ Ejecución:                                                 ││
│  │ • Periódica: cada 15 minutos (WorkManager)                ││
│  │ • Sin que app esté activa                                  ││
│  │ • Después de reinicio del dispositivo                      ││
│  │                                                             ││
│  │ Proceso:                                                    ││
│  │ 1. Obtiene recordatorios pendientes                         ││
│  │ 2. Para cada recordatorio:                                  ││
│  │    a. if reminderTime <= ahora:                             ││
│  │       - Obtiene info de reserva                             ││
│  │       - Muestra notificación                                ││
│  │       - Marca como notificado                               ││
│  │ 3. Result.success() o Result.retry()                        ││
│  │                                                             ││
│  │ Configuración:                                              ││
│  │ • BackoffPolicy.EXPONENTIAL                                ││
│  │ • ExistingPeriodicWorkPolicy.KEEP                          ││
│  └────────────────────────────────────────────────────────────┘│
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    EXTERNAL COMPONENTS                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Android System                                                 │
│  ├─ WorkManager (androidx.work)                                │
│  │  ├─ Programa tareas periódicas                             │
│  │  ├─ Gestiona background execution                          │
│  │  └─ Respeta Doze Mode y batería                            │
│  │                                                              │
│  ├─ NotificationManager                                        │
│  │  ├─ Crea canales de notificación                           │
│  │  └─ Muestra notificaciones al usuario                      │
│  │                                                              │
│  └─ Manifest                                                    │
│     ├─ POST_NOTIFICATIONS (Android 13+)                       │
│     ├─ SCHEDULE_EXACT_ALARM                                    │
│     └─ RECEIVE_BOOT_COMPLETED                                  │
│                                                                  │
│  Entities (Models)                                              │
│  ├─ Reservation (existente)                                    │
│  └─ AppointmentReminder (nueva)                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Flujo de Datos

### 1️⃣ Crear Recordatorio (UserAction)

```
Usuario crea reserva
    ↓
RemindersScreen.scheduleReminder(reservationId)
    ↓
RemindersViewModel.scheduleReminder()
    ↓
ScheduleAppointmentReminderUseCase(reservationId)
    ├─ Validaciones:
    │  ├─ ID no vacío
    │  ├─ Reserva existe
    │  ├─ Fecha es futura
    │  └─ No existe recordatorio previo
    ├─ Cálculos:
    │  └─ reminderTime = reservation.date - 1 hora
    ├─ Creación:
    │  └─ AppointmentReminder(...)
    └─ Persistencia:
       └─ ReminderRepositoryImpl.createReminder()
           ↓
       MutableStateFlow.emit(recordatorios)
    
    Programmer Worker:
    └─ WorkManager.enqueueUniquePeriodicWork()
       └─ AppointmentReminderWorker cada 15 min
    
    Notificación UI:
    └─ successMessage = "Recordatorio programado"
```

### 2️⃣ Enviar Notificación (Background)

```
WorkManager Timer (cada 15 minutos)
    ↓
AppointmentReminderWorker.doWork()
    ├─ ReminderRepositoryImpl.getPendingReminders()
    │  └─ Filtra: !isNotified && reminderTime <= ahora
    ├─ Para cada recordatorio:
    │  ├─ ReservationRepositoryImpl.getReservationById()
    │  ├─ Obtiene: doctorName, date, specialty
    │  ├─ ReminderNotificationManager.showAppointmentReminder()
    │  │  └─ NotificationManager.notify()
    │  │     └─ Muestra en bandeja de notificaciones
    │  └─ ReminderRepositoryImpl.markReminderAsNotified()
    │
    └─ Result.success() o Result.retry()
    
    Usuario ve notificación:
    ┌─────────────────────────────────┐
    │ Recordatorio de Cita             │
    │ Tu cita con el Dr. Pérez es hoy │
    │ a las 14:30 (Cardiología)        │
    └─────────────────────────────────┘
```

### 3️⃣ Cancelar Recordatorio (UserAction)

```
Usuario toca botón Cancelar
    ↓
RemindersScreen.onCancelClick(reminderId)
    ↓
RemindersViewModel.cancelReminder(reminderId)
    ↓
CancelReminderUseCase(reminderId)
    ├─ Validaciones: ID no vacío
    └─ ReminderRepositoryImpl.deleteReminder()
       ├─ Elimina del Map
       └─ Emite cambio a StateFlow
    
    Notificación UI:
    └─ successMessage = "Recordatorio cancelado"
    
    Recarga lista:
    └─ RemindersScreen se actualiza
```

## 🔌 Integración de Componentes

```
┌────────────────────────────────────────────────────────┐
│                    MainActivity                         │
└────────────────────────────────────────────────────────┘
                      ↓
┌────────────────────────────────────────────────────────┐
│              Solicitar Permisos (Android 13+)           │
│         ActivityCompat.requestPermissions()             │
│         Manifest.permission.POST_NOTIFICATIONS          │
└────────────────────────────────────────────────────────┘
                      ↓
┌────────────────────────────────────────────────────────┐
│              Crear RemindersViewModel                   │
│  val workManager = WorkManager.getInstance(context)    │
│  val viewModel = RemindersViewModel(workManager)       │
└────────────────────────────────────────────────────────┘
                      ↓
┌────────────────────────────────────────────────────────┐
│           Mostrar RemindersScreen                       │
│  RemindersScreen(userId, viewModel)                    │
└────────────────────────────────────────────────────────┘
                      ↓
┌────────────────────────────────────────────────────────┐
│         Sistema de Notificaciones Android               │
│      (Funciona incluso con app cerrada)                 │
└────────────────────────────────────────────────────────┘
```

## 📦 Dependencias

```
androidx.work:work-runtime-ktx:2.8.1
    ├─ WorkManager (tareas periódicas)
    ├─ CoroutineWorker (async/await)
    └─ Manejo de background tasks

androidx.core:core:1.6.1
    ├─ NotificationCompat (compatibilidad)
    ├─ NotificationManager (mostrar notificaciones)
    └─ NotificationChannel (Android 8.0+)

androidx.lifecycle:lifecycle-* (existente)
    ├─ ViewModel
    └─ viewModelScope (Coroutines)

kotlinx.coroutines:* (existente)
    ├─ Dispatchers (threads)
    └─ StateFlow (observables)
```

## 🗂️ Estructura de Carpetas

```
VitalCareApp/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml (⭐ modificado)
│   │   ├── java/cl/duoc/app/
│   │   │   ├── data/
│   │   │   │   ├── notification/ (⭐ NUEVA)
│   │   │   │   │   ├── ReminderNotificationManager.kt
│   │   │   │   │   ├── AppointmentReminderWorker.kt
│   │   │   │   │   └── README.md
│   │   │   │   └── repository/
│   │   │   │       └── ReminderRepositoryImpl.kt (⭐ NUEVO)
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── repository/
│   │   │   │   │   └── ReminderRepository.kt (⭐ NUEVO)
│   │   │   │   └── usecase/
│   │   │   │       └── ReminderUseCases.kt (⭐ NUEVO)
│   │   │   │
│   │   │   ├── model/
│   │   │   │   └── Entities.kt (⭐ modificado)
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── RemindersViewModel.kt (⭐ NUEVO)
│   │   │   │   └── reminders/ (⭐ NUEVA)
│   │   │   │       └── RemindersScreen.kt
│   │   │   │
│   │   │   └── utils/
│   │   │       ├── ReminderTestDataGenerator.kt (⭐ NUEVO)
│   │   │       └── TestDataGenerator.kt (⭐ modificado)
│   │   │
│   ├── build.gradle.kts (⭐ modificado)
│   
├── gradle/
│   └── libs.versions.toml (⭐ modificado)
│
├── REMINDERS_INTEGRATION_GUIDE.md (⭐ NUEVO)
└── IMPLEMENTATION_CHECKLIST.md (⭐ NUEVO)
```

## 🎯 Puntos Clave de Diseño

### 1. Separación de Responsabilidades
- **UI:** Solo presenta datos y eventos
- **ViewModel:** Coordina UI y lógica
- **UseCase:** Reglas de negocio
- **Repository:** Abstracción de datos
- **Worker:** Tareas en background

### 2. Reactividad
- **StateFlow:** Cambios en estado UI
- **CoroutineWorker:** Tareas async
- **Suspendable functions:** Operaciones blocking

### 3. Persistencia (Diseño)
- Actualmente: En memoria (MutableStateFlow)
- Futuro: Room Database (sin cambios en API)
- Remoto: API Rest (sin cambios en Architecture)

### 4. Notificaciones
- **Canal:** Creado por primera vez en onCreate
- **Permiso:** POST_NOTIFICATIONS (runtime en 13+)
- **Formato:** Profesional con todos los datos
- **Timing:** 1 hora antes de cita

### 5. Background Execution
- **Periodic:** 15 minutos (configurable)
- **Policy:** KEEP (no duplica workers)
- **Backoff:** Exponential (reintentos inteligentes)
- **Doze:** Respeta optimizaciones del sistema

---

## ✨ Características Implementadas

| Feature | Status | Details |
|---------|--------|---------|
| Programar recordatorios | ✅ | Validaciones + scheduling |
| Cancelar recordatorios | ✅ | Por ID o por reserva |
| Notificaciones en background | ✅ | WorkManager + app cerrada |
| Formato profesional | ✅ | Fecha, hora, doctor |
| Permisos configurados | ✅ | AndroidManifest + runtime |
| Manejo de errores | ✅ | Try-catch + validation |
| UI responsiva | ✅ | Compose + StateFlow |
| Testing utilities | ✅ | Data generators |
| Documentación | ✅ | Javadoc + guías |

---

Diagrama actualizado: Noviembre 2024 ✅

