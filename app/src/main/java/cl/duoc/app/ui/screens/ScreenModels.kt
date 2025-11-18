package cl.duoc.app.ui.screens

// Enum para estados de carga
enum class EstadoCarga {
    CARGANDO,
    CARGADO,
    ERROR
}

// ViewModel para pantalla de signos vitales (stub)
class SignosVitalesViewModel {
    val signosVitales = androidx.lifecycle.MutableLiveData<List<Any>>()
    val estadoCarga = androidx.lifecycle.MutableLiveData<EstadoCarga>()

    fun cargarSignosVitales() {
        // Implementación básica
    }
}

