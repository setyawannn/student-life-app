package com.example.student_life_app.features.schedule.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.schedule.data.repository.FakeScheduleRepository
import com.example.student_life_app.features.schedule.data.repository.ScheduleRepository
import com.example.student_life_app.features.schedule.model.Schedule
import com.example.student_life_app.features.schedule.service.ScheduleService
import com.example.student_life_app.features.schedule.service.ScheduleServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State layar Jadwal. `schedules` berisi SEMUA jadwal (semua hari);
 * pemilahan per hari dilakukan di UI (filter berdasarkan dayOfWeek).
 */
data class ScheduleUiState(
    val isLoading: Boolean = false,
    val schedules: List<Schedule> = emptyList(),
    val errorMessage: String? = null
)

class ScheduleViewModel(
    private val repository: ScheduleRepository = FakeScheduleRepository(),
    val service: ScheduleService = ScheduleServiceImpl(),
    private val userId: String = "default_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        observeSchedules()
    }

    private fun observeSchedules() {
        viewModelScope.launch {
            repository.getSchedules(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, schedules = resource.data, errorMessage = null)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = resource.message)
                    }
                }
            }
        }
    }

    fun saveSchedule(schedule: Schedule, isEdit: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = if (isEdit) repository.updateSchedule(schedule) else repository.createSchedule(schedule)
            when (result) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun deleteSchedule(scheduleId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val result = repository.deleteSchedule(scheduleId)) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }
}