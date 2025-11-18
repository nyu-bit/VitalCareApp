package cl.duoc.app.data.anomaly

import cl.duoc.app.model.Alert
import cl.duoc.app.model.VitalSigns
import cl.duoc.app.utils.Constants
import java.time.LocalDateTime

/**
 * Servicio para detectar anomalías en signos vitales
 * Compara valores contra rangos normales y genera alertas automáticas
 * 
 * HU-04: Alerta Automática por Anomalías
 */
class AnomalyDetectionService {

    /**
     * Resultado de la detección de anomalías
     */
    data class AnomalyResult(
        val hasAnomaly: Boolean,
        val anomalyType: String? = null,
        val priority: String? = null,
        val description: String? = null,
        val recommendedAction: String? = null
    )

    /**
     * Analiza signos vitales y detecta anomalías
     * @param vitalSigns Signos vitales a analizar
     * @return Lista de anomalías detectadas
     */
    fun detectAnomalies(vitalSigns: VitalSigns): List<AnomalyResult> {
        val anomalies = mutableListOf<AnomalyResult>()

        // Verificar presión arterial
        vitalSigns.bloodPressureSystolic?.let { systolic ->
            vitalSigns.bloodPressureDiastolic?.let { diastolic ->
                val pressureAnomaly = checkBloodPressure(systolic, diastolic)
                if (pressureAnomaly.hasAnomaly) {
                    anomalies.add(pressureAnomaly)
                }
            }
        }

        // Verificar frecuencia cardíaca
        vitalSigns.heartRate?.let { heartRate ->
            val heartRateAnomaly = checkHeartRate(heartRate)
            if (heartRateAnomaly.hasAnomaly) {
                anomalies.add(heartRateAnomaly)
            }
        }

        // Verificar saturación de oxígeno
        vitalSigns.oxygenSaturation?.let { oxygen ->
            val oxygenAnomaly = checkOxygenSaturation(oxygen)
            if (oxygenAnomaly.hasAnomaly) {
                anomalies.add(oxygenAnomaly)
            }
        }

        // Verificar temperatura
        vitalSigns.temperature?.let { temperature ->
            val temperatureAnomaly = checkTemperature(temperature)
            if (temperatureAnomaly.hasAnomaly) {
                anomalies.add(temperatureAnomaly)
            }
        }

        return anomalies
    }

    /**
     * Verifica presión arterial
     */
    private fun checkBloodPressure(systolic: Int, diastolic: Int): AnomalyResult {
        val normalSystolicMin = Constants.VitalSigns.NORMAL_SYSTOLIC_MIN
        val normalSystolicMax = Constants.VitalSigns.NORMAL_SYSTOLIC_MAX
        val normalDiastolicMin = Constants.VitalSigns.NORMAL_DIASTOLIC_MIN
        val normalDiastolicMax = Constants.VitalSigns.NORMAL_DIASTOLIC_MAX

        return when {
            // Presión alta (Hipertensión)
            systolic > normalSystolicMax || diastolic > normalDiastolicMax -> {
                val priority = when {
                    systolic >= 180 || diastolic >= 120 -> Constants.AnomalyDetection.ALERT_PRIORITY_HIGH
                    systolic >= 140 || diastolic >= 90 -> Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM
                    else -> Constants.AnomalyDetection.ALERT_PRIORITY_LOW
                }
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_PRESSURE_HIGH,
                    priority = priority,
                    description = "Presión arterial elevada: $systolic/$diastolic mmHg (Normal: $normalSystolicMin-$normalSystolicMax/$normalDiastolicMin-$normalDiastolicMax)",
                    recommendedAction = when (priority) {
                        Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> "⚠️ URGENTE: Buscar atención médica inmediata"
                        Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM -> "Consultar con médico pronto, monitorear presión"
                        else -> "Controlar presión regularmente y evitar sal"
                    }
                )
            }
            // Presión baja (Hipotensión)
            systolic < normalSystolicMin || diastolic < normalDiastolicMin -> {
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_PRESSURE_LOW,
                    priority = Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM,
                    description = "Presión arterial baja: $systolic/$diastolic mmHg (Normal: $normalSystolicMin-$normalSystolicMax/$normalDiastolicMin-$normalDiastolicMax)",
                    recommendedAction = "Descansar, hidratarse y consultar con médico si hay síntomas"
                )
            }
            else -> AnomalyResult(hasAnomaly = false)
        }
    }

    /**
     * Verifica frecuencia cardíaca
     */
    private fun checkHeartRate(heartRate: Int): AnomalyResult {
        val normalMin = Constants.VitalSigns.NORMAL_HEART_RATE_MIN
        val normalMax = Constants.VitalSigns.NORMAL_HEART_RATE_MAX

        return when {
            // Taquicardia
            heartRate > normalMax -> {
                val priority = when {
                    heartRate >= 140 -> Constants.AnomalyDetection.ALERT_PRIORITY_HIGH
                    heartRate >= 120 -> Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM
                    else -> Constants.AnomalyDetection.ALERT_PRIORITY_LOW
                }
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_HEART_RATE_HIGH,
                    priority = priority,
                    description = "Frecuencia cardíaca elevada: $heartRate bpm (Normal: $normalMin-$normalMax)",
                    recommendedAction = when (priority) {
                        Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> "⚠️ URGENTE: Buscar atención médica inmediata"
                        else -> "Descansar, calmarse y monitorear. Consultar médico si persiste"
                    }
                )
            }
            // Bradicardia
            heartRate < normalMin -> {
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_HEART_RATE_LOW,
                    priority = Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM,
                    description = "Frecuencia cardíaca baja: $heartRate bpm (Normal: $normalMin-$normalMax)",
                    recommendedAction = "Consultar con médico si hay mareos o fatiga"
                )
            }
            else -> AnomalyResult(hasAnomaly = false)
        }
    }

    /**
     * Verifica saturación de oxígeno
     */
    private fun checkOxygenSaturation(oxygen: Int): AnomalyResult {
        val normalMin = Constants.VitalSigns.NORMAL_OXYGEN_SATURATION_MIN
        val critical = Constants.VitalSigns.CRITICAL_OXYGEN_SATURATION

        return when {
            oxygen < critical -> {
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_OXYGEN_LOW,
                    priority = Constants.AnomalyDetection.ALERT_PRIORITY_HIGH,
                    description = "⚠️ Saturación de oxígeno crítica: $oxygen% (Normal: ≥$normalMin%)",
                    recommendedAction = "🚨 EMERGENCIA: Buscar atención médica de inmediato"
                )
            }
            oxygen < normalMin -> {
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_OXYGEN_LOW,
                    priority = Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM,
                    description = "Saturación de oxígeno baja: $oxygen% (Normal: ≥$normalMin%)",
                    recommendedAction = "Consultar médico pronto, respirar profundamente"
                )
            }
            else -> AnomalyResult(hasAnomaly = false)
        }
    }

    /**
     * Verifica temperatura corporal
     */
    private fun checkTemperature(temperature: Double): AnomalyResult {
        val normalMin = Constants.VitalSigns.NORMAL_TEMPERATURE_MIN
        val normalMax = Constants.VitalSigns.NORMAL_TEMPERATURE_MAX

        return when {
            // Fiebre
            temperature > normalMax -> {
                val priority = when {
                    temperature >= 39.5 -> Constants.AnomalyDetection.ALERT_PRIORITY_HIGH
                    temperature >= 38.5 -> Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM
                    else -> Constants.AnomalyDetection.ALERT_PRIORITY_LOW
                }
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_TEMPERATURE_HIGH,
                    priority = priority,
                    description = "Temperatura elevada: $temperature°C (Normal: $normalMin-$normalMax°C)",
                    recommendedAction = when (priority) {
                        Constants.AnomalyDetection.ALERT_PRIORITY_HIGH -> "⚠️ Fiebre alta: Buscar atención médica"
                        else -> "Tomar antipirético, hidratarse y descansar"
                    }
                )
            }
            // Hipotermia
            temperature < normalMin -> {
                AnomalyResult(
                    hasAnomaly = true,
                    anomalyType = Constants.AnomalyDetection.ANOMALY_TYPE_TEMPERATURE_LOW,
                    priority = Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM,
                    description = "Temperatura baja: $temperature°C (Normal: $normalMin-$normalMax°C)",
                    recommendedAction = "Abrigarse, tomar bebidas calientes y consultar médico"
                )
            }
            else -> AnomalyResult(hasAnomaly = false)
        }
    }

    /**
     * Convierte anomalías detectadas en alertas para guardar en base de datos
     */
    fun createAlertsFromAnomalies(
        userId: String,
        vitalSigns: VitalSigns,
        anomalies: List<AnomalyResult>
    ): List<Alert> {
        return anomalies.map { anomaly ->
            Alert(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                title = anomaly.anomalyType ?: "Anomalía detectada",
                message = anomaly.description ?: "",
                severity = anomaly.priority ?: Constants.AnomalyDetection.ALERT_PRIORITY_LOW,
                type = "Signos Vitales",
                isRead = false,
                timestamp = System.currentTimeMillis(),
                relatedId = vitalSigns.id
            )
        }
    }


    /**
     * Determina si una anomalía requiere notificación inmediata
     */
    fun requiresImmediateNotification(anomaly: AnomalyResult): Boolean {
        return anomaly.hasAnomaly && 
               (anomaly.priority == Constants.AnomalyDetection.ALERT_PRIORITY_HIGH ||
                anomaly.priority == Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM)
    }
}
