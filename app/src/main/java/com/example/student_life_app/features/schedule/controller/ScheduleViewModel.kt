package com.example.student_life_app.features.schedule.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.schedule.data.repository.FakeScheduleRepository
import com.example.student_life_app.features.schedule.data.repository.ScheduleRepository
import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.model.Schedule
import com.example.student_life_app.features.schedule.service.ScheduleService
import com.example.student_life_app.features.schedule.service.ScheduleServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val selectedDay: DayOfWeekEnum = DayOfWeekEnum.SENIN,
    val schedules: List<Schedule> = emptyList(),
    val errorMessage: String? = null
)

class ScheduleViewModel(
    private val repository: ScheduleRepository = FakeScheduleRepository(),
    private val service: ScheduleService = ScheduleServiceImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedules("default_user", DayOfWeekEnum.SENIN)
    }

    fun selectDay(day: DayOfWeekEnum, userId: String = "default_user") {
        _uiState.update { it.copy(selectedDay = day) }
        loadSchedules(userId, day)
    }

    fun loadSchedules(userId: String = "default_user", day: DayOfWeekEnum = _uiState.value.selectedDay) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getSchedulesByDay(userId, day).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val sorted = service.sortSchedulesByTime(resource.data)
                        _uiState.update { it.copy(isLoading = false, schedules = sorted) }
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }
}
