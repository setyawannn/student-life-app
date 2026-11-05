package com.example.student_life_app.features.task.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.task.data.repository.FakeTaskRepository
import com.example.student_life_app.features.task.data.repository.TaskRepository
import com.example.student_life_app.features.task.model.Task
import com.example.student_life_app.features.task.model.TaskStatus
import com.example.student_life_app.features.task.service.TaskService
import com.example.student_life_app.features.task.service.TaskServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: String = "Semua",
    val filterOptions: List<String> = listOf("Semua", "Belum Selesai", "Selesai"),
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val errorMessage: String? = null
)

class TaskViewModel(
    private val repository: TaskRepository = FakeTaskRepository(),
    private val service: TaskService = TaskServiceImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks("default_user")
    }

    fun loadTasks(userId: String = "default_user") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getTasks(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val sorted = service.sortTasksByDeadline(resource.data)
                        val total = sorted.size
                        val completed = sorted.count { it.status == TaskStatus.COMPLETED }
                        val pending = total - completed

                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                allTasks = sorted,
                                totalCount = total,
                                pendingCount = pending,
                                completedCount = completed,
                                filteredTasks = applyFilter(sorted, current.searchQuery, current.selectedFilter)
                            )
                        }
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredTasks = applyFilter(current.allTasks, query, current.selectedFilter)
            )
        }
    }

    fun onFilterSelect(filter: String) {
        _uiState.update { current ->
            current.copy(
                selectedFilter = filter,
                filteredTasks = applyFilter(current.allTasks, current.searchQuery, filter)
            )
        }
    }

    fun toggleTaskCompletion(task: Task) {
        val newStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.TODO else TaskStatus.COMPLETED
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, newStatus)
            loadTasks()
        }
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
