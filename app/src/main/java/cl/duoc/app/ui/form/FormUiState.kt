package cl.duoc.app.ui.form

/**
 * Estado de la UI para un formulario genérico
 * Representa todos los estados posibles de un formulario
 */
data class FormUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val generalError: String? = null
)
