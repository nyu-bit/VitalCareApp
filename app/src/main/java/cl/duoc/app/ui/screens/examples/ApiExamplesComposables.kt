package cl.duoc.app.ui.screens.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.app.data.api.SignosVitalesDto
import cl.duoc.app.data.api.UbicacionDto
import cl.duoc.app.data.api.AlertaDto
import cl.duoc.app.data.repository.*
import cl.duoc.app.ui.alerts.AlertsViewModel
import kotlinx.coroutines.launch

/**
 * EJEMPLOS DE COMPOSABLES QUE USAN LAS APIS
 * Estos ejemplos muestran cómo integrar los ViewModels y APIs en la UI
 *
 * NOTA: Estos son ejemplos básicos. Para la integración real, usar las pantallas
 * específicas: AlertasScreen, VitalesScreen, etc.
 */

// ============================================================
// Ejemplo: Cómo usar AlertsViewModel en una Composable
// ============================================================
@Composable
fun AlertasScreenExample(
    pacienteId: String,
    viewModel: AlertsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pacienteId) {
        viewModel.loadAlerts(pacienteId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ejemplo: Alertas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.allAlerts.isNotEmpty() -> {
                LazyColumn {
                    items(uiState.allAlerts) { alert ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = alert.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = alert.severity,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.markAsAttended(alert.id) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Atendida")
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteAlerta(alert.id) },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                Text("Sin alertas")
            }
        }
    }
}

/**
 * PATRÓN ARQUITECTÓNICO TÍPICO
 *
 * Este es el flujo recomendado para cualquier pantalla que consume APIs:
 *
 * 1. COMPOSABLE (UI)
 *    @Composable
 *    fun MyScreen(viewModel: MyViewModel) {
 *        val uiState by viewModel.uiState.collectAsState()
 *        // Mostrar UI basada en uiState
 *    }
 *
 * 2. VIEWMODEL (Lógica)
 *    class MyViewModel : ViewModel() {
 *        private val repository = MyRepository()
 *        private val _uiState = MutableStateFlow(MyUiState())
 *        val uiState = _uiState.asStateFlow()
 *
 *        fun loadData() {
 *            viewModelScope.launch {
 *                _uiState.update { it.copy(isLoading = true) }
 *                val result = repository.getData()
 *                result.onSuccess { ... }.onFailure { ... }
 *            }
 *        }
 *    }
 *
 * 3. REPOSITORY (Acceso a datos)
 *    class MyRepository {
 *        private val api = RetrofitInstance.getMyApi()
 *        suspend fun getData(): Result<List<MyDto>> {
 *            return try {
 *                Result.success(api.getData())
 *            } catch (e: Exception) {
 *                Result.failure(e)
 *            }
 *        }
 *    }
 *
 * 4. RETROFIT API INTERFACE
 *    interface MyApi {
 *        @GET("/endpoint")
 *        suspend fun getData(): List<MyDto>
 *    }
 *
 * 5. DTO (Data Transfer Object)
 *    data class MyDto(val id: String, val name: String)
 */

