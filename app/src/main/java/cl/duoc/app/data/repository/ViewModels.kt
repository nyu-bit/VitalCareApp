package cl.duoc.app.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ============================================================
// VIEWMODEL - VITALES
// ============================================================

/**
 * ViewModel para gestión de Signos Vitales
 * Consume VitalesRepository y gestiona el estado de UI
 *
 * Estado:
 *   - Loading: Cargando datos
 *   - Success: Datos cargados exitosamente
 *   - Error: Error durante la operación
 */
class VitalesViewModel : ViewModel() {
    private val repository = VitalesRepository()

    // Estado UI
    private val _vitales = MutableStateFlow<List<SignosVitalesDto>>(emptyList())
    val vitales: StateFlow<List<SignosVitalesDto>> = _vitales

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga todos los signos vitales disponibles
     */
    fun loadVitales() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getAllVitales()

            result.fold(
                onSuccess = { signos ->
                    _vitales.value = signos
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cargar vitales"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Carga signos vitales de un paciente específico
     */
    fun loadByPaciente(pacienteId: String) {
        if (pacienteId.isBlank()) {
            _error.value = "ID de paciente inválido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getByPaciente(pacienteId)

            result.fold(
                onSuccess = { signos ->
                    _vitales.value = signos
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cargar vitales"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Crea un nuevo registro de signos vitales
     */
    fun createVital(signoVital: SignosVitalesDto) {
        if (signoVital.pacienteId.isBlank()) {
            _error.value = "El ID del paciente es requerido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.createVital(signoVital)

            result.fold(
                onSuccess = { creado ->
                    // Agregar el nuevo vital a la lista
                    _vitales.value = _vitales.value + creado
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al crear vital"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Elimina un registro de signos vitales
     */
    fun deleteVital(vitalesId: String) {
        if (vitalesId.isBlank()) {
            _error.value = "ID de vital inválido"
            return
        }

        viewModelScope.launch {
            val result = repository.deleteVitales(vitalesId)

            result.fold(
                onSuccess = {
                    // Remover de la lista
                    _vitales.value = _vitales.value.filter { it.id != vitalesId }
                    _error.value = null
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al eliminar vital"
                }
            )
        }
    }

    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        _error.value = null
    }
}

// ============================================================
// VIEWMODEL - UBICACIÓN
// ============================================================

/**
 * ViewModel para gestión de Ubicaciones
 * Consume UbicacionRepository y gestiona el estado de UI
 */
class UbicacionViewModel : ViewModel() {
    private val repository = UbicacionRepository()

    // Estado UI
    private val _ubicaciones = MutableStateFlow<List<UbicacionDto>>(emptyList())
    val ubicaciones: StateFlow<List<UbicacionDto>> = _ubicaciones

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga todas las ubicaciones disponibles
     */
    fun loadUbicacion() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getAll()

            result.fold(
                onSuccess = { ubicaciones ->
                    _ubicaciones.value = ubicaciones
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cargar ubicaciones"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Carga ubicaciones de un paciente específico
     */
    fun loadByPaciente(pacienteId: String) {
        if (pacienteId.isBlank()) {
            _error.value = "ID de paciente inválido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getByPaciente(pacienteId)

            result.fold(
                onSuccess = { ubicaciones ->
                    _ubicaciones.value = ubicaciones
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cargar ubicaciones"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Guarda una nueva ubicación
     */
    fun saveUbicacion(ubicacion: UbicacionDto) {
        if (ubicacion.pacienteId.isBlank()) {
            _error.value = "El ID del paciente es requerido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.saveUbicacion(ubicacion)

            result.fold(
                onSuccess = { guardada ->
                    // Agregar la nueva ubicación a la lista
                    _ubicaciones.value = _ubicaciones.value + guardada
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al guardar ubicación"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        _error.value = null
    }
}

// ============================================================
// VIEWMODEL - ALERTAS
// ============================================================

/**
 * ViewModel para gestión de Alertas
 * Consume AlertasRepository y gestiona el estado de UI
 */
class AlertasViewModel : ViewModel() {
    private val repository = AlertasRepository()

    // Estado UI
    private val _alertas = MutableStateFlow<List<AlertaDto>>(emptyList())
    val alertas: StateFlow<List<AlertaDto>> = _alertas

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga todas las alertas disponibles
     */
    fun loadAlertas() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getAll()

            result.fold(
                onSuccess = { alertas ->
                    _alertas.value = alertas
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cargar alertas"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Carga alertas de un paciente específico
     */
    fun loadByPaciente(pacienteId: String) {
        if (pacienteId.isBlank()) {
            _error.value = "ID de paciente inválido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getByPaciente(pacienteId)

            result.fold(
                onSuccess = { alertas ->
                    _alertas.value = alertas
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cargar alertas"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Crea una nueva alerta
     */
    fun createAlerta(alerta: AlertaDto) {
        if (alerta.pacienteId.isBlank()) {
            _error.value = "El ID del paciente es requerido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.createAlerta(alerta)

            result.fold(
                onSuccess = { creada ->
                    // Agregar la nueva alerta a la lista
                    _alertas.value = _alertas.value + creada
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al crear alerta"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Marca una alerta como atendida (leída)
     */
    fun markAsAttended(alerta: AlertaDto) {
        if (alerta.id.isNullOrBlank()) {
            _error.value = "ID de alerta inválido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.markAsAttended(alerta)

            result.fold(
                onSuccess = { actualizada ->
                    // Actualizar en la lista
                    _alertas.value = _alertas.value.map {
                        if (it.id == actualizada.id) actualizada else it
                    }
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al marcar alerta"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Elimina una alerta
     */
    fun deleteAlerta(alertaId: String) {
        if (alertaId.isBlank()) {
            _error.value = "ID de alerta inválido"
            return
        }

        viewModelScope.launch {
            val result = repository.deleteAlerta(alertaId)

            result.fold(
                onSuccess = {
                    // Remover de la lista
                    _alertas.value = _alertas.value.filter { it.id != alertaId }
                    _error.value = null
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al eliminar alerta"
                }
            )
        }
    }

    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        _error.value = null
    }
}

// ============================================================
// VIEWMODEL - DATOS CONSOLIDADOS DE PACIENTE
// ============================================================

/**
 * ViewModel para gestión de datos completos de un paciente
 * Consume PacienteDataRepository y gestiona el estado de UI
 */
class PacienteDataViewModel : ViewModel() {
    private val repository = PacienteDataRepository()

    // Estado UI
    private val _pacienteData = MutableStateFlow<PacienteCompleteData?>(null)
    val pacienteData: StateFlow<PacienteCompleteData?> = _pacienteData

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga datos consolidados de un paciente
     */
    fun loadPacienteData(pacienteId: String) {
        if (pacienteId.isBlank()) {
            _error.value = "ID de paciente inválido"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getPacienteCompleteData(pacienteId)

            result.fold(
                onSuccess = { data ->
                    _pacienteData.value = data
                    _loading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Error al cargar datos del paciente"
                    _loading.value = false
                }
            )
        }
    }

    /**
     * Limpia los datos del paciente
     */
    fun clearPacienteData() {
        _pacienteData.value = null
        _error.value = null
    }

    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        _error.value = null
    }
}

