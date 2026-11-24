package com.example.student_life_app.features.note.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.note.data.repository.FakeNoteRepository
import com.example.student_life_app.features.note.data.repository.NoteRepository
import com.example.student_life_app.features.note.model.Note
import com.example.student_life_app.features.note.service.NoteService
import com.example.student_life_app.features.note.service.NoteServiceImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    var searchQuery by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Semua (4)")

    private var loadJob: Job? = null

    val uiState: StateFlow<NoteUiState> = combine(
        _isLoading,
        _errorMessage,
        _allNotes,
        _searchQuery,
        _selectedCategory
    ) { isLoading, errorMessage, notes, query, selectedCategory ->
        val total = notes.size
        val photoCount = notes.count { it.imageUrl != null }
        val courseCount = notes.map { it.courseName }.distinct().size

        val categories = mutableListOf("Semua ($total)")
        notes.map { it.courseName }.distinct().forEach { course ->
            val count = notes.count { it.courseName == course }
            categories.add("$course ($count)")
        }

        val activeCategory = if (categories.any { it.startsWith(selectedCategory.substringBefore(" (")) }) {
            categories.firstOrNull { it.startsWith(selectedCategory.substringBefore(" (")) } ?: categories.first()
        } else {
            categories.first()
        }

        NoteUiState(
            isLoading = isLoading,
            searchQuery = query,
            selectedCategory = activeCategory,
            categoryFilters = categories,
            allNotes = notes,
            filteredNotes = applyFilter(notes, query, activeCategory),
            totalNotesCount = total,
            photoNotesCount = photoCount,
            coursesCount = courseCount,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteUiState()
    )

    init {
        loadNotes("default_user")
    }

    fun loadNotes(userId: String = "default_user") {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getNotes(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _allNotes.value = resource.data
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

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
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
