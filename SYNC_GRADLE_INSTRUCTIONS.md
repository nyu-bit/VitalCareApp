# Instrucciones para Sincronizar Gradle

Los cambios realizados al archivo requieren que sincronices Gradle para que descargue las dependencias faltantes.

## Pasos para sincronizar:

### Opción 1: Desde Android Studio (Recomendado)
1. Abre Android Studio
2. Ve a **File > Sync Now** (o presiona **Ctrl+Alt+Y** en Windows)
3. Espera a que la sincronización se complete
4. Los errores deberían desaparecer

### Opción 2: Desde la línea de comandos
```bash
cd C:\Users\esteb\AndroidStudioProjects\VitalCareApp
.\gradlew.bat clean build
```

### Opción 3: Usar Gradle wrapper
```bash
cd C:\Users\esteb\AndroidStudioProjects\VitalCareApp
.\gradlew.bat --refresh-dependencies
```

## Cambios realizados:

### 1. **gradle/libs.versions.toml**
   - Agregadas las versiones faltantes para:
     - `room = "2.6.1"`
     - `playServicesLocation = "21.1.0"`
     - `playServicesMaps = "18.2.0"`
     - `mapsCompose = "4.1.1"`
     - `accompanistPermissions = "0.33.2-alpha"`
     - `gsonVersion = "2.10.1"`

### 2. **build.gradle.kts (raíz)**
   - Agregados los repositorios requeridos:
     ```kotlin
     repositories {
         google()
         mavenCentral()
     }
     ```

### 3. **PatientLocationMapScreen.kt**
   - Agregado import de `java.util.Locale`
   - Eliminadas llaves de cierre duplicadas
   - Corregidos todos los `String.format()` para usar `Locale.US`
   - Arreglado el problema del "delegated property" con `patientLocation`

## Esperado después de la sincronización:

Una vez sincronizado, todos los errores de "Unresolved reference" desaparecerán porque:
- Google Maps Play Services se descargará
- Google Maps Compose se descargará
- Todas las dependencias estarán disponibles

Si después de sincronizar siguen habiendo errores, intenta:
1. **File > Invalidate Caches / Restart** en Android Studio
2. Vuelve a ejecutar Sync Now

