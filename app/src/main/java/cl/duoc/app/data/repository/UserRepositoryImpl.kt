package cl.duoc.app.data.repository

import cl.duoc.app.domain.repository.UserRepository
import cl.duoc.app.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementación concreta del repositorio de usuarios
 * Usa almacenamiento en memoria (Map) para esta demostración
 * En producción, esto debería usar Room o una API remota
 */
class UserRepositoryImpl : UserRepository {
    
    // Almacenamiento en memoria simulado
    private val users = MutableStateFlow<Map<String, User>>(emptyMap())
    
    /**
     * Obtiene un usuario por su ID
     */
    override suspend fun getUserById(userId: String): User? {
        return users.value[userId]
    }
    
    /**
     * Obtiene todos los usuarios
     */
    override suspend fun getAllUsers(): List<User> {
        return users.value.values.toList()
    }
    
    /**
     * Guarda un usuario nuevo o actualiza uno existente
     */
    override suspend fun saveUser(user: User): Boolean {
        return try {
            users.value = users.value + (user.id to user)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Actualiza un usuario existente
     */
    override suspend fun updateUser(user: User): Boolean {
        return try {
            if (users.value.containsKey(user.id)) {
                users.value = users.value + (user.id to user)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina un usuario por su ID
     */
    override suspend fun deleteUser(userId: String): Boolean {
        return try {
            users.value = users.value - userId
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Busca un usuario por su email
     */
    override suspend fun getUserByEmail(email: String): User? {
        return users.value.values.find { it.email.equals(email, ignoreCase = true) }
    }
    
    /**
     * Observa cambios en un usuario específico
     * Retorna un Flow reactivo que emite cuando el usuario cambia
     */
    override fun observeUser(userId: String): Flow<User?> {
        return users.map { userMap -> userMap[userId] }
    }
}
