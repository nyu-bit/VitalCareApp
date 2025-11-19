package cl.duoc.app.ui.pacientes

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import cl.duoc.app.ui.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteDetailScreen(
    pacienteId: Long,
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var paciente by remember { mutableStateOf<cl.duoc.app.data.entity.Paciente?>(null) }
    
    LaunchedEffect(pacienteId) {
        delay(100)
        isVisible = true
        viewModel.viewModelScope.launch {
            paciente = viewModel.getPacienteById(pacienteId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Paciente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)) + 
                    slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { it / 4 }
                    )
        ) {
            if (paciente == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header con nombre
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "${paciente!!.nombre} ${paciente!!.apellido}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (paciente!!.activo) "✓ Activo" else "✗ Inactivo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (paciente!!.activo) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    // Información personal
                    InfoCard(
                        title = "Información Personal",
                        items = listOf(
                            "RUT" to paciente!!.rut,
                            "Fecha de Nacimiento" to paciente!!.fechaNacimiento,
                            "Dirección" to paciente!!.direccion
                        )
                    )
                    
                    // Información de contacto
                    InfoCard(
                        title = "Información de Contacto",
                        items = listOf(
                            "Email" to "📧 ${paciente!!.email}",
                            "Teléfono" to "📞 ${paciente!!.telefono}"
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$label:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
