package cl.duoc.app.data.api

import cl.duoc.app.data.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Ejemplos de uso de las interfaces Retrofit con patrones recomendados
 * para la arquitectura de VitalCare
 */

// ============================================================
// Ejemplo 1: Uso en un Repository
// ============================================================

/**
 * Ejemplo de repository que usa VitalesApi
 */
class VitalesRepository {
    private val vitalesApi = RetrofitInstance.getVitalesApi()

    suspend fun obtenerTodosLosVitales(): List<SignosVitalesDto> = withContext(Dispatchers.IO) {
        vitalesApi.getAllVitales()
    }

    suspend fun obtenerVitalesPorPaciente(pacienteId: String): List<SignosVitalesDto> =
        withContext(Dispatchers.IO) {
            vitalesApi.getVitalesByPaciente(pacienteId)
        }

    suspend fun guardarVitales(signos: SignosVitalesDto): SignosVitalesDto =
        withContext(Dispatchers.IO) {
            vitalesApi.createVitales(signos)
        }

    suspend fun eliminarVitales(vitalesId: String): Unit = withContext(Dispatchers.IO) {
        vitalesApi.deleteVitales(vitalesId)
    }
}

// ============================================================
// Ejemplo 2: Uso en un ViewModel (con StateFlow)
// ============================================================

/**
 * Ejemplo de ViewModel que maneja signos vitales
 * Requiere: androidx.lifecycle:lifecycle-viewmodel-compose
 */
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VitalesViewModel : ViewModel() {
    private val repository = VitalesRepository()

    private val _signos = MutableStateFlow<List<SignosVitalesDto>>(emptyList())
    val signos: StateFlow<List<SignosVitalesDto>> = _signos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarVitalesPorPaciente(pacienteId: String) {
        viewModelScope.launch {
            try {
                val resultado = repository.obtenerVitalesPorPaciente(pacienteId)
                _signos.value = resultado
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun guardarVitales(signos: SignosVitalesDto) {
        viewModelScope.launch {
            try {
                repository.guardarVitales(signos)
                cargarVitalesPorPaciente(signos.pacienteId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
*/

// ============================================================
// Ejemplo 3: Uso en un composable (sin ViewModel)
// ============================================================

/**
 * Ejemplo de uso directo en un composable con efectos
 * Requiere: androidx.lifecycle:lifecycle-viewmodel-compose
 */
/*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text

@Composable
fun VitalesScreen(pacienteId: String) {
    var signos by remember { mutableStateOf<List<SignosVitalesDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pacienteId) {
        try {
            val vitalesApi = RetrofitInstance.getVitalesApi()
            signos = vitalesApi.getVitalesByPaciente(pacienteId)
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> Text("Cargando...")
        error != null -> Text("Error: $error")
        else -> {
            LazyColumn {
                items(signos) { signo ->
                    VitalSignItem(signo)
                }
            }
        }
    }
}

@Composable
fun VitalSignItem(signo: SignosVitalesDto) {
    Text("Frecuencia Cardíaca: ${signo.frecuenciaCardiaca} bpm")
    Text("Temperatura: ${signo.temperatura}°C")
}
*/

// ============================================================
// Ejemplo 4: Manejo de errores detallado
// ============================================================

/**
 * Ejemplo de manejo robusto de errores HTTP
 */
suspend fun obtenerVitalesConManejodeErrores(pacienteId: String): Result<List<SignosVitalesDto>> {
    return try {
        val vitalesApi = RetrofitInstance.getVitalesApi()
        val resultado = vitalesApi.getVitalesByPaciente(pacienteId)
        Result.success(resultado)
    } catch (e: retrofit2.HttpException) {
        // Error HTTP específico
        when (e.code()) {
            404 -> Result.failure(Exception("Paciente no encontrado"))
            401 -> Result.failure(Exception("No autorizado"))
            else -> Result.failure(Exception("Error HTTP: ${e.code()}"))
        }
    } catch (e: java.io.IOException) {
        // Error de conexión
        Result.failure(Exception("Error de conexión: ${e.message}"))
    } catch (e: Exception) {
        // Otros errores
        Result.failure(e)
    }
}

// ============================================================
// Ejemplo 5: Operaciones concurrentes
// ============================================================

/**
 * Ejemplo de obtener datos de múltiples APIs simultáneamente
 */
suspend fun obtenerDatosCombinados(pacienteId: String): Result<CombinedPatientData> {
    return try {
        val vitalesApi = RetrofitInstance.getVitalesApi()
        val ubicacionApi = RetrofitInstance.getUbicacionApi()
        val alertasApi = RetrofitInstance.getAlertasApi()

        val vitalesDeferred = kotlinx.coroutines.async {
            vitalesApi.getVitalesByPaciente(pacienteId)
        }
        val ubicacionesDeferred = kotlinx.coroutines.async {
            ubicacionApi.getUbicacionesByPaciente(pacienteId)
        }
        val alertasDeferred = kotlinx.coroutines.async {
            alertasApi.getAlertasByPaciente(pacienteId)
        }

        val datos = CombinedPatientData(
            vitales = vitalesDeferred.await(),
            ubicaciones = ubicacionesDeferred.await(),
            alertas = alertasDeferred.await()
        )

        Result.success(datos)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Clase para agrupar datos de múltiples APIs
 */
data class CombinedPatientData(
    val vitales: List<SignosVitalesDto>,
    val ubicaciones: List<UbicacionDto>,
    val alertas: List<AlertaDto>
)

