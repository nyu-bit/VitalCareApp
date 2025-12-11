package cl.duoc.app.ui.vitalsigns

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.data.api.SignosVitalesDto
import java.text.SimpleDateFormat
import java.util.*

/**
 * ============================================================
 * PANTALLA VITALES - COMPOSE
 * ============================================================
 *
 * Patrón MVVM: UI consume datos del ViewModel
 * Responsabilidad: Mostrar datos y capturar interacciones
 *
 * Flujo: UI ← VitalesViewModel ← Repository ← API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalesScreen(
    viewModel: VitalesViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    pacienteId: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (pacienteId.isNotEmpty()) {
            viewModel.loadByPaciente(pacienteId)
        } else {
            viewModel.loadAllVitales()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Signos Vitales", fontWeight = FontWeight.Bold)
                        Text(
                            "${uiState.vitales.size} registros",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh(pacienteId) }) {
                        Icon(Icons.Default.Refresh, "Refrescar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState(modifier = Modifier.fillMaxSize())
                }
                uiState.error != null -> {
                    ErrorState(
                        errorMessage = uiState.error!!,
                        onRetry = { viewModel.refresh(pacienteId) },
                        onDismiss = { viewModel.clearError() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.vitales.isEmpty() -> {
                    EmptyState(
                        message = "No hay signos vitales registrados",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    VitalesListContent(
                        vitales = uiState.vitales,
                        onVitalSelected = { viewModel.selectVital(it) },
                        onVitalDeleted = { viewModel.deleteVital(it.id ?: "") }
                    )
                }
            }
        }
    }
}

/**
 * Contenido: Lista de vitales
 */
@Composable
private fun VitalesListContent(
    vitales: List<SignosVitalesDto>,
    onVitalSelected: (SignosVitalesDto) -> Unit,
    onVitalDeleted: (SignosVitalesDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(vitales, key = { it.id ?: "" }) { vital ->
            VitalCard(
                vital = vital,
                onSelected = { onVitalSelected(vital) },
                onDeleted = { onVitalDeleted(vital) }
            )
        }
    }
}

/**
 * Tarjeta de un signo vital
 */
@Composable
private fun VitalCard(
    vital: SignosVitalesDto,
    onSelected: () -> Unit,
    onDeleted: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado: Fecha y acciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .marginBottom(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(vital.fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                IconButton(
                    onClick = onDeleted,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Grid de vitales: 2x2
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Fila 1: Frecuencia Cardíaca y Temperatura
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VitalMetric(
                        icon = Icons.Default.Favorite,
                        label = "Frecuencia",
                        value = "${vital.frecuenciaCardiaca}",
                        unit = "bpm",
                        modifier = Modifier.weight(1f),
                        riskLevel = getRiskLevel(vital.frecuenciaCardiaca, 60, 100)
                    )

                    VitalMetric(
                        icon = Icons.Default.Thermostat,
                        label = "Temperatura",
                        value = "%.1f".format(vital.temperatura),
                        unit = "°C",
                        modifier = Modifier.weight(1f),
                        riskLevel = getRiskLevel(vital.temperatura.toInt(), 36, 37)
                    )
                }

                // Fila 2: Presión Arterial y Saturación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VitalMetric(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "Presión",
                        value = vital.presionArterial,
                        unit = "mmHg",
                        modifier = Modifier.weight(1f),
                        riskLevel = "normal"
                    )

                    VitalMetric(
                        icon = Icons.Default.Air,
                        label = "O₂",
                        value = "${vital.saturacionOxigeno}",
                        unit = "%",
                        modifier = Modifier.weight(1f),
                        riskLevel = getRiskLevel(vital.saturacionOxigeno, 95, 100)
                    )
                }
            }

            // Notas si existen
            if (!vital.notas.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notas: ${vital.notas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Métrica individual de vital
 */
@Composable
private fun VitalMetric(
    icon: androidx.compose.material.icons.materialIcon,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    riskLevel: String = "normal"
) {
    val backgroundColor = when (riskLevel) {
        "danger" -> Color(0xFFFFCDD2)      // Rojo suave
        "warning" -> Color(0xFFFFF9C4)     // Amarillo suave
        else -> Color(0xFFC8E6C9)          // Verde suave
    }

    val iconColor = when (riskLevel) {
        "danger" -> Color(0xFFC62828)      // Rojo oscuro
        "warning" -> Color(0xFFF57C00)     // Naranja oscuro
        else -> Color(0xFF2E7D32)          // Verde oscuro
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Baseline
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/**
 * Estado: Cargando
 */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Cargando signos vitales...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Estado: Error
 */
@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Error al cargar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Descartar")
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Text("Reintentar")
            }
        }
    }
}

/**
 * Estado: Sin datos
 */
@Composable
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.HealthAndSafety,
            contentDescription = "Sin datos",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/**
 * Funciones auxiliares
 */
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("es", "ES"))
        val date = inputFormat.parse(dateString) ?: return dateString
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}

private fun getRiskLevel(value: Int, min: Int, max: Int): String {
    return when {
        value < min - 10 || value > max + 10 -> "danger"
        value < min || value > max -> "warning"
        else -> "normal"
    }
}

private fun getRiskLevel(value: Double, min: Double, max: Double): String {
    return when {
        value < min - 1 || value > max + 1 -> "danger"
        value < min || value > max -> "warning"
        else -> "normal"
    }
}

// Extensión para marginBottom (fix)
fun Modifier.marginBottom(value: Dp): Modifier = this.padding(bottom = value)

