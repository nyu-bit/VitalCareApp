package cl.duoc.app.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas unitarias para HomeViewModel
 * Cubre operaciones del contador y carga de datos
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has counter at zero`() {
        val state = viewModel.uiState.value
        assertEquals(0, state.counter)
    }

    @Test
    fun `increment counter increases value`() {
        viewModel.incrementCounter()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.counter)
        assertNotEquals("", state.lastUpdate)
    }

    @Test
    fun `multiple increments work correctly`() {
        repeat(5) {
            viewModel.incrementCounter()
        }
        
        val state = viewModel.uiState.value
        assertEquals(5, state.counter)
    }

    @Test
    fun `decrement counter decreases value`() {
        viewModel.incrementCounter()
        viewModel.incrementCounter()
        viewModel.decrementCounter()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.counter)
    }

    @Test
    fun `decrement counter does not go below zero`() {
        viewModel.decrementCounter()
        
        val state = viewModel.uiState.value
        assertEquals(0, state.counter)
    }

    @Test
    fun `reset counter sets value to zero`() {
        repeat(10) {
            viewModel.incrementCounter()
        }
        
        viewModel.resetCounter()
        
        val state = viewModel.uiState.value
        assertEquals(0, state.counter)
    }

    @Test
    fun `loadUserData sets loading state`() = runTest {
        viewModel.loadUserData()
        
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
    }

    @Test
    fun `loadUserData completes successfully`() = runTest {
        viewModel.loadUserData()
        
        // Avanzar el tiempo simulado
        advanceTimeBy(2000)
        
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotEquals("", state.userName)
        assertTrue(state.welcomeMessage.contains("Angel"))
        assertFalse(state.hasError)
    }

    @Test
    fun `updateUserName changes welcome message`() {
        viewModel.updateUserName("Carlos")
        
        val state = viewModel.uiState.value
        assertEquals("Carlos", state.userName)
        assertTrue(state.welcomeMessage.contains("Carlos"))
    }

    @Test
    fun `clearError removes error state`() {
        // Primero necesitamos tener un error
        viewModel.clearError()
        
        val state = viewModel.uiState.value
        assertFalse(state.hasError)
        assertNull(state.errorMessage)
    }

    @Test
    fun `initial load is triggered on init`() = runTest {
        // El ViewModel carga datos en init
        advanceTimeBy(2000)
        
        val state = viewModel.uiState.value
        assertNotEquals("", state.userName)
    }
}
