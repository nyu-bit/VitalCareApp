package cl.duoc.app.data.anomaly

import cl.duoc.app.model.VitalSigns
import cl.duoc.app.utils.Constants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para AnomalyDetectionService
 * 
 * HU-04: Alerta Automática por Anomalías
 * Valida la detección correcta de anomalías en signos vitales
 */
class AnomalyDetectionServiceTest {

    private lateinit var anomalyDetectionService: AnomalyDetectionService

    @Before
    fun setup() {
        anomalyDetectionService = AnomalyDetectionService()
    }

    // ========== Tests de Presión Arterial ==========

    @Test
    fun `detectAnomalies detecta presión arterial normal`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            bloodPressureSystolic = 110,
            bloodPressureDiastolic = 70,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertTrue("No debería detectar anomalías en presión normal", anomalies.isEmpty())
    }

    @Test
    fun `detectAnomalies detecta hipertensión alta`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            bloodPressureSystolic = 190,
            bloodPressureDiastolic = 125,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertTrue("Debería detectar anomalía", anomaly.hasAnomaly)
        assertEquals("Debería ser presión alta", 
            Constants.AnomalyDetection.ANOMALY_TYPE_PRESSURE_HIGH, 
            anomaly.anomalyType)
        assertEquals("Debería ser prioridad alta",
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH,
            anomaly.priority)
    }

    @Test
    fun `detectAnomalies detecta hipotensión`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            bloodPressureSystolic = 85,
            bloodPressureDiastolic = 55,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertEquals("Debería ser presión baja",
            Constants.AnomalyDetection.ANOMALY_TYPE_PRESSURE_LOW,
            anomaly.anomalyType)
    }

    // ========== Tests de Frecuencia Cardíaca ==========

    @Test
    fun `detectAnomalies detecta frecuencia cardíaca normal`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 75,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertTrue("No debería detectar anomalías en frecuencia normal", anomalies.isEmpty())
    }

    @Test
    fun `detectAnomalies detecta taquicardia`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 145,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertEquals("Debería ser frecuencia alta",
            Constants.AnomalyDetection.ANOMALY_TYPE_HEART_RATE_HIGH,
            anomaly.anomalyType)
        assertEquals("Debería ser prioridad alta",
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH,
            anomaly.priority)
    }

    @Test
    fun `detectAnomalies detecta bradicardia`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 50,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertEquals("Debería ser frecuencia baja",
            Constants.AnomalyDetection.ANOMALY_TYPE_HEART_RATE_LOW,
            anomaly.anomalyType)
    }

    // ========== Tests de Saturación de Oxígeno ==========

    @Test
    fun `detectAnomalies detecta saturación de oxígeno normal`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            oxygenSaturation = 98,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertTrue("No debería detectar anomalías en oxígeno normal", anomalies.isEmpty())
    }

    @Test
    fun `detectAnomalies detecta saturación de oxígeno crítica`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            oxygenSaturation = 85,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertEquals("Debería ser oxígeno bajo",
            Constants.AnomalyDetection.ANOMALY_TYPE_OXYGEN_LOW,
            anomaly.anomalyType)
        assertEquals("Debería ser prioridad alta",
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH,
            anomaly.priority)
    }

    @Test
    fun `detectAnomalies detecta saturación de oxígeno baja moderada`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            oxygenSaturation = 92,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertEquals("Debería ser prioridad media",
            Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM,
            anomaly.priority)
    }

    // ========== Tests de Temperatura ==========

    @Test
    fun `detectAnomalies detecta temperatura normal`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            temperature = 36.5,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertTrue("No debería detectar anomalías en temperatura normal", anomalies.isEmpty())
    }

    @Test
    fun `detectAnomalies detecta fiebre alta`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            temperature = 39.8,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertEquals("Debería ser temperatura alta",
            Constants.AnomalyDetection.ANOMALY_TYPE_TEMPERATURE_HIGH,
            anomaly.anomalyType)
        assertEquals("Debería ser prioridad alta",
            Constants.AnomalyDetection.ALERT_PRIORITY_HIGH,
            anomaly.priority)
    }

    @Test
    fun `detectAnomalies detecta hipotermia`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            temperature = 35.5,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 1 anomalía", 1, anomalies.size)
        val anomaly = anomalies.first()
        assertEquals("Debería ser temperatura baja",
            Constants.AnomalyDetection.ANOMALY_TYPE_TEMPERATURE_LOW,
            anomaly.anomalyType)
    }

    // ========== Tests de Múltiples Anomalías ==========

    @Test
    fun `detectAnomalies detecta múltiples anomalías simultáneas`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            bloodPressureSystolic = 190,
            bloodPressureDiastolic = 125,
            heartRate = 150,
            oxygenSaturation = 88,
            temperature = 39.5,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertEquals("Debería detectar 4 anomalías", 4, anomalies.size)
        assertTrue("Todas deberían ser anomalías", anomalies.all { it.hasAnomaly })
        
        // Verificar que hay anomalías de alta prioridad
        val highPriorityCount = anomalies.count { 
            it.priority == Constants.AnomalyDetection.ALERT_PRIORITY_HIGH 
        }
        assertTrue("Debería haber anomalías de alta prioridad", highPriorityCount > 0)
    }

    // ========== Tests de Creación de Alertas ==========

    @Test
    fun `createAlertsFromAnomalies crea alertas correctamente`() {
        val userId = "user1"
        val vitalSigns = VitalSigns(
            id = "1",
            userId = userId,
            heartRate = 75,
            bloodPressureSystolic = 190,
            bloodPressureDiastolic = 125,
            oxygenSaturation = 98,
            timestamp = System.currentTimeMillis()
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)
        val alerts = anomalyDetectionService.createAlertsFromAnomalies(
            userId = userId,
            vitalSigns = vitalSigns,
            anomalies = anomalies
        )

        assertEquals("Debería crear alertas", alerts.isNotEmpty(), true)
        val alert = alerts.firstOrNull()
        alert?.let {
            assertEquals("UserId correcto", userId, it.userId)
            assertEquals("Type es Signos Vitales", "Signos Vitales", it.type)
        }
    }
        assertEquals("Título correcto",
            Constants.AnomalyDetection.ANOMALY_TYPE_PRESSURE_HIGH, 
            alert.title)
        assertFalse("Alerta no leída", alert.isRead)
        assertNotNull("Snapshot presente", alert.vitalSignsSnapshot)
    }

    // ========== Tests de Notificaciones Inmediatas ==========

    @Test
    fun `requiresImmediateNotification retorna true para prioridad alta`() {
        val anomaly = AnomalyDetectionService.AnomalyResult(
            hasAnomaly = true,
            priority = Constants.AnomalyDetection.ALERT_PRIORITY_HIGH
        )

        val requiresNotification = anomalyDetectionService.requiresImmediateNotification(anomaly)

        assertTrue("Prioridad alta requiere notificación inmediata", requiresNotification)
    }

    @Test
    fun `requiresImmediateNotification retorna true para prioridad media`() {
        val anomaly = AnomalyDetectionService.AnomalyResult(
            hasAnomaly = true,
            priority = Constants.AnomalyDetection.ALERT_PRIORITY_MEDIUM
        )

        val requiresNotification = anomalyDetectionService.requiresImmediateNotification(anomaly)

        assertTrue("Prioridad media requiere notificación inmediata", requiresNotification)
    }

    @Test
    fun `requiresImmediateNotification retorna false para prioridad baja`() {
        val anomaly = AnomalyDetectionService.AnomalyResult(
            hasAnomaly = true,
            priority = Constants.AnomalyDetection.ALERT_PRIORITY_LOW
        )

        val requiresNotification = anomalyDetectionService.requiresImmediateNotification(anomaly)

        assertFalse("Prioridad baja no requiere notificación inmediata", requiresNotification)
    }

    // ========== Tests de Valores Nulos ==========

    @Test
    fun `detectAnomalies maneja valores nulos correctamente`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            bloodPressureSystolic = null,
            bloodPressureDiastolic = null,
            heartRate = null,
            oxygenSaturation = null,
            temperature = null,
            timestamp = "2024-01-01T10:00:00"
        )

        val anomalies = anomalyDetectionService.detectAnomalies(vitalSigns)

        assertTrue("No debería detectar anomalías con valores nulos", anomalies.isEmpty())
    }
}
