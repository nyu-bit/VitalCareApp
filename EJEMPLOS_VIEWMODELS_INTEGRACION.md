# 🎬 EJEMPLOS DE INTEGRACIÓN - VIEWMODELS CON REPOSITORIOS

## 🎯 Ejemplo 1: ViewModel de Vitales

```kotlin
package cl.duoc.app.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.api.SignosVitalesDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VitalesViewModel : ViewModel() {
    
    // Repository
    private val vitalesRepository = VitalesRepository()
    
    // State
    private val _uiState = MutableStateFlow<VitalesUiState>(VitalesUiState.Loading)
    val uiState: StateFlow<VitalesUiState> = _uiState
    
    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    /**
     * Carga todos los signos vitales
     */
    fun cargarTodosLosVitales() {
        viewModelScope.launch {
            _uiState.value = VitalesUiState.Loading
            
            val result = vitalesRepository.getAllVitales()
            
            result.fold(
                onSuccess = { signos ->
                    _uiState.value = VitalesUiState.Success(signos)
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _uiState.value = VitalesUiState.Error(error.message ?: "Error desconocido")
                    _errorMessage.value = error.message
                }
            )
        }
    }
    
    /**
     * Carga vitales de un paciente específico
     */
    fun cargarVitalesPorPaciente(pacienteId: String) {
        if (pacienteId.isBlank()) {
            _errorMessage.value = "ID de paciente inválido"
            return
        }
        
        viewModelScope.launch {
            _uiState.value = VitalesUiState.Loading
            
            val result = vitalesRepository.getByPaciente(pacienteId)
            
            result.fold(
                onSuccess = { signos ->
                    _uiState.value = VitalesUiState.Success(signos)
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _uiState.value = VitalesUiState.Error(error.message ?: "Error al cargar vitales")
                    _errorMessage.value = error.message
                }
            )
        }
    }
    
    /**
     * Crea un nuevo signo vital
     */
    fun crearVital(signoVital: SignosVitalesDto) {
        if (signoVital.pacienteId.isBlank()) {
            _errorMessage.value = "El ID del paciente es requerido"
            return
        }
        
        viewModelScope.launch {
            _uiState.value = VitalesUiState.Loading
            
            val result = vitalesRepository.createVital(signoVital)
            
            result.fold(
                onSuccess = { vitalCreado ->
                    _uiState.value = VitalesUiState.Success(listOf(vitalCreado))
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _uiState.value = VitalesUiState.Error(error.message ?: "Error al crear vital")
                    _errorMessage.value = error.message
                }
            )
        }
    }
    
    /**
     * Elimina un vital
     */
    fun eliminarVital(vitalesId: String) {
        if (vitalesId.isBlank()) {
            _errorMessage.value = "ID de vital inválido"
            return
        }
        
        viewModelScope.launch {
            val result = vitalesRepository.deleteVitales(vitalesId)
            
            result.fold(
                onSuccess = {
                    _errorMessage.value = "Vital eliminado correctamente"
                    cargarTodosLosVitales() // Recargar lista
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }
}

/**
 * Estados de UI para Vitales
 */
sealed class VitalesUiState {
    object Loading : VitalesUiState()
    data class Success(val signos: List<SignosVitalesDto>) : VitalesUiState()
    data class Error(val message: String) : VitalesUiState()
}
```

---

## 🎬 Ejemplo 2: ViewModel de Alertas

```kotlin
package cl.duoc.app.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.api.AlertaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertasViewModel : ViewModel() {
    
    private val alertasRepository = AlertasRepository()
    
    private val _uiState = MutableStateFlow<AlertasUiState>(AlertasUiState.Loading)
    val uiState: StateFlow<AlertasUiState> = _uiState
    
    /**
     * Carga todas las alertas
     */
    fun cargarTodas() {
        viewModelScope.launch {
            _uiState.value = AlertasUiState.Loading
            
            val result = alertasRepository.getAll()
            
            _uiState.value = result.fold(
                onSuccess = { alertas -> AlertasUiState.Success(alertas) },
                onFailure = { error -> AlertasUiState.Error(error.message ?: "Error") }
            )
        }
    }
    
    /**
     * Carga alertas de un paciente
     */
    fun cargarPorPaciente(pacienteId: String) {
        viewModelScope.launch {
            _uiState.value = AlertasUiState.Loading
            
            val result = alertasRepository.getByPaciente(pacienteId)
            
            _uiState.value = result.fold(
                onSuccess = { alertas -> AlertasUiState.Success(alertas) },
                onFailure = { error -> AlertasUiState.Error(error.message ?: "Error") }
            )
        }
    }
    
    /**
     * Crea una nueva alerta
     */
    fun crearAlerta(alerta: AlertaDto) {
        viewModelScope.launch {
            _uiState.value = AlertasUiState.Loading
            
            val result = alertasRepository.createAlerta(alerta)
            
            _uiState.value = result.fold(
                onSuccess = { nueva -> AlertasUiState.Success(listOf(nueva)) },
                onFailure = { error -> AlertasUiState.Error(error.message ?: "Error") }
            )
        }
    }
    
    /**
     * Marca una alerta como atendida (leída)
     */
    fun marcarComoLeida(alerta: AlertaDto) {
        viewModelScope.launch {
            val result = alertasRepository.markAsAttended(alerta)
            
            result.onSuccess {
                cargarTodas() // Recargar para reflejar cambio
            }
        }
    }
    
    /**
     * Elimina una alerta
     */
    fun eliminarAlerta(alertaId: String) {
        viewModelScope.launch {
            val result = alertasRepository.deleteAlerta(alertaId)
            
            result.onSuccess {
                cargarTodas() // Recargar lista
            }
        }
    }
}

sealed class AlertasUiState {
    object Loading : AlertasUiState()
    data class Success(val alertas: List<AlertaDto>) : AlertasUiState()
    data class Error(val message: String) : AlertasUiState()
}
```

---

## 🎬 Ejemplo 3: Uso en Composable

```kotlin
package cl.duoc.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.data.api.AlertaDto
import cl.duoc.app.data.repository.AlertasViewModel
import cl.duoc.app.data.repository.AlertasUiState

@Composable
fun PantallaAlertas(pacienteId: String) {
    val viewModel: AlertasViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    // Cargar alertas cuando se monta la pantalla
    LaunchedEffect(pacienteId) {
        viewModel.cargarPorPaciente(pacienteId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Alertas",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mostrar UI según el estado
        when (val state = uiState) {
            is AlertasUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            is AlertasUiState.Success -> {
                LazyColumn {
                    items(state.alertas) { alerta ->
                        AlertaItem(
                            alerta = alerta,
                            onMarcarLeida = { viewModel.marcarComoLeida(alerta) },
                            onEliminar = { viewModel.eliminarAlerta(alerta.id ?: "") }
                        )
                    }
                }
            }
            
            is AlertasUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AlertaItem(
    alerta: AlertaDto,
    onMarcarLeida: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(alerta.titulo, style = MaterialTheme.typography.bodyLarge)
                    Text(alerta.mensaje, style = MaterialTheme.typography.bodySmall)
                    Text("Severidad: ${alerta.severidad}")
                }
                
                Button(onClick = onMarcarLeida) {
                    Text(if (alerta.leida) "Leída" else "Marcar leída")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(onClick = onEliminar, modifier = Modifier.fillMaxWidth()) {
                Text("Eliminar")
            }
        }
    }
}
```

---

## 🎬 Ejemplo 4: Manejo Detallado de Errores

```kotlin
class PacienteDataViewModel : ViewModel() {
    
    private val repository = PacienteDataRepository()
    
    private val _uiState = MutableStateFlow<PacienteUiState>(PacienteUiState.Loading)
    val uiState: StateFlow<PacienteUiState> = _uiState
    
    fun cargarDatosPaciente(pacienteId: String) {
        if (pacienteId.isBlank()) {
            _uiState.value = PacienteUiState.Error("ID de paciente inválido")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = PacienteUiState.Loading
            
            val result = repository.getPacienteCompleteData(pacienteId)
            
            _uiState.value = result.fold(
                onSuccess = { data ->
                    // Validar que hay datos
                    if (data.vitales.isEmpty() && 
                        data.ubicaciones.isEmpty() && 
                        data.alertas.isEmpty()) {
                        PacienteUiState.Empty("No hay datos para este paciente")
                    } else {
                        PacienteUiState.Success(data)
                    }
                },
                onFailure = { error ->
                    when {
                        error.message?.contains("conexión", ignoreCase = true) == true -> {
                            PacienteUiState.Error("Error de conexión. Verifica tu internet.")
                        }
                        error.message?.contains("404", ignoreCase = true) == true -> {
                            PacienteUiState.Error("Paciente no encontrado")
                        }
                        error.message?.contains("401", ignoreCase = true) == true -> {
                            PacienteUiState.Error("No autorizado. Inicia sesión nuevamente.")
                        }
                        else -> {
                            PacienteUiState.Error(error.message ?: "Error desconocido")
                        }
                    }
                }
            )
        }
    }
}

sealed class PacienteUiState {
    object Loading : PacienteUiState()
    data class Success(val data: PacienteCompleteData) : PacienteUiState()
    data class Error(val message: String) : PacienteUiState()
    data class Empty(val message: String) : PacienteUiState()
}
```

---

## ✅ Checklist de Integración

- [ ] Importar Repository en ViewModel
- [ ] Crear StateFlow para UI State
- [ ] Implementar métodos que usan viewModelScope.launch
- [ ] Manejar todos los casos: Loading, Success, Error
- [ ] Validar entrada antes de llamar repository
- [ ] Usar fold para manejar Result<T>
- [ ] Actualizar UI según el estado
- [ ] Mostrar mensajes de error al usuario
- [ ] Implementar retry logic (opcional)
- [ ] Agregar logging (opcional)

---

**Generado:** Diciembre 2025  
**Estado:** ✅ LISTO PARA USAR  
**Próximo:** Conectar Composables con ViewModels

