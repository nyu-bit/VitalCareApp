package cl.duoc.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.database.VitalCareDatabase
import cl.duoc.app.data.entity.Cita
import cl.duoc.app.data.entity.Especialidad
import cl.duoc.app.data.entity.EstadoCita
import cl.duoc.app.data.entity.Paciente
import cl.duoc.app.data.repository.CitaRepository
import cl.duoc.app.data.repository.EspecialidadRepository
import cl.duoc.app.data.repository.PacienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SortedStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    // Base de datos y repositorios
    private val database = VitalCareDatabase.getDatabase(application)
    private val pacienteRepository = PacienteRepository(database.pacienteDao())
    private val especialidadRepository = EspecialidadRepository(database.especialidadDao())
    private val citaRepository = CitaRepository(database.citaDao())
    
    // Contador de ejemplo
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()
    
    // Flows observables desde la base de datos
    val pacientes = pacienteRepository.pacientesActivos
    val especialidades = especialidadRepository.especialidadesActivas
    val citas = citaRepository.todasCitas
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    // Mensaje de feedback
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()
    
    init {
        // Inicializar con datos de ejemplo si la base de datos está vacía
        initializeSampleData()
    }
    
    fun inc() { 
        _counter.value++ 
    }
    
    fun reset() { 
        _counter.value = 0 
    }
    
    /**
     * Inicializa la base de datos con datos de ejemplo
     */
    private fun initializeSampleData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Insertar especialidades de ejemplo
                val especialidades = listOf(
                    Especialidad(
                        nombre = "Psicología",
                        descripcion = "Atención psicológica general",
                        duracionConsulta = 45
                    ),
                    Especialidad(
                        nombre = "Psiquiatría",
                        descripcion = "Evaluación y tratamiento psiquiátrico",
                        duracionConsulta = 30
                    ),
                    Especialidad(
                        nombre = "Terapia Familiar",
                        descripcion = "Terapia para familias y parejas",
                        duracionConsulta = 60
                    )
                )
                especialidadRepository.insertAll(especialidades)
                
                // Insertar pacientes de ejemplo
                val pacientes = listOf(
                    Paciente(
                        rut = "12345678-9",
                        nombre = "Juan",
                        apellido = "Pérez",
                        email = "juan.perez@email.com",
                        telefono = "+56912345678",
                        fechaNacimiento = "1990-05-15",
                        direccion = "Av. Principal 123, Santiago"
                    ),
                    Paciente(
                        rut = "98765432-1",
                        nombre = "María",
                        apellido = "González",
                        email = "maria.gonzalez@email.com",
                        telefono = "+56987654321",
                        fechaNacimiento = "1985-08-20",
                        direccion = "Calle Secundaria 456, Valparaíso"
                    )
                )
                pacienteRepository.insertAll(pacientes)
                
                // Insertar citas de ejemplo
                val citas = listOf(
                    Cita(
                        pacienteId = 1,
                        especialidadId = 1,
                        fecha = "2025-11-25",
                        hora = "10:00",
                        estado = EstadoCita.PENDIENTE,
                        motivo = "Evaluación inicial",
                        observaciones = "Primera consulta"
                    ),
                    Cita(
                        pacienteId = 2,
                        especialidadId = 2,
                        fecha = "2025-11-26",
                        hora = "14:30",
                        estado = EstadoCita.CONFIRMADA,
                        motivo = "Control mensual",
                        observaciones = "Paciente regular"
                    )
                )
                citaRepository.insertAll(citas)
                
                _message.value = "✅ Base de datos inicializada con datos de ejemplo"
            } catch (e: Exception) {
                _message.value = "❌ Error al inicializar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpia el mensaje de feedback
     */
    fun clearMessage() {
        _message.value = null
    }
}
