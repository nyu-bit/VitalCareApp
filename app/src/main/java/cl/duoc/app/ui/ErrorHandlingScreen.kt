package cl.duoc.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.app.utils.ErrorHandler

/**
 * Pantalla de ejemplo que demuestra el manejo correcto de errores en la UI
 * Muestra diferentes estados: loading, success, error
 */
@Composable
fun ErrorHandlingScreen(
    viewModel: ErrorHandlingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Manejo de Errores") }
            )
        },
        snackbarHost = {
            // Mostrar Snackbar cuando hay éxito
            if (uiState.savedSuccessfully) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { /* Dismiss */ }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text("Guardado exitosamente")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mostrar error si existe
            uiState.error?.let { errorMessage ->
                ErrorCard(
                    message = errorMessage,
                    errorType = uiState.errorType,
                    onDismiss = { viewModel.clearError() }
                )
            }

            // Mostrar loading
            if (uiState.isLoading) {
                LoadingState()
            }

            // Mostrar contenido o estado vacío
            when {
                uiState.user != null -> {
                    UserContent(user = uiState.user!!)
                }
                !uiState.isLoading && uiState.error == null -> {
                    EmptyState()
                }
            }

            // Botones de acción
            ActionButtons(
                viewModel = viewModel,
                isLoading = uiState.isLoading
            )
        }
    }
}

/**
 * Componente que muestra un error de forma user-friendly
 */
@Composable
private fun ErrorCard(
    message: String,
    errorType: ErrorHandler.ErrorType?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (errorType) {
                ErrorHandler.ErrorType.VALIDATION -> MaterialTheme.colorScheme.tertiaryContainer
                ErrorHandler.ErrorType.NETWORK -> MaterialTheme.colorScheme.errorContainer
                ErrorHandler.ErrorType.DATABASE -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (errorType) {
                    ErrorHandler.ErrorType.VALIDATION -> Icons.Default.Warning
                    else -> Icons.Default.Error
                },
                contentDescription = "Error",
                tint = when (errorType) {
                    ErrorHandler.ErrorType.VALIDATION -> MaterialTheme.colorScheme.onTertiaryContainer
                    else -> MaterialTheme.colorScheme.onErrorContainer
                }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getErrorTitle(errorType),
                    style = MaterialTheme.typography.titleSmall,
                    color = when (errorType) {
                        ErrorHandler.ErrorType.VALIDATION -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (errorType) {
                        ErrorHandler.ErrorType.VALIDATION -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = when (errorType) {
                        ErrorHandler.ErrorType.VALIDATION -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }
    }
}

/**
 * Obtiene un título descriptivo según el tipo de error
 */
private fun getErrorTitle(errorType: ErrorHandler.ErrorType?): String {
    return when (errorType) {
        ErrorHandler.ErrorType.NETWORK -> "Error de Conexión"
        ErrorHandler.ErrorType.DATABASE -> "Error de Datos"
        ErrorHandler.ErrorType.VALIDATION -> "Validación"
        ErrorHandler.ErrorType.AUTHENTICATION -> "Autenticación"
        ErrorHandler.ErrorType.UNKNOWN -> "Error"
        null -> "Error"
    }
}

/**
 * Estado de carga
 */
@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = "Cargando...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Estado vacío
 */
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "No hay datos",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Presiona un botón para cargar datos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Muestra el contenido del usuario
 */
@Composable
private fun UserContent(
    user: cl.duoc.app.model.User,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Usuario Cargado",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "Nombre: ${user.name}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            user.email?.let {
                Text(
                    text = "Email: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            user.phone?.let {
                Text(
                    text = "Teléfono: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Botones de acción para demostrar diferentes escenarios
 */
@Composable
private fun ActionButtons(
    viewModel: ErrorHandlingViewModel,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { viewModel.loadUserBasic("user123") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Cargar Usuario (Try-Catch)")
        }

        Button(
            onClick = { viewModel.loadUserWithSafeCall("user123") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Cargar Usuario (SafeCall)")
        }

        OutlinedButton(
            onClick = { viewModel.loadUserBasic("") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Simular Error de Validación")
        }

        OutlinedButton(
            onClick = { viewModel.performComplexOperation("complex123") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Operación Compleja")
        }
    }
}

/**
 * Preview de la pantalla
 */
@Preview(showBackground = true)
@Composable
private fun ErrorHandlingScreenPreview() {
    MaterialTheme {
        // Preview requeriría un ViewModel mock
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ErrorCard(
                    message = "No se pudo conectar al servidor. Verifica tu conexión a internet.",
                    errorType = ErrorHandler.ErrorType.NETWORK,
                    onDismiss = {}
                )
                
                ErrorCard(
                    message = "El email ingresado no es válido. Por favor, verifica el formato.",
                    errorType = ErrorHandler.ErrorType.VALIDATION,
                    onDismiss = {}
                )
            }
        }
    }
}
