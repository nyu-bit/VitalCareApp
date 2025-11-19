package cl.duoc.app.data.repository

import cl.duoc.app.data.dao.EspecialidadDao
import cl.duoc.app.data.entity.Especialidad
import kotlinx.coroutines.flow.Flow

/**
 * Repository para Especialidades
 * Abstrae el acceso a datos y centraliza la lógica de negocio
 */
class EspecialidadRepository(private val especialidadDao: EspecialidadDao) {
    
    val todasEspecialidades: Flow<List<Especialidad>> = especialidadDao.getAllEspecialidades()
    val especialidadesActivas: Flow<List<Especialidad>> = especialidadDao.getEspecialidadesActivas()
    
    fun getEspecialidadById(id: Long): Flow<Especialidad?> {
        return especialidadDao.getEspecialidadByIdFlow(id)
    }
    
    suspend fun insert(especialidad: Especialidad): Long {
        return especialidadDao.insert(especialidad)
    }
    
    suspend fun insertAll(especialidades: List<Especialidad>) {
        especialidadDao.insertAll(especialidades)
    }
    
    suspend fun update(especialidad: Especialidad) {
        especialidadDao.update(especialidad)
    }
    
    suspend fun delete(especialidad: Especialidad) {
        especialidadDao.delete(especialidad)
    }
    
    suspend fun deleteById(id: Long) {
        especialidadDao.deleteById(id)
    }
}
