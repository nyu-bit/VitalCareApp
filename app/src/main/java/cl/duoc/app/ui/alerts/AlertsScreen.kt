package cl.duoc.app.ui.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.Alert
import cl.duoc.app.utils.FormatUtils

/**
 * Pantalla de Historial de Alertas (HU-15)
 * 
 * Muestra:
 * - Lista de alertas ordenadas por fecha
 * - Filtros por severidad, tipo y estado
 * - Indicadores visuales de severidad
 * - Acciones: marcar como leída, eliminar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel,
    onNavigateBack: () -> Unit = {},
    onAlertClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Alertas")
                        if (uiState.unreadCount > 0) {
                            Text(
                                text = "${uiState.unreadCount} sin leer",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Botón de marcar todas como leídas
                    if (uiState.unreadCount > 0) {
                        IconButton(onClick = { viewModel.markAllAsRead() }) {
                            Icon(Icons.Default.DoneAll, "Marcar todas como leídas")
                        }
                    }
                    
                    // Botón de refrescar
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refrescar")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorView(
                    error = uiState.error!!,
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.allAlerts.isEmpty() -> {
                EmptyView(modifier = Modifier.padding(paddingValues))
            }
            else -> {
                AlertsContent(
                    uiState = uiState,
                    onAlertClick = onAlertClick,
                    onMarkAsRead = viewModel::markAsRead,
                    onDeleteAlert = viewModel::deleteAlert,
                    onSeverityFilterSelected = viewModel::filterBySeverity,
                    onTypeFilterSelected = viewModel::filterByType,
                    onToggleUnreadFilter = { viewModel.filterByReadStatus(!uiState.showOnlyUnread) },
                    onClearFilters = viewModel::clearFilters,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Contenido principal con filtros y lista
 */
@Composable
private fun AlertsContent(
    uiState: AlertsUiState,
    onAlertClick: (String) -> Unit,
    onMarkAsRead: (String) -> Unit,
    onDeleteAlert: (String) -> Unit,
    onSeverityFilterSelected: (String?) -> Unit,
    onTypeFilterSelected: (String?) -> Unit,
    onToggleUnreadFilter: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filtros de severidad
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SeverityFilterChips(
                selectedSeverity = uiState.selectedSeverityFilter,
                onSeveritySelected = onSeverityFilterSelected,
                criticalCount = uiState.criticalCount,
                highCount = uiState.highCount,
                mediumCount = uiState.mediumCount
            )
        }

        // Filtros de tipo
        item {
            TypeFilterChips(
                selectedType = uiState.selectedTypeFilter,
                onTypeSelected = onTypeFilterSelected,
                vitalSignsCount = uiState.vitalSignsCount,
                appointmentCount = uiState.appointmentCount,
                medicationCount = uiState.medicationCount,
                systemCount = uiState.systemCount
            )
        }

        // Filtro de no leídas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = uiState.showOnlyUnread,
                    onClick = onToggleUnreadFilter,
                    label = { Text("Solo no leídas (${uiState.unreadCount})") },
                    leadingIcon = if (uiState.showOnlyUnread) {
                        { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                    } else null
                )

                if (uiState.selectedSeverityFilter != null ||
                    uiState.selectedTypeFilter != null ||
                    uiState.showOnlyUnread) {
                    TextButton(onClick = onClearFilters) {
                        Text("Limpiar filtros")
                    }
                }
            }
        }

        // Contador de resultados
        item {
            Text(
                text = "${uiState.filteredAlerts.size} alertas",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Lista de alertas
        items(
            items = uiState.filteredAlerts,
            key = { it.id }
        ) { alert ->
            AlertItem(
                alert = alert,
                onClick = { onAlertClick(alert.id) },
                onMarkAsRead = { onMarkAsRead(alert.id) },
                onDelete = { onDeleteAlert(alert.id) },
                modifier = Modifier.animateItem()
            )
        }

        // Espaciado final
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Chips de filtro por severidad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeverityFilterChips(
    selectedSeverity: String?,
    onSeveritySelected: (String?) -> Unit,
    criticalCount: Int,
    highCount: Int,
    mediumCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filtrar por severidad:",
            style = MaterialTheme.typography.labelLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedSeverity == null,
                onClick = { onSeveritySelected(null) },
                label = { Text("Todas") }
            )

            if (criticalCount > 0) {
                FilterChip(
                    selected = selectedSeverity == "Crítico",
                    onClick = { onSeveritySelected("Crítico") },
                    label = { Text("Crítico ($criticalCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.error
                    )
                )
            }

            if (highCount > 0) {
                FilterChip(
                    selected = selectedSeverity == "Alto",
                    onClick = { onSeveritySelected("Alto") },
                    label = { Text("Alto ($highCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                )
            }

            if (mediumCount > 0) {
                FilterChip(
                    selected = selectedSeverity == "Medio",
                    onClick = { onSeveritySelected("Medio") },
                    label = { Text("Medio ($mediumCount)") }
                )
            }
        }
    }
}

/**
 * Chips de filtro por tipo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeFilterChips(
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    vitalSignsCount: Int,
    appointmentCount: Int,
    medicationCount: Int,
    systemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filtrar por tipo:",
            style = MaterialTheme.typography.labelLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = { Text("Todos") }
            )

            if (vitalSignsCount > 0) {
                FilterChip(
                    selected = selectedType == "Signos Vitales",
                    onClick = { onTypeSelected("Signos Vitales") },
                    label = { Text("Signos ($vitalSignsCount)") }
                )
            }

            if (appointmentCount > 0) {
                FilterChip(
                    selected = selectedType == "Cita",
                    onClick = { onTypeSelected("Cita") },
                    label = { Text("Citas ($appointmentCount)") }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (medicationCount > 0) {
                FilterChip(
                    selected = selectedType == "Medicamento",
                    onClick = { onTypeSelected("Medicamento") },
                    label = { Text("Medicamentos ($medicationCount)") }
                )
            }

            if (systemCount > 0) {
                FilterChip(
                    selected = selectedType == "Sistema",
                    onClick = { onTypeSelected("Sistema") },
                    label = { Text("Sistema ($systemCount)") }
                )
            }
        }
    }
}

/**
 * Card individual de alerta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertCard(
    alert: Alert,
    onClick: () -> Unit,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = when {
                !alert.isRead && alert.severity == "Crítico" -> MaterialTheme.colorScheme.errorContainer
                !alert.isRead && alert.severity == "Alto" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                !alert.isRead -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = FormatUtils.formatDateTime(alert.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Alias para AlertCard para compatibilidad
 */
@Composable
private fun AlertItem(
    alert: Alert,
    onClick: () -> Unit,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertCard(
        alert = alert,
        onClick = onClick,
        onMarkAsRead = onMarkAsRead,
        onDelete = onDelete,
        modifier = modifier
    )
}

/**
 * Vista de carga
 */
@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Vista de error
 */
@Composable
private fun ErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

/**
 * Vista vacía
 */
@Composable
private fun EmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "No hay alertas",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "No hay alertas para mostrar en este momento",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
