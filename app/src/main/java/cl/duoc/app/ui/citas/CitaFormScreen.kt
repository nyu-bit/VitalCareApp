package cl.duoc.app.ui.citas

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.entity.Cita
import cl.duoc.app.data.entity.EstadoCita as EstadoEnum
import cl.duoc.app.ui.HomeViewModel
import cl.duoc.app.utils.Validators
import cl.duoc.app.utils.LocationHelper
import cl.duoc.app.utils.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaFormScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var fecha by remember { mutableStateOf("") }
    var fechaError by remember { mutableStateOf<String?>(null) }

    var hora by remember { mutableStateOf("") }
    var horaError by remember { mutableStateOf<String?>(null) }

    var observaciones by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(EstadoEnum.PENDIENTE) }
    var ubicacion by remember { mutableStateOf<String?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var isFormVisible by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val notificationHelper = remember { NotificationHelper(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        delay(100)
        isFormVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Cita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isFormVisible,
            enter = fadeIn(animationSpec = tween(400)) +
                    slideInVertically(
                        animationSpec = tween(400),
                        initialOffsetY = { it / 4 }
                    )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Complete los datos de la cita",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Campo Fecha
                OutlinedTextField(
                    value = fecha,
                    onValueChange = {
                        fecha = it
                        if (it.isNotBlank()) {
                            fechaError = Validators.validateDate(it).errorMessage
                        }
                    },
                    label = { Text("Fecha *") },
                    placeholder = { Text("2024-12-31") },
                    isError = fechaError != null,
                    supportingText = {
                        if (fechaError != null) {
                            Text(fechaError!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Formato: yyyy-MM-dd")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Campo Hora
                OutlinedTextField(
                    value = hora,
                    onValueChange = {
                        hora = it
                        if (it.isNotBlank()) {
                            horaError = Validators.validateTime(it).errorMessage
                        }
                    },
                    label = { Text("Hora *") },
                    placeholder = { Text("14:30") },
                    isError = horaError != null,
                    supportingText = {
                        if (horaError != null) {
                            Text(horaError!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Formato: HH:mm")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Dropdown de Estados
                ExposedDropdownMenuBox(
                    expanded = expandedEstado,
                    onExpandedChange = { expandedEstado = !expandedEstado }
                ) {
                    TextField(
                        value = estado.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false }
                    ) {
                        listOf(EstadoEnum.PENDIENTE, EstadoEnum.CONFIRMADA, EstadoEnum.COMPLETADA, EstadoEnum.CANCELADA).forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    estado = option
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }

                // Campo Observaciones
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Observaciones") },
                    placeholder = { Text("Notas adicionales sobre la cita...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Botón para obtener ubicación
                ElevatedButton(
                    onClick = {
                        isLoadingLocation = true
                        viewModel.viewModelScope.launch {
                            if (locationHelper.hasLocationPermission()) {
                                val location = locationHelper.getCurrentLocation()
                                ubicacion = location?.let {
                                    locationHelper.formatCoordinates(it)
                                }
                            }
                            isLoadingLocation = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoadingLocation
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.LocationOn, "Obtener ubicación")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Ubicación")
                }

                if (!ubicacion.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📍 $ubicacion",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón Guardar
                ElevatedButton(
                    onClick = {
                        val fechaValidation = Validators.validateDate(fecha)
                        val horaValidation = Validators.validateTime(hora)

                        fechaError = fechaValidation.errorMessage
                        horaError = horaValidation.errorMessage

                        val isFormValid = fechaValidation.isValid && horaValidation.isValid

                        if (isFormValid) {
                            val cita = Cita(
                                pacienteId = 1,
                                especialidadId = 1,
                                fecha = fecha,
                                hora = hora,
                                estado = estado,
                                motivo = "Cita médica",
                                observaciones = observaciones.trim(),
                                ubicacion = ubicacion
                            )

                            viewModel.viewModelScope.launch {
                                try {
                                    val citaId = viewModel.insertCita(cita)

                                    notificationHelper.showCitaConfirmationNotification(
                                        citaId = citaId,
                                        fecha = fecha,
                                        hora = hora
                                    )

                                    showSuccessAnimation = true
                                    snackbarMessage = "✅ Cita registrada exitosamente"
                                    showSnackbar = true
                                    delay(1500)
                                    onSaveSuccess()
                                } catch (e: Exception) {
                                    snackbarMessage = "❌ Error: ${e.message}"
                                    showSnackbar = true
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Check, "Guardar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Cita")
                }
            }
        }
    }
}

