# 🔧 Guía de Solución de Problemas - VitalCareApp

Esta guía explica los errores más comunes que pueden aparecer en Android Studio al desarrollar VitalCareApp y proporciona soluciones detalladas para cada uno.

## 📋 Tabla de Contenidos

1. [Errores de Compilación](#errores-de-compilación)
2. [Errores de Dependencias](#errores-de-dependencias)
3. [Errores de Room Database](#errores-de-room-database)
4. [Errores de Compose](#errores-de-compose)
5. [Errores de Coroutines](#errores-de-coroutines)
6. [Errores de Testing](#errores-de-testing)
7. [Errores de Runtime](#errores-de-runtime)
8. [Errores de Git](#errores-de-git)

---

## 1. Errores de Compilación

### ❌ Error: "Unresolved reference"

**Mensaje de error:**
```
Unresolved reference: nombre_de_clase
```

**Causas comunes:**
- Import faltante
- Clase no existe en el paquete especificado
- Error tipográfico en el nombre

**Soluciones:**

1. **Agregar el import correcto:**
   ```kotlin
   import cl.duoc.app.model.User
   import cl.duoc.app.domain.repository.UserRepository
   ```

2. **Verificar el package declaration:**
   - Asegúrate que el archivo esté en el paquete correcto
   - El `package` al inicio del archivo debe coincidir con la estructura de carpetas

3. **Sincronizar proyecto:**
   - `File > Sync Project with Gradle Files`
   - O presiona el ícono de elefante en la barra de herramientas

4. **Invalidar cache:**
   - `File > Invalidate Caches / Restart...`
   - Selecciona "Invalidate and Restart"

---

### ❌ Error: "Type mismatch"

**Mensaje de error:**
```
Type mismatch: inferred type is String but Int was expected
```

**Causa:**
- Estás pasando un tipo de dato incorrecto a una función o variable

**Solución:**

```kotlin
// ❌ Incorrecto
val edad: Int = "25"

// ✅ Correcto
val edad: Int = 25
// O convertir:
val edad: Int = "25".toInt()
```

---

### ❌ Error: "Smart cast to 'Type' is impossible"

**Mensaje de error:**
```
Smart cast to 'User' is impossible, because 'user' is a mutable property
```

**Causa:**
- Kotlin no puede hacer smart cast en propiedades mutables que pueden cambiar

**Solución:**

```kotlin
// ❌ Problema
var user: User? = getUser()
if (user != null) {
    println(user.name) // Error aquí
}

// ✅ Solución 1: Usar let
user?.let {
    println(it.name)
}

// ✅ Solución 2: Variable local
val localUser = user
if (localUser != null) {
    println(localUser.name)
}

// ✅ Solución 3: Safe call
println(user?.name)
```

---

## 2. Errores de Dependencias

### ❌ Error: "Failed to resolve"

**Mensaje de error:**
```
Failed to resolve: androidx.compose.ui:ui:1.5.0
```

**Causas:**
- Repositorio no configurado
- Versión no existe
- Problemas de conexión

**Soluciones:**

1. **Verificar repositorios en `settings.gradle.kts`:**
   ```kotlin
   dependencyResolutionManagement {
       repositories {
           google()
           mavenCentral()
       }
   }
   ```

2. **Verificar versiones en `libs.versions.toml`:**
   ```toml
   [versions]
   compose = "1.5.4"
   ```

3. **Sincronizar con Gradle:**
   - Click en "Sync Now" en el banner amarillo
   - O `File > Sync Project with Gradle Files`

4. **Limpiar proyecto:**
   ```bash
   ./gradlew clean
   ./gradlew build --refresh-dependencies
   ```

---

### ❌ Error: "Duplicate class found"

**Mensaje de error:**
```
Duplicate class kotlin.collections.ArrayDeque found in modules
```

**Causa:**
- Múltiples versiones de la misma librería

**Solución:**

Agregar exclusiones en `build.gradle.kts`:
```kotlin
dependencies {
    implementation("androidx.room:room-runtime:2.6.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
}
```

---

## 3. Errores de Room Database

### ❌ Error: "Cannot figure out how to save this field into database"

**Mensaje de error:**
```
Cannot figure out how to save this field into database. You can consider adding a type converter for it.
```

**Causa:**
- Room no sabe cómo guardar un tipo complejo (Date, List, enum, etc.)

**Solución:**

Crear un TypeConverter:

```kotlin
import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

// Registrar en el Database
@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VitalCareDatabase : RoomDatabase() {
    // ...
}
```

---

### ❌ Error: "Migration didn't properly handle"

**Mensaje de error:**
```
Room cannot verify the data integrity. Looks like you've changed schema but forgot to update the version number.
```

**Causa:**
- Cambiaste la estructura de la BD pero no la versión

**Solución:**

```kotlin
// Opción 1: Incrementar versión y crear migración
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT")
    }
}

Room.databaseBuilder(context, VitalCareDatabase::class.java, "database")
    .addMigrations(MIGRATION_1_2)
    .build()

// Opción 2: Durante desarrollo, destruir y recrear
Room.databaseBuilder(context, VitalCareDatabase::class.java, "database")
    .fallbackToDestructiveMigration()
    .build()
```

---

### ❌ Error: "A RoomDatabase class must be annotated with @Database"

**Solución:**

```kotlin
@Database(
    entities = [User::class, Reservation::class, VitalSigns::class],
    version = 1,
    exportSchema = false
)
abstract class VitalCareDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reservationDao(): ReservationDao
    abstract fun vitalSignsDao(): VitalSignsDao
}
```

---

## 4. Errores de Compose

### ❌ Error: "@Composable invocations can only happen from the context of a @Composable function"

**Causa:**
- Intentas llamar una función @Composable desde una función regular

**Solución:**

```kotlin
// ❌ Incorrecto
fun regularFunction() {
    Text("Hello") // Error
}

// ✅ Correcto
@Composable
fun ComposableFunction() {
    Text("Hello")
}

// ✅ Para usar en ViewModel, usar efecto
@Composable
fun MyScreen(viewModel: MyViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
}
```

---

### ❌ Error: "Calling setValue on a StateFlow is not allowed"

**Mensaje de error:**
```
MutableStateFlow.value cannot be called from a coroutine
```

**Causa:**
- Intentas modificar StateFlow.value directamente desde una coroutine

**Solución:**

```kotlin
// ❌ Incorrecto
viewModelScope.launch {
    _uiState.value = _uiState.value.copy(loading = true)
}

// ✅ Correcto: Usar update
viewModelScope.launch {
    _uiState.update { it.copy(loading = true) }
}

// ✅ O usar emit
viewModelScope.launch {
    _uiState.emit(currentState.copy(loading = true))
}
```

---

### ❌ Error: "Remembered value is accessed but not initialized"

**Causa:**
- Estado no inicializado correctamente con remember

**Solución:**

```kotlin
// ❌ Incorrecto
@Composable
fun MyScreen() {
    var text: String by remember { mutableStateOf() } // Sin valor inicial
}

// ✅ Correcto
@Composable
fun MyScreen() {
    var text by remember { mutableStateOf("") }
}
```

---

## 5. Errores de Coroutines

### ❌ Error: "Suspend function should be called only from a coroutine or another suspend function"

**Causa:**
- Intentas llamar una suspend function desde código regular

**Solución:**

```kotlin
// ❌ Incorrecto
fun loadData() {
    val data = repository.getData() // getData es suspend
}

// ✅ Correcto: Usar viewModelScope en ViewModel
fun loadData() {
    viewModelScope.launch {
        val data = repository.getData()
    }
}

// ✅ En Activity/Fragment
lifecycleScope.launch {
    val data = repository.getData()
}

// ✅ En función regular con runBlocking (solo para tests)
fun test() = runBlocking {
    val data = repository.getData()
}
```

---

### ❌ Error: "Job was cancelled"

**Mensaje de error:**
```
kotlinx.coroutines.JobCancellationException: Job was cancelled
```

**Causa:**
- Coroutine cancelada antes de completarse

**Solución:**

```kotlin
// ✅ Manejar cancelación
viewModelScope.launch {
    try {
        val data = repository.getData()
    } catch (e: CancellationException) {
        // No capturar, dejar que se propague
        throw e
    } catch (e: Exception) {
        // Manejar otros errores
        handleError(e)
    }
}

// ✅ Usar NonCancellable para operaciones críticas
viewModelScope.launch {
    withContext(NonCancellable) {
        repository.saveImportantData()
    }
}
```

---

## 6. Errores de Testing

### ❌ Error: "Method ... not mocked"

**Mensaje de error:**
```
java.lang.RuntimeException: Method println not mocked
```

**Causa:**
- Código de Android llamado en unit test sin mockear

**Solución:**

1. **Agregar en `build.gradle.kts`:**
   ```kotlin
   android {
       testOptions {
           unitTests {
               isReturnDefaultValues = true
           }
       }
   }
   ```

2. **Mockear dependencias de Android:**
   ```kotlin
   @Mock
   lateinit var context: Context
   
   @Before
   fun setup() {
       MockitoAnnotations.openMocks(this)
   }
   ```

---

### ❌ Error: "This job has not completed yet"

**Causa:**
- Test termina antes que las coroutines en ViewModel

**Solución:**

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test coroutine`() = runTest {
        viewModel.loadData()
        advanceUntilIdle() // Esperar a que termine
        
        val state = viewModel.uiState.value
        assertTrue(state.isLoaded)
    }
}
```

---

## 7. Errores de Runtime

### ❌ Error: "NullPointerException"

**Mensaje de error:**
```
java.lang.NullPointerException: Attempt to invoke virtual method on a null object reference
```

**Prevención:**

```kotlin
// ❌ Evitar
val name = user.name // Si user es null, crash

// ✅ Usar safe calls
val name = user?.name

// ✅ Usar elvis operator
val name = user?.name ?: "Unknown"

// ✅ Usar let
user?.let {
    println(it.name)
}

// ✅ Usar requireNotNull con mensaje
val safeUser = requireNotNull(user) { "User should not be null at this point" }
```

---

### ❌ Error: "IllegalStateException: Fragment not attached to a context"

**Causa:**
- Intentas acceder al contexto después de que el Fragment se destruyó

**Solución:**

```kotlin
// ✅ Verificar si está attached
if (isAdded) {
    requireContext().getString(R.string.app_name)
}

// ✅ Usar viewLifecycleOwner en Fragments
viewLifecycleOwner.lifecycleScope.launch {
    // Operaciones
}
```

---

### ❌ Error: "Resources$NotFoundException"

**Mensaje de error:**
```
android.content.res.Resources$NotFoundException: String resource ID #0x7f0e0123
```

**Causa:**
- Recurso no existe o ID incorrecto

**Solución:**

1. **Limpiar y reconstruir:**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

2. **Verificar que el recurso existe en `res/values/strings.xml`:**
   ```xml
   <string name="app_name">VitalCareApp</string>
   ```

3. **Usar recursos correctamente:**
   ```kotlin
   // ✅ Correcto
   getString(R.string.app_name)
   
   // ❌ Incorrecto
   getString(12345)
   ```

---

## 8. Errores de Git

### ❌ Error: "Your local changes would be overwritten by merge"

**Solución:**

```bash
# Opción 1: Guardar cambios temporalmente
git stash
git pull
git stash pop

# Opción 2: Commit cambios
git add .
git commit -m "Save work in progress"
git pull

# Opción 3: Descartar cambios locales
git reset --hard HEAD
git pull
```

---

### ❌ Error: "Merge conflict"

**Solución:**

1. **Ver archivos en conflicto:**
   ```bash
   git status
   ```

2. **Abrir archivo y resolver conflictos:**
   ```kotlin
   <<<<<<< HEAD
   val name = "Angel"
   =======
   val name = "Carlos"
   >>>>>>> feature-branch
   ```

3. **Elegir versión correcta y eliminar marcadores:**
   ```kotlin
   val name = "Angel"
   ```

4. **Marcar como resuelto y completar merge:**
   ```bash
   git add archivo_resuelto.kt
   git commit -m "Resolve merge conflict"
   ```

---

## 🔍 Herramientas de Diagnóstico

### Logcat

Para ver logs en Android Studio:

```kotlin
// En código
import android.util.Log

Log.d("TAG", "Debug message")
Log.i("TAG", "Info message")
Log.w("TAG", "Warning message")
Log.e("TAG", "Error message", exception)

// O usar nuestro ErrorHandler
ErrorHandler.logError("MyViewModel", "Error loading data", exception)
```

### Build Analyzer

1. `View > Tool Windows > Build Analyzer`
2. Analiza por qué la compilación es lenta
3. Muestra plugins que tardan más

### Profiler

1. `View > Tool Windows > Profiler`
2. Monitorea CPU, memoria, red
3. Identifica memory leaks y problemas de performance

---

## 📞 Recursos Adicionales

- [Documentación oficial de Android](https://developer.android.com)
- [Kotlin docs](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose docs](https://developer.android.com/jetpack/compose/documentation)
- [Room Database guide](https://developer.android.com/training/data-storage/room)
- [Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html)

---

## ✅ Checklist de Solución Rápida

Cuando tengas un error, intenta en orden:

1. ☐ Leer el mensaje de error completo
2. ☐ Verificar imports
3. ☐ Sync Project with Gradle Files
4. ☐ Clean Project + Rebuild
5. ☐ Invalidate Caches / Restart
6. ☐ Verificar versiones en `libs.versions.toml`
7. ☐ Revisar Logcat para más detalles
8. ☐ Buscar en Stack Overflow / documentación oficial
9. ☐ Consultar esta guía
10. ☐ Pedir ayuda al equipo

---

**Última actualización:** Noviembre 2025  
**Versión:** 1.0  
**Proyecto:** VitalCareApp - Clean Architecture
