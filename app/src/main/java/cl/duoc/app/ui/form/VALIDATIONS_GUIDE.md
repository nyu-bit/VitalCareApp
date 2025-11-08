# Validaciones Personalizadas - Guía de Uso

## Descripción
Sistema completo de validaciones reutilizables para formularios en Kotlin con mensajes de error reactivos.

## Archivos
- `FormValidators.kt`: Objeto con validaciones predefinidas y personalizables
- `FormViewModel.kt`: Ejemplo de uso en ViewModel con validación en submit
- `ReactiveFormViewModel.kt`: Ejemplo con validaciones en tiempo real

## Validaciones Disponibles

### 1. Validaciones Básicas

```kotlin
// Campo requerido
FormValidators.required("El nombre")

// Longitud mínima
FormValidators.minLength(3, "El nombre")

// Longitud máxima
FormValidators.maxLength(50, "El nombre")
```

### 2. Formato de Email

```kotlin
// Email básico
FormValidators.email

// Email estricto (recomendado)
FormValidators.emailStrict
```

### 3. Contraseñas

```kotlin
// Contraseña simple (solo longitud)
FormValidators.password(minLength = 6)

// Contraseña segura
FormValidators.password(
    minLength = 8,
    requireUppercase = true,
    requireLowercase = true,
    requireDigit = true,
    requireSpecialChar = true
)

// Coincidencia de contraseñas
FormValidators.passwordMatch(originalPassword)
```

### 4. Validaciones de Tipo

```kotlin
// Solo letras
FormValidators.onlyLetters("El nombre")

// Solo números
FormValidators.onlyDigits("El código")

// Rango numérico
FormValidators.numberRange(18, 120, "La edad")
```

### 5. Validaciones Específicas (Chile)

```kotlin
// Teléfono chileno
FormValidators.phoneChile

// RUT chileno con dígito verificador
FormValidators.rutChile
```

### 6. Validaciones Avanzadas

```kotlin
// Patrón regex personalizado
FormValidators.pattern(
    regex = "^[A-Z]{3}-\\d{4}$".toRegex(),
    errorMessage = "Formato debe ser ABC-1234"
)

// Lista negra
FormValidators.notIn(
    listOf("admin", "root", "test"),
    "Este nombre de usuario"
)
```

## Combinar Validaciones

Puedes combinar múltiples validaciones para un mismo campo:

```kotlin
val nameValidator = FormValidators.combine(
    FormValidators.required("El nombre"),
    FormValidators.minLength(3, "El nombre"),
    FormValidators.maxLength(50, "El nombre"),
    FormValidators.onlyLetters("El nombre")
)
```

## Uso en ViewModel

### Opción 1: Validación al enviar formulario

```kotlin
class FormViewModel : ViewModel() {
    private val emailValidator = FormValidators.emailStrict
    
    private fun validateForm() {
        val emailResult = emailValidator.validate(state.email)
        if (emailResult is ValidationResult.Error) {
            _state.update { it.copy(emailError = emailResult.message) }
        }
    }
}
```

### Opción 2: Validación en tiempo real

```kotlin
class ReactiveViewModel : ViewModel() {
    private val emailValidator = FormValidators.emailStrict
    
    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
        
        // Validar inmediatamente
        val error = emailValidator.getErrorOrNull(email)
        _state.update { it.copy(emailError = error) }
    }
}
```

## Extensiones Útiles

```kotlin
// Obtener mensaje de error o null
val error: String? = validator.getErrorOrNull(value)

// Verificar si es válido
val isValid: Boolean = validator.isValid(value)
```

## Ejemplos Completos

### Formulario de Registro

```kotlin
// Validadores
private val nameValidator = FormValidators.combine(
    FormValidators.required("El nombre"),
    FormValidators.minLength(3, "El nombre")
)

private val emailValidator = FormValidators.emailStrict

private val passwordValidator = FormValidators.password(
    minLength = 8,
    requireUppercase = true,
    requireDigit = true
)

// Uso
fun onSubmit() {
    val errors = mutableListOf<String>()
    
    nameValidator.validate(state.name).let {
        if (it is ValidationResult.Error) errors.add(it.message)
    }
    
    emailValidator.validate(state.email).let {
        if (it is ValidationResult.Error) errors.add(it.message)
    }
    
    passwordValidator.validate(state.password).let {
        if (it is ValidationResult.Error) errors.add(it.message)
    }
    
    if (errors.isEmpty()) {
        // Procesar formulario
    }
}
```

## Características

✅ **Reutilizable**: Define una vez, usa en múltiples formularios
✅ **Composable**: Combina validaciones según necesites
✅ **Reactivo**: Mensajes de error actualizados en tiempo real
✅ **Extensible**: Fácil agregar nuevas validaciones personalizadas
✅ **Tipado**: Uso de sealed classes para resultados seguros
✅ **Localizable**: Mensajes de error personalizables
✅ **Testeable**: Funciones puras fáciles de probar

## Crear Validador Personalizado

```kotlin
// En FormValidators.kt
fun custom(predicate: (String) -> Boolean, errorMsg: String): Validator = 
    Validator { value ->
        if (predicate(value)) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errorMsg)
        }
    }

// Uso
val noSpacesValidator = FormValidators.custom(
    predicate = { !it.contains(" ") },
    errorMsg = "No se permiten espacios"
)
```
