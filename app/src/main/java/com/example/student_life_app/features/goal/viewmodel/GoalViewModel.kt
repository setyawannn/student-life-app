package com.example.student_life_app.features.goal.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class GoalUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val searchQuery: String = "",
    val selectedFilterTab: String = "Semua",
    val goals: List<StudyGoal> = emptyList(),
    val filteredGoals: List<StudyGoal> = emptyList(),
    val selectedGoal: StudyGoal? = null,
    val academicProgressPercentage: Int = 0,
    val totalGoalsCount: Int = 0,
    val completedGoalsCount: Int = 0,
    val activeGoalsCount: Int = 0,
    val errorMessage: String? = null
)

class GoalViewModel(
    private val repository: GoalRepository = SupabaseGoalRepositoryImpl(),
    private val service: GoalService = GoalServiceImpl()
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _isSaving = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _goals = MutableStateFlow<List<StudyGoal>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilterTab = MutableStateFlow("Semua")
    private val _selectedGoal = MutableStateFlow<StudyGoal?>(null)

    private var loadJob: Job? = null

    private val _statusTriple = combine(_isLoading, _isSaving, _errorMessage) { l, s, e ->
        Triple(l, s, e)
    }

    val uiState: StateFlow<GoalUiState> = combine(
        _statusTriple,
        _goals,
        _searchQuery,
        _selectedFilterTab,
        _selectedGoal
    ) { (isLoading, isSaving, errorMessage), goals, query, selectedTab, selectedGoal ->
        val total = goals.size
        val completed = goals.count { it.progress >= 100 }
        val active = total - completed
        val academicProgress = if (total > 0) (goals.sumOf { it.progress } / total) else 0
        val syncedSelected = selectedGoal?.id?.let { id -> goals.firstOrNull { it.id == id } } ?: selectedGoal

        GoalUiState(
            isLoading = isLoading,
            isSaving = isSaving,
            searchQuery = query,
            selectedFilterTab = selectedTab,
            goals = goals,
            filteredGoals = applyFilters(goals, query, selectedTab),
            selectedGoal = syncedSelected,
            academicProgressPercentage = academicProgress,
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
        loadGoals()
    }

    fun loadGoals(userId: String = FakeGoalRepository.DEFAULT_USER_ID) {
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

    fun selectGoal(goalId: String) {
        _selectedGoal.value = _goals.value.firstOrNull { it.id == goalId } ?: _selectedGoal.value
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        _searchQuery.value = query
    }

    fun onFilterTabChange(tab: String) {
        _selectedFilterTab.value = tab
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
            _errorMessage.value = validationError
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            val goal = StudyGoal(
                id = goalId ?: UUID.randomUUID().toString(),
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
                    _selectedGoal.value = goal
                    _isSaving.value = false
                    loadGoals(userId)
                    onSuccess()
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                    _isSaving.value = false
                }
                Resource.Loading -> {
                    _isSaving.value = false
                }
            }
        }
    }

    fun toggleMilestone(
        goalId: String,
        milestoneId: String,
        userId: String = FakeGoalRepository.DEFAULT_USER_ID
    ) {
        viewModelScope.launch {
            val goal = _goals.value.firstOrNull { it.id == goalId }
                ?: _selectedGoal.value
                ?: return@launch

            val updatedMilestones = goal.milestones.map {
                if (it.id == milestoneId) it.copy(isCompleted = !it.isCompleted) else it
            }
            val completedCount = updatedMilestones.count { it.isCompleted }
            val totalCount = updatedMilestones.size
            val newProgress = if (totalCount > 0) {
                ((completedCount.toFloat() / totalCount.toFloat()) * 100).toInt()
            } else {
                goal.progress
            }

            val updatedGoal = goal.copy(
                milestones = updatedMilestones,
                progress = newProgress
            )
            _selectedGoal.value = updatedGoal
            repository.updateGoal(updatedGoal)
            loadGoals(userId)
        }
    }

    fun updateProgress(
        goalId: String,
        progress: Int,
        userId: String = FakeGoalRepository.DEFAULT_USER_ID
    ) {
        viewModelScope.launch {
            val goal = _goals.value.firstOrNull { it.id == goalId } ?: _selectedGoal.value
            val newProgress = progress.coerceIn(0, 100)
            if (goal != null) {
                val updatedGoal = goal.copy(progress = newProgress)
                _selectedGoal.value = updatedGoal
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

    fun deleteGoal(
        goalId: String,
        onSuccess: () -> Unit = {},
        userId: String = FakeGoalRepository.DEFAULT_USER_ID
    ) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            if (_selectedGoal.value?.id == goalId) {
                _selectedGoal.value = null
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
