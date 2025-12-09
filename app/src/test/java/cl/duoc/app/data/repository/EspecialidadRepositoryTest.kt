package cl.duoc.app.data.repository

import cl.duoc.app.data.dao.EspecialidadDao
import cl.duoc.app.data.entity.Especialidad
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests para EspecialidadRepository
 * Prueba la lógica de negocio y abstracción del acceso a datos de especialidades
 */
class EspecialidadRepositoryTest {

    private lateinit var mockDao: EspecialidadDao
    private lateinit var repository: EspecialidadRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockDao = mockk()
        repository = EspecialidadRepository(mockDao)
    }

    @Test
    fun testGetTodasEspecialidades() = runTest {
        // Arrange
        val especialidades = listOf(
            Especialidad(1, "Cardiología", "Corazón", 30, true),
            Especialidad(2, "Neurología", "Nervios", 30, true)
        )
        every { mockDao.getAllEspecialidades() } returns flowOf(especialidades)

        // Act
        val result = repository.todasEspecialidades

        // Assert
        assertNotNull(result)
        verify { mockDao.getAllEspecialidades() }
    }

    @Test
    fun testGetEspecialidadesActivas() = runTest {
        // Arrange
        val especialidadesActivas = listOf(
            Especialidad(1, "Cardiología", "Corazón", 30, true),
            Especialidad(2, "Pediatría", "Niños", 20, true)
        )
        every { mockDao.getEspecialidadesActivas() } returns flowOf(especialidadesActivas)

        // Act
        val result = repository.especialidadesActivas

        // Assert
        assertNotNull(result)
        verify { mockDao.getEspecialidadesActivas() }
    }

    @Test
    fun testGetEspecialidadById() = runTest {
        // Arrange
        val especialidad = Especialidad(1, "Dermatología", "Piel", 25, true)
        every { mockDao.getEspecialidadByIdFlow(1) } returns flowOf(especialidad)

        // Act
        val result = repository.getEspecialidadById(1)

        // Assert
        assertNotNull(result)
        verify { mockDao.getEspecialidadByIdFlow(1) }
    }

    @Test
    fun testGetEspecialidadByIdNull() = runTest {
        // Arrange
        every { mockDao.getEspecialidadByIdFlow(999) } returns flowOf(null)

        // Act
        val result = repository.getEspecialidadById(999)

        // Assert
        assertNotNull(result)
        verify { mockDao.getEspecialidadByIdFlow(999) }
    }

    @Test
    fun testInsertEspecialidad() = runTest {
        // Arrange
        val especialidad = Especialidad(0, "Oftalmología", "Ojos", 20, true)
        coEvery { mockDao.insert(especialidad) } returns 1L

        // Act
        val result = repository.insert(especialidad)

        // Assert
        assertEquals(1L, result)
        coVerify { mockDao.insert(especialidad) }
    }

    @Test
    fun testInsertAllEspecialidades() = runTest {
        // Arrange
        val especialidades = listOf(
            Especialidad(0, "Psiquiatría", "Salud mental", 45, true),
            Especialidad(0, "Cirugía", "Intervenciones", 60, true)
        )
        coEvery { mockDao.insertAll(especialidades) } just Runs

        // Act
        repository.insertAll(especialidades)

        // Assert
        coVerify { mockDao.insertAll(especialidades) }
    }

    @Test
    fun testUpdateEspecialidad() = runTest {
        // Arrange
        val especialidad = Especialidad(1, "Urología", "Sistema urinario", 25, true)
        coEvery { mockDao.update(especialidad) } just Runs

        // Act
        repository.update(especialidad)

        // Assert
        coVerify { mockDao.update(especialidad) }
    }

    @Test
    fun testDeleteEspecialidad() = runTest {
        // Arrange
        val especialidad = Especialidad(1, "Reumatología", "Articulaciones", 30, true)
        coEvery { mockDao.delete(especialidad) } just Runs

        // Act
        repository.delete(especialidad)

        // Assert
        coVerify { mockDao.delete(especialidad) }
    }

    @Test
    fun testDeleteEspecialidadById() = runTest {
        // Arrange
        coEvery { mockDao.deleteById(1) } just Runs

        // Act
        repository.deleteById(1)

        // Assert
        coVerify { mockDao.deleteById(1) }
    }

    @Test
    fun testInsertEspecialidadMultiples() = runTest {
        // Arrange
        val especialidades = listOf(
            Especialidad(0, "Otorrinolaringología", "Oído, nariz y garganta", 25, true),
            Especialidad(0, "Endocrinología", "Hormonas", 30, true),
            Especialidad(0, "Traumatología", "Huesos", 30, true),
            Especialidad(0, "Oncología", "Cáncer", 40, true)
        )
        coEvery { mockDao.insertAll(especialidades) } just Runs

        // Act
        repository.insertAll(especialidades)

        // Assert
        coVerify { mockDao.insertAll(especialidades) }
    }

    @Test
    fun testGetEspecialidadesActivasEmpty() = runTest {
        // Arrange
        every { mockDao.getEspecialidadesActivas() } returns flowOf(emptyList())

        // Act
        val result = repository.especialidadesActivas

        // Assert
        assertNotNull(result)
        verify { mockDao.getEspecialidadesActivas() }
    }

    @Test
    fun testUpdateEspecialidadInactiva() = runTest {
        // Arrange
        val especialidad = Especialidad(1, "Homeopatía", "Medicina alternativa", 30, false)
        coEvery { mockDao.update(especialidad) } just Runs

        // Act
        repository.update(especialidad)

        // Assert
        assertFalse(especialidad.activa)
        coVerify { mockDao.update(especialidad) }
    }

    @Test
    fun testEspecialidadDuracionVariada() = runTest {
        // Arrange
        val especialidades = listOf(
            Especialidad(0, "Esp1", "Desc1", 15, true),
            Especialidad(0, "Esp2", "Desc2", 30, true),
            Especialidad(0, "Esp3", "Desc3", 60, true),
            Especialidad(0, "Esp4", "Desc4", 90, true)
        )
        coEvery { mockDao.insertAll(especialidades) } just Runs

        // Act
        repository.insertAll(especialidades)

        // Assert
        coVerify { mockDao.insertAll(especialidades) }
        assertTrue(especialidades.all { it.duracionConsulta > 0 })
    }
}

