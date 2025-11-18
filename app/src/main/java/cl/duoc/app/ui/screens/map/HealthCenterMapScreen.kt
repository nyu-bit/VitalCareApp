package cl.duoc.app.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * Pantalla para visualizar la ubicación del centro de salud mental en un mapa
 * Permite al usuario ver el centro de salud y obtener direcciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthCenterMapScreen(
    viewModel: HealthCenterMapViewModel,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val healthCenter = uiState.healthCenter
    val userLocation = uiState.userLocation

    // Cargar ubicación del usuario al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUserLocation()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Centro de Salud Mental") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (healthCenter != null) {
                        IconButton(onClick = { viewModel.centerOnHealthCenter() }) {
                            Icon(Icons.Default.Navigation, contentDescription = "Centrar en centro")
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
                    // Mostrar indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                healthCenter != null -> {
                    // Mostrar mapa del centro
                    HealthCenterMapContent(
                        healthCenter = healthCenter,
                        userLocation = userLocation,
                        zoom = uiState.mapZoom
                    )
                }
                uiState.hasError -> {
                    // Mostrar error
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
                            uiState.errorMessage ?: "No se pudo cargar el centro de salud",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadHealthCenterLocation() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Contenido del mapa del centro de salud
 */
@Composable
fun HealthCenterMapContent(
    healthCenter: cl.duoc.app.model.HealthCenter,
    userLocation: cl.duoc.app.model.LocationData?,
    zoom: Float = 15f
) {
    val centerLocation = LatLng(healthCenter.latitude, healthCenter.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLocation, zoom)
    }

    // Actualizar zoom cuando cambia
    LaunchedEffect(zoom) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.zoomTo(zoom)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(bottom = 200.dp)
        ) {
            // Marcador del centro de salud
            Marker(
                state = rememberMarkerState(position = centerLocation),
                title = healthCenter.name,
                snippet = healthCenter.address
            )

            // Marcador de ubicación del usuario (si está disponible)
            if (userLocation != null) {
                val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)
                Marker(
                    state = rememberMarkerState(position = userLatLng),
                    title = "Mi ubicación",
                    snippet = "Estoy aquí"
                )
            }
        }

        // Panel de información en la parte inferior
        HealthCenterInfoPanel(
            healthCenter = healthCenter,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

/**
 * Panel de información del centro de salud
 */
@Composable
fun HealthCenterInfoPanel(
    healthCenter: cl.duoc.app.model.HealthCenter,
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
            Text(
                text = healthCenter.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = healthCenter.address,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (!healthCenter.schedule.isNullOrEmpty()) {
                Text(
                    text = "Horario: ${healthCenter.schedule}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Abrir en Google Maps */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = "Dirección"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dirección")
                }

                if (!healthCenter.phone.isNullOrEmpty()) {
                    Button(
                        onClick = { /* Llamar */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Llamar"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Llamar")
                    }
                }
            }
        }
    }
}

