import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.VitalSigns
import cl.duoc.app.model.formatDateTime
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CardVitalSigns(
    vitalSigns: VitalSigns,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Frecuencia Cardíaca
            vitalSigns.heartRate?.let { heartRate ->
                VitalSignRow("Frecuencia Cardíaca", heartRate.toString(), "lpm")
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Presión Arterial
            if (vitalSigns.bloodPressureSystolic != null && vitalSigns.bloodPressureDiastolic != null) {
                VitalSignRow(
                    "Presión Arterial",
                    "${vitalSigns.bloodPressureSystolic}/${vitalSigns.bloodPressureDiastolic}",
                    "mmHg"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Saturación de Oxígeno
            vitalSigns.oxygenSaturation?.let { saturation ->
                VitalSignRow("Saturación O₂", saturation.toString(), "%")
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Temperatura
            vitalSigns.temperature?.let { temp ->
                VitalSignRow("Temperatura", String.format("%.1f", temp), "°C")
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Fecha y hora
            Text(
                text = vitalSigns.timestamp.formatDateTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Notas (si existen)
            vitalSigns.notes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VitalSignRow(
    label: String,
    value: String,
    unit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
