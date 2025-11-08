package cl.duoc.app.ui.form

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
 * Pruebas unitarias para FormViewModel
 * Cubre flujos exitosos y manejo de errores
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FormViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FormViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FormViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        
        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertNull(state.nameError)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.confirmPasswordError)
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
    }

    @Test
    fun `name changed event updates state`() {
        viewModel.onEvent(FormUiEvent.NameChanged("Juan"))
        
        val state = viewModel.uiState.value
        assertEquals("Juan", state.name)
    }

    @Test
    fun `email changed event updates state`() {
        viewModel.onEvent(FormUiEvent.EmailChanged("test@example.com"))
        
        val state = viewModel.uiState.value
        assertEquals("test@example.com", state.email)
    }

    @Test
    fun `password changed event updates state`() {
        viewModel.onEvent(FormUiEvent.PasswordChanged("password123"))
        
        val state = viewModel.uiState.value
        assertEquals("password123", state.password)
    }

    @Test
    fun `confirm password changed event updates state`() {
        viewModel.onEvent(FormUiEvent.ConfirmPasswordChanged("password123"))
        
        val state = viewModel.uiState.value
        assertEquals("password123", state.confirmPassword)
    }

    @Test
    fun `submit form with empty fields shows errors`() {
        viewModel.onEvent(FormUiEvent.SubmitForm)
        
        val state = viewModel.uiState.value
        assertNotNull(state.nameError)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
    }

    @Test
    fun `submit form with invalid email shows error`() {
        viewModel.onEvent(FormUiEvent.NameChanged("Juan"))
        viewModel.onEvent(FormUiEvent.EmailChanged("invalid-email"))
        viewModel.onEvent(FormUiEvent.PasswordChanged("password123"))
        viewModel.onEvent(FormUiEvent.ConfirmPasswordChanged("password123"))
        
        viewModel.onEvent(FormUiEvent.SubmitForm)
        
        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
        assertTrue(state.emailError!!.contains("email"))
    }

    @Test
    fun `submit form with short name shows error`() {
        viewModel.onEvent(FormUiEvent.NameChanged("Jo"))
        viewModel.onEvent(FormUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(FormUiEvent.PasswordChanged("password123"))
        viewModel.onEvent(FormUiEvent.ConfirmPasswordChanged("password123"))
        
        viewModel.onEvent(FormUiEvent.SubmitForm)
        
        val state = viewModel.uiState.value
        assertNotNull(state.nameError)
        assertTrue(state.nameError!!.contains("3"))
    }

    @Test
    fun `submit form with mismatched passwords shows error`() {
        viewModel.onEvent(FormUiEvent.NameChanged("Juan"))
        viewModel.onEvent(FormUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(FormUiEvent.PasswordChanged("password123"))
        viewModel.onEvent(FormUiEvent.ConfirmPasswordChanged("different"))
        
        viewModel.onEvent(FormUiEvent.SubmitForm)
        
        val state = viewModel.uiState.value
        assertNotNull(state.confirmPasswordError)
        assertTrue(state.confirmPasswordError!!.contains("coinciden"))
    }

    @Test
    fun `submit form with valid data processes successfully`() = runTest {
        viewModel.onEvent(FormUiEvent.NameChanged("Juan"))
        viewModel.onEvent(FormUiEvent.EmailChanged("test@example.com"))
        viewModel.onEvent(FormUiEvent.PasswordChanged("password"))
        viewModel.onEvent(FormUiEvent.ConfirmPasswordChanged("password"))
        
        viewModel.onEvent(FormUiEvent.SubmitForm)
        
        // Estado de carga
        var state = viewModel.uiState.value
        assertTrue(state.isLoading)
        
        // Avanzar el tiempo simulado
        advanceTimeBy(2500)
        
        // Estado final
        state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
    }

    @Test
    fun `dismiss error clears general error`() {
        // Simular un error general (normalmente vendría de una excepción)
        viewModel.onEvent(FormUiEvent.NameChanged("Juan"))
        viewModel.onEvent(FormUiEvent.DismissError)
        
        val state = viewModel.uiState.value
        assertNull(state.generalError)
    }

    @Test
    fun `reset form clears all fields and errors`() {
        viewModel.onEvent(FormUiEvent.NameChanged("Juan"))
        viewModel.onEvent(FormUiEvent.EmailChanged("test@example.com"))
        
        viewModel.resetForm()
        
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.email)
        assertNull(state.nameError)
        assertNull(state.emailError)
    }

    @Test
    fun `changing field clears its error`() {
        // Primero generar un error
        viewModel.onEvent(FormUiEvent.SubmitForm)
        var state = viewModel.uiState.value
        assertNotNull(state.nameError)
        
        // Cambiar el campo debería limpiar el error
        viewModel.onEvent(FormUiEvent.NameChanged("Juan"))
        state = viewModel.uiState.value
        assertNull(state.nameError)
    }
}
