package cl.duoc.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Modelo de datos para una tarea
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
)

/**
 * Prioridad de una tarea
 */
enum class Priority {
    LOW, MEDIUM, HIGH
}

/**
 * Estado de la lista de tareas
 */
data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val filter: TaskFilter = TaskFilter.ALL
)

/**
 * Filtro para las tareas
 */
enum class TaskFilter {
    ALL, COMPLETED, PENDING
}

/**
 * ViewModel de ejemplo que demuestra el uso de StateFlow y LiveData
 * para exponer datos a la UI
 * 
 * Este ViewModel gestiona una lista de tareas y demuestra:
 * - Uso de StateFlow para estado reactivo
 * - Uso de LiveData como alternativa
 * - Operaciones CRUD básicas
 * - Filtrado y transformación de datos
 */
class ExampleViewModel : ViewModel() {

    // ===== Ejemplo con StateFlow =====
    
    private val _taskListState = MutableStateFlow(TaskListState())
    val taskListState: StateFlow<TaskListState> = _taskListState.asStateFlow()

    // ===== Ejemplo con LiveData =====
    
    private val _selectedTask = MutableLiveData<Task?>()
    val selectedTask: LiveData<Task?> = _selectedTask

    private val _totalTasks = MutableLiveData(0)
    val totalTasks: LiveData<Int> = _totalTasks

    private val _completedTasksCount = MutableLiveData(0)
    val completedTasksCount: LiveData<Int> = _completedTasksCount

    init {
        loadInitialData()
    }

    /**
     * Carga datos iniciales
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _taskListState.value = _taskListState.value.copy(isLoading = true)
            
            try {
                // Simular carga desde repositorio
                delay(1000)
                
                val initialTasks = listOf(
                    Task(1, "Configurar proyecto", "Setup inicial de la app", false, Priority.HIGH),
                    Task(2, "Implementar UI", "Crear pantallas en Compose", false, Priority.MEDIUM),
                    Task(3, "Agregar validaciones", "Validar formularios", false, Priority.HIGH),
                    Task(4, "Escribir tests", "Unit tests y UI tests", false, Priority.LOW)
                )
                
                _taskListState.value = _taskListState.value.copy(
                    tasks = initialTasks,
                    isLoading = false
                )
                
                updateTaskCounters()
                
            } catch (e: Exception) {
                _taskListState.value = _taskListState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar tareas: ${e.message}"
                )
            }
        }
    }

    /**
     * Agrega una nueva tarea
     */
    fun addTask(title: String, description: String, priority: Priority = Priority.MEDIUM) {
        val currentTasks = _taskListState.value.tasks
        val newTask = Task(
            id = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1,
            title = title,
            description = description,
            priority = priority
        )
        
        _taskListState.value = _taskListState.value.copy(
            tasks = currentTasks + newTask
        )
        
        updateTaskCounters()
    }

    /**
     * Marca una tarea como completada o pendiente
     */
    fun toggleTaskCompletion(taskId: Int) {
        val updatedTasks = _taskListState.value.tasks.map { task ->
            if (task.id == taskId) {
                task.copy(isCompleted = !task.isCompleted)
            } else {
                task
            }
        }
        
        _taskListState.value = _taskListState.value.copy(tasks = updatedTasks)
        updateTaskCounters()
    }

    /**
     * Elimina una tarea
     */
    fun deleteTask(taskId: Int) {
        val updatedTasks = _taskListState.value.tasks.filter { it.id != taskId }
        _taskListState.value = _taskListState.value.copy(tasks = updatedTasks)
        updateTaskCounters()
    }

    /**
     * Selecciona una tarea (usando LiveData)
     */
    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    /**
     * Limpia la selección
     */
    fun clearSelection() {
        _selectedTask.value = null
    }

    /**
     * Cambia el filtro de tareas
     */
    fun setFilter(filter: TaskFilter) {
        _taskListState.value = _taskListState.value.copy(filter = filter)
    }

    /**
     * Obtiene las tareas filtradas
     */
    fun getFilteredTasks(): List<Task> {
        val tasks = _taskListState.value.tasks
        return when (_taskListState.value.filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
        }
    }

    /**
     * Actualiza los contadores de tareas usando LiveData
     */
    private fun updateTaskCounters() {
        val tasks = _taskListState.value.tasks
        _totalTasks.value = tasks.size
        _completedTasksCount.value = tasks.count { it.isCompleted }
    }

    /**
     * Recarga todas las tareas
     */
    fun refreshTasks() {
        loadInitialData()
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _taskListState.value = _taskListState.value.copy(errorMessage = null)
    }
}
