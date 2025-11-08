package cl.duoc.app.data.repository

import cl.duoc.app.domain.repository.VitalSignsRepository
import cl.duoc.app.model.VitalSigns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementación concreta del repositorio de signos vitales
 * Usa almacenamiento en memoria (Map) para esta demostración
 * En producción, esto debería usar Room o una API remota
 */
class VitalSignsRepositoryImpl : VitalSignsRepository {
    
    // Almacenamiento en memoria simulado
    private val vitalSigns = MutableStateFlow<Map<String, VitalSigns>>(emptyMap())
    
    /**
     * Obtiene un registro de signos vitales por su ID
     */
    override suspend fun getVitalSignsById(vitalSignsId: String): VitalSigns? {
        return vitalSigns.value[vitalSignsId]
    }
    
    /**
     * Obtiene todos los registros de signos vitales de un usuario
     */
    override suspend fun getVitalSignsByUserId(userId: String): List<VitalSigns> {
        return vitalSigns.value.values
            .filter { it.userId == userId }
            .sortedByDescending { it.timestamp }
    }
    
    /**
     * Obtiene los últimos N registros de un usuario
     */
    override suspend fun getLatestVitalSigns(userId: String, limit: Int): List<VitalSigns> {
        return vitalSigns.value.values
            .filter { it.userId == userId }
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    /**
     * Guarda un nuevo registro de signos vitales
     */
    override suspend fun saveVitalSigns(vitalSigns: VitalSigns): Boolean {
        return try {
            this.vitalSigns.value = this.vitalSigns.value + (vitalSigns.id to vitalSigns)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina un registro de signos vitales
     */
    override suspend fun deleteVitalSigns(vitalSignsId: String): Boolean {
        return try {
            vitalSigns.value = vitalSigns.value - vitalSignsId
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina todos los registros de un usuario
     */
    override suspend fun deleteAllVitalSigns(userId: String): Boolean {
        return try {
            vitalSigns.value = vitalSigns.value.filterValues { it.userId != userId }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Observa cambios en los signos vitales de un usuario
     * Retorna un Flow reactivo que emite cuando los registros cambian
     */
    override fun observeVitalSigns(userId: String): Flow<List<VitalSigns>> {
        return vitalSigns.map { vitalSignsMap ->
            vitalSignsMap.values
                .filter { it.userId == userId }
                .sortedByDescending { it.timestamp }
        }
    }
}
