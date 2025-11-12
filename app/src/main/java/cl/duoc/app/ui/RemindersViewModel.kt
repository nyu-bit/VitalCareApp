package cl.duoc.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import cl.duoc.app.data.repository.ReminderRepositoryImpl
import cl.duoc.app.data.repository.ReservationRepositoryImpl
import cl.duoc.app.domain.usecase.CancelReminderByReservationUseCase
import cl.duoc.app.domain.usecase.CancelReminderUseCase
import cl.duoc.app.domain.usecase.GetUpcomingRemindersUseCase
import cl.duoc.app.domain.usecase.ScheduleAppointmentReminderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar recordatorios de citas
 *
 * Responsabilidades:
 * - Coordinar casos de uso
 * - Gestionar estado de UI
 * - Manejar errores
 */
class RemindersViewModel(
    workManager: WorkManager
) : ViewModel() {

    // Repositorios
    private val reminderRepository = ReminderRepositoryImpl()
    private val reservationRepository = ReservationRepositoryImpl()

    // Casos de uso
    private val scheduleReminderUseCase = ScheduleAppointmentReminderUseCase(
        reminderRepository = reminderRepository,
        reservationRepository = reservationRepository,
        workManager = workManager
    )
    private val cancelReminderUseCase = CancelReminderUseCase(reminderRepository)
    private val cancelReminderByReservationUseCase = CancelReminderByReservationUseCase(reminderRepository)
    private val getUpcomingRemindersUseCase = GetUpcomingRemindersUseCase(
        reminderRepository = reminderRepository,
        reservationRepository = reservationRepository
    )

    // Estados
    private val _upcomingReminders = MutableStateFlow<List<GetUpcomingRemindersUseCase.UpcomingReminder>>(emptyList())
    val upcomingReminders: StateFlow<List<GetUpcomingRemindersUseCase.UpcomingReminder>> = _upcomingReminders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    /**
     * Programa un recordatorio para una cita
     *
     * @param reservationId ID de la reserva
     */
    fun scheduleReminder(reservationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = scheduleReminderUseCase(reservationId)

            result.onSuccess {
                _successMessage.value = "Recordatorio programado exitosamente"
                loadUpcomingReminders()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconocido"
            }

            _isLoading.value = false
        }
    }

    /**
     * Cancela un recordatorio
     *
     * @param reminderId ID del recordatorio
     */
    fun cancelReminder(reminderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = cancelReminderUseCase(reminderId)

            result.onSuccess {
                _successMessage.value = "Recordatorio cancelado"
                loadUpcomingReminders()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconocido"
            }

            _isLoading.value = false
        }
    }

    /**
     * Cancela recordatorio por ID de reserva
     *
     * @param reservationId ID de la reserva
     */
    fun cancelReminderByReservation(reservationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = cancelReminderByReservationUseCase(reservationId)

            result.onSuccess {
                _successMessage.value = "Recordatorio de la cita cancelado"
                loadUpcomingReminders()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error desconocido"
            }

            _isLoading.value = false
        }
    }

    /**
     * Carga los recordatorios próximos de un usuario
     *
     * @param userId ID del usuario
     */
    fun loadUpcomingReminders(userId: String = "user_test") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = getUpcomingRemindersUseCase(userId)

            result.onSuccess { reminders ->
                _upcomingReminders.value = reminders
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al cargar recordatorios"
            }

            _isLoading.value = false
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Limpia el mensaje de éxito
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}

