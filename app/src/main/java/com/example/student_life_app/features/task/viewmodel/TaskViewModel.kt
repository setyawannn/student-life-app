package com.example.student_life_app.features.task.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.task.data.repository.SupabaseTaskRepository
import com.example.student_life_app.features.task.data.repository.TaskRepository
import com.example.student_life_app.features.task.model.Task
import com.example.student_life_app.features.task.model.TaskStatus
import com.example.student_life_app.features.task.service.TaskService
import com.example.student_life_app.features.task.service.TaskServiceImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class TaskListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: String = "Semua",
    val filterOptions: List<String> = listOf("Semua", "Belum Selesai", "Selesai"),
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedTask: Task? = null,
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val errorMessage: String? = null
)

class TaskViewModel(
    private val repository: TaskRepository = SupabaseTaskRepository(),
    private val service: TaskService = TaskServiceImpl()
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow("Semua")
    private val _selectedTask = MutableStateFlow<Task?>(null)

    private var loadJob: Job? = null

    private val _statusPair = combine(_isLoading, _errorMessage) { loading, error ->
        loading to error
    }

    val uiState: StateFlow<TaskListUiState> = combine(
        _statusPair,
        _allTasks,
        _searchQuery,
        _selectedFilter,
        _selectedTask
    ) { (isLoading, errorMessage), allTasks, query, selectedFilter, selectedTask ->
        val total = allTasks.size
        val completed = allTasks.count { it.status == TaskStatus.COMPLETED }
        val pending = total - completed
        TaskListUiState(
            isLoading = isLoading,
            searchQuery = query,
            selectedFilter = selectedFilter,
            allTasks = allTasks,
            filteredTasks = applyFilter(allTasks, query, selectedFilter),
            selectedTask = selectedTask,
            totalCount = total,
            pendingCount = pending,
            completedCount = completed,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskListUiState()
    )

    init {
        loadTasks("default_user")
    }

    fun loadTasks(userId: String = "default_user") {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getTasks(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _allTasks.value = service.sortTasksByDeadline(resource.data)
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _errorMessage.value = resource.message
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun loadTaskDetail(taskId: String) {
        viewModelScope.launch {
            when (val result = repository.getTaskById(taskId)) {
                is Resource.Success -> _selectedTask.value = result.data
                is Resource.Error -> _errorMessage.value = result.message
                else -> Unit
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        _searchQuery.value = query
    }

    fun onFilterSelect(filter: String) {
        _selectedFilter.value = filter
    }

    fun toggleTaskCompletion(task: Task) {
        val newStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.TODO else TaskStatus.COMPLETED
        updateTaskStatus(task.id, newStatus)
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, newStatus)
            loadTaskDetail(taskId)
            loadTasks()
        }
    }

    fun saveTask(
        taskId: String?,
        title: String,
        courseName: String,
        description: String,
        dueDate: String,
        status: TaskStatus,
        onSuccess: () -> Unit
    ) {
        val validationError = service.validateTaskInput(title, courseName)
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }
        if (dueDate.isBlank()) {
            _errorMessage.value = "Tenggat waktu tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            _errorMessage.value = null
            if (taskId == null) {
                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    userId = "default_user",
                    title = title.trim(),
                    courseName = courseName.trim(),
                    description = description.trim().ifBlank { null },
                    dueDate = dueDate.trim(),
                    status = status
                )
                repository.createTask(newTask)
            } else {
                val updatedTask = Task(
                    id = taskId,
                    userId = "default_user",
                    title = title.trim(),
                    courseName = courseName.trim(),
                    description = description.trim().ifBlank { null },
                    dueDate = dueDate.trim(),
                    status = status
                )
                repository.updateTask(updatedTask)
            }
            loadTasks()
            onSuccess()
        }
    }

    fun deleteTask(taskId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            loadTasks()
            onSuccess()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun applyFilter(tasks: List<Task>, query: String, filter: String): List<Task> {
        return tasks.filter { task ->
            val matchesFilter = when (filter) {
                "Selesai" -> task.status == TaskStatus.COMPLETED
                "Belum Selesai" -> task.status != TaskStatus.COMPLETED
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                task.title.contains(query, ignoreCase = true) ||
                task.courseName.contains(query, ignoreCase = true) ||
                (task.description?.contains(query, ignoreCase = true) == true)
            matchesFilter && matchesQuery
        }
    }
}
