package com.example.student_life_app.features.goal.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.data.repository.FakeGoalRepository
import com.example.student_life_app.features.goal.data.repository.GoalRepository
import com.example.student_life_app.features.goal.model.StudyGoal
import com.example.student_life_app.features.goal.service.GoalService
import com.example.student_life_app.features.goal.service.GoalServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        loadGoals("default_user")
    }

    fun loadGoals(userId: String = "default_user") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getGoals(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val goals = resource.data
                        val total = goals.size
                        val completed = goals.count { it.progress >= 100 }
                        val active = total - completed

                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                goals = goals,
                                totalGoalsCount = total,
                                completedGoalsCount = completed,
                                activeGoalsCount = active,
                                filteredGoals = applyFilter(goals, current.searchQuery)
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
                filteredGoals = applyFilter(current.goals, query)
            )
        }
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
