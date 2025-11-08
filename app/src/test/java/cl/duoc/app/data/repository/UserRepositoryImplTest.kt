package cl.duoc.app.data.repository

import cl.duoc.app.model.User
import cl.duoc.app.utils.TestDataGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para UserRepositoryImpl
 * Cubre operaciones CRUD y casos de error
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        repository = UserRepositoryImpl()
    }

    @Test
    fun `saveUser stores user successfully`() = runTest {
        val user = TestDataGenerator.generateTestUser(
            id = "1",
            name = "Juan Pérez",
            email = "juan@example.com",
            phone = "912345678"
        )

        val result = repository.saveUser(user)
        assertTrue(result)
    }

    @Test
    fun `getUserById returns correct user`() = runTest {
        val user = TestDataGenerator.generateTestUser(
            id = "1",
            name = "Juan Pérez",
            email = "juan@example.com"
        )
        
        repository.saveUser(user)
        val retrievedUser = repository.getUserById("1")
        
        assertNotNull(retrievedUser)
        assertEquals("Juan Pérez", retrievedUser?.name)
        assertEquals("juan@example.com", retrievedUser?.email)
    }

    @Test
    fun `getUserById returns null when user does not exist`() = runTest {
        val user = repository.getUserById("nonexistent")
        assertNull(user)
    }

    @Test
    fun `getAllUsers returns all stored users`() = runTest {
        val user1 = User("1", "Juan", "juan@example.com")
        val user2 = User("2", "María", "maria@example.com")
        
        repository.saveUser(user1)
        repository.saveUser(user2)
        
        val users = repository.getAllUsers()
        assertEquals(2, users.size)
    }

    @Test
    fun `updateUser modifies existing user`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val updatedUser = user.copy(name = "Juan Carlos")
        val result = repository.updateUser(updatedUser)
        
        assertTrue(result)
        val retrieved = repository.getUserById("1")
        assertEquals("Juan Carlos", retrieved?.name)
    }

    @Test
    fun `updateUser fails for non-existing user`() = runTest {
        val user = User("nonexistent", "Test", "test@example.com")
        val result = repository.updateUser(user)
        
        assertFalse(result)
    }

    @Test
    fun `deleteUser removes user successfully`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val result = repository.deleteUser("1")
        assertTrue(result)
        
        val retrieved = repository.getUserById("1")
        assertNull(retrieved)
    }

    @Test
    fun `getUserByEmail finds user by email`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val found = repository.getUserByEmail("juan@example.com")
        assertNotNull(found)
        assertEquals("Juan", found?.name)
    }

    @Test
    fun `getUserByEmail is case insensitive`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val found = repository.getUserByEmail("JUAN@example.com")
        assertNotNull(found)
    }

    @Test
    fun `getUserByEmail returns null when not found`() = runTest {
        val found = repository.getUserByEmail("notfound@example.com")
        assertNull(found)
    }

    @Test
    fun `observeUser emits updates when user changes`() = runTest {
        val user = User("1", "Juan", "juan@example.com")
        repository.saveUser(user)
        
        val flow = repository.observeUser("1")
        val firstValue = flow.first()
        
        assertNotNull(firstValue)
        assertEquals("Juan", firstValue?.name)
    }

    @Test
    fun `saveUser overwrites existing user with same id`() = runTest {
        val user1 = User("1", "Juan", "juan@example.com")
        val user2 = User("1", "Juan Carlos", "juancarlos@example.com")
        
        repository.saveUser(user1)
        repository.saveUser(user2)
        
        val retrieved = repository.getUserById("1")
        assertEquals("Juan Carlos", retrieved?.name)
        
        val allUsers = repository.getAllUsers()
        assertEquals(1, allUsers.size)
    }
}
