package cl.duoc.app.ui.pacientes

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import cl.duoc.app.data.entity.Paciente
import cl.duoc.app.ui.HomeViewModel
import cl.duoc.app.utils.Validators
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteFormScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var rut by remember { mutableStateOf("") }
    var rutError by remember { mutableStateOf<String?>(null) }
    
    var nombre by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf<String?>(null) }
    
    var apellido by remember { mutableStateOf("") }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    
    var telefono by remember { mutableStateOf("") }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    
    var fechaNacimiento by remember { mutableStateOf("") }
    var fechaError by remember { mutableStateOf<String?>(null) }
    
    var direccion by remember { mutableStateOf("") }
    var direccionError by remember { mutableStateOf<String?>(null) }
    
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
                title = { Text("Registrar Paciente") },
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
                    // Validar todos los campos
                    val rutValidation = Validators.validateRut(rut)
                    val nombreValidation = Validators.validateNotEmpty(nombre, "Nombre")
                    val apellidoValidation = Validators.validateNotEmpty(apellido, "Apellido")
                    val emailValidation = Validators.validateEmail(email)
                    val telefonoValidation = Validators.validatePhone(telefono)
                    val fechaValidation = Validators.validateDate(fechaNacimiento)
                    val direccionValidation = Validators.validateNotEmpty(direccion, "Dirección")
                    
                    rutError = rutValidation.errorMessage
                    nombreError = nombreValidation.errorMessage
                    apellidoError = apellidoValidation.errorMessage
                    emailError = emailValidation.errorMessage
                    telefonoError = telefonoValidation.errorMessage
                    fechaError = fechaValidation.errorMessage
                    direccionError = direccionValidation.errorMessage
                    
                    val isFormValid = rutValidation.isValid &&
                            nombreValidation.isValid &&
                            apellidoValidation.isValid &&
                            emailValidation.isValid &&
                            telefonoValidation.isValid &&
                            fechaValidation.isValid &&
                            direccionValidation.isValid
                    
                    if (isFormValid) {
                        val paciente = Paciente(
                            rut = rut.replace(".", "").replace("-", ""),
                            nombre = nombre.trim(),
                            apellido = apellido.trim(),
                            email = email.trim(),
                            telefono = telefono.trim(),
                            fechaNacimiento = fechaNacimiento,
                            direccion = direccion.trim(),
                            activo = true
                        )
                        
                        viewModel.viewModelScope.launch {
                            try {
                                viewModel.insertPaciente(paciente)
                                showSuccessAnimation = true
                                snackbarMessage = "✅ Paciente registrado exitosamente"
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
                text = { Text("Guardar Paciente") }
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
                    text = "Complete los datos del paciente",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Campo RUT con validación
            OutlinedTextField(
                value = rut,
                onValueChange = { 
                    rut = it
                    if (it.isNotBlank()) {
                        rutError = Validators.validateRut(it).errorMessage
                    }
                },
                label = { Text("RUT *") },
                placeholder = { Text("12345678-9") },
                isError = rutError != null,
                supportingText = {
                    if (rutError != null) {
                        Text(rutError!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Formato: 12345678-9")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { 
                    nombre = it
                    if (it.isNotBlank()) {
                        nombreError = Validators.validateNotEmpty(it, "Nombre").errorMessage
                    }
                },
                label = { Text("Nombre *") },
                isError = nombreError != null,
                supportingText = {
                    if (nombreError != null) {
                        Text(nombreError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Campo Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = { 
                    apellido = it
                    if (it.isNotBlank()) {
                        apellidoError = Validators.validateNotEmpty(it, "Apellido").errorMessage
                    }
                },
                label = { Text("Apellido *") },
                isError = apellidoError != null,
                supportingText = {
                    if (apellidoError != null) {
                        Text(apellidoError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    if (it.isNotBlank()) {
                        emailError = Validators.validateEmail(it).errorMessage
                    }
                },
                label = { Text("Email *") },
                placeholder = { Text("ejemplo@correo.com") },
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            // Campo Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { 
                    telefono = it
                    if (it.isNotBlank()) {
                        telefonoError = Validators.validatePhone(it).errorMessage
                    }
                },
                label = { Text("Teléfono *") },
                placeholder = { Text("+56912345678") },
                isError = telefonoError != null,
                supportingText = {
                    if (telefonoError != null) {
                        Text(telefonoError!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Formato: +56912345678")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            
            // Campo Fecha de Nacimiento
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { 
                    fechaNacimiento = it
                    if (it.isNotBlank()) {
                        fechaError = Validators.validateDate(it).errorMessage
                    }
                },
                label = { Text("Fecha de Nacimiento *") },
                placeholder = { Text("1990-05-15") },
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
            
            // Campo Dirección
            OutlinedTextField(
                value = direccion,
                onValueChange = { 
                    direccion = it
                    if (it.isNotBlank()) {
                        direccionError = Validators.validateNotEmpty(it, "Dirección").errorMessage
                    }
                },
                label = { Text("Dirección *") },
                placeholder = { Text("Av. Principal 123, Santiago") },
                isError = direccionError != null,
                supportingText = {
                    if (direccionError != null) {
                        Text(direccionError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
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
