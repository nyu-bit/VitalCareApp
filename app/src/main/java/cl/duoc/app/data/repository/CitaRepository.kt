package cl.duoc.app.data.repository

import cl.duoc.app.data.dao.CitaDao
import cl.duoc.app.data.entity.Cita
import cl.duoc.app.data.entity.EstadoCita
import kotlinx.coroutines.flow.Flow

/**
 * Repository para Citas
 * Abstrae el acceso a datos y centraliza la lógica de negocio
 */
class CitaRepository(private val citaDao: CitaDao) {
    
    val todasCitas: Flow<List<Cita>> = citaDao.getAllCitas()
    
    fun getCitaById(id: Long): Flow<Cita?> {
        return citaDao.getCitaByIdFlow(id)
    }
    
    suspend fun getCitaByIdSync(id: Long): Cita? {
        return citaDao.getCitaById(id)
    }
    
    fun getCitasByPaciente(pacienteId: Long): Flow<List<Cita>> {
        return citaDao.getCitasByPaciente(pacienteId)
    }
    
    fun getCitasByEspecialidad(especialidadId: Long): Flow<List<Cita>> {
        return citaDao.getCitasByEspecialidad(especialidadId)
    }
    
    fun getCitasByEstado(estado: EstadoCita): Flow<List<Cita>> {
        return citaDao.getCitasByEstado(estado)
    }
    
    fun getCitasByFecha(fecha: String): Flow<List<Cita>> {
        return citaDao.getCitasByFecha(fecha)
    }
    
    fun getCitasByRangoFechas(fechaInicio: String, fechaFin: String): Flow<List<Cita>> {
        return citaDao.getCitasByRangoFechas(fechaInicio, fechaFin)
    }
    
    fun getCitasPendientesByPaciente(pacienteId: Long): Flow<List<Cita>> {
        return citaDao.getCitasByPacienteAndEstados(
            pacienteId,
            listOf(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA)
        )
    }
    
    suspend fun insert(cita: Cita): Long {
        return citaDao.insert(cita)
    }
    
    suspend fun insertAll(citas: List<Cita>) {
        citaDao.insertAll(citas)
    }
    
    suspend fun update(cita: Cita) {
        citaDao.update(cita)
    }
    
    suspend fun delete(cita: Cita) {
        citaDao.delete(cita)
    }
    
    suspend fun deleteById(id: Long) {
        citaDao.deleteById(id)
    }
    
    suspend fun updateEstado(citaId: Long, nuevoEstado: EstadoCita) {
        citaDao.updateEstado(citaId, nuevoEstado)
    }
    
    suspend fun confirmarCita(citaId: Long) {
        citaDao.updateEstado(citaId, EstadoCita.CONFIRMADA)
    }
    
    suspend fun cancelarCita(citaId: Long) {
        citaDao.updateEstado(citaId, EstadoCita.CANCELADA)
    }
    
    suspend fun completarCita(citaId: Long) {
        citaDao.updateEstado(citaId, EstadoCita.COMPLETADA)
    }
}
