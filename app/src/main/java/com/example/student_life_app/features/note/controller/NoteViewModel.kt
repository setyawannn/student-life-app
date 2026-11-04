package com.example.student_life_app.features.note.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.note.data.repository.NoteRepository
import com.example.student_life_app.features.note.model.Note
import com.example.student_life_app.features.note.service.NoteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val errorMessage: String? = null
)

class NoteViewModel(
    private val repository: NoteRepository,
    private val service: NoteService
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    fun loadNotes(userId: String) {
        viewModelScope.launch {
            repository.getNotes(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> _uiState.update { it.copy(isLoading = false, notes = resource.data) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }
}
