# 📱 Guía Completa: Generación de APK - VitalCare App

## ¿Qué es un APK?

**APK** = Android Package Kit. Es el archivo que contiene toda tu app Android compilada y lista para instalar en dispositivos reales.

---

## 🔧 PASO 1: PREPARAR EL PROYECTO

### 1.1 Verificar que todo compile correctamente

```bash
# En Android Studio, ejecuta:
./gradlew clean build

# Espera a que termine sin errores
# Si hay errores, corrígelos antes de continuar
```

### 1.2 Configurar el archivo build.gradle.kts

Verifica que tengas esta configuración:

```kotlin
android {
    namespace = "com.tu.app" // Tu paquete único
    compileSdk = 34 // Android 14
    
    defaultConfig {
        applicationId = "com.tu.app" // ID único de la app
        minSdk = 24 // Android 7.0
        targetSdk = 34 // Android 14
        versionCode = 1 // Aumenta cada vez que publiques
        versionName = "1.0.0" // Versión visible al usuario
    }
    
    buildTypes {
        debug {
            // Para desarrollo
            isDebuggable = true
        }
        release {
            // Para producción
            isMinifyEnabled = true // Reduce tamaño
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 🔐 PASO 2: CREAR CLAVE DE FIRMA (Solo la primera vez)

Para distribuir tu app, necesitas firmarla con una clave única.

### Opción A: Generar desde Android Studio (Recomendado)

1. **Menu → Build → Generate Signed Bundle/APK**
2. Selecciona **APK**
3. Click **Next**
4. En "Key store path", haz click en **Create new...**
5. Completa los datos:
   ```
   Key store file: /path/to/vitalcare.jks
   Password: [Tu contraseña segura]
   Confirm: [Repite la contraseña]
   
   Key alias: vitalcare_key
   Password: [Contraseña del key]
   Confirm: [Repite]
   
   Validity: 25 años (mínimo)
   
   Certificate:
   First and Last Name: Tu nombre
   Organization Unit: Tu universidad/empresa
   Organization: Tu organización
   City: Santiago
   State: Santiago
   Country: CL
   ```
6. Click **OK**
7. **Importante**: Guarda el archivo `.jks` en un lugar seguro y anota las contraseñas

### Opción B: Generar desde Terminal

```bash
keytool -genkey -v -keystore ~/vitalcare.jks \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias vitalcare_key \
    -keypass tu_password \
    -storepass tu_password \
    -dname "CN=Tu Nombre, O=Tu Org, L=Santiago, ST=Santiago, C=CL"
```

---

## 📦 PASO 3: CONFIGURAR ARCHIVO DE FIRMADO

Crea o actualiza `local.properties` en la raíz del proyecto:

```properties
# ~/.gradle/gradle.properties
RELEASE_STORE_FILE=~/vitalcare.jks
RELEASE_STORE_PASSWORD=tu_password
RELEASE_KEY_ALIAS=vitalcare_key
RELEASE_KEY_PASSWORD=tu_password
```

O en `build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: "vitalcare.jks")
            storePassword = System.getenv("RELEASE_STORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 🎯 PASO 4: GENERAR APK (3 Formas)

### Forma 1: Desde Android Studio (Visual - Recomendado)

1. **Menu → Build → Build Bundle(s)/APK(s) → Build APK(s)**
2. Espera a que compile
3. Verás notificación: "APK(s) generated successfully"
4. Click en **locate** para ver el archivo
5. El APK estará en: `app/build/outputs/apk/debug/app-debug.apk`

### Forma 2: APK Release (Para producción)

1. **Menu → Build → Build Bundle(s)/APK(s) → Build APK(s)**
2. Selecciona **release**
3. Completa los datos de firma
4. El APK estará en: `app/build/outputs/apk/release/app-release.apk`

### Forma 3: Desde Terminal

```bash
# APK Debug (desarrollo)
./gradlew assembleDebug

# APK Release (producción)
./gradlew assembleRelease

# Ambas configuraciones
./gradlew assemble

# Ver ubicación del archivo generado
find . -name "*.apk" -type f
```

---

## ✅ PASO 5: VERIFICAR EL APK

Después de generar el APK, verifica:

### 5.1 Tamaño del archivo

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Esperado: 50-150 MB (dependiendo de tus librerías)
```

### 5.2 Instalar en dispositivo físico

```bash
# Conecta tu dispositivo Android por USB
# Activa "Depuración USB" en el dispositivo

# Desde Android Studio:
# Click en "Run" o presiona Shift+F10

# O desde terminal:
./gradlew installDebug

# O con adb:
adb install app/build/outputs/apk/debug/app-debug.apk

# Verificar instalación
adb shell pm list packages | grep vitalcare
```

### 5.3 Instalar en emulador

```bash
# Si tienes emulador corriendo
./gradlew installDebug

# O directamente:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 5.4 Inspeccionar contenido del APK

```bash
# Descomprimir APK (es un ZIP)
unzip app-debug.apk -d apk_content

# Ver estructura
tree apk_content/

# Ver AndroidManifest.xml
cat apk_content/AndroidManifest.xml
```

---

## 🐛 PASO 6: SOLUCIÓN DE PROBLEMAS COMUNES

### Error: "Build failed"

```bash
# Limpia y reconstruye
./gradlew clean

# Si persiste:
./gradlew build --info  # Ver detalles del error

# Verifica Gradle y SDK
./gradlew --version
```

### Error: "Gradle daemon not responding"

```bash
./gradlew --stop  # Detiene los daemon
./gradlew assembleDebug  # Vuelve a intentar
```

### Error: "Invalid signature"

- Verifica que la contraseña sea correcta
- Regenera el archivo `.jks`
- Reinicia Android Studio

### Error: "Module app not found"

```bash
# Verifica structure.gradle (antiguo) o settings.gradle.kts

# settings.gradle.kts debe incluir:
include(":app")
rootProject.name = "VitalCare"
```

### El APK es muy grande

```kotlin
# En build.gradle.kts release:
buildTypes {
    release {
        isMinifyEnabled = true  // Activar ProGuard
        isShrinkResources = true  // Eliminar recursos no usados
    }
}
```

---

## 📊 PASO 7: INFORMACIÓN DEL APK GENERADO

Después de compilar, verifica:

```bash
# Ver información del APK
aapt dump badging app/build/outputs/apk/release/app-release.apk

# Extraer permisos
aapt dump permissions app-release.apk

# Ver tamaño de métodos
apkanalyzer aab dump manifest app-release.apk
```

Deberías ver algo como:

```
package: name='com.tu.app' versionCode='1' versionName='1.0.0'
uses-permission: 'android.permission.INTERNET'
uses-permission: 'android.permission.ACCESS_FINE_LOCATION'
uses-permission: 'android.permission.ACCESS_COARSE_LOCATION'
sdkVersion:'24' targetSdkVersion:'34'
```

---

## 🚀 PASO 8: DISTRIBUIR EL APK

### Opción 1: Instalar en dispositivo amigo

```bash
# Transfiere el archivo APK al dispositivo
# O comparte por Dropbox, Drive, AirDrop

# Para instalar:
adb install camino/al/app-debug.apk
```

### Opción 2: Google Play Store

Para la presentación final, **no es necesario publicar**, pero si quisieras:

1. Crear cuenta de desarrollador ($25 USD)
2. Subir APK en Google Play Console
3. Llenar detalles de la app
4. Enviar a revisión (48 horas)

### Opción 3: Firebase App Distribution

```bash
# Comando para distribuir a testers
firebase appdistribution:distribute app-release.apk \
    --release-notes="Primera versión de VitalCare" \
    --testers-file=testers.txt
```

---

## 📋 CHECKLIST ANTES DE GENERAR APK

- [ ] Código compila sin errores (`./gradlew clean build`)
- [ ] Todos los tests pasan (`./gradlew test`)
- [ ] Sin warnings importantes
- [ ] Versión en `build.gradle.kts` está actualizada
- [ ] AndroidManifest.xml tiene todos los permisos necesarios
- [ ] Proguard/R8 configurado en release
- [ ] Activos (imágenes, strings) están optimizados
- [ ] No hay archivos de desarrollo en el APK
- [ ] Probaste en dispositivo real o emulador
- [ ] Screenshots funcionan correctamente

---

## 💾 ARCHIVOS GENERADOS

Después de `./gradlew assembleDebug` y `./gradlew assembleRelease`:

```
app/build/outputs/
├── apk/
│   ├── debug/
│   │   └── app-debug.apk
│   ├── release/
│   │   └── app-release.apk
│   └── bundle/
│       └── release/app-release.aab  (Para Google Play)
└── logs/
    └── manifest-merger-release-report.txt
```

**Archivos importantes:**
- `app-debug.apk` → Para tests durante desarrollo
- `app-release.apk` → Para producción (más optimizado, más pequeño)
- `app-release.aab` → Para publicar en Google Play

---

## 🔐 SEGURIDAD: PROTEGER TU CLAVE

**NUNCA hagas esto:**
```kotlin
// ❌ MALO - Contraseña en código
signingConfig {
    storePassword = "micontraseña123"
}

// ❌ MALO - Archivo .jks en repositorio Git
git add vitalcare.jks
```

**SIEMPRE haz esto:**
```bash
# ✅ BUENO - Usar variables de entorno
export RELEASE_STORE_PASSWORD="micontraseña123"

# ✅ BUENO - Guardar .jks en lugar seguro
mv vitalcare.jks ~/.android/vitalcare.jks

# ✅ BUENO - Agregar a .gitignore
echo "*.jks" >> .gitignore
echo "local.properties" >> .gitignore
```

---

## 📱 INSTALACIÓN EN DISPOSITIVO REAL

### Requisitos previos:

1. Cable USB
2. Teléfono Android desbloqueado
3. Modo "Depuración USB" activado

### Pasos:

1. Conecta el teléfono por USB
2. Ejecuta: `adb devices` (verifica que aparezca tu dispositivo)
3. Ejecuta: `./gradlew installDebug`
4. La app se instala automáticamente
5. Abre "VitalCare" en tu teléfono

---

## 📊 MONITOREAR LA COMPILACIÓN

```bash
# Ver progreso detallado
./gradlew assembleRelease --info

# Ver tiempos de compilación
./gradlew assembleRelease --profile

# Parallelizar compilación (más rápido)
./gradlew assemble -x test -Dorg.gradle.parallel=true
```

---

## ✅ COMANDO FINAL (TODO EN UNO)

Para generar APK listo para presentar:

```bash
# 1. Limpiar
./gradlew clean

# 2. Ejecutar tests
./gradlew test

# 3. Generar APK Debug (para mostrar en vivo)
./gradlew assembleDebug

# 4. Generar APK Release (para entregar)
./gradlew assembleRelease

# 5. Ver archivos generados
ls -lh app/build/outputs/apk/*/app*.apk

echo "✅ APKs generados exitosamente"
```

---

## 🎯 PARA LA PRESENTACIÓN

Trae ambos APK:
- **app-debug.apk** → Instala y demuestra en vivo (es el que lleva logs)
- **app-release.apk** → Entrégalo como artefacto (más optimizado)

**Tamaño típico:**
- Debug: 100-150 MB
- Release: 60-100 MB

---

¡Listo! Con esto tienes tu APK completamente funcional para presentar. 🚀
