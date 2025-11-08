package cl.duoc.app.domain.usecase

import cl.duoc.app.data.repository.UserRepositoryImpl
import cl.duoc.app.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para casos de uso de usuarios
 * Cubre validaciones y lógica de negocio
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserUseCasesTest {

    private lateinit var repository: UserRepositoryImpl
    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var saveUserUseCase: SaveUserUseCase
    private lateinit var deleteUserUseCase: DeleteUserUseCase
    private lateinit var validateCredentialsUseCase: ValidateUserCredentialsUseCase

    @Before
    fun setup() {
        repository = UserRepositoryImpl()
        getUserUseCase = GetUserUseCase(repository)
        saveUserUseCase = SaveUserUseCase(repository)
        deleteUserUseCase = DeleteUserUseCase(repository)
        validateCredentialsUseCase = ValidateUserCredentialsUseCase(repository)
    }

    @Test
    fun `GetUserUseCase returns user when exists`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val result = getUserUseCase("1")
        assertNotNull(result)
        assertEquals("Juan", result?.name)
    }

    @Test
    fun `GetUserUseCase returns null when user does not exist`() = runTest {
        val result = getUserUseCase("nonexistent")
        assertNull(result)
    }

    @Test
    fun `GetUserUseCase throws exception for blank userId`() = runTest {
        try {
            getUserUseCase("")
            fail("Should throw exception")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("ID"))
        }
    }

    @Test
    fun `SaveUserUseCase succeeds with valid user`() = runTest {
        val user = User("1", "Juan Pérez", "juan@example.com", "912345678")
        
        val result = saveUserUseCase(user)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun `SaveUserUseCase fails with blank name`() = runTest {
        val user = User("1", "", "juan@example.com")
        
        val result = saveUserUseCase(user)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("nombre"))
    }

    @Test
    fun `SaveUserUseCase fails with blank email`() = runTest {
        val user = User("1", "Juan", "")
        
        val result = saveUserUseCase(user)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("email"))
    }

    @Test
    fun `SaveUserUseCase fails with invalid email format`() = runTest {
        val user = User("1", "Juan", "invalid-email")
        
        val result = saveUserUseCase(user)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("válido"))
    }

    @Test
    fun `SaveUserUseCase fails when email already exists`() = runTest {
        val user1 = User("1", "Juan", "juan@example.com")
        val user2 = User("2", "María", "juan@example.com")
        
        saveUserUseCase(user1)
        val result = saveUserUseCase(user2)
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("existe"))
    }

    @Test
    fun `SaveUserUseCase allows updating same user email`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        saveUserUseCase(user)
        
        val updatedUser = user.copy(name = "Juan Carlos")
        val result = saveUserUseCase(updatedUser)
        
        assertTrue(result.isSuccess)
    }

    @Test
    fun `DeleteUserUseCase succeeds when user exists`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val result = deleteUserUseCase("1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `DeleteUserUseCase throws exception for blank userId`() = runTest {
        try {
            deleteUserUseCase("")
            fail("Should throw exception")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("ID"))
        }
    }

    @Test
    fun `ValidateUserCredentialsUseCase succeeds with valid credentials`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val result = validateCredentialsUseCase("juan@example.com", "password123")
        assertTrue(result.isSuccess)
        assertEquals("Juan", result.getOrNull()?.name)
    }

    @Test
    fun `ValidateUserCredentialsUseCase fails with invalid email`() = runTest {
        val result = validateCredentialsUseCase("notfound@example.com", "password123")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("Credenciales"))
    }

    @Test
    fun `ValidateUserCredentialsUseCase fails with blank email`() = runTest {
        val result = validateCredentialsUseCase("", "password123")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("email"))
    }

    @Test
    fun `ValidateUserCredentialsUseCase fails with blank password`() = runTest {
        val result = validateCredentialsUseCase("juan@example.com", "")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message!!.contains("contraseña"))
    }
}
