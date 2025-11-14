# INSTRUCCIONES RÁPIDAS DE INICIO

## 🚀 Para Empezar (5 pasos)

### 1. Obtener Google Maps API Key (2 minutos)

```bash
# Ir a Google Cloud Console
https://console.cloud.google.com/

# Crear proyecto → Habilitar "Maps SDK for Android"
# Crear API Key → Copiar clave
```

### 2. Agregar API Key en AndroidManifest.xml

```xml
<!-- En: app/src/main/AndroidManifest.xml -->
<application>
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="YOUR_API_KEY_HERE" />
    ...
</application>
```

### 3. Inicializar Dependencias en MainActivity

```kotlin
// En MainActivity.onCreate()
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Inicializar Service Locator
    ServiceLocator.initialize(this)
    
    // Inicializar datos de prueba
    lifecycleScope.launch {
        initializeHealthCenters()
    }
    
    // ... resto del código
}

private suspend fun initializeHealthCenters() {
    val db = VitalCareDatabase.getInstance(this)
    val centers = TestDataGenerator.generateHealthCenters()
    centers.forEach { center ->
        db.healthCenterDao().insertHealthCenter(center.toEntity())
    }
}
```

### 4. Compilar y Ejecutar

```bash
# Terminal en raíz del proyecto
./gradlew clean
./gradlew build
./gradlew installDebug

# O en Android Studio: Run > Run 'app'
```

### 5. Probar Funcionalidades

- **HU1 - Mapa Centro**: Abrir app → ver mapa del centro de salud ✓
- **HU2 - Perfil**: Ir a perfil → editar datos → guardar ✓
- **HU3 - Mapa Paciente**: Simular ubicación → ver marcador ✓
- **HU4 - SOS**: Presionar botón rojo → ver notificación ✓

---

## 📂 Estructura de Archivos Nuevos

```
app/src/main/java/cl/duoc/app/
├── di/
│   └── ServiceLocator.kt ⭐ (Inyección de dependencias)
├── domain/
│   ├── repository/
│   │   ├── LocationRepository.kt
│   │   └── SOSRepository.kt
│   └── usecase/
│       ├── LocationUseCases.kt
│       └── SOSUseCases.kt
├── data/
│   ├── repository/
│   │   ├── LocationRepositoryImpl.kt
│   │   └── SOSRepositoryImpl.kt
│   ├── notification/
│   │   └── NotificationManager.kt
│   └── TestDataGenerator.kt
└── ui/
    ├── components/
    │   └── PermissionComponents.kt
    ├── screens/
    │   ├── map/
    │   │   ├── HealthCenterMapViewModel.kt
    │   │   ├── HealthCenterMapScreen.kt
    │   │   ├── PatientLocationMapViewModel.kt
    │   │   └── PatientLocationMapScreen.kt
    │   ├── profile/
    │   │   ├── UserProfileViewModel.kt
    │   │   └── UserProfileScreen.kt
    │   └── sos/
    │       ├── SOSViewModel.kt
    │       └── SOSScreen.kt
```

---

## ⚡ Uso Rápido en Compose

### Pantalla Mapa Centro de Salud
```kotlin
@Composable
fun MyApp() {
    HealthCenterMapScreen(
        viewModel = ServiceLocator.provideHealthCenterMapViewModel(),
        onBackClick = { /* ... */ }
    )
}
```

### Pantalla Perfil de Usuario
```kotlin
@Composable
fun MyApp() {
    UserProfileScreen(
        userId = "user_1",
        viewModel = ServiceLocator.provideUserProfileViewModel(),
        onBackClick = { /* ... */ }
    )
}
```

### Pantalla Mapa Paciente
```kotlin
@Composable
fun MyApp() {
    PatientLocationMapScreen(
        patientId = "patient_123",
        patientName = "Juan García",
        viewModel = ServiceLocator.providePatientLocationMapViewModel(),
        onBackClick = { /* ... */ }
    )
}
```

### Pantalla SOS
```kotlin
@Composable
fun MyApp() {
    SOSScreen(
        userId = "user_1",
        viewModel = ServiceLocator.provideSOSViewModel(),
        onBackClick = { /* ... */ }
    )
}
```

---

## 🔑 Credenciales de Prueba

Usuarios de prueba (TestDataGenerator):

```
Usuario 1:
  ID: user_1
  Nombre: Juan García
  Email: juan.garcia@example.com
  Phone: +56912345678
  RUT: 12.345.678-9

Usuario 2:
  ID: user_2
  Nombre: María López
  Email: maria.lopez@example.com
  Phone: +56987654321
  RUT: 87.654.321-0
```

Centros de Salud (5 disponibles en Santiago):
```
- Centro de Salud Mental Santiago Centro
- Clínica Psiquiátrica Universitaria
- Instituto Psicopedagógico de Santiago
- Centro de Salud Mental Providencia
- Fundación Espíritu de Salud Mental
```

---

## 🧪 Probar en Emulador

### Simular Ubicación GPS

1. **Abrir Extended Controls** en emulador
   - Click en 3 puntos vertical (arriba derecha)
   - Seleccionar "Extended controls"

2. **Ir a Location**
   - Seleccionar "Single Point"

3. **Ingresar Coordenadas**
   ```
   Latitude: -33.8688
   Longitude: -70.2093
   ```

4. **Click en "Send"**

---

## 📋 Checklist de Integración

- [ ] Obtener Google Maps API Key
- [ ] Agregar clave en AndroidManifest.xml
- [ ] Inicializar ServiceLocator en MainActivity
- [ ] Insertar centros de salud en BD
- [ ] Compilar proyecto (./gradlew build)
- [ ] Instalar app en emulador/dispositivo
- [ ] Probar HU1 - Mapa Centro
- [ ] Probar HU2 - Perfil Usuario
- [ ] Probar HU3 - Mapa Paciente
- [ ] Probar HU4 - SOS Notificación
- [ ] Verificar permisos se solicitan
- [ ] Verificar notificaciones se muestran

---

## 🐛 Solución Rápida de Problemas

| Problema | Solución |
|----------|----------|
| Maps muestra gris | Verificar Google Maps API Key en AndroidManifest |
| Permisos no pedidos | Usar `LocationPermissionHandler` en Compose |
| SOS no notifica | Verificar Android 8+ tiene canales de notificación |
| DB no inicializa | Llamar `initializeHealthCenters()` en MainActivity |
| Ubicación null | Simular GPS en emulador o usar dispositivo real |

---

## 📞 Documentación Completa

- **USER_STORIES_IMPLEMENTATION.md** - Detalle técnico de cada HU
- **INTEGRATION_GUIDE.md** - Guía paso a paso completa
- **IMPLEMENTATION_SUMMARY.md** - Resumen ejecutivo

---

## ✅ Verificación Final

Antes de considerar "completado":

```kotlin
✓ Todos los archivos compilados sin errores
✓ Todas las dependencias agregadas
✓ Permisos en AndroidManifest.xml
✓ Google Maps API Key configurada
✓ Centros de salud en BD
✓ ViewModels y Screens funcionales
✓ Notificaciones funcionan
✓ Permisos se solicitan
✓ Ubicación funciona (simulada o real)
✓ Datos persisten en Room
```

---

## 🎯 Próximo Paso

Cuando todo funcione localmente:

1. **Integrar con Navigation Compose**
   - Agregar rutas a NavHost
   - Conectar botones de navegación

2. **Temas y Estilos**
   - Aplicar tema de la app
   - Ajustar colores y tipografía

3. **Tests Unitarios**
   - Tests para ViewModels
   - Tests para Repositorios

4. **Deploy a Producción**
   - Build release
   - Cargar a Play Store

---

**¡Listo para empezar!** 🚀

Si hay dudas, revisar los archivos de documentación incluidos.

