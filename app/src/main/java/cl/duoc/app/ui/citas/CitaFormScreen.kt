package cl.duoc.app.ui.citas

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import cl.duoc.app.data.entity.Cita
import cl.duoc.app.ui.HomeViewModel
import cl.duoc.app.utils.Validators
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
    var estado by remember { mutableStateOf("pendiente") }
    
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var isFormVisible by remember { mutableStateOf(false) }
    
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
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val fechaValidation = Validators.validateDate(fecha)
                    val horaValidation = Validators.validateTime(hora)
                    
                    fechaError = fechaValidation.errorMessage
                    horaError = horaValidation.errorMessage
                    
                    val isFormValid = fechaValidation.isValid && horaValidation.isValid
                    
                    if (isFormValid) {
                        val cita = Cita(
                            pacienteId = 1, // TODO: Seleccionar paciente
                            especialidadId = 1, // TODO: Seleccionar especialidad
                            fecha = fecha,
                            hora = hora,
                            estado = estado,
                            observaciones = observaciones.trim()
                        )
                        
                        viewModel.viewModelScope.launch {
                            try {
                                viewModel.insertCita(cita)
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
                    } else {
                        snackbarMessage = "⚠️ Por favor corrija los errores en el formulario"
                        showSnackbar = true
                    }
                },
                icon = { Icon(Icons.Default.Check, "Guardar") },
                text = { Text("Guardar Cita") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        LaunchedEffect(showSnackbar) {
            if (showSnackbar) {
                snackbarHostState.showSnackbar(snackbarMessage)
                showSnackbar = false
            }
        }
        
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
                    singleLine = true,
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
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                // Campo Estado
                var expanded by remember { mutableStateOf(false) }
                val estados = listOf("pendiente", "confirmada", "completada", "cancelada")
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        estados.forEach { estadoOption ->
                            DropdownMenuItem(
                                text = { Text(estadoOption.uppercase()) },
                                onClick = {
                                    estado = estadoOption
                                    expanded = false
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
                    minLines = 3,
                    maxLines = 5
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "ℹ️ Nota: En futuras versiones se podrá seleccionar el paciente y la especialidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Animación de éxito
        if (showSuccessAnimation) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
                exit = scaleOut(animationSpec = tween(300)) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
