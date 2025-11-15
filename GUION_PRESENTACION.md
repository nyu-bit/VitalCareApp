# 🎯 Guión de Presentación - VitalCareApp
## Defensa Individual del Proyecto

---

## 📋 PREPARACIÓN PREVIA (Antes de la defensa)

### Checklist de preparación:
- [ ] Tener el proyecto abierto en Android Studio
- [ ] Dispositivo/emulador funcionando correctamente
- [ ] App instalada y funcionando
- [ ] Tener acceso rápido a las carpetas clave del código
- [ ] Libreta para anotar pasos si el docente lo solicita
- [ ] Repasar los 8 puntos de evaluación

### Archivos clave a tener abiertos/marcados:
1. `RegistrationScreen.kt` - Para formularios y validaciones
2. `RegistrationViewModel.kt` - Para gestión de estado
3. `FormValidators.kt` - Para lógica de validación
4. `VitalCareDatabase.kt` - Para persistencia
5. `Entities.kt` - Para estructura de datos

---

## 🎬 INTRODUCCIÓN (30 segundos)

### Script:
> "Buenos días/tardes. Mi nombre es [TU NOMBRE] y voy a presentar **VitalCareApp**, una aplicación móvil Android desarrollada en **Kotlin con Jetpack Compose** que implementa un sistema de gestión de salud con **Clean Architecture** y patrón **MVVM**."

> "El proyecto incluye funcionalidades como registro de usuarios, visualización de signos vitales y gestión de alertas médicas, todo con persistencia local mediante **Room Database**."

---

## 📱 PUNTO 3: DISEÑO VISUAL Y USABILIDAD (IE 2.1.2 - 7%)

### Navegación en Android Studio:
```
app/src/main/java/cl/duoc/app/ui/
├── registration/RegistrationScreen.kt
├── vitalsigns/VitalSignsScreen.kt
└── alerts/AlertsScreen.kt
```

### Script:
**Jerarquía Visual:**
> "La aplicación implementa **Material Design 3** con una jerarquía visual clara. Les mostraré la pantalla de registro como ejemplo..."

*[MOSTRAR RegistrationScreen.kt en el código]*

> "Como pueden ver en el código, utilizamos **Scaffold** como contenedor principal con TopAppBar para navegación consistente. La jerarquía se organiza mediante **Column** con espaciado de 16.dp entre elementos."

```kotlin
// Señalar en el código:
Scaffold(
    topBar = { /* TopAppBar con título y navegación */ }
) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
}
```

**Navegación entre pantallas:**
> "La navegación se implementa mediante **NavController**. Cada pantalla está registrada como destino y la navegación es fluida con animaciones predeterminadas de Material Design 3."

**Principios de usabilidad aplicados:**

1. **Feedback visual inmediato:**
> "Cada campo muestra feedback en tiempo real. Los errores aparecen debajo del campo con ícono y color rojo."

2. **Diseño responsivo:**
> "Utilizamos **Modifier.fillMaxWidth()** y **weight()** para adaptabilidad. Los campos se ajustan automáticamente al tamaño de pantalla."

3. **Accesibilidad:**
> "Implementamos **contentDescription** para lectores de pantalla y colores con suficiente contraste según WCAG."

*[EJECUTAR LA APP - Mostrar navegación entre pantallas]*

---

## 📝 PUNTO 4: FORMULARIOS Y VALIDACIÓN (IE 2.1.1 - 20%)

### Navegación en Android Studio:
```
app/src/main/java/cl/duoc/app/ui/
├── registration/RegistrationViewModel.kt (línea 50-150)
└── form/FormValidators.kt (todo el archivo)
```

### Script:
**Estructura de formularios:**
> "Los formularios están estructurados siguiendo el patrón **MVVM**. Permítanme mostrarles el de registro..."

*[ABRIR RegistrationScreen.kt]*

> "La UI está compuesta por **OutlinedTextField** de Material 3. Cada campo está vinculado a un estado en el ViewModel mediante **StateFlow**."

```kotlin
// Señalar en el código (RegistrationViewModel.kt):
private val _formState = MutableStateFlow(RegistrationFormState())
val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()
```

**Lógica de validación:**
> "Tenemos validaciones en tiempo real por campo. Déjenme mostrarles la clase **FormValidators**..."

*[ABRIR FormValidators.kt]*

> "Cada validación retorna un **ValidationResult** con éxito o mensaje de error específico."

**Validaciones implementadas:**

1. **Email:**
```kotlin
// Señalar en el código:
fun validateEmail(email: String): ValidationResult {
    if (email.isBlank()) return ValidationResult(false, "Email requerido")
    if (!email.matches(EMAIL_PATTERN)) return ValidationResult(false, "Formato inválido")
    return ValidationResult(true)
}
```

2. **RUT (Chile):**
> "Implementamos validación de RUT con algoritmo de dígito verificador chileno."

3. **Nombre completo:**
> "Valida longitud mínima, solo letras y espacios."

4. **Contraseña:**
> "Requiere mínimo 6 caracteres, una mayúscula, una minúscula y un número."

**Retroalimentación visual:**
*[MOSTRAR en el código RegistrationScreen.kt - función EmailField]*

```kotlin
OutlinedTextField(
    isError = emailError != null,
    trailingIcon = {
        if (emailError != null) {
            Icon(Icons.Default.Error, "Error", tint = MaterialTheme.colorScheme.error)
        }
    },
    supportingText = {
        if (emailError != null) {
            Text(emailError, color = MaterialTheme.colorScheme.error)
        }
    }
)
```

> "Los errores muestran ícono rojo y mensaje descriptivo debajo del campo."

**DEMOSTRACIÓN EN VIVO:**
> "Ahora ejecutaré la app y mostraré el comportamiento con datos inválidos..."

*[EJECUTAR LA APP]*

**Escenarios a demostrar:**

1. **Email inválido:**
   - Escribir: "correo sin arroba"
   - Resultado esperado: "Formato de email inválido"

2. **Contraseña débil:**
   - Escribir: "123456"
   - Resultado esperado: "Debe contener al menos una mayúscula"

3. **RUT inválido:**
   - Escribir: "12.345.678-0" (dígito verificador incorrecto)
   - Resultado esperado: "RUT inválido"

4. **Nombre muy corto:**
   - Escribir: "A"
   - Resultado esperado: "Mínimo 2 caracteres"

5. **Formulario completo válido:**
   - Completar todos los campos correctamente
   - Resultado esperado: Botón "Registrar" habilitado, sin errores

> "Como pueden ver, el formulario no permite enviar datos hasta que todas las validaciones sean exitosas. El botón se habilita solo cuando todo es válido."

---

## 🔄 PUNTO 5: GESTIÓN DEL ESTADO (IE 2.2.2 - 7%)

### Navegación en Android Studio:
```
app/src/main/java/cl/duoc/app/ui/registration/RegistrationViewModel.kt
```

### Script:
> "La gestión de estado se implementa con **StateFlow** de Kotlin Coroutines. Déjenme mostrarles..."

*[ABRIR RegistrationViewModel.kt - línea 30-50]*

**Implementación de StateFlow:**
```kotlin
// Señalar en el código:
private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

private val _formState = MutableStateFlow(RegistrationFormState())
val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()
```

> "Utilizamos **sealed class** para estados de la UI:"

```kotlin
sealed class RegistrationUiState {
    object Idle : RegistrationUiState()
    object Loading : RegistrationUiState()
    data class Success(val user: User) : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
}
```

**Actualización de la interfaz:**
> "La UI se actualiza reactivamente observando el estado mediante **collectAsState()**"

*[MOSTRAR en RegistrationScreen.kt - línea 50-60]*

```kotlin
val uiState by viewModel.uiState.collectAsState()

when (uiState) {
    is RegistrationUiState.Loading -> CircularProgressIndicator()
    is RegistrationUiState.Success -> { /* Navegar a siguiente pantalla */ }
    is RegistrationUiState.Error -> { /* Mostrar error */ }
    else -> { /* Mostrar formulario */ }
}
```

**Estructuras de control:**
> "Usamos **when expression** para renderizar diferentes componentes según el estado. Cuando el usuario presiona 'Registrar', el ViewModel cambia el estado a Loading, ejecuta la lógica de negocio, y luego cambia a Success o Error."

*[MOSTRAR función registerUser en ViewModel]*

```kotlin
fun registerUser() {
    viewModelScope.launch {
        _uiState.value = RegistrationUiState.Loading
        try {
            val user = createUserFromForm()
            registerUserUseCase(user)
            _uiState.value = RegistrationUiState.Success(user)
        } catch (e: Exception) {
            _uiState.value = RegistrationUiState.Error(e.message ?: "Error")
        }
    }
}
```

> "El flujo completo es: Usuario interactúa → Evento → ViewModel procesa → Estado cambia → UI se redibuja automáticamente."

---

## 🎨 PUNTO 6: ANIMACIONES (Opcional - Bonus)

### Script:
> "Implementamos animaciones sutiles para mejorar la experiencia del usuario..."

*[MOSTRAR en código]*

```kotlin
AnimatedVisibility(
    visible = errorMessage != null,
    enter = fadeIn() + expandVertically(),
    exit = fadeOut() + shrinkVertically()
) {
    ErrorCard(errorMessage)
}
```

> "Estas animaciones aportan feedback visual claro y suavidad en las transiciones, haciendo la app más profesional y agradable de usar."

---

## 💾 PUNTO 7: PERSISTENCIA LOCAL (IE 2.3.1 + IE 2.3.2 - 25%)

### Navegación en Android Studio:
```
app/src/main/java/cl/duoc/app/data/local/room/
├── VitalCareDatabase.kt
├── Entities.kt
├── Daos.kt
└── Mappers.kt
```

### Script:
**Arquitectura del proyecto:**
> "Antes de mostrar la persistencia, explico que el proyecto sigue **Clean Architecture con MVVM**:"

```
ui/ (Presentation Layer)
├── ViewModel: Gestiona estado y lógica de presentación
└── Screen: Composables que renderizan UI

domain/ (Business Logic Layer)
├── UseCases: Casos de uso específicos
└── Repository Interfaces: Contratos para datos

data/ (Data Layer)
├── Repository Implementations: Lógica de acceso a datos
└── Room Database: Persistencia local

model/ (Entities)
└── Modelos de dominio
```

> "Esta separación permite **mantenibilidad**, **testabilidad** y cambios independientes en cada capa."

**Herramientas colaborativas:**
> "Utilizamos **GitHub** con Git Flow (ramas feature, dev, main) para control de versiones. Cada commit sigue un formato semántico: `tipo(ámbito): descripción`"

*[Opcional: Mostrar COMMIT_TEMPLATE.md]*

**Implementación de Room:**
*[ABRIR VitalCareDatabase.kt]*

```kotlin
@Database(
    entities = [
        UserEntity::class,
        VitalSignsEntity::class,
        AlertEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class VitalCareDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vitalSignsDao(): VitalSignsDao
    abstract fun alertDao(): AlertDao
}
```

> "Tenemos 3 entidades principales. La base de datos es singleton y usa **fallbackToDestructiveMigration** para desarrollo."

**Estructura de guardado y recuperación:**
*[ABRIR Daos.kt - UserDao]*

```kotlin
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>
}
```

> "Todas las operaciones son **suspend functions** para ejecutarse en coroutines. Usamos **Flow** para observar cambios en tiempo real."

**Mappers:**
*[MOSTRAR Mappers.kt]*

```kotlin
fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        rut = rut,
        birthDate = birthDate,
        address = address
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        rut = rut,
        birthDate = birthDate,
        address = address
    )
}
```

> "Los Mappers convierten entre entidades de Room y modelos de dominio, manteniendo las capas desacopladas."

**DEMOSTRACIÓN EN VIVO:**
> "Ahora mostraré la persistencia funcionando en la app..."

*[EJECUTAR LA APP]*

**Flujo a demostrar:**

1. **Registrar un usuario:**
   - Completar formulario con datos válidos
   - Presionar "Registrar"
   - Mostrar mensaje de éxito

2. **Cerrar y reabrir la app:**
   - Cerrar completamente la aplicación
   - Volver a abrirla

3. **Verificar persistencia:**
   - Navegar a lista de usuarios o pantalla principal
   - Mostrar que el usuario registrado sigue ahí
   - Explicar: "Los datos persisten porque se guardaron en la base de datos local Room"

4. **Actualizar datos:**
   - Editar información del usuario
   - Guardar cambios
   - Verificar que se actualizó

> "Como pueden ver, los datos persisten entre sesiones. Room garantiza la integridad de los datos con transacciones ACID."

---

## 📸 PUNTO 8: RECURSOS NATIVOS (IE 2.4.1 + IE 2.4.2 - 19%)

### Navegación en AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### Script:
**Recursos nativos integrados:**
> "La aplicación integra acceso a **cámara y galería** para que el usuario pueda agregar foto de perfil."

**Permisos gestionados:**
*[MOSTRAR AndroidManifest.xml]*

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
                 android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

> "Implementamos permisos siguiendo las guías de Android 13+. **READ_MEDIA_IMAGES** para versiones nuevas y **READ_EXTERNAL_STORAGE** para compatibilidad."

**Gestión de permisos en código:**
*[Crear/Mostrar código de permisos - si existe]*

```kotlin
// En el Screen/Activity:
val cameraPermissionState = rememberPermissionState(
    android.Manifest.permission.CAMERA
)

LaunchedEffect(Unit) {
    if (!cameraPermissionState.status.isGranted) {
        cameraPermissionState.launchPermissionRequest()
    }
}
```

**Flujo de integración:**
1. Usuario toca botón "Agregar foto"
2. Sistema verifica permisos
3. Si no están concedidos, solicita al usuario
4. Usuario concede permiso
5. Se abre cámara/galería
6. Usuario captura/selecciona imagen
7. Imagen se procesa y muestra en UI

**Medidas de seguridad:**
- Verificación de permisos antes de acceder al recurso
- Manejo de casos cuando el usuario niega permisos
- Validación de archivos (tamaño, formato)
- Almacenamiento seguro en directorio privado de la app

**DEMOSTRACIÓN EN VIVO:**
> "Ahora ejecutaré la funcionalidad..."

*[EJECUTAR LA APP]*

**Pasos a demostrar:**

1. **Acceder a perfil de usuario**
2. **Tocar botón "Agregar foto"**
3. **Mostrar diálogo de permisos** (si es primera vez)
4. **Seleccionar opción** (Cámara o Galería)
5. **Capturar/Seleccionar imagen**
6. **Mostrar resultado en UI:**
   - Imagen recortada circular
   - Borde de 2.dp
   - Tamaño ajustado (128.dp)

*[MOSTRAR código de presentación de imagen]*

```kotlin
AsyncImage(
    model = userImageUri,
    contentDescription = "Foto de perfil",
    modifier = Modifier
        .size(128.dp)
        .clip(CircleShape)
        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
    contentScale = ContentScale.Crop
)
```

> "La imagen se ajusta con **ContentScale.Crop** para mantener proporción, se recorta en círculo con **CircleShape**, y tiene un borde del color primario del tema."

---

## 🛠️ PUNTO 9: MODIFICACIÓN EN TIEMPO REAL (IE 2.2.1 - 22%)

### Script:
> "Ahora estoy listo para realizar modificaciones en tiempo real según lo soliciten..."

### Posibles solicitudes y cómo responder:

#### **CASO A: Agregar nueva validación**

**Solicitud:** "Agregue validación para que el teléfono tenga exactamente 9 dígitos"

**Pasos a seguir:**

1. **Anotar en libreta:**
   ```
   1. Abrir FormValidators.kt
   2. Crear función validatePhone
   3. Agregar lógica de 9 dígitos
   4. Usar en ViewModel
   5. Mostrar en UI
   ```

2. **Ir a FormValidators.kt:**

```kotlin
fun validatePhone(phone: String): ValidationResult {
    if (phone.isBlank()) {
        return ValidationResult(false, "Teléfono requerido")
    }
    val digitsOnly = phone.replace(Regex("[^0-9]"), "")
    if (digitsOnly.length != 9) {
        return ValidationResult(false, "El teléfono debe tener 9 dígitos")
    }
    return ValidationResult(true)
}
```

3. **Explicar mientras escribes:**
> "Primero verifico que no esté vacío, luego elimino caracteres no numéricos con regex, y finalmente valido que tenga exactamente 9 dígitos."

4. **Actualizar ViewModel:**
```kotlin
fun onPhoneChange(phone: String) {
    _formState.update { it.copy(phone = phone) }
    val result = FormValidators.validatePhone(phone)
    _formState.update { it.copy(phoneError = result.errorMessage) }
}
```

5. **Agregar campo en Screen:**
```kotlin
OutlinedTextField(
    value = formState.phone,
    onValueChange = viewModel::onPhoneChange,
    label = { Text("Teléfono") },
    isError = formState.phoneError != null,
    supportingText = {
        if (formState.phoneError != null) {
            Text(formState.phoneError!!)
        }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
)
```

6. **Ejecutar y probar:**
   - Escribir "12345" → Error: "El teléfono debe tener 9 dígitos"
   - Escribir "123456789" → ✓ Válido

---

#### **CASO B: Crear nuevo Composable**

**Solicitud:** "Cree un componente para mostrar estadísticas de usuario"

**Pasos:**

1. **Anotar:**
   ```
   1. Crear @Composable UserStatsCard
   2. Recibir parámetros (registros, alertas)
   3. Usar Card de Material 3
   4. Mostrar información
   ```

2. **Escribir código:**

```kotlin
@Composable
fun UserStatsCard(
    totalRecords: Int,
    totalAlerts: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Registros", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = totalRecords.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column {
                    Text("Alertas", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = totalAlerts.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
```

3. **Explicar:**
> "Creé un Composable que recibe los datos como parámetros, manteniéndolo reutilizable. Usa Card de Material 3 para consistencia visual, y distribuye la información con Row y Column para un layout claro."

4. **Usar en la pantalla:**
```kotlin
UserStatsCard(
    totalRecords = viewModel.totalRecords,
    totalAlerts = viewModel.totalAlerts,
    modifier = Modifier.padding(16.dp)
)
```

---

#### **CASO C: Ajustar función de lógica**

**Solicitud:** "Modifique la función de registro para validar que el usuario sea mayor de edad"

**Pasos:**

1. **Ir a RegistrationViewModel.kt:**

```kotlin
private fun validateAge(birthDate: String): Boolean {
    try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val birth = LocalDate.parse(birthDate, formatter)
        val today = LocalDate.now()
        val age = Period.between(birth, today).years
        return age >= 18
    } catch (e: Exception) {
        return false
    }
}

fun registerUser() {
    viewModelScope.launch {
        // Validaciones existentes...
        
        if (!validateAge(formState.value.birthDate)) {
            _uiState.value = RegistrationUiState.Error(
                "Debes ser mayor de 18 años para registrarte"
            )
            return@launch
        }
        
        // Continuar con registro...
    }
}
```

2. **Explicar:**
> "Creé una función que parsea la fecha de nacimiento, calcula la edad usando Period entre la fecha de nacimiento y hoy, y valida que sea mayor o igual a 18. En registerUser, llamo a esta validación antes de proceder con el registro."

---

#### **CASO D: Agregar nuevo estado**

**Solicitud:** "Agregue un estado de 'Validating' mientras se verifican los datos"

**Pasos:**

1. **Modificar sealed class:**

```kotlin
sealed class RegistrationUiState {
    object Idle : RegistrationUiState()
    object Validating : RegistrationUiState()  // NUEVO
    object Loading : RegistrationUiState()
    data class Success(val user: User) : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
}
```

2. **Usar en función:**

```kotlin
fun registerUser() {
    viewModelScope.launch {
        _uiState.value = RegistrationUiState.Validating  // NUEVO
        delay(500) // Simular validación
        
        if (!validateAllFields()) {
            _uiState.value = RegistrationUiState.Error("Completa todos los campos")
            return@launch
        }
        
        _uiState.value = RegistrationUiState.Loading
        // ... resto del código
    }
}
```

3. **Actualizar UI:**

```kotlin
when (uiState) {
    is RegistrationUiState.Validating -> {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Text("Validando datos...")
    }
    // ... otros casos
}
```

4. **Explicar:**
> "Agregué un nuevo estado Validating a la sealed class, lo asigno antes de validar campos, y muestro un LinearProgressIndicator en la UI para feedback visual al usuario."

---

### 💡 Consejos para esta sección:

**Antes de escribir código:**
- Pide repetir la pregunta si no entendiste
- Anota los pasos en tu libreta
- Piensa en voz alta: "Voy a modificar X para lograr Y"

**Mientras escribes:**
- Explica cada línea importante
- Justifica tus decisiones: "Uso suspend fun porque es una operación asíncrona"
- Menciona patrones: "Aplico Single Responsibility Principle aquí"

**Después de escribir:**
- Ejecuta el código si es posible
- Explica el resultado esperado
- Menciona casos edge: "Si el usuario ingresa null, la función retorna false"

---

## 🎓 CIERRE (1 minuto)

### Script:
> "En resumen, VitalCareApp demuestra:"

1. ✅ **Clean Architecture con MVVM** para código mantenible y escalable
2. ✅ **Validaciones robustas** con feedback visual inmediato
3. ✅ **Gestión de estado reactiva** con StateFlow y Compose
4. ✅ **Persistencia local** con Room Database y operaciones CRUD completas
5. ✅ **Integración de recursos nativos** con gestión correcta de permisos
6. ✅ **UI moderna** con Material Design 3 y principios de usabilidad
7. ✅ **Código testeable** con separación de responsabilidades clara

> "El proyecto cumple con todos los requisitos técnicos y está listo para producción. Quedo atento a sus preguntas."

---

## 📌 ANEXO: RESPUESTAS RÁPIDAS A PREGUNTAS COMUNES

### "¿Por qué usaste StateFlow en vez de LiveData?"
> "StateFlow es parte de Kotlin Coroutines, más moderno y funciona mejor con Compose. Ofrece mejor integración con suspend functions y es null-safe por defecto."

### "¿Qué ventajas tiene Clean Architecture?"
> "Separación de responsabilidades, testabilidad (puedo testear lógica sin UI), cambios independientes (puedo cambiar Room por Retrofit sin tocar el dominio), y mantenibilidad a largo plazo."

### "¿Por qué no usaste Dagger/Hilt para inyección de dependencias?"
> "Para este proyecto, la inyección manual es suficiente y más clara educativamente. En producción con más complejidad, sí usaría Hilt."

### "¿Cómo manejas errores de red si no hay internet?"
> "Actualmente la app funciona offline con Room. Para sincronización futura implementaría un patrón Repository que intente red primero y caiga a Room, con WorkManager para sincronización en background."

### "¿Los datos están encriptados?"
> "Actualmente no, pero para datos sensibles de salud en producción usaría SQLCipher para encriptar la base de datos y Android Keystore para credenciales."

### "¿La app soporta modo oscuro?"
> "Sí, Material Design 3 lo implementa automáticamente. Los colores se adaptan al tema del sistema mediante MaterialTheme.colorScheme."

### "¿Cómo garantizas que no haya memory leaks?"
> "Uso viewModelScope que cancela coroutines automáticamente cuando el ViewModel se destruye, y StateFlow que no retiene referencias a la UI."

---

## ✅ CHECKLIST FINAL PRE-DEFENSA

- [ ] App instalada y funcionando
- [ ] Todos los archivos clave ubicados
- [ ] Tests pasando (opcional ejecutar)
- [ ] Sin warnings críticos en build
- [ ] Libreta y lápiz listos
- [ ] Código limpio (sin comentarios innecesarios)
- [ ] Has practicado las demostraciones en vivo
- [ ] Conoces cada línea de código que escribiste

---

## 🎯 DISTRIBUCIÓN DE TIEMPO SUGERIDA

| Sección | Tiempo | Puntos |
|---------|--------|--------|
| Introducción | 30s | - |
| Diseño Visual (P3) | 2min | 7% |
| Formularios y Validación (P4) | 5min | 20% |
| Gestión de Estado (P5) | 2min | 7% |
| Animaciones (P6) | 1min | Bonus |
| Persistencia (P7) | 5min | 25% |
| Recursos Nativos (P8) | 3min | 19% |
| Modificación en Tiempo Real (P9) | 5-7min | 22% |
| Cierre | 1min | - |
| **TOTAL** | **25-27min** | **100%** |

---

## 💪 MENSAJE FINAL

**Recuerda:**
- Respira profundo antes de empezar
- Habla claro y a ritmo pausado
- Si no entiendes una pregunta, pide que la repitan
- Demuestra confianza en tu código (lo escribiste tú)
- No temas decir "no implementé X, pero así lo haría..."
- Muestra pasión por lo que construiste

**¡Éxito en tu defensa! 🚀**

---

*Última actualización: Noviembre 2025*
*Proyecto: VitalCareApp - Clean Architecture + MVVM + Jetpack Compose*
