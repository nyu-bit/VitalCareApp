package cl.duoc.app.data.repository

import cl.duoc.app.data.local.room.*
import cl.duoc.app.domain.repository.VitalSignsRepository
import cl.duoc.app.model.VitalSigns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de signos vitales usando Room Database
 * Esta implementación persiste los datos localmente
 * 
 * @property vitalSignsDao DAO de signos vitales de Room
 */
class VitalSignsRepositoryRoomImpl(
    private val vitalSignsDao: VitalSignsDao
) : VitalSignsRepository {
    
    /**
     * Obtiene un registro de signos vitales por su ID desde la base de datos local
     */
    override suspend fun getVitalSignsById(vitalSignsId: String): VitalSigns? {
        return vitalSignsDao.getVitalSignsById(vitalSignsId)?.toDomain()
    }
    
    /**
     * Obtiene todos los registros de signos vitales de un usuario desde la base de datos local
     */
    override suspend fun getVitalSignsByUserId(userId: String): List<VitalSigns> {
        return vitalSignsDao.getVitalSignsByUserId(userId).toVitalSignsDomainList()
    }
    
    /**
     * Obtiene los últimos N registros de un usuario desde la base de datos local
     */
    override suspend fun getLatestVitalSigns(userId: String, limit: Int): List<VitalSigns> {
        return vitalSignsDao.getLatestVitalSigns(userId, limit).toVitalSignsDomainList()
    }
    
    /**
     * Guarda un nuevo registro de signos vitales en la base de datos local
     */
    override suspend fun saveVitalSigns(vitalSigns: VitalSigns): Boolean {
        return try {
            vitalSignsDao.insertVitalSigns(vitalSigns.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina un registro de signos vitales de la base de datos local
     */
    override suspend fun deleteVitalSigns(vitalSignsId: String): Boolean {
        return try {
            vitalSignsDao.deleteVitalSignsById(vitalSignsId)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina todos los registros de un usuario de la base de datos local
     */
    override suspend fun deleteAllVitalSigns(userId: String): Boolean {
        return try {
            vitalSignsDao.deleteAllVitalSignsByUserId(userId)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Observa cambios en los signos vitales de un usuario
     */
    override fun observeVitalSigns(userId: String): Flow<List<VitalSigns>> {
        return vitalSignsDao.observeVitalSigns(userId)
            .map { it.toVitalSignsDomainList() }
    }
}
