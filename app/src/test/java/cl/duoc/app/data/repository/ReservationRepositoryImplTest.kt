package cl.duoc.app.data.repository

import cl.duoc.app.model.Reservation
import cl.duoc.app.model.ReservationStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para ReservationRepositoryImpl
 */
class ReservationRepositoryImplTest {

    private lateinit var repository: ReservationRepositoryImpl

    @Before
    fun setup() {
        repository = ReservationRepositoryImpl()
    }

    @Test
    fun createReservationStoresReservationSuccessfully() = runBlocking {
        val reservation = Reservation(
            id = "1",
            userId = "user1",
            specialty = "Cardiología",
            doctorName = "Dr. Smith",
            date = System.currentTimeMillis() + 86400000
        )

        val result = repository.createReservation(reservation)
        assertTrue(result)
    }

    @Test
    fun getReservationByIdReturnsCorrectReservation() = runBlocking {
        val reservation = Reservation(
            id = "1",
            userId = "user1",
            specialty = "Cardiología",
            doctorName = "Dr. Smith",
            date = System.currentTimeMillis()
        )
        
        repository.createReservation(reservation)
        val retrieved = repository.getReservationById("1")
        
        assertNotNull(retrieved)
        assertEquals("Cardiología", retrieved?.specialty)
    }

    @Test
    fun getReservationsByUserIdReturnsUserReservations() = runBlocking {
        val res1 = Reservation("1", "user1", "Cardiología", "Dr. Smith", System.currentTimeMillis())
        val res2 = Reservation("2", "user1", "Neurología", "Dr. Jones", System.currentTimeMillis())
        val res3 = Reservation("3", "user2", "Pediatría", "Dr. Brown", System.currentTimeMillis())
        
        repository.createReservation(res1)
        repository.createReservation(res2)
        repository.createReservation(res3)
        
        val user1Reservations = repository.getReservationsByUserId("user1")
        assertEquals(2, user1Reservations.size)
    }

    @Test
    fun getReservationsByStatusFiltersCorrectly() = runBlocking {
        val res1 = Reservation("1", "user1", "Cardiología", "Dr. Smith",
            System.currentTimeMillis(), ReservationStatus.PENDING)
        val res2 = Reservation("2", "user1", "Neurología", "Dr. Jones", 
            System.currentTimeMillis(), ReservationStatus.CONFIRMED)
        
        repository.createReservation(res1)
        repository.createReservation(res2)
        
        val pending = repository.getReservationsByStatus("user1", ReservationStatus.PENDING)
        assertEquals(1, pending.size)
        assertEquals("Cardiología", pending[0].specialty)
    }

    @Test
    fun updateReservationModifiesExistingReservation() = runBlocking {
        val reservation = Reservation("1", "user1", "Cardiología", "Dr. Smith", System.currentTimeMillis())
        repository.createReservation(reservation)
        
        val updated = reservation.copy(doctorName = "Dr. Johnson")
        val result = repository.updateReservation(updated)
        
        assertTrue(result)
        val retrieved = repository.getReservationById("1")
        assertEquals("Dr. Johnson", retrieved?.doctorName)
    }

    @Test
    fun cancelReservationChangesStatusToCancelled() = runBlocking {
        val reservation = Reservation("1", "user1", "Cardiología", "Dr. Smith",
            System.currentTimeMillis(), ReservationStatus.PENDING)
        repository.createReservation(reservation)
        
        val result = repository.cancelReservation("1")
        assertTrue(result)
        
        val retrieved = repository.getReservationById("1")
        assertEquals(ReservationStatus.CANCELLED, retrieved?.status)
    }

    @Test
    fun cancelReservationFailsForNonExistingReservation() = runBlocking {
        val result = repository.cancelReservation("nonexistent")
        assertFalse(result)
    }

    @Test
    fun deleteReservationRemovesReservation() = runBlocking {
        val reservation = Reservation("1", "user1", "Cardiología", "Dr. Smith", System.currentTimeMillis())
        repository.createReservation(reservation)
        
        val result = repository.deleteReservation("1")
        assertTrue(result)
        
        val retrieved = repository.getReservationById("1")
        assertNull(retrieved)
    }

    @Test
    fun observeReservationsEmitsUpdates() = runBlocking {
        val reservation = Reservation("1", "user1", "Cardiología", "Dr. Smith", System.currentTimeMillis())
        repository.createReservation(reservation)
        
        val flow = repository.observeReservations("user1")
        val firstValue = flow.first()
        
        assertEquals(1, firstValue.size)
        assertEquals("Cardiología", firstValue[0].specialty)
    }
}
