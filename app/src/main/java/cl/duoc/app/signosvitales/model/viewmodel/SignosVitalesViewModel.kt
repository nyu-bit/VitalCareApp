import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.app.signosvitales.model.EstadoSalud
import cl.duoc.app.signosvitales.model.SignoVital
import cl.duoc.app.signosvitales.model.TipoSignoVital
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class SignosVitalesViewModel : ViewModel() {

    private val _signosVitales = MutableStateFlow<List<SignoVital>>(emptyList())
    val signosVitales: StateFlow<List<SignoVital>> = _signosVitales.asStateFlow()

    private val _estadoCarga = MutableStateFlow<EstadoCarga>(EstadoCarga.CARGANDO)
    val estadoCarga: StateFlow<EstadoCarga> = _estadoCarga.asStateFlow()

    fun cargarSignosVitales() {
        viewModelScope.launch {
            _estadoCarga.value = EstadoCarga.CARGANDO
            try {
                // Simular carga de datos (reemplazar con tu lógica real)
                val datos = generarDatosEjemplo()
                _signosVitales.value = datos
                _estadoCarga.value = EstadoCarga.EXITOSO
            } catch (e: Exception) {
                _estadoCarga.value = EstadoCarga.ERROR
            }
        }
    }

    fun agregarSignoVital(signoVital: SignoVital) {
        viewModelScope.launch {
            val nuevaLista = _signosVitales.value.toMutableList()
            nuevaLista.add(signoVital)
            _signosVitales.value = nuevaLista
        }
    }

    private fun generarDatosEjemplo(): List<SignoVital> {
        return listOf(
            SignoVital(
                tipo = TipoSignoVital.FRECUENCIA_CARDIACA,
                valor = 72.0,
                unidad = "lpm",
                fechaHora = Date(),
                estado = EstadoSalud.OPTIMO
            ),
            SignoVital(
                tipo = TipoSignoVital.TEMPERATURA,
                valor = 36.5,
                unidad = "°C",
                fechaHora = Date(),
                estado = EstadoSalud.NORMAL
            ),
            SignoVital(
                tipo = TipoSignoVital.SATURACION_OXIGENO,
                valor = 98.0,
                unidad = "%",
                fechaHora = Date(),
                estado = EstadoSalud.OPTIMO
            )
        )
    }
}

enum class EstadoCarga {
    CARGANDO, EXITOSO, ERROR
}