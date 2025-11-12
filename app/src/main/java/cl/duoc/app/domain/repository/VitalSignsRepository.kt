package cl.duoc.app.domain.repository

import cl.duoc.app.model.VitalSigns
import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) del repositorio de signos vitales
 * Define las operaciones disponibles para gestionar registros de signos vitales
 */
interface VitalSignsRepository {
    
    /**
     * Obtiene un registro de signos vitales por su ID
     * 
     * @param vitalSignsId ID del registro
     * @return Registro encontrado o null si no existe
     */
    suspend fun getVitalSignsById(vitalSignsId: String): VitalSigns?
    
    /**
     * Obtiene todos los registros de signos vitales de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de todos los registros del usuario
     */
    suspend fun getVitalSignsByUserId(userId: String): List<VitalSigns>
    
    /**
     * Obtiene los últimos N registros de un usuario
     * 
     * @param userId ID del usuario
     * @param limit Cantidad máxima de registros a retornar
     * @return Lista de registros ordenados por fecha descendente
     */
    suspend fun getLatestVitalSigns(userId: String, limit: Int = 10): List<VitalSigns>
    
    /**
     * Guarda un nuevo registro de signos vitales
     * 
     * @param vitalSigns Registro a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun saveVitalSigns(vitalSigns: VitalSigns): Boolean
    
    /**
     * Elimina un registro de signos vitales
     * 
     * @param vitalSignsId ID del registro a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun deleteVitalSigns(vitalSignsId: String): Boolean
    
    /**
     * Elimina todos los registros de un usuario
     * 
     * @param userId ID del usuario
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun deleteAllVitalSigns(userId: String): Boolean
    
    /**
     * Observa cambios en los signos vitales de un usuario
     * 
     * @param userId ID del usuario
     * @return Flow que emite la lista actualizada de registros
     */
    fun observeVitalSigns(userId: String): Flow<List<VitalSigns>>
}
