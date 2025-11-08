package cl.duoc.app.ui.registration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import cl.duoc.app.domain.usecase.SaveUserUseCase
import cl.duoc.app.domain.usecase.ValidateUserDataUseCase
import cl.duoc.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Pruebas unitarias para RegistrationViewModel (HU-02)
 * Cubre validaciones, estados y flujo de registro
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var saveUserUseCase: SaveUserUseCase

    @Mock
    private lateinit var validateUserDataUseCase: ValidateUserDataUseCase

    private lateinit var viewModel: RegistrationViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = RegistrationViewModel(saveUserUseCase, validateUserDataUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== Pruebas de Validación ==========

    @Test
    fun `onNameChanged updates state and validates`() {
        viewModel.onNameChanged("Juan Pérez")

        val state = viewModel.uiState.value
        assertEquals("Juan Pérez", state.name)
        assertNull(state.nameError)
    }

    @Test
    fun `onNameChanged shows error for empty name`() {
        viewModel.onNameChanged("")

        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertNotNull(state.nameError)
    }

    @Test
    fun `onNameChanged shows error for short name`() {
        viewModel.onNameChanged("A")

        val state = viewModel.uiState.value
        assertNotNull(state.nameError)
    }

    @Test
    fun `onEmailChanged updates state and validates`() {
        viewModel.onEmailChanged("juan@example.com")

        val state = viewModel.uiState.value
        assertEquals("juan@example.com", state.email)
        assertNull(state.emailError)
    }

    @Test
    fun `onEmailChanged shows error for invalid email`() {
        viewModel.onEmailChanged("invalid-email")

        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
    }

    @Test
    fun `onPhoneChanged updates state and validates`() {
        viewModel.onPhoneChanged("912345678")

        val state = viewModel.uiState.value
        assertEquals("912345678", state.phone)
        assertNull(state.phoneError)
    }

    @Test
    fun `onPhoneChanged filters non-numeric characters`() {
        viewModel.onPhoneChanged("91-234-5678")

        val state = viewModel.uiState.value
        assertEquals("912345678", state.phone)
    }

    @Test
    fun `onRutChanged updates state and validates`() {
        viewModel.onRutChanged("12345678-9")

        val state = viewModel.uiState.value
        assertEquals("12345678-9", state.rut)
    }

    @Test
    fun `onBirthDateChanged updates state`() {
        viewModel.onBirthDateChanged("15/08/1990")

        val state = viewModel.uiState.value
        assertEquals("15/08/1990", state.birthDate)
    }

    @Test
    fun `onAddressChanged updates state`() {
        viewModel.onAddressChanged("Av. Principal 123")

        val state = viewModel.uiState.value
        assertEquals("Av. Principal 123", state.address)
    }

    // ========== Pruebas de Estado del Formulario ==========

    @Test
    fun `initial state has empty fields`() {
        val state = viewModel.uiState.value

        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.phone)
        assertEquals("", state.rut)
        assertFalse(state.isFormValid)
    }

    @Test
    fun `form is valid when required fields are filled correctly`() {
        viewModel.onNameChanged("Juan Pérez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onRutChanged("12345678-9")

        val state = viewModel.uiState.value
        assertTrue(state.isFormValid)
    }

    @Test
    fun `form is invalid when name is empty`() {
        viewModel.onNameChanged("")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onRutChanged("12345678-9")

        val state = viewModel.uiState.value
        assertFalse(state.isFormValid)
    }

    @Test
    fun `form is invalid when email is invalid`() {
        viewModel.onNameChanged("Juan Pérez")
        viewModel.onEmailChanged("invalid")
        viewModel.onRutChanged("12345678-9")

        val state = viewModel.uiState.value
        assertFalse(state.isFormValid)
    }

    // ========== Pruebas de Registro ==========

    @Test
    fun `registerUser shows errors when form is invalid`() {
        viewModel.onNameChanged("")
        viewModel.onEmailChanged("invalid")

        viewModel.registerUser()

        val state = viewModel.uiState.value
        assertNotNull(state.generalError)
        assertFalse(state.isRegistering)
    }

    @Test
    fun `registerUser succeeds with valid data`() = runTest {
        // Preparar
        viewModel.onNameChanged("Juan Pérez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onRutChanged("12345678-9")
        viewModel.onPhoneChanged("912345678")

        `when`(validateUserDataUseCase.execute(any())).thenReturn(Unit)
        `when`(saveUserUseCase.execute(any())).thenReturn(true)

        // Ejecutar
        viewModel.registerUser()
        advanceUntilIdle()

        // Verificar
        val state = viewModel.uiState.value
        assertTrue(state.registrationSuccess)
        assertFalse(state.isRegistering)
        assertNotNull(state.registeredUser)
        assertNull(state.generalError)

        verify(validateUserDataUseCase).execute(any())
        verify(saveUserUseCase).execute(any())
    }

    @Test
    fun `registerUser handles save failure`() = runTest {
        // Preparar
        viewModel.onNameChanged("Juan Pérez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onRutChanged("12345678-9")

        `when`(validateUserDataUseCase.execute(any())).thenReturn(Unit)
        `when`(saveUserUseCase.execute(any())).thenReturn(false)

        // Ejecutar
        viewModel.registerUser()
        advanceUntilIdle()

        // Verificar
        val state = viewModel.uiState.value
        assertFalse(state.registrationSuccess)
        assertFalse(state.isRegistering)
        assertNotNull(state.generalError)
    }

    @Test
    fun `registerUser handles validation exception`() = runTest {
        // Preparar
        viewModel.onNameChanged("Juan Pérez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onRutChanged("12345678-9")

        `when`(validateUserDataUseCase.execute(any()))
            .thenThrow(IllegalArgumentException("Usuario ya existe"))

        // Ejecutar
        viewModel.registerUser()
        advanceUntilIdle()

        // Verificar
        val state = viewModel.uiState.value
        assertFalse(state.registrationSuccess)
        assertFalse(state.isRegistering)
        assertNotNull(state.generalError)
        assertTrue(state.generalError!!.contains("Usuario ya existe"))
    }

    @Test
    fun `registerUser sets loading state during registration`() = runTest {
        // Preparar
        viewModel.onNameChanged("Juan Pérez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onRutChanged("12345678-9")

        `when`(validateUserDataUseCase.execute(any())).thenReturn(Unit)
        `when`(saveUserUseCase.execute(any())).thenReturn(true)

        // Ejecutar
        viewModel.registerUser()

        // Verificar estado inicial
        var state = viewModel.uiState.value
        assertTrue(state.isRegistering)

        // Completar
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertFalse(state.isRegistering)
    }

    // ========== Pruebas de Utilidades ==========

    @Test
    fun `resetForm clears all fields`() {
        // Llenar formulario
        viewModel.onNameChanged("Juan Pérez")
        viewModel.onEmailChanged("juan@example.com")
        viewModel.onRutChanged("12345678-9")

        // Resetear
        viewModel.resetForm()

        // Verificar
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.rut)
        assertFalse(state.registrationSuccess)
        assertNull(state.generalError)
    }

    @Test
    fun `clearGeneralError removes error message`() {
        // Simular error
        viewModel.registerUser() // Fallará por validación

        var state = viewModel.uiState.value
        assertNotNull(state.generalError)

        // Limpiar error
        viewModel.clearGeneralError()

        state = viewModel.uiState.value
        assertNull(state.generalError)
    }

    // Helper para mockito any()
    private fun <T> any(): T {
        verify(mock(T::class.java), never())
        return null as T
    }
}
