package com.example.student_life_app.features.task.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.task.data.repository.TaskRepository
import com.example.student_life_app.features.task.model.Task
import com.example.student_life_app.features.task.model.TaskStatus
import com.example.student_life_app.features.task.service.TaskService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)

class TaskViewModel(
    private val repository: TaskRepository,
    private val service: TaskService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    fun loadTasks(userId: String) {
        viewModelScope.launch {
            repository.getTasks(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> {
                        val sorted = service.sortTasksByDeadline(resource.data)
                        _uiState.update { it.copy(isLoading = false, tasks = sorted) }
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }

    fun updateStatus(taskId: String, status: TaskStatus, userId: String) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, status)
            loadTasks(userId)
        }
    }
}
