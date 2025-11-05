package cl.duoc.app.signosvitales.model

import java.util.Date

data class SignoVital(
    val id: Long = 0,
    val tipo: TipoSignoVital,
    val valor: Double,
    val unidad: String,
    val fechaHora: Date,
    val estado: EstadoSalud,
    val notas: String? = null
)

enum class TipoSignoVital {
    FRECUENCIA_CARDIACA,
    PRESION_ARTERIAL_SISTOLICA,
    PRESION_ARTERIAL_DIASTOLICA,
    TEMPERATURA,
    SATURACION_OXIGENO,
    FRECUENCIA_RESPIRATORIA,
    GLUCOSA
}

enum class EstadoSalud(val color: String) {
    OPTIMO("#4CAF50"),
    NORMAL("#2196F3"),
    ALERTA("#FF9800"),
    PELIGRO("#F44336"),
}

data class PresionArterial (
    val sistolica: Double,
    val diastolica: Double,
    val fechaHora: Date
) {
    fun obtenerEstado(): EstadoSalud {
        return when {
            sistolica < 90 || diastolica < 60 -> EstadoSalud.ALERTA
            sistolica <= 120 && diastolica <= 80 -> EstadoSalud.OPTIMO
            sistolica <= 139 || diastolica <=89 -> EstadoSalud.NORMAL
            else -> EstadoSalud.ALERTA
        }
    }
}