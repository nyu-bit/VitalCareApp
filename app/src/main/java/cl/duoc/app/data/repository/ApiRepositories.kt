package cl.duoc.app.data.repository

import cl.duoc.app.data.RetrofitInstance
import cl.duoc.app.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

// ============================================================
// REPOSITORY REMOTO - VITALES
// ============================================================

/**
 * Repository para Signos Vitales
 * Consume la API de Vitales mediante Retrofit
 * Proporciona una capa de abstracción entre el ViewModel y la API
 */
class VitalesRepository {
    private val vitalesApi = RetrofitInstance.getVitalesApi()

    /**
     * Obtiene todos los signos vitales disponibles
     * GET /vitales
     */
    suspend fun getAllVitales(): Result<List<SignosVitalesDto>> = withContext(Dispatchers.IO) {
        try {
            val signos = vitalesApi.getAllVitales()
            logSuccess("VitalesRepository.getAllVitales()", signos.size)
            Result.success(signos)
        } catch (e: IOException) {
            logError("VitalesRepository.getAllVitales()", "Error de conexión", e)
            Result.failure(Exception("Error de conexión: ${e.message}", e))
        } catch (e: retrofit2.HttpException) {
            logError("VitalesRepository.getAllVitales()", "Error HTTP ${e.code()}", e)
            Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
        } catch (e: Exception) {
            logError("VitalesRepository.getAllVitales()", "Error desconocido", e)
            Result.failure(Exception("Error desconocido: ${e.message}", e))
        }
    }

    /**
     * Obtiene signos vitales de un paciente específico
     * GET /vitales/paciente/{id}
     */
    suspend fun getByPaciente(pacienteId: String): Result<List<SignosVitalesDto>> =
        withContext(Dispatchers.IO) {
            try {
                if (pacienteId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de paciente no puede estar vacío"))
                }
                val signos = vitalesApi.getVitalesByPaciente(pacienteId)
                logSuccess("VitalesRepository.getByPaciente($pacienteId)", signos.size)
                Result.success(signos)
            } catch (e: IOException) {
                logError("VitalesRepository.getByPaciente($pacienteId)", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("VitalesRepository.getByPaciente($pacienteId)", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("VitalesRepository.getByPaciente($pacienteId)", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Crea un nuevo registro de signos vitales
     * POST /vitales
     */
    suspend fun createVital(signoVital: SignosVitalesDto): Result<SignosVitalesDto> =
        withContext(Dispatchers.IO) {
            try {
                if (signoVital.pacienteId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de paciente requerido"))
                }
                val resultado = vitalesApi.createVitales(signoVital)
                logSuccess("VitalesRepository.createVital()", "Vital creado con ID: ${resultado.id}")
                Result.success(resultado)
            } catch (e: IOException) {
                logError("VitalesRepository.createVital()", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("VitalesRepository.createVital()", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("VitalesRepository.createVital()", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }


    /**
     * Elimina un registro de signos vitales
     * DELETE /vitales/{id}
     */
    suspend fun deleteVitales(vitalesId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                if (vitalesId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de vital no puede estar vacío"))
                }
                vitalesApi.deleteVitales(vitalesId)
                logSuccess("VitalesRepository.deleteVitales($vitalesId)", "Vital eliminado")
                Result.success(Unit)
            } catch (e: IOException) {
                logError("VitalesRepository.deleteVitales($vitalesId)", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("VitalesRepository.deleteVitales($vitalesId)", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("VitalesRepository.deleteVitales($vitalesId)", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }
}

// ============================================================
// REPOSITORY REMOTO - UBICACIÓN
// ============================================================

/**
 * Repository para Ubicación
 * Consume la API de Ubicación mediante Retrofit
 * Proporciona una capa de abstracción entre el ViewModel y la API
 */
class UbicacionRepository {
    private val ubicacionApi = RetrofitInstance.getUbicacionApi()

    /**
     * Obtiene todas las ubicaciones disponibles
     * GET /ubicacion
     */
    suspend fun getAll(): Result<List<UbicacionDto>> =
        withContext(Dispatchers.IO) {
            try {
                val ubicaciones = ubicacionApi.getAllUbicaciones()
                logSuccess("UbicacionRepository.getAll()", ubicaciones.size)
                Result.success(ubicaciones)
            } catch (e: IOException) {
                logError("UbicacionRepository.getAll()", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("UbicacionRepository.getAll()", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("UbicacionRepository.getAll()", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Obtiene ubicaciones de un paciente específico
     * GET /ubicacion/paciente/{id}
     */
    suspend fun getByPaciente(pacienteId: String): Result<List<UbicacionDto>> =
        withContext(Dispatchers.IO) {
            try {
                if (pacienteId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de paciente no puede estar vacío"))
                }
                val ubicaciones = ubicacionApi.getUbicacionesByPaciente(pacienteId)
                logSuccess("UbicacionRepository.getByPaciente($pacienteId)", ubicaciones.size)
                Result.success(ubicaciones)
            } catch (e: IOException) {
                logError("UbicacionRepository.getByPaciente($pacienteId)", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("UbicacionRepository.getByPaciente($pacienteId)", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("UbicacionRepository.getByPaciente($pacienteId)", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Crea una nueva ubicación
     * POST /ubicacion
     */
    suspend fun saveUbicacion(ubicacion: UbicacionDto): Result<UbicacionDto> =
        withContext(Dispatchers.IO) {
            try {
                if (ubicacion.pacienteId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de paciente requerido"))
                }
                val resultado = ubicacionApi.createUbicacion(ubicacion)
                logSuccess("UbicacionRepository.saveUbicacion()", "Ubicación guardada con ID: ${resultado.id}")
                Result.success(resultado)
            } catch (e: IOException) {
                logError("UbicacionRepository.saveUbicacion()", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("UbicacionRepository.saveUbicacion()", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("UbicacionRepository.saveUbicacion()", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }
}

// ============================================================
// REPOSITORY REMOTO - ALERTAS
// ============================================================

/**
 * Repository para Alertas
 * Consume la API de Alertas mediante Retrofit
 * Proporciona una capa de abstracción entre el ViewModel y la API
 */
class AlertasRepository {
    private val alertasApi = RetrofitInstance.getAlertasApi()

    /**
     * Obtiene todas las alertas disponibles
     * GET /alertas
     */
    suspend fun getAll(): Result<List<AlertaDto>> =
        withContext(Dispatchers.IO) {
            try {
                val alertas = alertasApi.getAllAlertas()
                logSuccess("AlertasRepository.getAll()", alertas.size)
                Result.success(alertas)
            } catch (e: IOException) {
                logError("AlertasRepository.getAll()", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("AlertasRepository.getAll()", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("AlertasRepository.getAll()", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Obtiene alertas de un paciente específico
     * GET /alertas/paciente/{id}
     */
    suspend fun getByPaciente(pacienteId: String): Result<List<AlertaDto>> =
        withContext(Dispatchers.IO) {
            try {
                if (pacienteId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de paciente no puede estar vacío"))
                }
                val alertas = alertasApi.getAlertasByPaciente(pacienteId)
                logSuccess("AlertasRepository.getByPaciente($pacienteId)", alertas.size)
                Result.success(alertas)
            } catch (e: IOException) {
                logError("AlertasRepository.getByPaciente($pacienteId)", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("AlertasRepository.getByPaciente($pacienteId)", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("AlertasRepository.getByPaciente($pacienteId)", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Crea una nueva alerta
     * POST /alertas
     */
    suspend fun createAlerta(alerta: AlertaDto): Result<AlertaDto> =
        withContext(Dispatchers.IO) {
            try {
                if (alerta.pacienteId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de paciente requerido"))
                }
                val resultado = alertasApi.createAlerta(alerta)
                logSuccess("AlertasRepository.createAlerta()", "Alerta creada con ID: ${resultado.id}")
                Result.success(resultado)
            } catch (e: IOException) {
                logError("AlertasRepository.createAlerta()", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("AlertasRepository.createAlerta()", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("AlertasRepository.createAlerta()", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Marca una alerta como atendida
     * PUT /alertas/{id}
     */
    suspend fun markAsAttended(alerta: AlertaDto): Result<AlertaDto> =
        withContext(Dispatchers.IO) {
            try {
                if (alerta.id.isNullOrBlank()) {
                    return@withContext Result.failure(Exception("ID de alerta requerido"))
                }
                val alertaActualizada = alerta.copy(leida = true)
                val resultado = alertasApi.updateAlerta(alerta.id!!, alertaActualizada)
                logSuccess("AlertasRepository.markAsAttended(${alerta.id})", "Alerta marcada como leída")
                Result.success(resultado)
            } catch (e: IOException) {
                logError("AlertasRepository.markAsAttended()", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("AlertasRepository.markAsAttended()", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("AlertasRepository.markAsAttended()", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }

    /**
     * Elimina una alerta
     * DELETE /alertas/{id}
     */
    suspend fun deleteAlerta(alertaId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                if (alertaId.isBlank()) {
                    return@withContext Result.failure(Exception("ID de alerta no puede estar vacío"))
                }
                alertasApi.deleteAlerta(alertaId)
                logSuccess("AlertasRepository.deleteAlerta($alertaId)", "Alerta eliminada")
                Result.success(Unit)
            } catch (e: IOException) {
                logError("AlertasRepository.deleteAlerta($alertaId)", "Error de conexión", e)
                Result.failure(Exception("Error de conexión: ${e.message}", e))
            } catch (e: retrofit2.HttpException) {
                logError("AlertasRepository.deleteAlerta($alertaId)", "Error HTTP ${e.code()}", e)
                Result.failure(Exception("Error HTTP ${e.code()}: ${e.message()}", e))
            } catch (e: Exception) {
                logError("AlertasRepository.deleteAlerta($alertaId)", "Error desconocido", e)
                Result.failure(Exception("Error desconocido: ${e.message}", e))
            }
        }
}

/**
 * Repository combinado para operaciones que requieren múltiples APIs
 * Útil para obtener datos consolidados de un paciente
 */
class PacienteDataRepository {
    private val vitalesRepository = VitalesRepository()
    private val ubicacionRepository = UbicacionRepository()
    private val alertasRepository = AlertasRepository()

    /**
     * Obtiene todos los datos de un paciente
     */
    suspend fun getPacienteCompleteData(pacienteId: String): Result<PacienteCompleteData> {
        return try {
            val vitalesResult = vitalesRepository.getByPaciente(pacienteId)
            val ubicacionesResult = ubicacionRepository.getByPaciente(pacienteId)
            val alertasResult = alertasRepository.getByPaciente(pacienteId)

            if (vitalesResult.isSuccess && ubicacionesResult.isSuccess && alertasResult.isSuccess) {
                val data = PacienteCompleteData(
                    vitales = vitalesResult.getOrNull() ?: emptyList(),
                    ubicaciones = ubicacionesResult.getOrNull() ?: emptyList(),
                    alertas = alertasResult.getOrNull() ?: emptyList()
                )
                logSuccess("PacienteDataRepository.getPacienteCompleteData($pacienteId)", "Datos completos obtenidos")
                Result.success(data)
            } else {
                val error = vitalesResult.exceptionOrNull()
                    ?: ubicacionesResult.exceptionOrNull()
                    ?: alertasResult.exceptionOrNull()
                    ?: Exception("Error desconocido")
                val errorMessage = error.message ?: "Error desconocido"
                logError("PacienteDataRepository.getPacienteCompleteData($pacienteId)", errorMessage, error)
                Result.failure(error)
            }
        } catch (e: Throwable) {
            logError("PacienteDataRepository.getPacienteCompleteData()", "Error desconocido", e)
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

// ============================================================
// FUNCIONES AUXILIARES DE LOGGING
// ============================================================

/**
 * Registra un éxito en las operaciones del repositorio
 * Útil para debugging y auditoría
 */
private fun logSuccess(operacion: String, mensaje: Any?) {
    val timestamp = java.time.LocalDateTime.now()
    println("✅ [$timestamp] SUCCESS - $operacion: $mensaje")
    // TODO: Integrar con sistema de logging real (Firebase, Sentry, etc.)
}

/**
 * Registra un error en las operaciones del repositorio
 * Útil para debugging y auditoría
 */
private fun logError(operacion: String, tipoError: String, excepcion: Exception) {
    val timestamp = java.time.LocalDateTime.now()
    println("❌ [$timestamp] ERROR - $operacion | $tipoError: ${excepcion.message}")
    excepcion.printStackTrace()
    // TODO: Integrar con sistema de logging real (Firebase, Sentry, etc.)
}

/**
 * Clase que agrupa todos los datos de un paciente
 */
data class PacienteCompleteData(
    val vitales: List<SignosVitalesDto>,
    val ubicaciones: List<UbicacionDto>,
    val alertas: List<AlertaDto>
)

