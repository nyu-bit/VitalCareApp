# 🚀 GUÍA DE COMPILACIÓN Y EJECUCIÓN - VitalCareApp

## Estado del Proyecto: ✅ LISTO PARA COMPILAR

---

## 📋 Requisitos Previos

### Entorno de Desarrollo
- ✅ Android Studio (versión 2023.1 o superior)
- ✅ JDK 11 o superior
- ✅ Android SDK 36 (compileSdk)
- ✅ Android API 24 o superior (minSdk)

### Configuración
```
AGP: 8.12.3
Kotlin: 2.0.21
Gradle: 8.x (incluido con Android Studio)
```

---

## 🔧 PASOS PARA COMPILAR

### Opción 1: Android Studio (Recomendado)

#### 1. Abrir el Proyecto
```
File → Open → C:\Users\esteb\AndroidStudioProjects\VitalCareApp
```

#### 2. Esperar a que se cargue Gradle
```
Esperar a que aparezca: "Gradle Build Sync Completed"
```

#### 3. Sincronizar Gradle (si es necesario)
```
File → Sync Now
O: Ctrl + Shift + O
```

#### 4. Compilar el Proyecto
```
Build → Clean Build
Build → Make Project (Ctrl + F9)
O directamente: Build → Build Bundle(s) / APK(s) → Build APK(s)
```

#### 5. Esperar a Compilación
```
Esperar el mensaje: "Build completed successfully"
La compilación toma entre 2-5 minutos en primera ejecución
```

---

### Opción 2: Terminal (PowerShell)

#### 1. Navegar al Directorio
```powershell
cd C:\Users\esteb\AndroidStudioProjects\VitalCareApp
```

#### 2. Limpiar (Primera Vez)
```powershell
.\gradlew clean
```

#### 3. Compilar
```powershell
.\gradlew build
```

#### 4. Construir APK (Debug)
```powershell
.\gradlew assembleDebug
```

**Ubicación del APK:**
```
app\build\outputs\apk\debug\app-debug.apk
```

#### 5. Construir APK (Release - sin firmar)
```powershell
.\gradlew assembleRelease
```

---

## ▶️ PASOS PARA EJECUTAR

### Opción 1: Android Studio (Más Fácil)

#### 1. Conectar Dispositivo o Abrir Emulador
```
- Dispositivo: Conectar vía USB con Debug habilitado
- Emulador: Abrir desde AVD Manager (Tools → Device Manager)
```

#### 2. Verificar Dispositivo
```
Run → Select Device
Debe aparecer tu dispositivo/emulador
```

#### 3. Ejecutar Aplicación
```
Run → Run 'app'
O: Shift + F10
```

#### 4. Esperar a que se Instale
```
Primera ejecución: 1-2 minutos
Ejecuciones posteriores: 10-30 segundos
```

#### 5. Aplicación en Pantalla
```
La aplicación debe iniciar automáticamente en el dispositivo
Pantalla de Login debería aparecer
```

---

### Opción 2: Terminal

#### 1. Instalar APK en Dispositivo/Emulador
```powershell
.\gradlew installDebug
```

#### 2. Iniciar Aplicación
```powershell
adb shell am start -n cl.duoc.app/.MainActivity
```

#### 3. Ver Logs en Tiempo Real
```powershell
adb logcat | findstr "VitalCareApp"
```

---

## 🎮 PRIMER USO DE LA APLICACIÓN

### Pantalla Inicial
1. **Login Screen** aparece automáticamente
   - Campo Email: `test@example.com` (o cualquier email)
   - Campo Contraseña: Cualquier contraseña (demo mode)
   - Botón "Iniciar Sesión"

2. **Dashboard Screen** se abre tras login
   - Muestra signos vitales simulados
   - Animaciones Lottie de latidos cardíacos
   - Botón para navegar a Perfil

3. **Funcionalidades Disponibles**
   - Dashboard: Ver signos vitales
   - Perfil: Editar información personal
   - SOS: Activar emergencia
   - Recordatorios: Ver citas programadas
   - Alertas: Historial de alertas
   - Mapas: Ver centros de salud

---

## ⚠️ SOLUCIÓN DE PROBLEMAS

### Error: "Gradle Sync Failed"
```
Solución:
1. File → Invalidate Caches
2. Restart IDE
3. File → Sync Now
4. Si persiste: Delete .gradle folder y reintentar
```

### Error: "Could not find com.airbnb.android:lottie-compose:6.4.0"
```
Solución:
1. File → Settings → System Settings → Updates
2. Asegurar que repositories están configurados (Google, Maven Central)
3. Reintenta Gradle Sync
```

### Error: "Compilation Failed"
```
Soluciones comunes:
1. File → Invalidate Caches → Restart
2. Build → Clean Build
3. File → Sync Now
4. Borrar carpeta: app/build/ y reintentar
```

### No aparece dispositivo en "Select Device"
```
Android Studio:
1. Tools → Device Manager → Crear nuevo AVD si no existe
2. O conectar dispositivo físico con USB Debug habilitado

Dispositivo Físico:
1. Settings → About Phone → Build Number (tocar 7 veces)
2. Settings → Developer Options → USB Debugging (ON)
3. Conectar con cable USB
4. Run → Select Device → Seleccionar tu dispositivo
```

### APK muy lento al instalar
```
Solución:
1. Usar Release build en lugar de Debug:
   ./gradlew installRelease
2. O esperar a que finalice completamente
```

---

## 📊 INFORMACIÓN ÚTIL

### Estructura de Carpetas Generadas
```
VitalCareApp/
├── app/build/            ← Binarios compilados
│   ├── outputs/          ← APKs y bundles
│   ├── generated/        ← Código generado
│   └── intermediates/    ← Archivos temporales
├── .gradle/              ← Cache de Gradle
└── build/                ← Build cache
```

### Comandos Gradle Útiles
```powershell
# Limpiar
.\gradlew clean

# Compilar
.\gradlew build

# Compilar sin tests
.\gradlew build -x test

# Instalar en dispositivo (Debug)
.\gradlew installDebug

# Instalar y ejecutar
.\gradlew installDebug
.\gradlew runDebug

# Ver tasks disponibles
.\gradlew tasks

# Reporte de dependencias
.\gradlew dependencies

# Limpiar cache
.\gradlew cleanBuildCache
```

### Tamaño Esperado
```
APK Debug:    ~50-100 MB
APK Release:  ~30-50 MB
Build Time:   2-5 minutos (primera vez)
              30 segundos - 2 minutos (posteriores)
```

---

## ✅ CHECKLIST DE COMPILACIÓN

- [ ] Android Studio abierto
- [ ] Proyecto cargado sin errores
- [ ] Gradle Sync completado
- [ ] No hay errores rojos en el code
- [ ] Build → Make Project completado sin errores
- [ ] Dispositivo/Emulador conectado
- [ ] Run → Run 'app' ejecutado
- [ ] Aplicación instalada en dispositivo
- [ ] Pantalla de Login visible
- [ ] Puedo hacer login y ver el Dashboard

---

## 🎯 PRÓXIMOS PASOS

### Después de Compilar
1. ✅ Explorar la aplicación
2. ✅ Revisar diferentes pantallas
3. ✅ Probar la funcionalidad de login
4. ✅ Ver animaciones en Dashboard
5. ✅ Hacer login y explorar opciones

### Para Desarrollo
1. 📝 Modificar código
2. 🔄 Build automático mientras escribes (hot reload no disponible en Android)
3. 🧪 Ejecutar tests: `./gradlew test`
4. 📊 Ver logs: View → Tool Windows → Logcat

---

## 📞 SOPORTE

Si encuentras errores:

1. **Error de Compilación**
   - Ir a Build → Analyze Stack Trace
   - Google el error exacto
   - Revisar gradle/build configuration

2. **Error en Runtime**
   - Ver Logcat (View → Tool Windows → Logcat)
   - Buscar línea roja con "ERROR" o "EXCEPTION"
   - Revisar stack trace completo

3. **Documento de Errores**
   - Ver: `CORRECCIONES_APLICADAS.md`
   - Ver: `ESTADO_FINAL_PROYECTO.md`

---

**Última Actualización**: 2025-01-18
**Status**: ✅ Listo para compilar
**Versión Gradle**: 8.12.3
**Versión Kotlin**: 2.0.21
**SDK Compilación**: 36
**SDK Mínimo**: 24

