# 🗄️ Arquitectura de Base de Datos - VitalCare

## SQLite con Room Database

Este proyecto utiliza **Room**, la biblioteca de persistencia de datos recomendada por Android, que proporciona una capa de abstracción sobre SQLite.

---

## 📊 Estructura de la Base de Datos

### **Archivo de Base de Datos**
- **Nombre**: `vitalcare_database.db`
- **Ubicación**: `/data/data/cl.duoc.app/databases/vitalcare_database.db`
- **Versión**: 1

---

## 🏗️ Arquitectura de Capas

```
┌─────────────────────────────────────┐
│         UI Layer (Compose)          │
│      HomeScreen.kt                  │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      ViewModel Layer                │
│      HomeViewModel.kt               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Repository Layer               │
│  - PacienteRepository.kt            │
│  - EspecialidadRepository.kt        │
│  - CitaRepository.kt                │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│         DAO Layer                   │
│  - PacienteDao.kt                   │
│  - EspecialidadDao.kt               │
│  - CitaDao.kt                       │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Room Database                  │
│   VitalCareDatabase.kt              │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      SQLite Database                │
│   vitalcare_database.db             │
└─────────────────────────────────────┘
```

---

## 📋 Tablas (Entidades)

### 1. **Pacientes** (`pacientes`)

| Columna         | Tipo    | Descripción                    |
|-----------------|---------|--------------------------------|
| id              | Long    | PK, Auto-incremento            |
| rut             | String  | RUT del paciente               |
| nombre          | String  | Nombre del paciente            |
| apellido        | String  | Apellido del paciente          |
| email           | String  | Email de contacto              |
| telefono        | String  | Teléfono de contacto           |
| fechaNacimiento | String  | Fecha de nacimiento (yyyy-MM-dd)|
| direccion       | String  | Dirección del paciente         |
| activo          | Boolean | Estado del registro            |

**Archivo**: `data/entity/Paciente.kt`

---

### 2. **Especialidades** (`especialidades`)

| Columna          | Tipo    | Descripción                   |
|------------------|---------|-------------------------------|
| id               | Long    | PK, Auto-incremento           |
| nombre           | String  | Nombre de la especialidad     |
| descripcion      | String  | Descripción de la especialidad|
| duracionConsulta | Int     | Duración en minutos           |
| activa           | Boolean | Estado de la especialidad     |

**Archivo**: `data/entity/Especialidad.kt`

---

### 3. **Citas** (`citas`)

| Columna        | Tipo        | Descripción                     |
|----------------|-------------|---------------------------------|
| id             | Long        | PK, Auto-incremento             |
| pacienteId     | Long        | FK → pacientes.id (CASCADE)     |
| especialidadId | Long        | FK → especialidades.id (CASCADE)|
| fecha          | String      | Fecha de la cita (yyyy-MM-dd)   |
| hora           | String      | Hora de la cita (HH:mm)         |
| estado         | EstadoCita  | PENDIENTE, CONFIRMADA, etc.     |
| motivo         | String      | Motivo de la consulta           |
| observaciones  | String?     | Observaciones adicionales       |
| ubicacion      | String?     | Coordenadas GPS (opcional)      |

**Foreign Keys con CASCADE DELETE:**
- Si se elimina un paciente, se eliminan todas sus citas
- Si se elimina una especialidad, se eliminan todas sus citas

**Archivo**: `data/entity/Cita.kt`

---

## 🔧 DAOs (Data Access Objects)

Los DAOs definen las operaciones SQL mediante anotaciones:

### **PacienteDao.kt**
```kotlin
@Query("SELECT * FROM pacientes WHERE activo = 1 ORDER BY apellido, nombre ASC")
fun getPacientesActivos(): Flow<List<Paciente>>

@Query("SELECT * FROM pacientes WHERE rut = :rut")
suspend fun getPacienteByRut(rut: String): Paciente?

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insert(paciente: Paciente): Long
```

### **EspecialidadDao.kt**
```kotlin
@Query("SELECT * FROM especialidades WHERE activa = 1 ORDER BY nombre ASC")
fun getEspecialidadesActivas(): Flow<List<Especialidad>>

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insert(especialidad: Especialidad): Long
```

### **CitaDao.kt**
```kotlin
@Query("SELECT * FROM citas WHERE pacienteId = :pacienteId ORDER BY fecha DESC")
fun getCitasByPaciente(pacienteId: Long): Flow<List<Cita>>

@Query("UPDATE citas SET estado = :nuevoEstado WHERE id = :citaId")
suspend fun updateEstado(citaId: Long, nuevoEstado: EstadoCita)
```

---

## 📦 Repositories

Los Repositories abstraen el acceso a datos y centralizan la lógica de negocio:

```kotlin
class PacienteRepository(private val pacienteDao: PacienteDao) {
    val pacientesActivos: Flow<List<Paciente>> = pacienteDao.getPacientesActivos()
    
    suspend fun insert(paciente: Paciente): Long {
        return pacienteDao.insert(paciente)
    }
}
```

---

## 💡 Características de Room

### ✅ **Ventajas**

1. **Type-Safe Queries**: Las consultas se validan en tiempo de compilación
2. **Coroutines Support**: Integración nativa con `suspend` y `Flow`
3. **LiveData/Flow**: Observación reactiva de cambios en la BD
4. **Migraciones**: Soporte para actualizar el esquema sin perder datos
5. **Relaciones**: Foreign keys automáticas con cascada
6. **Converters**: Conversión automática de tipos personalizados

### 🔄 **Flow vs Suspend**

- **Flow**: Observa cambios en tiempo real (reactivo)
  ```kotlin
  val pacientes: Flow<List<Paciente>> = dao.getAllPacientes()
  ```

- **Suspend**: Operaciones únicas (CRUD)
  ```kotlin
  suspend fun insert(paciente: Paciente): Long
  ```

---

## 🛠️ Inspeccionar la Base de Datos

### **Desde Android Studio:**

1. Ejecuta la app en un emulador o dispositivo
2. Ve a: `View → Tool Windows → App Inspection`
3. Selecciona la pestaña `Database Inspector`
4. Verás las tablas: `pacientes`, `especialidades`, `citas`

### **Desde ADB:**

```bash
# Conectar al dispositivo
adb shell

# Navegar a la base de datos
cd /data/data/cl.duoc.app/databases/

# Abrir SQLite
sqlite3 vitalcare_database.db

# Comandos útiles
.tables                    # Ver todas las tablas
SELECT * FROM pacientes;   # Consultar pacientes
.schema pacientes          # Ver estructura de tabla
.exit                      # Salir
```

---

## 📱 Uso en el ViewModel

```kotlin
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // Inicializar base de datos
    private val database = VitalCareDatabase.getDatabase(application)
    
    // Crear repositorios
    private val pacienteRepository = PacienteRepository(database.pacienteDao())
    
    // Observar datos reactivamente
    val pacientes = pacienteRepository.pacientesActivos
    
    // Insertar datos
    viewModelScope.launch {
        pacienteRepository.insert(nuevoPaciente)
    }
}
```

---

## 🔄 Datos de Ejemplo

La app se inicializa automáticamente con:

- **3 Especialidades**: Psicología, Psiquiatría, Terapia Familiar
- **2 Pacientes**: Juan Pérez, María González
- **2 Citas**: Una pendiente y una confirmada

Esto se ejecuta en `HomeViewModel.initializeSampleData()`

---

## 📚 Dependencias Usadas

```kotlin
// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
```

---

## 🎯 Próximos Pasos

1. **Agregar pantallas CRUD**: Crear, editar, eliminar pacientes y citas
2. **Implementar búsqueda**: Filtros y búsqueda de pacientes
3. **Agregar validaciones**: RUT, email, fechas
4. **Implementar migraciones**: Para actualizar el esquema sin perder datos
5. **Agregar GPS**: Guardar ubicación de las citas
6. **Notificaciones**: Recordatorios de citas próximas

---

## 📖 Documentación Oficial

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [DAO Guide](https://developer.android.com/training/data-storage/room/accessing-data)
- [Entities Guide](https://developer.android.com/training/data-storage/room/defining-data)
- [Migrations Guide](https://developer.android.com/training/data-storage/room/migrating-db-versions)
