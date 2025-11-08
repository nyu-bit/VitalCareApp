package cl.duoc.app.domain.repository

import cl.duoc.app.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Contrato (interfaz) del repositorio de usuarios
 * Define las operaciones disponibles para gestionar usuarios
 * 
 * Esta interfaz debe ser implementada por la capa de datos (data layer)
 * siguiendo el principio de inversión de dependencias de Clean Architecture
 */
interface UserRepository {
    
    /**
     * Obtiene un usuario por su ID
     * 
     * @param userId ID del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    suspend fun getUserById(userId: String): User?
    
    /**
     * Obtiene todos los usuarios
     * 
     * @return Lista de todos los usuarios
     */
    suspend fun getAllUsers(): List<User>
    
    /**
     * Crea o actualiza un usuario
     * 
     * @param user Usuario a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun saveUser(user: User): Boolean
    
    /**
     * Actualiza la información de un usuario existente
     * 
     * @param user Usuario con la información actualizada
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun updateUser(user: User): Boolean
    
    /**
     * Elimina un usuario por su ID
     * 
     * @param userId ID del usuario a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun deleteUser(userId: String): Boolean
    
    /**
     * Obtiene un usuario por su email
     * 
     * @param email Email del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    suspend fun getUserByEmail(email: String): User?
    
    /**
     * Observa cambios en un usuario específico
     * Retorna un Flow que emite cada vez que el usuario cambia
     * 
     * @param userId ID del usuario a observar
     * @return Flow que emite el usuario actualizado
     */
    fun observeUser(userId: String): Flow<User?>
}
