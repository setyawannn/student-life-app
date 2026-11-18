package com.example.student_life_app.features.schedule.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.schedule.data.repository.ScheduleRepository
import com.example.student_life_app.features.schedule.data.repository.SupabaseScheduleRepository
import com.example.student_life_app.features.schedule.model.Schedule
import com.example.student_life_app.features.schedule.service.ScheduleService
import com.example.student_life_app.features.schedule.service.ScheduleServiceImpl
import kotlinx.coroutines.Job
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
    val isSaving: Boolean = false,
    val schedules: List<Schedule> = emptyList(),
    val errorMessage: String? = null
)

class ScheduleViewModel(
    // Untuk kembali ke data dummy, ganti dengan FakeScheduleRepository()
    private val repository: ScheduleRepository = SupabaseScheduleRepository(),
    val service: ScheduleService = ScheduleServiceImpl(),
    private val userId: String = "default_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadSchedules()
    }

    /** Memuat (atau memuat ulang) daftar jadwal. Dipakai juga oleh tombol "Coba Lagi". */
    fun loadSchedules() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getSchedules(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = if (isEdit) repository.updateSchedule(schedule) else repository.createSchedule(schedule)
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
                is Resource.Error -> _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                is Resource.Loading -> _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun deleteSchedule(scheduleId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            when (val result = repository.deleteSchedule(scheduleId)) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }
}