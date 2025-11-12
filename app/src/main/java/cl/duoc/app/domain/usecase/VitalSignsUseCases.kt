package cl.duoc.app.domain.usecase

import cl.duoc.app.domain.repository.VitalSignsRepository
import cl.duoc.app.model.RiskLevel
import cl.duoc.app.model.VitalSigns
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso: Guardar signos vitales
 * 
 * Valida que los valores estén en rangos aceptables
 * 
 * @property vitalSignsRepository Repositorio de signos vitales
 */
class SaveVitalSignsUseCase(
    private val vitalSignsRepository: VitalSignsRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param vitalSigns Registro de signos vitales a guardar
     * @return Result<Boolean> indicando éxito o error
     */
    suspend operator fun invoke(vitalSigns: VitalSigns): Result<Boolean> {
        return try {
            // Validaciones de negocio
            require(vitalSigns.userId.isNotBlank()) { "El ID del usuario es requerido" }
            require(vitalSigns.heartRate in 30..220) { 
                "Frecuencia cardíaca fuera de rango (30-220 bpm)" 
            }
            require(vitalSigns.bloodPressureSystolic in 60..250) { 
                "Presión sistólica fuera de rango (60-250 mmHg)" 
            }
            require(vitalSigns.bloodPressureDiastolic in 40..150) { 
                "Presión diastólica fuera de rango (40-150 mmHg)" 
            }
            require(vitalSigns.oxygenSaturation in 70..100) { 
                "Saturación de oxígeno fuera de rango (70-100%)" 
            }
            
            val success = vitalSignsRepository.saveVitalSigns(vitalSigns)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al guardar signos vitales"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Caso de uso: Obtener últimos signos vitales
 * 
 * @property vitalSignsRepository Repositorio de signos vitales
 */
class GetLatestVitalSignsUseCase(
    private val vitalSignsRepository: VitalSignsRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario
     * @param limit Cantidad de registros a obtener
     * @return Lista de registros más recientes
     */
    suspend operator fun invoke(userId: String, limit: Int = 10): List<VitalSigns> {
        require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
        require(limit > 0) { "El límite debe ser mayor a 0" }
        
        return vitalSignsRepository.getLatestVitalSigns(userId, limit)
    }
}

/**
 * Caso de uso: Calcular nivel de riesgo de signos vitales
 * 
 * Analiza los valores y determina si están en rango normal, advertencia o peligro
 */
class CalculateRiskLevelUseCase {
    /**
     * Ejecuta el caso de uso
     * 
     * @param vitalSigns Signos vitales a analizar
     * @return Nivel de riesgo calculado
     */
    operator fun invoke(vitalSigns: VitalSigns): RiskLevel {
        var dangerCount = 0
        var warningCount = 0
        
        // Evaluar frecuencia cardíaca
        when {
            vitalSigns.heartRate < 50 || vitalSigns.heartRate > 120 -> dangerCount++
            vitalSigns.heartRate < 60 || vitalSigns.heartRate > 100 -> warningCount++
        }
        
        // Evaluar presión arterial sistólica
        when {
            vitalSigns.bloodPressureSystolic < 90 || vitalSigns.bloodPressureSystolic > 180 -> dangerCount++
            vitalSigns.bloodPressureSystolic < 100 || vitalSigns.bloodPressureSystolic > 140 -> warningCount++
        }
        
        // Evaluar presión arterial diastólica
        when {
            vitalSigns.bloodPressureDiastolic < 60 || vitalSigns.bloodPressureDiastolic > 110 -> dangerCount++
            vitalSigns.bloodPressureDiastolic < 65 || vitalSigns.bloodPressureDiastolic > 90 -> warningCount++
        }
        
        // Evaluar saturación de oxígeno
        when {
            vitalSigns.oxygenSaturation < 90 -> dangerCount++
            vitalSigns.oxygenSaturation < 95 -> warningCount++
        }
        
        return when {
            dangerCount > 0 -> RiskLevel.DANGER
            warningCount > 0 -> RiskLevel.WARNING
            else -> RiskLevel.NORMAL
        }
    }
}

/**
 * Caso de uso: Observar signos vitales
 * 
 * @property vitalSignsRepository Repositorio de signos vitales
 */
class ObserveVitalSignsUseCase(
    private val vitalSignsRepository: VitalSignsRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario
     * @return Flow que emite la lista de signos vitales cuando cambian
     */
    operator fun invoke(userId: String): Flow<List<VitalSigns>> {
        require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
        return vitalSignsRepository.observeVitalSigns(userId)
    }
}

/**
 * Caso de uso: Limpiar historial de signos vitales
 * 
 * @property vitalSignsRepository Repositorio de signos vitales
 */
class ClearVitalSignsHistoryUseCase(
    private val vitalSignsRepository: VitalSignsRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario
     * @return Result<Boolean> indicando éxito o error
     */
    suspend operator fun invoke(userId: String): Result<Boolean> {
        return try {
            require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
            
            val success = vitalSignsRepository.deleteAllVitalSigns(userId)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al limpiar el historial"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
