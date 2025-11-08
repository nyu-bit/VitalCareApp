package cl.duoc.app.data.repository

import cl.duoc.app.data.local.room.*
import cl.duoc.app.domain.repository.UserRepository
import cl.duoc.app.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de usuarios usando Room Database
 * Esta implementación persiste los datos localmente
 * 
 * @property userDao DAO de usuarios de Room
 */
class UserRepositoryRoomImpl(
    private val userDao: UserDao
) : UserRepository {
    
    /**
     * Obtiene un usuario por su ID desde la base de datos local
     */
    override suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)?.toDomain()
    }
    
    /**
     * Obtiene todos los usuarios desde la base de datos local
     */
    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().toDomainList()
    }
    
    /**
     * Guarda un usuario en la base de datos local
     */
    override suspend fun saveUser(user: User): Boolean {
        return try {
            userDao.insertUser(user.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Actualiza un usuario en la base de datos local
     */
    override suspend fun updateUser(user: User): Boolean {
        return try {
            userDao.updateUser(user.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina un usuario de la base de datos local
     */
    override suspend fun deleteUser(userId: String): Boolean {
        return try {
            userDao.deleteUserById(userId)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Busca un usuario por email en la base de datos local
     */
    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.toDomain()
    }
    
    /**
     * Observa cambios en un usuario específico
     */
    override fun observeUser(userId: String): Flow<User?> {
        return userDao.observeUser(userId).map { it?.toDomain() }
    }
}
