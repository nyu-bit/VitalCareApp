package cl.duoc.app.data.repository

import cl.duoc.app.data.dao.CitaDao
import cl.duoc.app.data.entity.Cita
import cl.duoc.app.data.entity.EstadoCita
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests para CitaRepository
 * Prueba la lógica de negocio y abstracción del acceso a datos de citas
 */
class CitaRepositoryTest {

    private lateinit var mockDao: CitaDao
    private lateinit var repository: CitaRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockDao = mockk()
        repository = CitaRepository(mockDao)
    }

    @Test
    fun testGetTodasCitas() = runTest {
        // Arrange
        val citas = listOf(
            Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.PENDIENTE),
            Cita(2, 2L, 2L, "2024-12-20", "14:00", EstadoCita.CONFIRMADA)
        )
        every { mockDao.getAllCitas() } returns flowOf(citas)

        // Act
        val result = repository.todasCitas

        // Assert
        assertNotNull(result)
        verify { mockDao.getAllCitas() }
    }

    @Test
    fun testGetCitaById() = runTest {
        // Arrange
        val cita = Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.PENDIENTE)
        every { mockDao.getCitaByIdFlow(1) } returns flowOf(cita)

        // Act
        val result = repository.getCitaById(1)

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitaByIdFlow(1) }
    }

    @Test
    fun testGetCitaByIdSync() = runTest {
        // Arrange
        val cita = Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.PENDIENTE)
        coEvery { mockDao.getCitaById(1) } returns cita

        // Act
        val result = repository.getCitaByIdSync(1)

        // Assert
        assertNotNull(result)
        assertEquals(1L, result?.id)
        coVerify { mockDao.getCitaById(1) }
    }

    @Test
    fun testGetCitaByIdSyncNull() = runTest {
        // Arrange
        coEvery { mockDao.getCitaById(999) } returns null

        // Act
        val result = repository.getCitaByIdSync(999)

        // Assert
        assertNull(result)
    }

    @Test
    fun testGetCitasByPaciente() = runTest {
        // Arrange
        val citas = listOf(
            Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.PENDIENTE),
            Cita(2, 1L, 2L, "2024-12-20", "14:00", EstadoCita.CONFIRMADA)
        )
        every { mockDao.getCitasByPaciente(1L) } returns flowOf(citas)

        // Act
        val result = repository.getCitasByPaciente(1L)

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitasByPaciente(1L) }
    }

    @Test
    fun testGetCitasByEspecialidad() = runTest {
        // Arrange
        val citas = listOf(
            Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.PENDIENTE),
            Cita(3, 2L, 1L, "2024-12-18", "11:00", EstadoCita.CONFIRMADA)
        )
        every { mockDao.getCitasByEspecialidad(1L) } returns flowOf(citas)

        // Act
        val result = repository.getCitasByEspecialidad(1L)

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitasByEspecialidad(1L) }
    }

    @Test
    fun testGetCitasByEstado() = runTest {
        // Arrange
        val citas = listOf(
            Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.PENDIENTE),
            Cita(2, 2L, 2L, "2024-12-20", "14:00", EstadoCita.PENDIENTE)
        )
        every { mockDao.getCitasByEstado(EstadoCita.PENDIENTE) } returns flowOf(citas)

        // Act
        val result = repository.getCitasByEstado(EstadoCita.PENDIENTE)

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitasByEstado(EstadoCita.PENDIENTE) }
    }

    @Test
    fun testGetCitasByFecha() = runTest {
        // Arrange
        val citas = listOf(
            Cita(1, 1L, 1L, "2024-12-15", "10:30"),
            Cita(2, 2L, 2L, "2024-12-15", "14:00")
        )
        every { mockDao.getCitasByFecha("2024-12-15") } returns flowOf(citas)

        // Act
        val result = repository.getCitasByFecha("2024-12-15")

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitasByFecha("2024-12-15") }
    }

    @Test
    fun testGetCitasByRangoFechas() = runTest {
        // Arrange
        val citas = listOf(
            Cita(1, 1L, 1L, "2024-12-15", "10:30"),
            Cita(2, 2L, 2L, "2024-12-20", "14:00")
        )
        every { mockDao.getCitasByRangoFechas("2024-12-15", "2024-12-20") } returns flowOf(citas)

        // Act
        val result = repository.getCitasByRangoFechas("2024-12-15", "2024-12-20")

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitasByRangoFechas("2024-12-15", "2024-12-20") }
    }

    @Test
    fun testGetCitasPendientesByPaciente() = runTest {
        // Arrange
        val citas = listOf(
            Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.PENDIENTE),
            Cita(2, 1L, 2L, "2024-12-20", "14:00", EstadoCita.CONFIRMADA)
        )
        every { mockDao.getCitasByPacienteAndEstados(1L, listOf(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA)) } returns flowOf(citas)

        // Act
        val result = repository.getCitasPendientesByPaciente(1L)

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitasByPacienteAndEstados(1L, listOf(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA)) }
    }

    @Test
    fun testInsertCita() = runTest {
        // Arrange
        val cita = Cita(0, 1L, 1L, "2024-12-15", "10:30")
        coEvery { mockDao.insert(cita) } returns 1L

        // Act
        val result = repository.insert(cita)

        // Assert
        assertEquals(1L, result)
        coVerify { mockDao.insert(cita) }
    }

    @Test
    fun testInsertAllCitas() = runTest {
        // Arrange
        val citas = listOf(
            Cita(0, 1L, 1L, "2024-12-15", "10:30"),
            Cita(0, 2L, 2L, "2024-12-20", "14:00")
        )
        coEvery { mockDao.insertAll(citas) } just Runs

        // Act
        repository.insertAll(citas)

        // Assert
        coVerify { mockDao.insertAll(citas) }
    }

    @Test
    fun testUpdateCita() = runTest {
        // Arrange
        val cita = Cita(1, 1L, 1L, "2024-12-15", "10:30", EstadoCita.CONFIRMADA)
        coEvery { mockDao.update(cita) } just Runs

        // Act
        repository.update(cita)

        // Assert
        coVerify { mockDao.update(cita) }
    }

    @Test
    fun testDeleteCita() = runTest {
        // Arrange
        val cita = Cita(1, 1L, 1L, "2024-12-15", "10:30")
        coEvery { mockDao.delete(cita) } just Runs

        // Act
        repository.delete(cita)

        // Assert
        coVerify { mockDao.delete(cita) }
    }

    @Test
    fun testDeleteCitaById() = runTest {
        // Arrange
        coEvery { mockDao.deleteById(1) } just Runs

        // Act
        repository.deleteById(1)

        // Assert
        coVerify { mockDao.deleteById(1) }
    }

    @Test
    fun testUpdateEstado() = runTest {
        // Arrange
        coEvery { mockDao.updateEstado(1, EstadoCita.CONFIRMADA) } just Runs

        // Act
        repository.updateEstado(1, EstadoCita.CONFIRMADA)

        // Assert
        coVerify { mockDao.updateEstado(1, EstadoCita.CONFIRMADA) }
    }

    @Test
    fun testConfirmarCita() = runTest {
        // Arrange
        coEvery { mockDao.updateEstado(1, EstadoCita.CONFIRMADA) } just Runs

        // Act
        repository.confirmarCita(1)

        // Assert
        coVerify { mockDao.updateEstado(1, EstadoCita.CONFIRMADA) }
    }

    @Test
    fun testCancelarCita() = runTest {
        // Arrange
        coEvery { mockDao.updateEstado(1, EstadoCita.CANCELADA) } just Runs

        // Act
        repository.cancelarCita(1)

        // Assert
        coVerify { mockDao.updateEstado(1, EstadoCita.CANCELADA) }
    }

    @Test
    fun testCompletarCita() = runTest {
        // Arrange
        coEvery { mockDao.updateEstado(1, EstadoCita.COMPLETADA) } just Runs

        // Act
        repository.completarCita(1)

        // Assert
        coVerify { mockDao.updateEstado(1, EstadoCita.COMPLETADA) }
    }

    @Test
    fun testGetCitasByPacienteEmpty() = runTest {
        // Arrange
        every { mockDao.getCitasByPaciente(999L) } returns flowOf(emptyList())

        // Act
        val result = repository.getCitasByPaciente(999L)

        // Assert
        assertNotNull(result)
        verify { mockDao.getCitasByPaciente(999L) }
    }
}

