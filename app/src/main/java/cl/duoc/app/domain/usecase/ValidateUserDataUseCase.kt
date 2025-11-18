package cl.duoc.app.domain.usecase

import cl.duoc.app.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ValidateUserDataUseCase {
    suspend operator fun invoke(user: User): Result<Boolean> {
        return try {
            // Validaciones de negocio
            require(!user.name.isNullOrBlank()) { "El nombre es requerido" }
            require(!user.email.isNullOrBlank()) { "El email es requerido" }
            require(user.email?.contains("@") == true) { "El email debe ser válido" }
            require(!user.phone.isNullOrBlank()) { "El teléfono es requerido" }
            require(!user.rut.isNullOrBlank()) { "El RUT es requerido" }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

