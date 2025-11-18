package cl.duoc.app.ui.dashboard

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.app.ui.animations.LottieHeartbeat
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Nivel de alerta para signos vitales
 */
enum class AlertLevel {
    NORMAL,      // Verde - Estable
    WARNING,     // Amarillo - Observación
    DANGER       // Rojo - Alerta
}

/**
 * Datos de un signo vital
 */
data class VitalSignData(
    val title: String,
    val value: String,
    val unit: String,
    val icon: ImageVector,
    val alertLevel: AlertLevel
)

/**
 * Pantalla de Dashboard principal
 * HU-11: Visualización de signos vitales del paciente
 * HU-08: Con animaciones visuales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Generar signos vitales simulados
    val vitalSigns = remember { generateSimulatedVitalSigns() }
    
    // Estado para controlar la visibilidad de las tarjetas con animación
    var cardsVisible by remember { mutableStateOf(false) }
    
    // Animar la aparición de las tarjetas al entrar
    LaunchedEffect(Unit) {
        delay(100) // Pequeño delay para una mejor experiencia
        cardsVisible = true
    }
    
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Dashboard",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Animación Lottie en la parte superior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieHeartbeat(
                        modifier = Modifier.size(100.dp)
                    )
                }
                
                // Encabezado
                Text(
                    text = "Estado General del Paciente",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Text(
                    text = "Signos vitales más recientes",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Tarjetas de signos vitales con animación
                vitalSigns.forEachIndexed { index, vitalSign ->
                    AnimatedVisibility(
                        visible = cardsVisible,
                        enter = fadeIn() + expandVertically()
                    ) {
                        VitalSignCard(
                            vitalSignData = vitalSign,
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Detalle de ${vitalSign.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
                
                // Información adicional
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Los valores se actualizan automáticamente cada 5 minutos",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual de signo vital
 */
@Composable
fun VitalSignCard(
    vitalSignData: VitalSignData,
    onClick: () -> Unit
) {
    val alertColor = when (vitalSignData.alertLevel) {
        AlertLevel.NORMAL -> Color(0xFF4CAF50)  // Verde
        AlertLevel.WARNING -> Color(0xFFFFC107) // Amarillo
        AlertLevel.DANGER -> Color(0xFFF44336)  // Rojo
    }
    
    val alertText = when (vitalSignData.alertLevel) {
        AlertLevel.NORMAL -> "Estable"
        AlertLevel.WARNING -> "Observación"
        AlertLevel.DANGER -> "Alerta"
    }
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vitalSignData.icon,
                            contentDescription = vitalSignData.title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = vitalSignData.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "${vitalSignData.value} ${vitalSignData.unit}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Estado de alerta
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = alertColor
                ) {}
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = alertText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = alertColor
                )
            }
        }
    }
}

/**
 * Genera signos vitales simulados con valores aleatorios
 */
fun generateSimulatedVitalSigns(): List<VitalSignData> {
    // Frecuencia cardíaca (60-100 normal, 45-60 o 100-120 warning, <45 o >120 danger)
    val heartRate = Random.nextInt(50, 130)
    val heartRateAlert = when {
        heartRate in 60..100 -> AlertLevel.NORMAL
        heartRate in 45..60 || heartRate in 100..120 -> AlertLevel.WARNING
        else -> AlertLevel.DANGER
    }
    
    // Presión arterial sistólica (90-120 normal, 120-140 o 80-90 warning, <80 o >140 danger)
    val systolic = Random.nextInt(75, 155)
    val diastolic = Random.nextInt(50, 95)
    val bloodPressureAlert = when {
        systolic in 90..120 && diastolic in 60..80 -> AlertLevel.NORMAL
        systolic in 80..90 || systolic in 120..140 -> AlertLevel.WARNING
        else -> AlertLevel.DANGER
    }
    
    // Temperatura (36.1-37.2 normal, 35.5-36.0 o 37.3-38.0 warning, <35.5 o >38.0 danger)
    val temperature = Random.nextDouble(35.0, 39.5)
    val temperatureAlert = when {
        temperature in 36.1..37.2 -> AlertLevel.NORMAL
        temperature in 35.5..36.0 || temperature in 37.3..38.0 -> AlertLevel.WARNING
        else -> AlertLevel.DANGER
    }
    
    // Oxígeno (95-100 normal, 90-94 warning, <90 danger)
    val oxygen = Random.nextInt(85, 101)
    val oxygenAlert = when {
        oxygen in 95..100 -> AlertLevel.NORMAL
        oxygen in 90..94 -> AlertLevel.WARNING
        else -> AlertLevel.DANGER
    }
    
    return listOf(
        VitalSignData(
            title = "Frecuencia Cardíaca",
            value = heartRate.toString(),
            unit = "bpm",
            icon = Icons.Default.Favorite,
            alertLevel = heartRateAlert
        ),
        VitalSignData(
            title = "Presión Arterial",
            value = "$systolic/$diastolic",
            unit = "mmHg",
            icon = Icons.Default.MonitorHeart,
            alertLevel = bloodPressureAlert
        ),
        VitalSignData(
            title = "Temperatura",
            value = String.format("%.1f", temperature),
            unit = "°C",
            icon = Icons.Default.Thermostat,
            alertLevel = temperatureAlert
        ),
        VitalSignData(
            title = "Nivel de Oxígeno",
            value = oxygen.toString(),
            unit = "%",
            icon = Icons.Default.Air,
            alertLevel = oxygenAlert
        )
    )
}

/**
 * Vista previa del Dashboard
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}
