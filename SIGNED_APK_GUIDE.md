# 📚 GUÍA COMPLETA: APK FIRMADO VITALCAREAPP

## 📦 Archivo APK Generado

```
app-release.apk
Ubicación: C:\Users\esteb\AndroidStudioProjects\VitalCareApp\app\build\outputs\apk\release\app-release.apk
```

---

## 🔐 Información de Firma

### Keystore
- **Ruta:** `C:\Users\esteb\.android\vitalcare_release.keystore`
- **Tipo:** JKS (Java KeyStore)
- **Algoritmo:** RSA 2048 bits
- **Validez:** 100 años (36500 días)

### Credenciales
```
Store Password: VitalCare@2025
Key Password: VitalCare@2025
Key Alias: vitalcare_key
```

### Certificado
```
CN: VitalCareApp
OU: Development
O: VitalCare
L: Santiago
ST: RM
C: CL
```

---

## 🚀 CÓMO INSTALAR EL APK

### Opción 1: ADB (Android Debug Bridge)
```bash
# Conectar dispositivo/emulador
# Asegurate que USB Debugging esté habilitado

# Instalar APK
adb install -r "C:\Users\esteb\AndroidStudioProjects\VitalCareApp\app\build\outputs\apk\release\app-release.apk"

# Verificar instalación
adb shell pm list packages | grep "cl.duoc.app"

# Iniciar aplicación
adb shell am start -n cl.duoc.app/.MainActivity
```

### Opción 2: Copiar manualmente
```bash
# Copiar el APK a la carpeta Downloads del dispositivo
adb push "app-release.apk" /sdcard/Download/

# Abrir file manager en dispositivo
# Navegar a Downloads
# Instalar el APK manualmente
```

### Opción 3: Google Play Store
```
1. Subir el APK a Google Play Console
2. Crear release en la tienda
3. Los usuarios pueden descargar e instalar
```

---

## 🔍 VERIFICAR LA FIRMA

### Verificar que el APK está firmado
```bash
# Usando jarsigner
"D:\JDK 21\bin\jarsigner.exe" -verify -verbose "app-release.apk"

# Salida esperada:
# - Certificate will expire on ...
# - jar verified.
```

### Ver detalles del certificado
```bash
# Listar certificados en el keystore
"D:\JDK 21\bin\keytool.exe" -list -v -keystore "vitalcare_release.keystore" ^
    -alias vitalcare_key ^
    -storepass VitalCare@2025

# Salida mostrará:
# - Alias
# - Creation date
# - Entry type
# - Certificate fingerprints
# - Owner
# - Issuer
# - Serial number
# - Valid from ... until ...
# - Certificate extensions
```

### Ver contenido del APK
```bash
# Los APKs son archivos ZIP
# Puedes extraer su contenido
unzip "app-release.apk" -d app_contents/

# Contenidos principales:
# - AndroidManifest.xml
# - classes.dex (bytecode)
# - lib/ (librerías nativas)
# - res/ (recursos)
# - assets/
```

---

## 📋 ESTRUCTURA DEL BUILD

### Gradle Configuration
```kotlin
// En app/build.gradle.kts

signingConfigs {
    create("release") {
        storeFile = file("${System.getProperty("user.home")}/.android/vitalcare_release.keystore")
        storePassword = "VitalCare@2025"
        keyAlias = "vitalcare_key"
        keyPassword = "VitalCare@2025"
    }
}

buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        signingConfig = signingConfigs.getByName("release")
    }
}
```

### Build Command
```bash
./gradlew.bat assembleRelease
```

---

## 🔧 TAREAS DE MANTENIMIENTO

### Renovar certificado (en 100 años)
```bash
# Crear nuevo keystore
keytool -genkey -v -keystore new_keystore.keystore \
    -keyalg RSA -keysize 2048 -validity 36500 \
    -alias vitalcare_key_v2

# Actualizar build.gradle.kts con nueva ruta
```

### Backup del Keystore
```bash
# Crear backup seguro
copy "C:\Users\esteb\.android\vitalcare_release.keystore" "D:\Backups\vitalcare_release_backup.keystore"

# Guardar en múltiples ubicaciones
# - Unidad externa USB encriptada
# - Cloud storage seguro (Google Drive, OneDrive)
# - Servidor de backup
```

### Cambiar contraseña del keystore
```bash
keytool -storepasswd -keystore vitalcare_release.keystore \
    -storepass VitalCare@2025 \
    -new NEW_PASSWORD
```

---

## 📊 INFORMACIÓN DEL APK

### Especificaciones
```
Nombre: VitalCareApp
Package: cl.duoc.app
Versión: 1.0
Version Code: 1
Min API: 24 (Android 7.0)
Target API: 36 (Android 15)
Compiled SDK: 36
```

### Tamaño esperado
```
Sin minificación: ~30-50 MB
Con minificación (ProGuard): ~20-30 MB
Tamaño final depende de:
- Dependencias incluidas
- Recursos (imágenes, layouts)
- Librerías nativas (NDK)
```

---

## 🎯 DISTRIBUCIÓN EN GOOGLE PLAY STORE

### Pasos para publicar
```
1. Crear cuenta en Google Play Console
   - Ir a https://play.google.com/console
   - Registrarse con cuenta Google

2. Crear nueva aplicación
   - Nombre: VitalCareApp
   - Descripción
   - Categoría

3. Preparar información
   - Descripción detallada
   - Screenshots (mínimo 2)
   - Icono de aplicación (512x512)
   - Banner (1024x500)

4. Configurar versión
   - Subir app-release.apk
   - Descripción de cambios
   - Versión: 1.0
   - Clasificación de contenido

5. Configuración de precios
   - Libre (gratis)
   - O seleccionar precio

6. Enviado para revisión
   - Google revisa (típicamente 24-48 horas)
   - Si aprueba, la aplicación se publica
```

### Requisitos de Google Play
```
✓ APK firmado digitalmente
✓ Versión válida (versión code > versión anterior)
✓ Descripción clara de la aplicación
✓ Screenshots funcionales
✓ Icono de aplicación de calidad
✓ Privacy policy (si procesa datos personales)
✓ Cumplimiento de políticas de Google
```

---

## 🔒 SEGURIDAD Y BUENAS PRÁCTICAS

### Proteger el Keystore
```
❌ NO:
   - Compartir keystore en repositorio público
   - Guardar contraseña en código fuente
   - Usar contraseña débil
   - Dejar keystore en escritorio

✅ SÍ:
   - Guardar en ~/.android/ (ubicación estándar)
   - Usar gestor de contraseñas
   - Hacer backup regular
   - Restringir acceso al archivo
   - Guardar contraseña de forma segura
```

### Versionado
```
Siempre incrementar:
- versionCode: número entero secuencial (1, 2, 3...)
- versionName: semántica (1.0, 1.1, 2.0...)

Regla de incremento:
- Patch (1.0.1): Bugfixes
- Minor (1.1): Nuevas features
- Major (2.0): Cambios significativos
```

---

## 📞 SOPORTE Y AYUDA

### Si algo falla en la instalación
```bash
# Verificar dispositivo conectado
adb devices

# Ver logs en tiempo real
adb logcat | grep cl.duoc.app

# Desinstalar APK anterior
adb uninstall cl.duoc.app

# Reinstalar
adb install -r app-release.apk
```

### Errores comunes

**Error: "Command 'keytool' not found"**
```
Solución: Usar ruta completa
"D:\JDK 21\bin\keytool.exe" ...
```

**Error: "Build failed"**
```
Solución: 
1. Limpiar build: ./gradlew clean
2. Verificar gradlew: ./gradlew --version
3. Sincronizar Gradle
4. Verificar configuración de firma en build.gradle.kts
```

**Error: "Unable to install"**
```
Solución:
1. Verificar USB Debugging habilitado
2. Desinstalar versión anterior: adb uninstall cl.duoc.app
3. Usar: adb install -r app-release.apk
```

---

## 📝 CHECKLIST FINAL

- ✅ Keystore creado correctamente
- ✅ Gradle configurado con firma
- ✅ APK generado exitosamente
- ✅ APK firmado digitalmente
- ✅ APK testeado en dispositivos
- ✅ Documentación completada
- ✅ Backup del keystore realizado
- ✅ Listo para distribución en Play Store

---

## 🎓 RECURSOS ADICIONALES

- [Android Official Documentation](https://developer.android.com/)
- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [Security & Privacy Best Practices](https://developer.android.com/topic/security)
- [App Release Process](https://developer.android.com/studio/publish)

---

**Última actualización:** 9 de Diciembre de 2025  
**APK Version:** 1.0  
**Estado:** ✅ LISTO PARA DISTRIBUCIÓN


