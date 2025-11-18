package cl.duoc.app.domain.usecase

import cl.duoc.app.domain.repository.UserRepository
import cl.duoc.app.model.User

/**
 * Caso de uso: Obtener información de un usuario
 * 
 * Este caso de uso encapsula la lógica de negocio para obtener
 * un usuario por su ID
 * 
 * @property userRepository Repositorio de usuarios
 */
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario a obtener
     * @return Usuario encontrado o null si no existe
     * @throws Exception si ocurre un error al obtener el usuario
     */
    suspend operator fun invoke(userId: String): User? {
        require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
        return userRepository.getUserById(userId)
    }
}

/**
 * Caso de uso: Crear o actualizar un usuario
 * 
 * Valida los datos del usuario antes de guardarlo
 * 
 * @property userRepository Repositorio de usuarios
 */
class SaveUserUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param user Usuario a guardar
     * @return Result<Boolean> indicando éxito o error
     */
    suspend operator fun invoke(user: User): Result<Boolean> {
        return try {
            // Validaciones de negocio
            require(!user.name.isNullOrBlank()) { "El nombre es requerido" }
            require(!user.email.isNullOrBlank()) { "El email es requerido" }
            require(user.email?.contains("@") == true) { "El email debe ser válido" }

            // Verificar si ya existe un usuario con ese email
            val existingUser = userRepository.getUserByEmail(user.email ?: "")
            if (existingUser != null && existingUser.id != user.id) {
                return Result.failure(Exception("Ya existe un usuario con ese email"))
            }
            
            val success = userRepository.saveUser(user)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al guardar el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Caso de uso: Eliminar un usuario
 * 
 * @property userRepository Repositorio de usuarios
 */
class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param userId ID del usuario a eliminar
     * @return Result<Boolean> indicando éxito o error
     */
    suspend operator fun invoke(userId: String): Result<Boolean> {
        return try {
            require(userId.isNotBlank()) { "El ID del usuario no puede estar vacío" }
            
            val success = userRepository.deleteUser(userId)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("No se pudo eliminar el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Caso de uso: Validar credenciales de usuario
 * 
 * Este caso de uso simula la validación de login
 * En producción, se debería comparar con contraseña encriptada
 * 
 * @property userRepository Repositorio de usuarios
 */
class ValidateUserCredentialsUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Ejecuta el caso de uso
     * 
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Result<User> con el usuario si las credenciales son válidas
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return try {
            require(email.isNotBlank()) { "El email es requerido" }
            require(password.isNotBlank()) { "La contraseña es requerida" }
            
            val user = userRepository.getUserByEmail(email)
            if (user != null) {
                // En producción: verificar password hasheada
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
