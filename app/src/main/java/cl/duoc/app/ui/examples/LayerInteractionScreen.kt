package cl.duoc.app.ui.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.User
import cl.duoc.app.model.VitalSigns
import cl.duoc.app.utils.FormatUtils

/**
 * EJEMPLO COMPLETO: Cómo la UI interactúa con el ViewModel
 * 
 * Esta pantalla demuestra:
 * 1. Cómo observar el estado del ViewModel con collectAsState()
 * 2. Cómo llamar funciones del ViewModel en respuesta a eventos de UI
 * 3. Cómo reaccionar a cambios de estado (loading, error, success)
 * 4. Cómo mostrar diferentes vistas según el estado
 * 
 * FLUJO DE INTERACCIÓN:
 * 1. Usuario interactúa con UI (click, input, etc.)
 * 2. UI llama a función del ViewModel
 * 3. ViewModel procesa (llama UseCase -> Repository)
 * 4. ViewModel actualiza StateFlow
 * 5. Compose detecta cambio automáticamente
 * 6. UI se recompone mostrando nueva información
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerInteractionScreen(
    viewModel: LayerInteractionViewModel,
    userId: String,
    modifier: Modifier = Modifier
) {
    // PASO 1: OBSERVAR EL ESTADO
    // collectAsState() convierte Flow en State que Compose puede observar
    // Cada vez que el ViewModel actualiza _uiState, esta UI se recompone automáticamente
    val uiState by viewModel.uiState.collectAsState()

    // PASO 2: EFECTOS LATERALES
    // LaunchedEffect se ejecuta cuando el composable entra en la composición
    // Es el lugar correcto para cargar datos iniciales
    LaunchedEffect(userId) {
        // Llamar al ViewModel para cargar datos
        // Esto inicia el flujo: UI -> ViewModel -> UseCase -> Repository
        viewModel.loadUserDashboard(userId)
        
        // También podemos suscribirnos a cambios en tiempo real
        viewModel.observeUserVitalSigns(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interacción entre Capas") },
                actions = {
                    // EJEMPLO: Botón que llama al ViewModel
                    IconButton(
                        onClick = { 
                            // UI -> ViewModel
                            viewModel.refreshAllData(userId) 
                        },
                        enabled = !uiState.isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refrescar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // PASO 3: RENDERIZADO CONDICIONAL BASADO EN ESTADO
        // La UI reacciona al estado del ViewModel
        when {
            // Estado: Cargando
            uiState.isLoading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            
            // Estado: Error
            uiState.error != null -> {
                ErrorView(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadUserDashboard(userId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            // Estado: Datos cargados
            uiState.currentUser != null -> {
                DashboardContent(
                    uiState = uiState,
                    onSaveUser = { name, email, phone ->
                        // UI -> ViewModel -> UseCase -> Repository
                        viewModel.saveUser(name, email, phone)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            // Estado: Vacío
            else -> {
                EmptyView(modifier = Modifier.padding(paddingValues))
            }
        }
    }
}

/**
 * Vista de carga que se muestra mientras el ViewModel procesa datos
 */
@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Cargando datos...",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "ViewModel → UseCase → Repository → Room",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Vista de error que permite reintentar
 */
@Composable
private fun ErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
            
            Text(
                text = "El error se propagó desde Repository → UseCase → ViewModel → UI",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Contenido principal del dashboard
 * Muestra cómo múltiples componentes observan diferentes partes del estado
 */
@Composable
private fun DashboardContent(
    uiState: InteractionUiState,
    onSaveUser: (String, String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sección: Información del Usuario
        item {
            UserInfoCard(user = uiState.currentUser!!)
        }

        // Sección: Estadísticas (si existen)
        uiState.statistics?.let { stats ->
            item {
                StatisticsCard(
                    statistics = stats,
                    averageRiskLevel = uiState.averageRiskLevel
                )
            }
        }

        // Sección: Signos Vitales Recientes
        item {
            Text(
                text = "Signos Vitales Recientes (${uiState.totalVitalSignsCount} total)",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // EJEMPLO: Mostrar indicador de carga para sección específica
        if (uiState.isLoadingVitalSigns) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        } else {
            // Lista de signos vitales
            items(uiState.recentVitalSigns) { vitalSigns ->
                VitalSignsCard(
                    vitalSigns = vitalSigns,
                    riskLevel = uiState.riskLevels[vitalSigns]
                )
            }
        }

        // Indicador de refrescando
        if (uiState.isRefreshing) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Última actualización
        uiState.lastRefreshTime?.let { timestamp ->
            item {
                Text(
                    text = "Última actualización: ${FormatUtils.formatDateTime(timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Card que muestra información del usuario
 * Observa uiState.currentUser
 */
@Composable
private fun UserInfoCard(
    user: User,
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
                text = "Información del Usuario",
                style = MaterialTheme.typography.titleMedium
            )
            
            Divider()
            
            InfoRow(label = "ID", value = user.id)
            InfoRow(label = "Nombre", value = user.name)
            user.email?.let { InfoRow(label = "Email", value = it) }
            user.phone?.let { InfoRow(label = "Teléfono", value = it) }
            
            Text(
                text = "Datos obtenidos desde: Room DB → Repository → UseCase → ViewModel → UI",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Card de estadísticas
 */
@Composable
private fun StatisticsCard(
    statistics: VitalSignsStatistics,
    averageRiskLevel: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Registros",
                    value = statistics.totalRecords.toString()
                )
                StatItem(
                    label = "FC Promedio",
                    value = "${statistics.averageHeartRate} bpm"
                )
                StatItem(
                    label = "SpO2 Promedio",
                    value = "${statistics.averageOxygenSaturation}%"
                )
            }
            
            averageRiskLevel?.let {
                Text(
                    text = "Nivel de Riesgo Promedio: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (it) {
                        "Crítico" -> MaterialTheme.colorScheme.error
                        "Alto" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            }
            
            Text(
                text = "Calculado por UseCase con datos de Repository",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Card de signos vitales individual
 */
@Composable
private fun VitalSignsCard(
    vitalSigns: VitalSigns,
    riskLevel: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
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
                    text = FormatUtils.formatDateTime(vitalSigns.timestamp),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = buildString {
                        vitalSigns.bloodPressureSystolic?.let { sys ->
                            vitalSigns.bloodPressureDiastolic?.let { dia ->
                                append("PA: ${FormatUtils.formatBloodPressure(sys, dia)} | ")
                            }
                        }
                        vitalSigns.heartRate?.let { 
                            append("FC: ${FormatUtils.formatHeartRate(it)} | ")
                        }
                        vitalSigns.oxygenSaturation?.let { 
                            append("SpO2: ${FormatUtils.formatOxygenSaturation(it)}")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            riskLevel?.let {
                Badge(
                    containerColor = when (it) {
                        "Crítico" -> MaterialTheme.colorScheme.error
                        "Alto" -> MaterialTheme.colorScheme.error
                        "Medio" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

/**
 * Componentes auxiliares
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay datos disponibles",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
