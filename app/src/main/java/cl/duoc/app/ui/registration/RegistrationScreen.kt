package cl.duoc.app.ui.registration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Pantalla de Registro de Usuario (HU-02)
 * 
 * Implementa:
 * - Formulario completo con validaciones en tiempo real
 * - Campos: Nombre, Email, Teléfono, RUT, Fecha de Nacimiento, Dirección
 * - Manejo de estados: loading, success, error
 * - Navegación del teclado entre campos
 * - Diseño responsive y accesible
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onNavigateBack: () -> Unit = {},
    onRegistrationSuccess: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Navegar cuando el registro sea exitoso
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            onRegistrationSuccess(uiState.registeredUser?.id ?: "")
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Encabezado
                RegistrationHeader()

                // Error general
                uiState.generalError?.let { error ->
                    ErrorBanner(
                        message = error,
                        onDismiss = { viewModel.clearGeneralError() }
                    )
                }

                // Formulario
                RegistrationForm(
                    uiState = uiState,
                    onNameChanged = viewModel::onNameChanged,
                    onEmailChanged = viewModel::onEmailChanged,
                    onPhoneChanged = viewModel::onPhoneChanged,
                    onRutChanged = viewModel::onRutChanged,
                    onBirthDateChanged = viewModel::onBirthDateChanged,
                    onAddressChanged = viewModel::onAddressChanged,
                    onSubmit = {
                        focusManager.clearFocus()
                        viewModel.registerUser()
                    }
                )

                // Botón de registro
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.registerUser()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isFormValid && !uiState.isRegistering
                ) {
                    if (uiState.isRegistering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (uiState.isRegistering) "Registrando..." else "Registrar Usuario"
                    )
                }

                // Información adicional
                Text(
                    text = "* Campos obligatorios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Espaciado final
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Diálogo de éxito
            if (uiState.registrationSuccess) {
                SuccessDialog(
                    userName = uiState.registeredUser?.name ?: "Usuario",
                    onDismiss = {
                        viewModel.resetForm()
                        onNavigateBack()
                    }
                )
            }
        }
    }
}

/**
 * Encabezado de la pantalla
 */
@Composable
private fun RegistrationHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Column {
                Text(
                    text = "Nuevo Paciente",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Completa el formulario para registrar un nuevo usuario",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Banner de error
 */
@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/**
 * Formulario de registro
 */
@Composable
private fun RegistrationForm(
    uiState: RegistrationUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onRutChanged: (String) -> Unit,
    onBirthDateChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    // Nombre *
    OutlinedTextField(
        value = uiState.name,
        onValueChange = onNameChanged,
        label = { Text("Nombre Completo *") },
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null)
        },
        isError = uiState.nameError != null,
        supportingText = uiState.nameError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    // RUT *
    OutlinedTextField(
        value = uiState.rut,
        onValueChange = onRutChanged,
        label = { Text("RUT *") },
        leadingIcon = {
            Icon(Icons.Default.Badge, contentDescription = null)
        },
        placeholder = { Text("12345678-9") },
        isError = uiState.rutError != null,
        supportingText = uiState.rutError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    // Email *
    OutlinedTextField(
        value = uiState.email,
        onValueChange = onEmailChanged,
        label = { Text("Email *") },
        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = null)
        },
        placeholder = { Text("ejemplo@correo.com") },
        isError = uiState.emailError != null,
        supportingText = uiState.emailError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    // Teléfono
    OutlinedTextField(
        value = uiState.phone,
        onValueChange = onPhoneChanged,
        label = { Text("Teléfono") },
        leadingIcon = {
            Icon(Icons.Default.Phone, contentDescription = null)
        },
        placeholder = { Text("912345678") },
        isError = uiState.phoneError != null,
        supportingText = uiState.phoneError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    // Fecha de Nacimiento
    OutlinedTextField(
        value = uiState.birthDate,
        onValueChange = onBirthDateChanged,
        label = { Text("Fecha de Nacimiento") },
        leadingIcon = {
            Icon(Icons.Default.CalendarToday, contentDescription = null)
        },
        placeholder = { Text("DD/MM/YYYY") },
        isError = uiState.birthDateError != null,
        supportingText = uiState.birthDateError?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    // Dirección
    OutlinedTextField(
        value = uiState.address,
        onValueChange = onAddressChanged,
        label = { Text("Dirección") },
        leadingIcon = {
            Icon(Icons.Default.Home, contentDescription = null)
        },
        placeholder = { Text("Calle, número, comuna") },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onSubmit()
            }
        ),
        minLines = 2,
        maxLines = 3,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Diálogo de éxito
 */
@Composable
private fun SuccessDialog(
    userName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text("¡Registro Exitoso!")
        },
        text = {
            Text("El usuario $userName ha sido registrado correctamente en el sistema.")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}
