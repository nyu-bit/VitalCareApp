package cl.duoc.app.ui.form

/**
 * Eventos de UI que puede disparar el usuario en un formulario
 * Representa todas las acciones que el usuario puede realizar
 */
sealed class FormUiEvent {
    data class NameChanged(val name: String) : FormUiEvent()
    data class EmailChanged(val email: String) : FormUiEvent()
    data class PasswordChanged(val password: String) : FormUiEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : FormUiEvent()
    object SubmitForm : FormUiEvent()
    object DismissError : FormUiEvent()
}
