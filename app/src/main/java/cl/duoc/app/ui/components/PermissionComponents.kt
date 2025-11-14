package cl.duoc.app.ui.components

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Componente para manejar solicitud de permisos de ubicación
 * Proporciona una interfaz amigable para solicitar permisos al usuario
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    content: @Composable () -> Unit
) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    when {
        locationPermissions.allPermissionsGranted -> {
            // Permisos otorgados - mostrar contenido
            onPermissionGranted()
            content()
        }
        locationPermissions.shouldShowRationale -> {
            // Mostrar diálogo explicativo
            PermissionRationaleDialog(
                title = "Permiso de Ubicación",
                message = "VitalCare necesita acceso a tu ubicación para mostrar mapas y tu posición en caso de emergencia.",
                onConfirm = {
                    locationPermissions.launchMultiplePermissionRequest()
                },
                onDismiss = {
                    onPermissionDenied()
                }
            )
        }
        else -> {
            // Primera solicitud - pedir permiso directamente
            LaunchedEffect(Unit) {
                locationPermissions.launchMultiplePermissionRequest()
            }

            content()
        }
    }
}

/**
 * Diálogo para solicitar ratificación de permiso
 */
@Composable
fun PermissionRationaleDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Permitir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Rechazar")
            }
        }
    )
}

/**
 * Componente que muestra un estado cuando faltan permisos
 */
@Composable
fun PermissionDeniedContent(
    title: String = "Permiso Denegado",
    message: String = "Se requieren permisos de ubicación para usar esta funcionalidad",
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

/**
 * Componente para mostrar el estado de carga de ubicación
 */
@Composable
fun LocationLoadingContent(
    message: String = "Obteniendo ubicación..."
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

