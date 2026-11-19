package com.example.student_life_app.features.goal.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.data.repository.FakeGoalRepository
import com.example.student_life_app.features.goal.data.repository.GoalRepository
import com.example.student_life_app.features.goal.data.repository.SupabaseGoalRepositoryImpl
import com.example.student_life_app.features.goal.model.GoalMilestone
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
    val isSaving: Boolean = false,
    val searchQuery: String = "",
    val selectedFilterTab: String = "Semua", // "Semua", "Sedang Berjalan", "Selesai"
    val goals: List<StudyGoal> = emptyList(),
    val filteredGoals: List<StudyGoal> = emptyList(),
    val selectedGoal: StudyGoal? = null,
    val academicProgressPercentage: Int = 0,
    val totalGoalsCount: Int = 0,
    val completedGoalsCount: Int = 0,
    val activeGoalsCount: Int = 0,
    val errorMessage: String? = null,
)

class GoalViewModel(
    private val repository: GoalRepository = SupabaseGoalRepositoryImpl(),
    private val service: GoalService = GoalServiceImpl(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    fun loadGoals(userId: String = FakeGoalRepository.DEFAULT_USER_ID) {
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
                        val academicProgress = if (total > 0) (goals.sumOf { it.progress } / total) else 0

                        _uiState.update { current ->
                            val currentSelectedId = current.selectedGoal?.id
                            val updatedSelected = currentSelectedId?.let { id -> goals.firstOrNull { it.id == id } }
                                ?: current.selectedGoal

                            current.copy(
                                isLoading = false,
                                goals = goals,
                                totalGoalsCount = total,
                                completedGoalsCount = completed,
                                activeGoalsCount = active,
                                academicProgressPercentage = academicProgress,
                                filteredGoals = applyFilters(goals, current.searchQuery, current.selectedFilterTab),
                                selectedGoal = updatedSelected
                            )
                        }
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }

    fun selectGoal(goalId: String) {
        _uiState.update { current ->
            current.copy(selectedGoal = current.goals.firstOrNull { it.id == goalId } ?: current.selectedGoal)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredGoals = applyFilters(current.goals, query, current.selectedFilterTab),
            )
        }
    }

    fun onFilterTabChange(tab: String) {
        _uiState.update { current ->
            current.copy(
                selectedFilterTab = tab,
                filteredGoals = applyFilters(current.goals, current.searchQuery, tab),
            )
        }
    }

    fun saveGoal(
        goalId: String?,
        title: String,
        category: String,
        priority: String,
        description: String?,
        deadline: String,
        progress: Int,
        milestones: List<GoalMilestone>,
        strategy: String?,
        weeklyReminder: Boolean,
        onSuccess: () -> Unit,
        userId: String = FakeGoalRepository.DEFAULT_USER_ID
    ) {
        val validationError = service.validateGoalInput(title, deadline)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val goal = StudyGoal(
                id = goalId ?: java.util.UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                category = category,
                priority = priority,
                description = description?.ifBlank { null },
                deadline = deadline,
                progress = progress,
                milestones = milestones,
                strategy = strategy?.ifBlank { null },
                weeklyReminder = weeklyReminder
            )
            val result = if (goalId == null) repository.createGoal(goal) else repository.updateGoal(goal)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false, selectedGoal = goal) }
                    loadGoals(userId)
                    onSuccess()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
                else -> {
                    _uiState.update { it.copy(isSaving = false) }
                }
            }
        }
    }

    fun toggleMilestone(goalId: String, milestoneId: String, userId: String = FakeGoalRepository.DEFAULT_USER_ID) {
        viewModelScope.launch {
            val goal = _uiState.value.goals.firstOrNull { it.id == goalId }
                ?: _uiState.value.selectedGoal
                ?: return@launch

            val updatedMilestones = goal.milestones.map {
                if (it.id == milestoneId) it.copy(isCompleted = !it.isCompleted) else it
            }
            val completedCount = updatedMilestones.count { it.isCompleted }
            val totalCount = updatedMilestones.size
            val newProgress = if (totalCount > 0) ((completedCount.toFloat() / totalCount.toFloat()) * 100).toInt() else goal.progress

            val updatedGoal = goal.copy(
                milestones = updatedMilestones,
                progress = newProgress
            )

            _uiState.update { it.copy(selectedGoal = updatedGoal) }
            repository.updateGoal(updatedGoal)
            loadGoals(userId)
        }
    }

    fun updateProgress(goalId: String, progress: Int, userId: String = FakeGoalRepository.DEFAULT_USER_ID) {
        viewModelScope.launch {
            val goal = _uiState.value.goals.firstOrNull { it.id == goalId }
                ?: _uiState.value.selectedGoal

            val newProgress = progress.coerceIn(0, 100)
            if (goal != null) {
                val updatedGoal = goal.copy(progress = newProgress)
                _uiState.update { it.copy(selectedGoal = updatedGoal) }
                repository.updateGoal(updatedGoal)
            } else {
                repository.updateGoalProgress(goalId, newProgress)
            }
            loadGoals(userId)
        }
    }

    fun markCompleted(goalId: String, userId: String = FakeGoalRepository.DEFAULT_USER_ID) {
        updateProgress(goalId, 100, userId)
    }

    fun deleteGoal(goalId: String, onSuccess: () -> Unit = {}, userId: String = FakeGoalRepository.DEFAULT_USER_ID) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            _uiState.update { current ->
                if (current.selectedGoal?.id == goalId) current.copy(selectedGoal = null) else current
            }
            loadGoals(userId)
            onSuccess()
        }
    }

    private fun applyFilters(goals: List<StudyGoal>, query: String, tab: String): List<StudyGoal> {
        return goals.filter { goal ->
            val matchesQuery = query.isBlank() ||
                goal.title.contains(query, ignoreCase = true) ||
                (goal.description?.contains(query, ignoreCase = true) == true) ||
                goal.category.contains(query, ignoreCase = true)

            val matchesTab = when (tab) {
                "Sedang Berjalan" -> goal.progress < 100
                "Selesai" -> goal.progress >= 100
                else -> true
            }

            matchesQuery && matchesTab
        }
    }
}
