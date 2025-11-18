package cl.duoc.app.ui.screens.sos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.SOSEvent
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.android.gms.maps.CameraUpdateFactory

/**
 * Pantalla para activar SOS y visualizar historial
 * Permite a tutores recibir notificaciones y actuar ante emergencias
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    userId: String,
    viewModel: SOSViewModel,
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadSOSHistory(userId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SOS y Emergencias") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                SOSContent(
                    uiState = uiState,
                    onSOSTriggered = { viewModel.triggerSOS() },
                    onSOSAcknowledged = { eventId -> viewModel.acknowledgeSOS(eventId) },
                    onSOSResolved = { eventId -> viewModel.resolveSOSEvent(eventId) }
                )
            }
        }
    }
}

/**
 * Contenido principal de la pantalla SOS
 */
@Composable
fun SOSContent(
    uiState: SOSUiState,
    onSOSTriggered: () -> Unit,
    onSOSAcknowledged: (String) -> Unit,
    onSOSResolved: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Botón SOS de emergencia
        SOSEmergencyButton(
            isActive = uiState.isSosActive,
            isTriggering = uiState.isTriggering,
            onClick = onSOSTriggered,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Mensajes de éxito y error
        if (uiState.successMessage != null) {
            AlertCard(
                message = uiState.successMessage,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                textColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        if (uiState.hasError && uiState.errorMessage != null) {
            AlertCard(
                message = uiState.errorMessage,
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                textColor = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Último evento SOS
        if (uiState.lastSOSEvent != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.lastSOSEvent.status == "TRIGGERED")
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                SOSEventCard(
                    event = uiState.lastSOSEvent,
                    onAcknowledge = { onSOSAcknowledged(uiState.lastSOSEvent.id) },
                    onResolve = { onSOSResolved(uiState.lastSOSEvent.id) }
                )
            }
        }

        // Historial de eventos SOS
        Text(
            text = "Historial de SOS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp, 8.dp)
        )

        if (uiState.sosHistory.isEmpty()) {
            EmptySOSHistory(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.sosHistory) { event ->
                    SOSHistoryEventCard(event)
                }
            }
        }
    }
}

/**
 * Botón SOS de emergencia (grande y destacado)
 */
@Composable
fun SOSEmergencyButton(
    isActive: Boolean,
    isTriggering: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(120.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
            contentColor = Color.White
        ),
        enabled = !isTriggering,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isTriggering) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Text(
                    text = "SOS",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "PRESIONA PARA EMERGENCIA",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Card para mostrar evento SOS actual
 */
@Composable
fun SOSEventCard(
    event: SOSEvent,
    onAcknowledge: () -> Unit = {},
    onResolve: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Evento SOS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Estado: ${event.status}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ubicación: ${String.format("%.4f", event.location.latitude)}, " +
                "${String.format("%.4f", event.location.longitude)}",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Hora: ${formatSOSTime(event.timestamp)}",
            style = MaterialTheme.typography.bodySmall
        )

        if (!event.tutorNotified) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAcknowledge,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Reconocer")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reconocer")
                }

                Button(
                    onClick = onResolve,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Resolver")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resolver")
                }
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "✓ Tutor notificado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Card para evento en el historial
 */
@Composable
fun SOSHistoryEventCard(event: SOSEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (event.status) {
                "TRIGGERED" -> MaterialTheme.colorScheme.errorContainer
                "ACKNOWLEDGED" -> MaterialTheme.colorScheme.primaryContainer
                "RESOLVED" -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SOS - ${event.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatSOSTime(event.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = when (event.status) {
                    "TRIGGERED" -> Icons.Default.Call
                    "ACKNOWLEDGED" -> Icons.Default.Check
                    else -> Icons.Default.Close
                },
                contentDescription = event.status
            )
        }
    }
}

/**
 * Componente para mostrar cuando no hay historial
 */
@Composable
fun EmptySOSHistory(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sin eventos de SOS",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

/**
 * Card de alerta
 */
@Composable
fun AlertCard(
    message: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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

/**
 * Formatea un timestamp a hora legible
 */
fun formatSOSTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
