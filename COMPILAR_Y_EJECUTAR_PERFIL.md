# 🚀 COMPILAR Y EJECUTAR - Perfil del Usuario

## 📋 REQUISITOS PREVIOS

Asegúrate de tener:
- ✅ Android Studio instalado
- ✅ SDK de Android configurado
- ✅ Emulador o dispositivo conectado
- ✅ Gradle sincronizado

---

## 🔨 PASO A PASO: COMPILAR Y EJECUTAR

### OPCIÓN 1: Desde Android Studio (RECOMENDADO)

#### Paso 1: Abrir el Proyecto
```
1. Abre Android Studio
2. File → Open → Selecciona: C:\Users\esteb\AndroidStudioProjects\VitalCareApp
3. Espera a que se cargue el proyecto
```

#### Paso 2: Sincronizar Gradle
```
1. File → Sync Now
   O
   Build → Clean Project
   Build → Rebuild Project
```

#### Paso 3: Selecciona el Emulador
```
1. En la barra superior, verás un dropdown con dispositivos
2. Selecciona tu emulador (ej: "Pixel 4 API 31")
3. Si no ves ninguno, crea uno:
   - Tools → Device Manager → Create device
```

#### Paso 4: Ejecuta la Aplicación
```
Opción A - Con botón verde:
1. Busca el botón verde de "Run" (▶) en la esquina superior
2. Haz clic en él
3. Verás el emulador iniciando y la app instalándose

Opción B - Con atajo de teclado:
1. Presiona: Shift + F10 (Windows/Linux) o Control + R (Mac)

Opción C - Con menú:
1. Run → Run 'app'
```

#### Paso 5: Espera la Compilación
```
La compilación puede tardar:
- Primera vez: 2-5 minutos
- Compilaciones posteriores: 30-60 segundos

Verás en la terminal:
BUILD SUCCESSFUL
```

---

### OPCIÓN 2: Usando Terminal/CMD

#### Paso 1: Abre PowerShell en el directorio del proyecto
```powershell
cd C:\Users\esteb\AndroidStudioProjects\VitalCareApp
```

#### Paso 2: Limpia el proyecto (opcional pero recomendado)
```powershell
.\gradlew.bat clean
```

#### Paso 3: Compila la aplicación
```powershell
.\gradlew.bat build -x test
```

#### Paso 4: Instala en el emulador
```powershell
.\gradlew.bat installDebug
```

#### Paso 5: Lanza la aplicación
```powershell
# En el emulador, busca el ícono de VitalCareApp y haz clic
# O desde la terminal:
adb shell am start -n cl.duoc.app/.MainActivity
```

---

## ✅ VERIFICAR QUE FUNCIONA

Una vez que la app esté ejecutándose en el emulador:

### 1. Pantalla de Login
```
Deberías ver:
- Campo de Email
- Campo de Contraseña
- Botón "Iniciar Sesión"
```

### 2. Inicia Sesión
```
Email: test@example.com
Contraseña: 123456789

(O usa las credenciales que tengas configuradas)
```

### 3. Dashboard
```
Deberías ver:
- Animación de latido del corazón
- "Estado General del Paciente"
- Tarjetas con Signos Vitales
- ⭐ NUEVO: ÍCONO DE PERFIL (👤) en la esquina superior derecha
```

### 4. Accede al Perfil
```
1. Haz clic en el ícono de Perfil (👤) en la esquina superior derecha
2. Se abrirá la pantalla de Perfil
3. Verás campos para editar
4. Puedes llenarlos y hacer clic en "Guardar cambios"
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Problema: "Gradle sync failed"
```
Solución:
1. File → Invalidate Caches → Invalidate and Restart
2. Vuelve a intentar la sincronización
3. Si persiste: elimina la carpeta .gradle y reintenta
```

### Problema: "No device found"
```
Solución:
1. Abre el Device Manager (Tools → Device Manager)
2. Crea un nuevo emulador si no existe
3. Inicia el emulador
4. Vuelve a hacer Run
```

### Problema: "Botón de Perfil no aparece"
```
Solución:
1. Asegúrate de haber compilado DESPUÉS del cambio
2. Limpia el proyecto: Build → Clean Project
3. Reconstruye: Build → Rebuild Project
4. Run nuevamente
```

### Problema: "La app se cae al abrir Perfil"
```
Solución:
1. Verifica que ProfileViewModel está correctamente inyectado
2. Revisa los logs: View → Tool Windows → Logcat
3. Busca errores en rojo
```

### Problema: "Los datos no se guardan"
```
Solución:
1. Verifica que SharedPreferencesManager existe
2. Verifica que tienes permisos de lectura/escritura
3. Revisa que el contexto se está pasando correctamente
```

---

## 📱 CARACTERÍSTICAS QUE DEBERÍAS VER

- ✅ Dashboard con signos vitales simulados
- ✅ Ícono de Perfil en la TopAppBar (NUEVO)
- ✅ Pantalla de Perfil editable
- ✅ Campos para tutor y paciente
- ✅ Botón Guardar Cambios
- ✅ Toast de confirmación
- ✅ Datos persisten entre sesiones
- ✅ Navegación suave entre pantallas

---

## 🎬 DEMO COMPLETO

```
Paso 1: Inicia la app
       ↓
Paso 2: Login con credenciales
       ↓
Paso 3: Ves el Dashboard
       ↓
Paso 4: Haz clic en el ícono de Perfil (👤)
       ↓
Paso 5: Se abre la pantalla de Perfil
       ↓
Paso 6: Edita los campos (ej: nombre del tutor)
       ↓
Paso 7: Haz clic en "Guardar cambios"
       ↓
Paso 8: Ves el mensaje "Datos actualizados correctamente"
       ↓
Paso 9: Regresa al Dashboard
       ↓
Paso 10: Vuelve al Perfil → Verás que los datos se guardaron
```

---

## 📊 TIEMPOS ESPERADOS

| Tarea | Tiempo |
|-------|--------|
| Sincronizar Gradle | 30s - 2m |
| Primera compilación | 2 - 5 minutos |
| Compilaciones posteriores | 30 - 60 segundos |
| Instalación en emulador | 15 - 30 segundos |
| Startup de app | 5 - 10 segundos |

---

## 🔍 VERIFICAR LA MODIFICACIÓN

Para confirmar que la modificación se aplicó correctamente:

```bash
# 1. Abre el archivo modificado
Busca: app\src\main\java\cl\duoc\app\ui\dashboard\DashboardScreen.kt

# 2. Busca dentro del archivo (Ctrl+F):
actions = {
    IconButton(
        onClick = { onNavigateToProfile() }

# 3. Si ves este código, la modificación está en su lugar ✅
```

---

## 💡 CONSEJOS

1. **Primer Build es lento**: Es normal, gradle descarga dependencias
2. **Caché Gradle**: Si hay problemas, limpia: `rm -rf .gradle`
3. **Emulador lento**: Aumenta la RAM en Device Manager
4. **Logcat útil**: Para debug, mira: View → Tool Windows → Logcat

---

## ✨ READY TO GO!

Una vez compilado, tu aplicación estará lista para:
- ✅ Ver el Dashboard
- ✅ Navegar al Perfil
- ✅ Editar datos personales
- ✅ Guardar cambios
- ✅ Visualizar todo funcionando suavemente

---

## 📞 PRÓXIMOS PASOS

Si todo funciona:
1. Prueba todas las pantallas
2. Intenta editar diferentes campos
3. Verifica que los datos persisten
4. Disfruta de tu aplicación VitalCareApp

Si hay problemas:
1. Revisa el archivo TECNICO_PERFIL_INTEGRACION.md
2. Verifica los logs en Logcat
3. Asegúrate de que la compilación fue exitosa

---

**¡Listo para compilar y ejecutar! 🚀**

Cualquier pregunta, revisa la carpeta de documentación o los archivos README.

