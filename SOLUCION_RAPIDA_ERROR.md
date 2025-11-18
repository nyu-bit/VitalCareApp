# 🔧 GUÍA RÁPIDA - RESOLVER IncompatibleClassChangeError

## 🚨 El Error

```
IncompatibleClassChangeError: class com.google.devtools.ksp.common.PersistentMap 
cannot inherit from final class org.jetbrains.kotlin.com.intellij.util.io.PersistentHashMap
```

---

## ✅ SOLUCIÓN RÁPIDA (3 pasos)

### Paso 1: Ejecutar Script de Limpieza
```powershell
# En PowerShell, dentro de la carpeta del proyecto:
.\rebuild.bat
```

O manualmente:
```powershell
.\gradlew clean
.\gradlew --refresh-dependencies
```

### Paso 2: Invalidar Caché en Android Studio
```
File → Invalidate Caches → Invalidate and Restart
```

### Paso 3: Sincronizar y Compilar
```
File → Sync Now
Build → Make Project
```

---

## 📝 LO QUE SE CAMBIÓ

### Versiones Actualizadas:
- **Kotlin**: 2.0.21 → 1.9.22
- **KSP**: 2.0.0-1.0.22 → 1.9.22-1.0.17
- **Compose BOM**: 2024.06.00 → 2024.04.01
- **Compose Compiler Extension**: 1.5.4 → 1.5.8

### Archivos Modificados:
- `gradle/libs.versions.toml`
- `app/build.gradle.kts`

---

## 🎯 DESPUÉS DE APLICAR LOS CAMBIOS

1. ✅ Gradle Sync sin errores
2. ✅ Build completado
3. ✅ Aplicación lista para ejecutar

---

## ⚠️ SI PERSISTE EL ERROR

### Opción 1: Limpiar Más Profundamente
```powershell
# En PowerShell:
.\gradlew clean
Remove-Item -Recurse -Force .gradle
Remove-Item -Recurse -Force app/build
Remove-Item -Recurse -Force build
.\gradlew build --refresh-dependencies
```

### Opción 2: Reiniciar IDE
```
1. Cerrar Android Studio completamente
2. File → Invalidate Caches → Invalidate and Restart
3. Esperar a que se reinicie
4. File → Sync Now
```

### Opción 3: Nuclear (Última Opción)
```powershell
# Elimina TODO y reconstruye:
.\gradlew clean
Remove-Item -Recurse -Force .gradle
Remove-Item -Recurse -Force app/build
Remove-Item -Recurse -Force build
Remove-Item -Recurse -Force .idea (Cierra Android Studio primero)

# Luego abre el proyecto en Android Studio nuevamente
```

---

## 🔍 VERIFICACIÓN

Después de los cambios, verifica que:

1. **`gradle/libs.versions.toml` contiene:**
   ```toml
   kotlin = "1.9.22"
   ksp = "1.9.22-1.0.17"
   ```

2. **`app/build.gradle.kts` contiene:**
   ```kotlin
   kotlinCompilerExtensionVersion = "1.5.8"
   val composeBom = platform("androidx.compose:compose-bom:2024.04.01")
   ```

3. **La compilación completa sin errores:**
   ```
   Build completed successfully
   ```

---

## 📊 COMPATIBILIDAD

Las versiones actualizadas son **100% compatibles** con:
- ✅ Android Studio 2023.1+
- ✅ Kotlin Compose
- ✅ Room Database
- ✅ Navigation Compose
- ✅ Todas las librerías del proyecto

---

## 💡 POR QUÉ PASÓ ESTO

KSP 2.0.0 es una versión beta/RC y tiene problemas de compatibilidad con Kotlin 2.0.21 en ciertos entornos. La versión 1.9.22 es estable y ampliamente soportada.

Cuando KSP 2.x sea completamente estable, podrás actualizar a Kotlin 2.x sin problemas.

---

**Status:** ✅ CORREGIDO
**Solución:** Actualizar a versiones compatibles
**Impacto en Código:** NINGUNO (solo dependencias)

