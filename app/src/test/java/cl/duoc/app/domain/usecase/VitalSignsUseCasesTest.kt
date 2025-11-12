package cl.duoc.app.domain.usecase

import cl.duoc.app.data.repository.VitalSignsRepositoryImpl
import cl.duoc.app.model.RiskLevel
import cl.duoc.app.model.VitalSigns
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para casos de uso de signos vitales
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VitalSignsUseCasesTest {

    private lateinit var repository: VitalSignsRepositoryImpl
    private lateinit var saveVitalSignsUseCase: SaveVitalSignsUseCase
    private lateinit var getLatestVitalSignsUseCase: GetLatestVitalSignsUseCase
    private lateinit var calculateRiskLevelUseCase: CalculateRiskLevelUseCase
    private lateinit var clearVitalSignsHistoryUseCase: ClearVitalSignsHistoryUseCase

    @Before
    fun setup() {
        repository = VitalSignsRepositoryImpl()
        saveVitalSignsUseCase = SaveVitalSignsUseCase(repository)
        getLatestVitalSignsUseCase = GetLatestVitalSignsUseCase(repository)
        calculateRiskLevelUseCase = CalculateRiskLevelUseCase()
        clearVitalSignsHistoryUseCase = ClearVitalSignsHistoryUseCase(repository)
    }

    @Test
    fun `SaveVitalSignsUseCase succeeds with valid data`() = runTest {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 75,
            bloodPressureSystolic = 120,
            bloodPressureDiastolic = 80,
            oxygenSaturation = 98
        )
        
        val result = saveVitalSignsUseCase(vitalSigns)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `SaveVitalSignsUseCase fails with invalid heart rate`() = runTest {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 250, // Fuera de rango
            bloodPressureSystolic = 120,
            bloodPressureDiastolic = 80,
            oxygenSaturation = 98
        )
        
        val result = saveVitalSignsUseCase(vitalSigns)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("cardíaca"))
    }

    @Test
    fun `SaveVitalSignsUseCase fails with invalid blood pressure`() = runTest {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 75,
            bloodPressureSystolic = 300, // Fuera de rango
            bloodPressureDiastolic = 80,
            oxygenSaturation = 98
        )
        
        val result = saveVitalSignsUseCase(vitalSigns)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("sistólica"))
    }

    @Test
    fun `SaveVitalSignsUseCase fails with invalid oxygen saturation`() = runTest {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 75,
            bloodPressureSystolic = 120,
            bloodPressureDiastolic = 80,
            oxygenSaturation = 110 // Fuera de rango
        )
        
        val result = saveVitalSignsUseCase(vitalSigns)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("oxígeno"))
    }

    @Test
    fun `GetLatestVitalSignsUseCase returns correct number of records`() = runTest {
        // Insertar varios registros
        repeat(5) { index ->
            val vitalSigns = VitalSigns(
                id = "vs_$index",
                userId = "user1",
                heartRate = 75,
                bloodPressureSystolic = 120,
                bloodPressureDiastolic = 80,
                oxygenSaturation = 98
            )
            repository.saveVitalSigns(vitalSigns)
        }
        
        val result = getLatestVitalSignsUseCase("user1", 3)
        assertEquals(3, result.size)
    }

    @Test
    fun `CalculateRiskLevelUseCase returns NORMAL for healthy values`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 75,
            bloodPressureSystolic = 120,
            bloodPressureDiastolic = 80,
            oxygenSaturation = 98
        )
        
        val risk = calculateRiskLevelUseCase(vitalSigns)
        assertEquals(RiskLevel.NORMAL, risk)
    }

    @Test
    fun `CalculateRiskLevelUseCase returns WARNING for borderline values`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 105, // Warning range
            bloodPressureSystolic = 145, // Warning range
            bloodPressureDiastolic = 80,
            oxygenSaturation = 94 // Warning range
        )
        
        val risk = calculateRiskLevelUseCase(vitalSigns)
        assertEquals(RiskLevel.WARNING, risk)
    }

    @Test
    fun `CalculateRiskLevelUseCase returns DANGER for critical values`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 130, // Danger range
            bloodPressureSystolic = 190, // Danger range
            bloodPressureDiastolic = 80,
            oxygenSaturation = 85 // Danger range
        )
        
        val risk = calculateRiskLevelUseCase(vitalSigns)
        assertEquals(RiskLevel.DANGER, risk)
    }

    @Test
    fun `CalculateRiskLevelUseCase returns DANGER for low oxygen`() {
        val vitalSigns = VitalSigns(
            id = "1",
            userId = "user1",
            heartRate = 75,
            bloodPressureSystolic = 120,
            bloodPressureDiastolic = 80,
            oxygenSaturation = 88 // Danger
        )
        
        val risk = calculateRiskLevelUseCase(vitalSigns)
        assertEquals(RiskLevel.DANGER, risk)
    }

    @Test
    fun `ClearVitalSignsHistoryUseCase removes all records`() = runTest {
        // Insertar registros
        repeat(3) { index ->
            val vitalSigns = VitalSigns(
                id = "vs_$index",
                userId = "user1",
                heartRate = 75,
                bloodPressureSystolic = 120,
                bloodPressureDiastolic = 80,
                oxygenSaturation = 98
            )
            repository.saveVitalSigns(vitalSigns)
        }
        
        val result = clearVitalSignsHistoryUseCase("user1")
        assertTrue(result.isSuccess)
        
        val remaining = repository.getVitalSignsByUserId("user1")
        assertEquals(0, remaining.size)
    }
}
