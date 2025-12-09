package cl.duoc.app.data.repository

import cl.duoc.app.data.dao.PacienteDao
import cl.duoc.app.data.entity.Paciente
import kotlinx.coroutines.flow.Flow

/**
 * Repository para Pacientes
 * Abstrae el acceso a datos y centraliza la lógica de negocio
 */
class PacienteRepository(private val pacienteDao: PacienteDao) {
    
    val todosPacientes: Flow<List<Paciente>> by lazy { pacienteDao.getAllPacientes() }
    val pacientesActivos: Flow<List<Paciente>> by lazy { pacienteDao.getPacientesActivos() }

    fun getPacienteById(id: Long): Flow<Paciente?> {
        return pacienteDao.getPacienteByIdFlow(id)
    }
    
    suspend fun getPacienteByIdSync(id: Long): Paciente? {
        return pacienteDao.getPacienteById(id)
    }
    
    suspend fun getPacienteByRut(rut: String): Paciente? {
        return pacienteDao.getPacienteByRut(rut)
    }
    
    suspend fun getPacienteByEmail(email: String): Paciente? {
        return pacienteDao.getPacienteByEmail(email)
    }
    
    fun searchPacientes(query: String): Flow<List<Paciente>> {
        return pacienteDao.searchPacientes(query)
    }
    
    suspend fun insert(paciente: Paciente): Long {
        return pacienteDao.insert(paciente)
    }
    
    suspend fun insertAll(pacientes: List<Paciente>) {
        pacienteDao.insertAll(pacientes)
    }
    
    suspend fun update(paciente: Paciente) {
        pacienteDao.update(paciente)
    }
    
    suspend fun delete(paciente: Paciente) {
        pacienteDao.delete(paciente)
    }
    
    suspend fun deleteById(id: Long) {
        pacienteDao.deleteById(id)
    }
}
