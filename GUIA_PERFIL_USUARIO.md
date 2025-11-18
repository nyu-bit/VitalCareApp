# 🎯 GUÍA RÁPIDA: Visualizar el Perfil del Usuario

## ¿Qué se hizo?

Se agregó un **botón de Perfil** en la parte superior derecha del Dashboard que permite acceder y editar el perfil del usuario.

---

## 📱 PASOS PARA VISUALIZAR EL PERFIL EN EL EMULADOR

### Paso 1: Inicia la aplicación
```
1. Abre Android Studio
2. Selecciona tu emulador
3. Ejecuta la aplicación (Run → Run 'app')
```

### Paso 2: Inicia sesión
```
1. Verás la pantalla de Login
2. Ingresa credenciales (email y contraseña)
3. Haz clic en "Iniciar Sesión"
```

### Paso 3: Accede al Perfil
```
1. Se abrirá el Dashboard con los Signos Vitales
2. Busca el ÍCONO DE PERFIL (👤) en la esquina superior DERECHA
3. Haz clic en ese ícono
```

### Paso 4: Visualiza y edita tu perfil
```
Una vez en la pantalla de Perfil, verás:

┌─────────────────────────────────────┐
│       Perfil                    👤  │ ← Botón para volver
├─────────────────────────────────────┤
│  Información Personal                │
├─────────────────────────────────────┤
│  DATOS DEL TUTOR                    │
│  ┌───────────────────────────────┐  │
│  │ Nombre del tutor:  [________]  │  │
│  │ Edad:              [________]  │  │
│  │ Teléfono:          [________]  │  │
│  └───────────────────────────────┘  │
├─────────────────────────────────────┤
│  DATOS DEL PACIENTE                 │
│  ┌───────────────────────────────┐  │
│  │ Nombre del paciente: [______] │  │
│  │ Contacto de emergencia: [___] │  │
│  └───────────────────────────────┘  │
├─────────────────────────────────────┤
│  [ GUARDAR CAMBIOS ]                │
├─────────────────────────────────────┤
│  Los datos se guardan localmente     │
└─────────────────────────────────────┘
```

### Paso 5: Edita tus datos
```
1. Haz clic en cualquier campo de texto
2. Modifica los valores que desees
3. Haz clic en el botón "GUARDAR CAMBIOS"
4. Verás un mensaje: "Datos actualizados correctamente"
```

---

## 🎨 INTERFACE

### TopAppBar (Barra Superior del Dashboard)
```
[Dashboard]                           [👤 Perfil]
        ↑                                 ↑
      Título                      Nuevo botón agregado
```

### Elementos de la Pantalla de Perfil

| Elemento | Descripción |
|----------|------------|
| **Ícono de Persona** | Indica que es la sección de perfil |
| **Datos del Tutor** | Información de la persona responsable |
| **Datos del Paciente** | Información del paciente monitorizado |
| **Botón Guardar** | Almacena los cambios localmente |
| **Toast Notification** | Confirma que los datos se guardaron |

---

## 📋 CAMPOS DISPONIBLES

### Tutor
- ✏️ **Nombre** - Nombre completo del tutor
- ✏️ **Edad** - Solo acepta números
- ✏️ **Teléfono** - Número de contacto

### Paciente
- ✏️ **Nombre** - Nombre completo del paciente
- ✏️ **Contacto de Emergencia** - Teléfono de emergencia

---

## 💾 ALMACENAMIENTO

Los datos se guardan en:
- **SharedPreferences** (base de datos local del dispositivo)
- **NO se pierden** al cerrar la aplicación
- **Se sincroniza** automáticamente

---

## 🔄 NAVEGACIÓN

```
Login → Dashboard → [Botón Perfil] → Pantalla Perfil
                                           ↓
                                    [Atrás] → Dashboard
```

---

## ✨ CARACTERÍSTICAS ESPECIALES

✅ **Validación**: Solo acepte números en el campo de edad  
✅ **Feedback**: Toast de confirmación al guardar  
✅ **Persistencia**: Los datos se mantienen entre sesiones  
✅ **Responsive**: La pantalla se adapta a cualquier tamaño  
✅ **Material Design**: Usa componentes modernos de Material 3  

---

## 🚀 LISTO PARA USAR

El perfil del usuario ya está **completamente funcional** en tu emulador.

**Solo necesitas:**
1. Compilar la aplicación
2. Ejecutarla en el emulador
3. ¡Hacer clic en el botón de Perfil! 👤

---

## ❓ PREGUNTAS FRECUENTES

**P: ¿Dónde se guardan los datos?**  
R: En la base de datos local del dispositivo (SharedPreferences)

**P: ¿Se borran si cierro la app?**  
R: No, los datos persisten

**P: ¿Puedo editar los campos vacíos?**  
R: Sí, todos los campos son editables

**P: ¿Hay validación de datos?**  
R: Sí, el campo de edad solo acepta números

**P: ¿Cómo vuelvo al Dashboard?**  
R: Presiona el botón atrás o el ícono de atrás en la TopAppBar

---

## 📝 NOTAS

- La pantalla de Perfil está **integrada completamente** con la navegación
- El ViewModel **maneja toda la lógica** automáticamente
- Los campos son **totalmente funcionales**

---

**¡Disfruta explorando el perfil del usuario en tu emulador! 🎉**

