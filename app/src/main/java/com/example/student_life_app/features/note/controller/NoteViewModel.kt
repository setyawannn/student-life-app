package com.example.student_life_app.features.note.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.note.data.repository.FakeNoteRepository
import com.example.student_life_app.features.note.data.repository.NoteRepository
import com.example.student_life_app.features.note.model.Note
import com.example.student_life_app.features.note.service.NoteService
import com.example.student_life_app.features.note.service.NoteServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String = "Semua (4)",
    val categoryFilters: List<String> = emptyList(),
    val allNotes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val totalNotesCount: Int = 0,
    val photoNotesCount: Int = 0,
    val coursesCount: Int = 0,
    val errorMessage: String? = null
)

class NoteViewModel(
    private val repository: NoteRepository = FakeNoteRepository(),
    private val service: NoteService = NoteServiceImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    init {
        loadNotes("default_user")
    }

    fun loadNotes(userId: String = "default_user") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getNotes(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val notes = resource.data
                        val total = notes.size
                        val photoCount = notes.count { it.imageUrl != null }
                        val courseCount = notes.map { it.courseName }.distinct().size

                        // Buat daftar kategori filter yang dinamis dari data
                        val categories = mutableListOf("Semua ($total)")
                        notes.map { it.courseName }.distinct().forEach { course ->
                            val count = notes.count { it.courseName == course }
                            categories.add("$course ($count)")
                        }

                        _uiState.update { current ->
                            val activeCategory = if (categories.any { it.startsWith(current.selectedCategory.substringBefore(" (")) }) {
                                categories.firstOrNull { it.startsWith(current.selectedCategory.substringBefore(" (")) } ?: categories.first()
                            } else {
                                categories.first()
                            }

                            current.copy(
                                isLoading = false,
                                allNotes = notes,
                                totalNotesCount = total,
                                photoNotesCount = photoCount,
                                coursesCount = courseCount,
                                categoryFilters = categories,
                                selectedCategory = activeCategory,
                                filteredNotes = applyFilter(notes, current.searchQuery, activeCategory)
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
                filteredNotes = applyFilter(current.allNotes, query, current.selectedCategory)
            )
        }
    }

    fun onCategorySelect(category: String) {
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                filteredNotes = applyFilter(current.allNotes, current.searchQuery, category)
            )
        }
    }

    fun onBookmarkToggle(noteId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(noteId)
            loadNotes()
        }
    }

    private fun applyFilter(notes: List<Note>, query: String, category: String): List<Note> {
        val rawCategory = if (category.startsWith("Semua")) "" else category.substringBefore(" (").trim()
        return notes.filter { note ->
            val matchesCategory = rawCategory.isEmpty() || note.courseName.equals(rawCategory, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                note.title.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true) ||
                note.courseName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }
}
