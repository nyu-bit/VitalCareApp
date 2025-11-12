# ⚡ QUICK START - MÓDULO DE RECORDATORIOS

## 30 segundos para empezar

### 1. Sincronizar (IMPRESCINDIBLE)
```
Android Studio: File → Sync Now
```

### 2. Copiar 3 líneas de código
```kotlin
// En MainActivity.kt
val workManager = WorkManager.getInstance(this)
val remindersViewModel = RemindersViewModel(workManager)
```

### 3. Mostrar pantalla
```kotlin
RemindersScreen(userId = "user_test", viewModel = remindersViewModel)
```

### 4. ¡Listo! 🎉
Ya está funcionando el módulo de recordatorios

---

## Prueba Rápida (2 minutos)

```kotlin
// 1. Crea una cita futura
val reservation = Reservation(
    id = "res_001",
    userId = "user_test",
    specialty = "Cardiología",
    doctorName = "Dr. Juan Pérez",
    date = System.currentTimeMillis() + (2 * 60 * 60 * 1000), // 2 horas
    status = ReservationStatus.CONFIRMED
)

// 2. Agrégala al repositorio
reservationRepository.createReservation(reservation)

// 3. Programa el recordatorio
remindersViewModel.scheduleReminder(reservation.id)

// 4. Ve en pantalla
// → Debería aparecer en RemindersScreen

// 5. Cierra app, espera 1 hora
// → Recibe notificación (incluso app cerrada)
```

---

## Permisos (Android 13+)

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        101
    )
}
```

---

## Archivos Clave

| Archivo | Líneas | Propósito |
|---------|--------|----------|
| `RemindersViewModel.kt` | 170 | Coordina todo |
| `RemindersScreen.kt` | 320 | Pantalla UI |
| `ReminderNotificationManager.kt` | 110 | Muestra notificaciones |
| `AppointmentReminderWorker.kt` | 80 | Ejecuta en background |
| `ReminderUseCases.kt` | 280 | Lógica de negocio |

---

## Casos de Uso

```kotlin
// Programar
viewModel.scheduleReminder(reservationId)

// Cancelar
viewModel.cancelReminder(reminderId)

// Obtener próximos
viewModel.loadUpcomingReminders(userId)

// Cancelar por reserva
viewModel.cancelReminderByReservation(reservationId)
```

---

## Estructura Mínima en tu App

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Solicitar permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
        
        setContent {
            val workManager = WorkManager.getInstance(this@MainActivity)
            val remindersViewModel = RemindersViewModel(workManager)
            
            RemindersScreen(
                userId = "user_123",
                viewModel = remindersViewModel
            )
        }
    }
}
```

---

## Testing Rápido

### Crear recordatorio
```kotlin
TestDataGenerator.generateTestReservations(3).forEach { 
    reservationRepository.createReservation(it)
    viewModel.scheduleReminder(it.id)
}
```

### Ver en logs
```bash
adb logcat | grep -i reminder
```

### Forzar notificación (sin esperar)
```bash
adb shell cmd jobscheduler run -u 0 -j 999 cl.duoc.app
```

---

## ¿Problemas?

| Problema | Solución |
|----------|----------|
| Dependencias no encontradas | `File → Sync Now` |
| App no compila | Sincronizar Gradle |
| No ve recordatorios | Crear cita con fecha futura |
| No recibe notificación | Revisar permisos en Ajustes |

---

## Documentación Completa

- `REMINDERS_INTEGRATION_GUIDE.md` - Guía completa
- `IMPLEMENTATION_CHECKLIST.md` - Todos los detalles
- `ARCHITECTURE_DIAGRAM.md` - Cómo funciona por dentro
- `EXECUTIVE_SUMMARY.md` - Resumen ejecutivo

---

## ¿Cuál es el siguiente paso?

1. ✅ Sincronizar Gradle
2. ✅ Copiar 3 líneas de código
3. ✅ Probar en emulador/dispositivo
4. ✅ Leer guía de integración completa
5. ✅ Integrar en tu código

---

**¡Ya está todo listo!** 🚀

Próximo paso: Sincroniza Gradle en Android Studio

