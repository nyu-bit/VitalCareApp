package cl.duoc.app.data.repository

import android.util.Log
import cl.duoc.app.data.RetrofitInstance
import cl.duoc.app.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * ============================================================
 * REPOSITORY REMOTO - VITALES
 * ============================================================
 *
 * Patrón: Repository que abstrae la fuente de datos (API)
 * Responsabilidad: Conectar con la API y manejar errores
 *
 * Flujo: ViewModel → VitalesRepository → VitalesApi → Retrofit → ms-vitales
 */
class VitalesRepository {
    private val vitalesApi = RetrofitInstance.getVitalesApi()
    private val TAG = "VitalesRepository"

    /**
     * Obtiene todos los signos vitales disponibles
     * GET /vitales
     *
     * @return Result<List<SignosVitalesDto>>
     */
    suspend fun getAllVitales(): Result<List<SignosVitalesDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Llamando: GET /vitales")
            val signos = vitalesApi.getAllVitales()
            Log.d(TAG, "Éxito: Obtenidas ${signos.size} vitales")
            Result.success(signos)
        } catch (e: IOException) {
            Log.e(TAG, "Error de conexión", e)
            Result.failure(Exception("Error de conexión: ${e.message}", e))
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "Error HTTP: ${e.code()}", e)
            Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
        } catch (e: Exception) {
            Log.e(TAG, "Error desconocido", e)
            Result.failure(Exception("Error desconocido: ${e.message}", e))
        }
    }

    /**
     * Obtiene signos vitales de un paciente específico
     * GET /vitales/paciente/{id}
     *
     * @param pacienteId ID del paciente
     * @return Result<List<SignosVitalesDto>>
     */
    suspend fun getByPaciente(pacienteId: String): Result<List<SignosVitalesDto>> =
        withContext(Dispatchers.IO) {
            try {
                if (pacienteId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de paciente inválido"))
                }
                Log.d(TAG, "Llamando: GET /vitales/paciente/$pacienteId")
                val signos = vitalesApi.getVitalesByPaciente(pacienteId)
                Log.d(TAG, "Éxito: Obtenidas ${signos.size} vitales del paciente")
                Result.success(signos)
            } catch (e: IOException) {
                Log.e(TAG, "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                Log.e(TAG, "Error HTTP: ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                Log.e(TAG, "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Crea un nuevo registro de signos vitales
     * POST /vitales
     *
     * @param signoVital Datos del vital a crear
     * @return Result<SignosVitalesDto>
     */
    suspend fun createVital(signoVital: SignosVitalesDto): Result<SignosVitalesDto> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando: POST /vitales")
                val resultado = vitalesApi.createVitales(signoVital)
                Log.d(TAG, "Éxito: Vital creado con ID ${resultado.id}")
                Result.success(resultado)
            } catch (e: IOException) {
                Log.e(TAG, "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                Log.e(TAG, "Error HTTP: ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                Log.e(TAG, "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Elimina un registro de signos vitales
     * DELETE /vitales/{id}
     *
     * @param id ID del vital a eliminar
     * @return Result<Unit>
     */
    suspend fun deleteVital(id: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                if (id.isBlank()) {
                    return@withContext Result.failure(Exception("ID inválido"))
                }
                Log.d(TAG, "Llamando: DELETE /vitales/$id")
                vitalesApi.deleteVitales(id)
                Log.d(TAG, "Éxito: Vital eliminado")
                Result.success(Unit)
            } catch (e: IOException) {
                Log.e(TAG, "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                Log.e(TAG, "Error HTTP: ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                Log.e(TAG, "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }
}

/**
 * ============================================================
 * REPOSITORY REMOTO - UBICACIÓN
 * ============================================================
 */
class UbicacionRepository {
    private val ubicacionApi = RetrofitInstance.getUbicacionApi()
    private val TAG = "UbicacionRepository"

    suspend fun getAll(): Result<List<UbicacionDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Llamando: GET /ubicacion")
            val ubicaciones = ubicacionApi.getAllUbicaciones()
            Log.d(TAG, "Éxito: Obtenidas ${ubicaciones.size} ubicaciones")
            Result.success(ubicaciones)
        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
            Result.failure(Exception("Error: ${e.message}", e))
        }
    }

    suspend fun getByPaciente(pacienteId: String): Result<List<UbicacionDto>> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando: GET /ubicacion/paciente/$pacienteId")
                val ubicaciones = ubicacionApi.getUbicacionesByPaciente(pacienteId)
                Log.d(TAG, "Éxito: Obtenidas ${ubicaciones.size} ubicaciones")
                Result.success(ubicaciones)
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
                Result.failure(Exception("Error: ${e.message}", e))
            }
        }

    suspend fun save(ubicacion: UbicacionDto): Result<UbicacionDto> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando: POST /ubicacion")
                val resultado = ubicacionApi.createUbicacion(ubicacion)
                Log.d(TAG, "Éxito: Ubicación guardada")
                Result.success(resultado)
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
                Result.failure(Exception("Error: ${e.message}", e))
            }
        }

    suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Llamando: DELETE /ubicacion/$id")
            ubicacionApi.deleteUbicacion(id)
            Log.d(TAG, "Éxito: Ubicación eliminada")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
            Result.failure(Exception("Error: ${e.message}", e))
        }
    }
}

/**
 * ============================================================
 * REPOSITORY REMOTO - ALERTAS
 * ============================================================
 */
class AlertasRepository {
    private val alertasApi = RetrofitInstance.getAlertasApi()
    private val TAG = "AlertasRepository"

    suspend fun getAll(): Result<List<AlertaDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Llamando: GET /alertas")
            val alertas = alertasApi.getAllAlertas()
            Log.d(TAG, "Éxito: Obtenidas ${alertas.size} alertas")
            Result.success(alertas)
        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
            Result.failure(Exception("Error: ${e.message}", e))
        }
    }

    suspend fun getByPaciente(pacienteId: String): Result<List<AlertaDto>> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando: GET /alertas/paciente/$pacienteId")
                val alertas = alertasApi.getAlertasByPaciente(pacienteId)
                Log.d(TAG, "Éxito: Obtenidas ${alertas.size} alertas")
                Result.success(alertas)
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
                Result.failure(Exception("Error: ${e.message}", e))
            }
        }

    suspend fun create(alerta: AlertaDto): Result<AlertaDto> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando: POST /alertas")
                val resultado = alertasApi.createAlerta(alerta)
                Log.d(TAG, "Éxito: Alerta creada")
                Result.success(resultado)
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
                Result.failure(Exception("Error: ${e.message}", e))
            }
        }

    suspend fun markAsAttended(id: String, alerta: AlertaDto): Result<AlertaDto> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando: PUT /alertas/$id")
                val alertaActualizada = alerta.copy(leida = true)
                val resultado = alertasApi.updateAlerta(id, alertaActualizada)
                Log.d(TAG, "Éxito: Alerta actualizada")
                Result.success(resultado)
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
                Result.failure(Exception("Error: ${e.message}", e))
            }
        }

    suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Llamando: DELETE /alertas/$id")
            alertasApi.deleteAlerta(id)
            Log.d(TAG, "Éxito: Alerta eliminada")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
            Result.failure(Exception("Error: ${e.message}", e))
        }
    }
}

// WeatherRepository comentado temporalmente - requiere WeatherApi y WeatherDto
/*
class WeatherRepository {
    private val weatherApi: WeatherApi by lazy {
        RetrofitInstance.getWeatherApi(WeatherApi::class.java)
    }
    private val TAG = "WeatherRepository"
    private val WEATHER_API_KEY = "tu_api_key_aqui"

    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherDto> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Llamando: GET /data/2.5/weather (lat=$latitude, lon=$longitude)")
                val weather = weatherApi.getWeatherByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey = WEATHER_API_KEY,
                    units = "metric"
                )
                Log.d(TAG, "Éxito: Clima obtenido de ${weather.name}")
                Result.success(weather)
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
                Result.failure(Exception("Error: ${e.message}", e))
            }
        }
}
*/

/**
 * Repository combinado para operaciones que requieren múltiples APIs
 */
class PacienteDataRepository {
    private val vitalesRepository = VitalesRepository()
    private val ubicacionRepository = UbicacionRepository()
    private val alertasRepository = AlertasRepository()

    suspend fun getPacienteCompleteData(pacienteId: String): Result<PacienteCompleteData> {
        return try {
            val vitalesResult = vitalesRepository.getByPaciente(pacienteId)
            val ubicacionesResult = ubicacionRepository.getByPaciente(pacienteId)
            val alertasResult = alertasRepository.getByPaciente(pacienteId)

            if (vitalesResult.isSuccess && ubicacionesResult.isSuccess && alertasResult.isSuccess) {
                Result.success(
                    PacienteCompleteData(
                        vitales = vitalesResult.getOrNull() ?: emptyList(),
                        ubicaciones = ubicacionesResult.getOrNull() ?: emptyList(),
                        alertas = alertasResult.getOrNull() ?: emptyList()
                    )
                )
            } else {
                val error = vitalesResult.exceptionOrNull()
                    ?: ubicacionesResult.exceptionOrNull()
                    ?: alertasResult.exceptionOrNull()
                    ?: Exception("Error desconocido")
                Result.failure(error)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Clase que agrupa todos los datos de un paciente
 */
data class PacienteCompleteData(
    val vitales: List<SignosVitalesDto>,
    val ubicaciones: List<UbicacionDto>,
    val alertas: List<AlertaDto>
)

