package cl.duoc.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Pantalla para visualizar y editar el perfil del usuario autenticado
 * Permite al usuario ver sus datos personales y actualizarlos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    viewModel: UserProfileViewModel,
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadUserProfile(userId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (!uiState.isEditing && uiState.user != null) {
                        IconButton(onClick = { viewModel.enterEditMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    } else if (uiState.isEditing) {
                        IconButton(onClick = { viewModel.saveChanges() }) {
                            Icon(Icons.Default.Save, contentDescription = "Guardar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.user != null -> {
                    UserProfileContent(
                        uiState = uiState,
                        onFieldChange = { field, value ->
                            viewModel.updateField(field, value)
                        },
                        onCancel = { viewModel.cancelEdit() },
                        onSave = { viewModel.saveChanges() }
                    )
                }
                uiState.hasError -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Error al cargar perfil",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            uiState.errorMessage ?: "No se pudo cargar los datos",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUserProfile(userId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}
/**
 * Contenido de la pantalla de perfil
 */
@Composable
fun UserProfileContent(
    uiState: UserProfileUiState,
    onFieldChange: (String, String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val user = uiState.user ?: return
    val editedUser = uiState.editedUser ?: user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Mensajes de error y éxito
        if (uiState.hasError && uiState.errorMessage != null) {
            AlertCard(
                message = uiState.errorMessage,
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                textColor = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.successMessage != null) {
            AlertCard(
                message = uiState.successMessage,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                textColor = MaterialTheme.colorScheme.tertiary
            )
        }

        // Nombre
        OutlinedTextField(
            value = if (uiState.isEditing) editedUser.name else user.name,
            onValueChange = { onFieldChange("name", it) },
            label = { Text("Nombre Completo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            readOnly = !uiState.isEditing,
            isError = uiState.validationErrors.containsKey("name"),
            supportingText = {
                if (uiState.validationErrors.containsKey("name")) {
                    Text(uiState.validationErrors["name"] ?: "")
                }
            }
        )

        // Email
        OutlinedTextField(
            value = if (uiState.isEditing) (editedUser.email ?: "") else (user.email ?: ""),
            onValueChange = { onFieldChange("email", it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            readOnly = !uiState.isEditing,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = uiState.validationErrors.containsKey("email"),
            supportingText = {
                if (uiState.validationErrors.containsKey("email")) {
                    Text(uiState.validationErrors["email"] ?: "")
                }
            }
        )

        // Teléfono
        OutlinedTextField(
            value = if (uiState.isEditing) (editedUser.phone ?: "") else (user.phone ?: ""),
            onValueChange = { onFieldChange("phone", it) },
            label = { Text("Teléfono") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            readOnly = !uiState.isEditing,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = uiState.validationErrors.containsKey("phone"),
            supportingText = {
                if (uiState.validationErrors.containsKey("phone")) {
                    Text(uiState.validationErrors["phone"] ?: "")
                }
            }
        )

        // RUT
        OutlinedTextField(
            value = if (uiState.isEditing) (editedUser.rut ?: "") else (user.rut ?: ""),
            onValueChange = { onFieldChange("rut", it) },
            label = { Text("RUT") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            readOnly = !uiState.isEditing
        )

        // Fecha de Nacimiento
        OutlinedTextField(
            value = if (uiState.isEditing) (editedUser.birthDate ?: "") else (user.birthDate ?: ""),
            onValueChange = { onFieldChange("birthDate", it) },
            label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            readOnly = !uiState.isEditing,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Dirección
        OutlinedTextField(
            value = if (uiState.isEditing) (editedUser.address ?: "") else (user.address ?: ""),
            onValueChange = { onFieldChange("address", it) },
            label = { Text("Dirección") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            readOnly = !uiState.isEditing,
            minLines = 3
        )

        // Botones de acción
        if (uiState.isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = { /* viewModel.saveChanges() */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Card para mostrar alertas
 */
@Composable
fun AlertCard(
    message: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

