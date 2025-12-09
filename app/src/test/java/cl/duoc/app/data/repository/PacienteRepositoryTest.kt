package cl.duoc.app.data.repository

import cl.duoc.app.data.dao.PacienteDao
import cl.duoc.app.data.entity.Paciente
import io.mockk.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests para PacienteRepository
 * Prueba la lógica de negocio y abstracción del acceso a datos
 */
class PacienteRepositoryTest {

    private lateinit var mockDao: PacienteDao
    private lateinit var repository: PacienteRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockDao = mockk()
        repository = PacienteRepository(mockDao)
    }

    @Test
    fun testGetTodosPacientesFlow() = runTest {
        // Arrange
        val pacientes = listOf(
            Paciente(1, "12345678-5", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1"),
            Paciente(2, "98765432-1", "María", "García", "maria@example.com", "+56912345679", "1985-05-20", "Calle 2")
        )
        every { mockDao.getAllPacientes() } returns flowOf(pacientes)

        // Act
        val result = repository.todosPacientes

        // Assert
        assertNotNull(result)
        verify { mockDao.getAllPacientes() }
    }

    @Test
    fun testGetPacientesActivosFlow() = runTest {
        // Arrange
        val pacientesActivos = listOf(
            Paciente(1, "12345678-5", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1", true),
            Paciente(2, "98765432-1", "María", "García", "maria@example.com", "+56912345679", "1985-05-20", "Calle 2", true)
        )
        every { mockDao.getPacientesActivos() } returns flowOf(pacientesActivos)

        // Act
        val result = repository.pacientesActivos

        // Assert
        assertNotNull(result)
        verify { mockDao.getPacientesActivos() }
    }

    @Test
    fun testGetPacienteByIdFlow() = runTest {
        // Arrange
        val paciente = Paciente(1, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        every { mockDao.getPacienteByIdFlow(1) } returns flowOf(paciente)

        // Act
        val result = repository.getPacienteById(1)

        // Assert
        assertNotNull(result)
        verify { mockDao.getPacienteByIdFlow(1) }
    }

    @Test
    fun testGetPacienteByIdSync() = runTest {
        // Arrange
        val paciente = Paciente(1, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        coEvery { mockDao.getPacienteById(1) } returns paciente

        // Act
        val result = repository.getPacienteByIdSync(1)

        // Assert
        assertNotNull(result)
        assertEquals("Juan", result?.nombre)
        coVerify { mockDao.getPacienteById(1) }
    }

    @Test
    fun testGetPacienteByIdSyncNull() = runTest {
        // Arrange
        coEvery { mockDao.getPacienteById(999) } returns null

        // Act
        val result = repository.getPacienteByIdSync(999)

        // Assert
        assertNull(result)
        coVerify { mockDao.getPacienteById(999) }
    }

    @Test
    fun testGetPacienteByRut() = runTest {
        // Arrange
        val paciente = Paciente(1, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        coEvery { mockDao.getPacienteByRut("12345678-9") } returns paciente

        // Act
        val result = repository.getPacienteByRut("12345678-9")

        // Assert
        assertNotNull(result)
        assertEquals("12345678-9", result?.rut)
        coVerify { mockDao.getPacienteByRut("12345678-9") }
    }

    @Test
    fun testGetPacienteByEmail() = runTest {
        // Arrange
        val paciente = Paciente(1, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        coEvery { mockDao.getPacienteByEmail("juan@example.com") } returns paciente

        // Act
        val result = repository.getPacienteByEmail("juan@example.com")

        // Assert
        assertNotNull(result)
        assertEquals("juan@example.com", result?.email)
        coVerify { mockDao.getPacienteByEmail("juan@example.com") }
    }

    @Test
    fun testSearchPacientes() = runTest {
        // Arrange
        val pacientes = listOf(
            Paciente(1, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        )
        every { mockDao.searchPacientes("Juan") } returns flowOf(pacientes)

        // Act
        val result = repository.searchPacientes("Juan")

        // Assert
        assertNotNull(result)
        verify { mockDao.searchPacientes("Juan") }
    }

    @Test
    fun testInsertPaciente() = runTest {
        // Arrange
        val paciente = Paciente(0, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        coEvery { mockDao.insert(paciente) } returns 1L

        // Act
        val result = repository.insert(paciente)

        // Assert
        assertEquals(1L, result)
        coVerify { mockDao.insert(paciente) }
    }

    @Test
    fun testInsertAllPacientes() = runTest {
        // Arrange
        val pacientes = listOf(
            Paciente(0, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1"),
            Paciente(0, "98765432-1", "María", "García", "maria@example.com", "+56912345679", "1985-05-20", "Calle 2")
        )
        coEvery { mockDao.insertAll(pacientes) } just Runs

        // Act
        repository.insertAll(pacientes)

        // Assert
        coVerify { mockDao.insertAll(pacientes) }
    }

    @Test
    fun testUpdatePaciente() = runTest {
        // Arrange
        val paciente = Paciente(1, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        coEvery { mockDao.update(paciente) } just Runs

        // Act
        repository.update(paciente)

        // Assert
        coVerify { mockDao.update(paciente) }
    }

    @Test
    fun testDeletePaciente() = runTest {
        // Arrange
        val paciente = Paciente(1, "12345678-9", "Juan", "Pérez", "juan@example.com", "+56912345678", "1990-01-15", "Calle 1")
        coEvery { mockDao.delete(paciente) } just Runs

        // Act
        repository.delete(paciente)

        // Assert
        coVerify { mockDao.delete(paciente) }
    }

    @Test
    fun testDeletePacienteById() = runTest {
        // Arrange
        coEvery { mockDao.deleteById(1) } just Runs

        // Act
        repository.deleteById(1)

        // Assert
        coVerify { mockDao.deleteById(1) }
    }

    @Test
    fun testGetPacienteByRutNotFound() = runTest {
        // Arrange
        coEvery { mockDao.getPacienteByRut("00000000-0") } returns null

        // Act
        val result = repository.getPacienteByRut("00000000-0")

        // Assert
        assertNull(result)
        coVerify { mockDao.getPacienteByRut("00000000-0") }
    }

    @Test
    fun testSearchPacientesEmpty() = runTest {
        // Arrange
        every { mockDao.searchPacientes("NonExistent") } returns flowOf(emptyList())

        // Act
        val result = repository.searchPacientes("NonExistent")

        // Assert
        assertNotNull(result)
        verify { mockDao.searchPacientes("NonExistent") }
    }

    @Test
    fun testInsertMultiplePacientes() = runTest {
        // Arrange
        val pacientes = listOf(
            Paciente(0, "11111111-1", "A", "A", "a@test.com", "+56911111111", "2000-01-01", "Dir A"),
            Paciente(0, "22222222-2", "B", "B", "b@test.com", "+56922222222", "2000-02-02", "Dir B"),
            Paciente(0, "33333333-3", "C", "C", "c@test.com", "+56933333333", "2000-03-03", "Dir C")
        )
        coEvery { mockDao.insertAll(pacientes) } just Runs

        // Act
        repository.insertAll(pacientes)

        // Assert
        coVerify { mockDao.insertAll(pacientes) }
    }
}

