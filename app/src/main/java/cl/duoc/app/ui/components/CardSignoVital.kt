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
import cl.duoc.app.model.SignoVital
import cl.duoc.app.model.TipoSignoVital
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TarjetaSignoVital(
    signoVital: SignoVital,
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
            // Header con tipo y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = obtenerNombreTipo(signoVital.tipo),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Indicador de estado
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = Color(android.graphics.Color.parseColor(signoVital.estado.color)),
                            shape = RoundedCornerShape(50)
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Valor principal
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = String.format("%.1f", signoVital.valor),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = signoVital.unidad,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Fecha y hora
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(signoVital.fechaHora),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Notas (si existen)
            signoVital.notas?.let { notas ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notas,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun obtenerNombreTipo(tipo: TipoSignoVital): String {
    return when (tipo) {
        TipoSignoVital.FRECUENCIA_CARDIACA -> "Frecuencia Cardíaca"
        TipoSignoVital.PRESION_ARTERIAL_SISTOLICA -> "Presión Sistólica"
        TipoSignoVital.PRESION_ARTERIAL_DIASTOLICA -> "Presión Diastólica"
        TipoSignoVital.TEMPERATURA -> "Temperatura"
        TipoSignoVital.SATURACION_OXIGENO -> "Saturación O₂"
        TipoSignoVital.FRECUENCIA_RESPIRATORIA -> "Frecuencia Respiratoria"
        TipoSignoVital.GLUCOSA -> "Glucosa"
    }
}