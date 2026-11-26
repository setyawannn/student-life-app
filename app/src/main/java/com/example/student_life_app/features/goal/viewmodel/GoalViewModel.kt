package com.example.student_life_app.features.goal.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.data.repository.FakeGoalRepository
import com.example.student_life_app.features.goal.data.repository.GoalRepository
import com.example.student_life_app.features.goal.model.StudyGoal
import com.example.student_life_app.features.goal.service.GoalService
import com.example.student_life_app.features.goal.service.GoalServiceImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val goals: List<StudyGoal> = emptyList(),
    val filteredGoals: List<StudyGoal> = emptyList(),
    val totalGoalsCount: Int = 0,
    val completedGoalsCount: Int = 0,
    val activeGoalsCount: Int = 0,
    val errorMessage: String? = null
)

class GoalViewModel(
    private val repository: GoalRepository = FakeGoalRepository(),
    private val service: GoalService = GoalServiceImpl()
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _goals = MutableStateFlow<List<StudyGoal>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    private var loadJob: Job? = null

    val uiState: StateFlow<GoalUiState> = combine(
        _isLoading,
        _errorMessage,
        _goals,
        _searchQuery
    ) { isLoading, errorMessage, goals, query ->
        val total = goals.size
        val completed = goals.count { it.progress >= 100 }
        val active = total - completed
        GoalUiState(
            isLoading = isLoading,
            searchQuery = query,
            goals = goals,
            filteredGoals = applyFilter(goals, query),
            totalGoalsCount = total,
            completedGoalsCount = completed,
            activeGoalsCount = active,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GoalUiState()
    )

    init {
        loadGoals("default_user")
    }

    fun loadGoals(userId: String = "default_user") {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getGoals(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _goals.value = resource.data
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

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        _searchQuery.value = query
    }

    fun updateProgress(goalId: String, progress: Int, userId: String = "default_user") {
        viewModelScope.launch {
            repository.updateGoalProgress(goalId, progress)
            loadGoals(userId)
        }
    }

    private fun applyFilter(goals: List<StudyGoal>, query: String): List<StudyGoal> {
        return if (query.isBlank()) goals else {
            goals.filter {
                it.title.contains(query, ignoreCase = true) ||
                (it.description?.contains(query, ignoreCase = true) == true)
            }
        }
    }
}
