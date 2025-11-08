package cl.duoc.app.ui.vitalsigns

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
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.VitalSigns
import cl.duoc.app.utils.FormatUtils
import cl.duoc.app.utils.Constants

/**
 * Pantalla de Visualización de Signos Vitales (HU-03)
 * 
 * Muestra:
 * - Lista de signos vitales con nivel de riesgo
 * - Estadísticas generales
 * - Filtros por nivel de riesgo
 * - Indicadores visuales de estado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalSignsScreen(
    viewModel: VitalSignsViewModel,
    userId: String,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar datos al montar la pantalla
    LaunchedEffect(userId) {
        viewModel.loadVitalSigns(userId)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Signos Vitales") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh(userId) }) {
                        Icon(Icons.Default.Refresh, "Refrescar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Agregar nuevo registro */ },
                icon = { Icon(Icons.Default.Add, "Agregar") },
                text = { Text("Nuevo Registro") }
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
                    onRetry = { viewModel.loadVitalSigns(userId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.vitalSignsList.isEmpty() -> {
                EmptyView(modifier = Modifier.padding(paddingValues))
            }
            else -> {
                VitalSignsContent(
                    uiState = uiState,
                    onRiskFilterSelected = viewModel::filterByRiskLevel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Contenido principal con estadísticas y lista
 */
@Composable
private fun VitalSignsContent(
    uiState: VitalSignsUiState,
    onRiskFilterSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Estadísticas
        uiState.statistics?.let { stats ->
            item {
                StatisticsCard(statistics = stats)
            }
        }

        // Filtros
        item {
            RiskFilterChips(
                selectedFilter = uiState.selectedRiskFilter,
                onFilterSelected = onRiskFilterSelected,
                totalCount = uiState.vitalSignsList.size,
                criticalCount = uiState.vitalSignsList.count { it.riskLevel == "Crítico" },
                highCount = uiState.vitalSignsList.count { it.riskLevel == "Alto" },
                mediumCount = uiState.vitalSignsList.count { it.riskLevel == "Medio" },
                lowCount = uiState.vitalSignsList.count { it.riskLevel == "Bajo" }
            )
        }

        // Header de lista
        item {
            Text(
                text = "${uiState.filteredVitalSigns.size} registros",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Lista de signos vitales
        items(uiState.filteredVitalSigns) { item ->
            VitalSignsCard(
                vitalSignsWithRisk = item,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Card con estadísticas generales
 */
@Composable
private fun StatisticsCard(
    statistics: VitalSignsStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Estadísticas Generales",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Divider()

            // Grid de estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                statistics.averageHeartRate?.let {
                    StatItem(
                        label = "FC Promedio",
                        value = FormatUtils.formatHeartRate(it),
                        icon = Icons.Default.Favorite
                    )
                }

                statistics.averageOxygenSaturation?.let {
                    StatItem(
                        label = "SpO2 Promedio",
                        value = FormatUtils.formatOxygenSaturation(it),
                        icon = Icons.Default.Air
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (statistics.averageSystolic != null && statistics.averageDiastolic != null) {
                    StatItem(
                        label = "PA Promedio",
                        value = FormatUtils.formatBloodPressure(
                            statistics.averageSystolic,
                            statistics.averageDiastolic
                        ),
                        icon = Icons.Default.MonitorHeart
                    )
                }

                statistics.averageTemperature?.let {
                    StatItem(
                        label = "Temp. Promedio",
                        value = "${String.format("%.1f", it)}°C",
                        icon = Icons.Default.Thermostat
                    )
                }
            }

            Text(
                text = "Basado en ${statistics.totalRecords} registros",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Item de estadística
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Chips de filtro por nivel de riesgo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RiskFilterChips(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    totalCount: Int,
    criticalCount: Int,
    highCount: Int,
    mediumCount: Int,
    lowCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filtrar por nivel de riesgo:",
            style = MaterialTheme.typography.labelLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterSelected(null) },
                label = { Text("Todos ($totalCount)") },
                leadingIcon = if (selectedFilter == null) {
                    { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                } else null
            )

            if (criticalCount > 0) {
                FilterChip(
                    selected = selectedFilter == "Crítico",
                    onClick = { onFilterSelected("Crítico") },
                    label = { Text("Crítico ($criticalCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.error
                    )
                )
            }

            if (highCount > 0) {
                FilterChip(
                    selected = selectedFilter == "Alto",
                    onClick = { onFilterSelected("Alto") },
                    label = { Text("Alto ($highCount)") }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (mediumCount > 0) {
                FilterChip(
                    selected = selectedFilter == "Medio",
                    onClick = { onFilterSelected("Medio") },
                    label = { Text("Medio ($mediumCount)") }
                )
            }

            if (lowCount > 0) {
                FilterChip(
                    selected = selectedFilter == "Bajo",
                    onClick = { onFilterSelected("Bajo") },
                    label = { Text("Bajo ($lowCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}

/**
 * Card individual de signos vitales
 */
@Composable
private fun VitalSignsCard(
    vitalSignsWithRisk: VitalSignsWithRisk,
    modifier: Modifier = Modifier
) {
    val vitalSigns = vitalSignsWithRisk.vitalSigns
    val riskLevel = vitalSignsWithRisk.riskLevel

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (riskLevel) {
                "Crítico" -> MaterialTheme.colorScheme.errorContainer
                "Alto" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header con fecha y nivel de riesgo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = FormatUtils.formatDateTime(vitalSigns.timestamp),
                    style = MaterialTheme.typography.titleSmall
                )

                Badge(
                    containerColor = when (riskLevel) {
                        "Crítico" -> MaterialTheme.colorScheme.error
                        "Alto" -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        "Medio" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                ) {
                    Text(riskLevel)
                }
            }

            Divider()

            // Signos vitales
            VitalSignRow(
                icon = Icons.Default.MonitorHeart,
                label = "Presión Arterial",
                value = vitalSigns.bloodPressureSystolic?.let { sys ->
                    vitalSigns.bloodPressureDiastolic?.let { dia ->
                        FormatUtils.formatBloodPressure(sys, dia)
                    }
                } ?: "N/A"
            )

            VitalSignRow(
                icon = Icons.Default.Favorite,
                label = "Frecuencia Cardíaca",
                value = vitalSigns.heartRate?.let { 
                    FormatUtils.formatHeartRate(it) 
                } ?: "N/A"
            )

            VitalSignRow(
                icon = Icons.Default.Air,
                label = "Saturación de Oxígeno",
                value = vitalSigns.oxygenSaturation?.let { 
                    FormatUtils.formatOxygenSaturation(it) 
                } ?: "N/A"
            )

            vitalSigns.temperature?.let { temp ->
                VitalSignRow(
                    icon = Icons.Default.Thermostat,
                    label = "Temperatura",
                    value = "${String.format("%.1f", temp)}°C"
                )
            }

            vitalSigns.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    Text(
                        text = "Notas: $notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Fila de signo vital
 */
@Composable
private fun VitalSignRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
    }
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
            imageVector = Icons.Default.HealthAndSafety,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "No hay registros de signos vitales",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Agrega tu primer registro usando el botón +",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
