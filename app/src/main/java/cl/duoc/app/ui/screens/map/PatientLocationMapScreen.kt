package cl.duoc.app.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

/**
 * Pantalla para visualizar la ubicación del paciente en un mapa (para tutores)
 * Permite al tutor ver dónde se encuentra el paciente en caso de emergencia
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientLocationMapScreen(
    patientId: String,
    patientName: String = "Paciente",
    viewModel: PatientLocationMapViewModel,
    onBackClick: () -> Unit = {}
) {
    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty()) {
            viewModel.loadPatientLocation(patientId, patientName)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicación - ${uiState.patientName}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshLocation(patientId) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
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
                uiState.patientLocation != null -> {
                    val patientLocation = uiState.patientLocation
                    if (patientLocation != null) {
                        PatientLocationMapContent(
                            location = patientLocation,
                            patientName = uiState.patientName,
                            isSimulated = uiState.isSimulated,
                            zoom = uiState.mapZoom,
                            onZoomIn = { viewModel.zoomIn() },
                            onZoomOut = { viewModel.zoomOut() },
                            onCenterPatient = { viewModel.centerOnPatient() }
                        )
                    }
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
                            "Error",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            uiState.errorMessage ?: "No se pudo cargar la ubicación",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPatientLocation(patientId, patientName) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Contenido del mapa de ubicación del paciente
 */
@Composable
fun PatientLocationMapContent(
    location: cl.duoc.app.model.LocationData,
    patientName: String,
    isSimulated: Boolean,
    zoom: Float = 16f,
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
    onCenterPatient: () -> Unit = {}
) {
    val patientLatLng = LatLng(location.latitude, location.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(patientLatLng, zoom)
    }

    LaunchedEffect(zoom) {
        cameraPositionState.animate(
            update = com.google.android.gms.maps.model.CameraUpdateFactory.zoomTo(zoom)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            // Marcador del paciente
            Marker(
                state = rememberMarkerState(position = patientLatLng),
                title = patientName,
                snippet = "Última ubicación conocida"
            )
        }

        // Botones de control de zoom
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = onZoomIn,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Acercar")
            }

            FloatingActionButton(
                onClick = onZoomOut,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Alejar")
            }
        }

        // Panel de información en la parte inferior
        PatientLocationInfoPanel(
            patientName = patientName,
            location = location,
            isSimulated = isSimulated,
            onCenterPatient = onCenterPatient,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

/**
 * Panel de información de ubicación del paciente
 */
@Composable
fun PatientLocationInfoPanel(
    patientName: String,
    location: cl.duoc.app.model.LocationData,
    isSimulated: Boolean,
    onCenterPatient: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patientName,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    if (isSimulated) {
                        Text(
                            text = "Ubicación simulada",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Coordenadas: ${String.format(Locale.US, "%.4f", location.latitude)}, ${String.format(Locale.US, "%.4f", location.longitude)}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Precisión: ${String.format(Locale.US, "%.1f", location.accuracy)} metros",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCenterPatient,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text("Centrar en paciente")
            }
        }
    }
}

