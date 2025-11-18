import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.app.model.VitalSigns
import cl.duoc.app.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSignosVitales(
    signosVitales: List<VitalSigns> = emptyList()
) {
    Scaffold(
        topBar = {
            Text(
                text = "Signos Vitales",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->
        if (signosVitales.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay datos de signos vitales")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(signosVitales) { vitalSign ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "FC: ${vitalSign.heartRate} bpm",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            vitalSign.bloodPressureSystolic?.let { sys ->
                                vitalSign.bloodPressureDiastolic?.let { dia ->
                                    Text(
                                        text = "PA: $sys/$dia mmHg",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            vitalSign.oxygenSaturation?.let { sat ->
                                Text(
                                    text = "SpO2: $sat%",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = FormatUtils.formatDateTime(vitalSign.timestamp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
