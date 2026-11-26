package com.example.student_life_app.features.note.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.note.data.repository.NoteRepository
import com.example.student_life_app.features.note.data.repository.SupabaseNoteRepository
import com.example.student_life_app.features.note.model.Note
import com.example.student_life_app.features.note.service.NoteService
import com.example.student_life_app.features.note.service.NoteServiceImpl
import com.example.student_life_app.features.note.utils.NoteUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteFormErrors(
    val titleError: String? = null,
    val courseError: String? = null,
    val contentError: String? = null
) {
    val hasError: Boolean
        get() = titleError != null || courseError != null || contentError != null
}

data class NoteUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String = "Semua (0)",
    val categoryFilters: List<String> = emptyList(),
    val allNotes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val totalNotesCount: Int = 0,
    val photoNotesCount: Int = 0,
    val coursesCount: Int = 0,
    val errorMessage: String? = null
)

class NoteViewModel(
    private val repository: NoteRepository = SupabaseNoteRepository(),
    private val service: NoteService = NoteServiceImpl()
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _isSaving = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Semua (0)")

    private var loadJob: Job? = null

    private val _statusFlow = combine(_isLoading, _isSaving, _errorMessage) { loading, saving, error ->
        Triple(loading, saving, error)
    }

    val uiState: StateFlow<NoteUiState> = combine(
        _statusFlow,
        _allNotes,
        _searchQuery,
        _selectedCategory
    ) { (isLoading, isSaving, errorMessage), notes, query, selectedCategory ->
        val total = notes.size
        val photoCount = notes.count { !it.imageUrl.isNullOrBlank() }
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
            isSaving = isSaving,
            isUploading = isSaving,
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

    fun validateNoteForm(title: String, courseName: String, content: String): NoteFormErrors {
        val error = service.validateNoteInput(title, courseName, content)
        if (error == null) return NoteFormErrors()
        return NoteFormErrors(
            titleError = if (title.isBlank()) "Judul catatan tidak boleh kosong" else null,
            courseError = if (courseName.isBlank()) "Nama mata kuliah tidak boleh kosong" else null,
            contentError = if (content.isBlank()) "Isi catatan tidak boleh kosong" else null
        )
    }

    fun saveNote(
        note: Note,
        isEdit: Boolean,
        imageBytes: ByteArray?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            val result = if (isEdit) {
                repository.updateNoteWithImage(note, imageBytes)
            } else {
                repository.createNoteWithImage(note, imageBytes)
            }
            when (result) {
                is Resource.Success -> {
                    _isSaving.value = false
                    onSuccess()
                }
                is Resource.Error -> {
                    _isSaving.value = false
                    _errorMessage.value = result.message
                }
                is Resource.Loading -> _isSaving.value = false
            }
        }
    }

    fun deleteNote(noteId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _errorMessage.value = null
            when (val result = repository.deleteNote(noteId)) {
                is Resource.Success -> onSuccess()
                is Resource.Error -> _errorMessage.value = result.message
                is Resource.Loading -> Unit
            }
        }
    }

    fun onBookmarkToggle(noteId: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            when (val result = repository.toggleBookmark(noteId)) {
                is Resource.Error -> _errorMessage.value = result.message
                else -> Unit
            }
        }
    }

    private fun applyFilter(notes: List<Note>, query: String, category: String): List<Note> {
        val rawCategory = if (category.startsWith("Semua")) "" else category.substringBefore(" (").trim()
        return notes.filter { note ->
            val matchesCategory = rawCategory.isEmpty() || note.courseName.equals(rawCategory, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                note.title.contains(query, ignoreCase = true) ||
                NoteUtils.stripHtmlTags(note.content).contains(query, ignoreCase = true) ||
                note.courseName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }
}
