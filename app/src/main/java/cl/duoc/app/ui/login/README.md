# Pantalla de Inicio de Sesión - VitalCare

## Descripción
Implementación de una pantalla de login completa para VitalCare – App Tutor usando Jetpack Compose y Material3.

## Estructura de Archivos

### 1. MainActivity.kt
Punto de entrada de la aplicación que inicializa `VitalCareApp()`.

### 2. VitalCareApp.kt
Composable principal que orquesta la navegación de la app. Actualmente muestra `LoginScreen()`.

### 3. LoginScreen.kt
Implementación completa de la pantalla de inicio de sesión con todas las validaciones.

## Características Implementadas

### ✅ Campos de Entrada
- **Correo electrónico**: Campo con teclado optimizado para email
- **Contraseña**: Campo con visualización ocultable (ícono de ojo)

### ✅ Validaciones
- Campo de email no vacío
- Formato de email válido usando `Patterns.EMAIL_ADDRESS`
- Campo de contraseña no vacío
- Mensajes de error en rojo debajo de cada campo

### ✅ Funcionalidad
- Toast con mensaje "Bienvenido(a)" al validar correctamente
- Limpieza de errores al escribir en los campos
- Botón de "¿Olvidaste tu contraseña?" (preparado para navegación futura)

### ✅ Diseño
- Layout centrado verticalmente
- Fondo claro con Material3
- Título "Inicio de Sesión" destacado
- Subtítulo "VitalCare – App Tutor"
- Estilos coherentes con Material3
- Preview incluido para desarrollo sin emulador

## Componentes Utilizados

- `OutlinedTextField` - Campos de texto estilizados
- `Button` - Botón principal de acción
- `Text` - Títulos y mensajes
- `Icon` - Íconos para mostrar/ocultar contraseña
- `MaterialTheme` - Sistema de temas de Material3
- `Surface` - Container principal
- `Column` - Layout vertical

## Cómo Usar

### Vista Previa
El archivo incluye un `@Preview` que permite ver el diseño en Android Studio sin ejecutar la app:
```kotlin
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview()
```

### Ejecutar la App
1. Compilar el proyecto: `./gradlew build`
2. Ejecutar en emulador o dispositivo físico
3. La pantalla de login se mostrará automáticamente

## Próximos Pasos (TODO)

- [ ] Implementar navegación al Dashboard tras login exitoso
- [ ] Crear pantalla de recuperación de contraseña
- [ ] Integrar con backend/API real para autenticación
- [ ] Agregar persistencia de sesión
- [ ] Implementar logout
- [ ] Agregar opción de "Recordar mis datos"

## Tecnologías

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - Framework UI moderno
- **Material3** - Sistema de diseño
- **Android SDK** - Plataforma móvil

## Validaciones Implementadas

| Campo | Validación | Mensaje de Error |
|-------|-----------|------------------|
| Email | No vacío | "El correo electrónico es obligatorio" |
| Email | Formato válido | "El formato del correo electrónico no es válido" |
| Contraseña | No vacío | "La contraseña es obligatoria" |

## Capturas

La pantalla incluye:
- Título principal en grande y negrita
- Subtítulo con el nombre de la app
- Campo de email con validación de formato
- Campo de contraseña con opción de mostrar/ocultar
- Mensajes de error en rojo
- Botón principal destacado
- Link para recuperar contraseña

---

**Autor**: Implementado para VitalCare - MajoApp branch  
**Fecha**: Noviembre 2025
