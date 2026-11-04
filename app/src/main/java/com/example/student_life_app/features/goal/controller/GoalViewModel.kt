package com.example.student_life_app.features.goal.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.data.repository.GoalRepository
import com.example.student_life_app.features.goal.model.StudyGoal
import com.example.student_life_app.features.goal.service.GoalService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoalUiState(
    val isLoading: Boolean = false,
    val goals: List<StudyGoal> = emptyList(),
    val errorMessage: String? = null
)

class GoalViewModel(
    private val repository: GoalRepository,
    private val service: GoalService
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    fun loadGoals(userId: String) {
        viewModelScope.launch {
            repository.getGoals(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> _uiState.update { it.copy(isLoading = false, goals = resource.data) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }

    fun updateProgress(goalId: String, progress: Int, userId: String) {
        viewModelScope.launch {
            repository.updateGoalProgress(goalId, progress)
            loadGoals(userId)
        }
    }
}
