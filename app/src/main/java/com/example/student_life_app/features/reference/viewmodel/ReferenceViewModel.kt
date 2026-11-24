package com.example.student_life_app.features.reference.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.reference.data.repository.FakeReferenceRepository
import com.example.student_life_app.features.reference.data.repository.ReferenceRepository
import com.example.student_life_app.features.reference.model.Reference
import com.example.student_life_app.features.reference.model.ReferenceCategory
import com.example.student_life_app.features.reference.service.ReferenceService
import com.example.student_life_app.features.reference.service.ReferenceServiceImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ReferenceFormErrors(
    val titleError: String? = null,
    val urlError: String? = null,
    val courseError: String? = null
) {
    val hasError: Boolean
        get() = titleError != null || urlError != null || courseError != null
}

data class ReferenceUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: ReferenceCategory? = null, // null = Semua
    val allReferences: List<Reference> = emptyList(),
    val filteredReferences: List<Reference> = emptyList(),
    val totalCount: Int = 0,
    val favoriteCount: Int = 0,
    val errorMessage: String? = null
)

class ReferenceViewModel(
    private val repository: ReferenceRepository = FakeReferenceRepository(),
    private val service: ReferenceService = ReferenceServiceImpl()
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _allReferences = MutableStateFlow<List<Reference>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<ReferenceCategory?>(null)

    private var loadJob: Job? = null

    val uiState: StateFlow<ReferenceUiState> = combine(
        _isLoading,
        _errorMessage,
        _allReferences,
        _searchQuery,
        _selectedCategory
    ) { isLoading, errorMessage, refs, query, category ->
        val total = refs.size
        val favCount = refs.count { it.isFavorite }
        ReferenceUiState(
            isLoading = isLoading,
            searchQuery = query,
            selectedCategory = category,
            allReferences = refs,
            filteredReferences = applyFilter(refs, query, category),
            totalCount = total,
            favoriteCount = favCount,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReferenceUiState()
    )

    init {
        loadReferences("default_user")
    }

    fun loadReferences(userId: String = "default_user") {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getReferences(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _allReferences.value = resource.data
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

    fun onCategorySelect(category: ReferenceCategory?) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(referenceId: String) {
        viewModelScope.launch {
            val currentItem = _allReferences.value.firstOrNull { it.id == referenceId }
            val currentStatus = currentItem?.isFavorite ?: false
            repository.toggleFavorite(referenceId, !currentStatus)
            loadReferences()
        }
    }

    fun createReference(
        title: String,
        url: String,
        category: ReferenceCategory,
        courseName: String,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val error = service.validateReferenceInput(title, url, courseName)
            if (error != null) {
                _errorMessage.value = error
                return@launch
            }
            val newRef = Reference(
                id = UUID.randomUUID().toString(),
                userId = "default_user",
                title = title,
                url = url,
                category = category,
                courseName = courseName,
                notes = notes,
                isFavorite = false
            )
            repository.createReference(newRef)
            loadReferences()
            onSuccess()
        }
    }

    fun updateReference(
        id: String,
        title: String,
        url: String,
        category: ReferenceCategory,
        courseName: String,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val error = service.validateReferenceInput(title, url, courseName)
            if (error != null) {
                _errorMessage.value = error
                return@launch
            }
            val current = _allReferences.value.firstOrNull { it.id == id }
            if (current != null) {
                val updated = current.copy(
                    title = title,
                    url = url,
                    category = category,
                    courseName = courseName,
                    notes = notes
                )
                repository.updateReference(updated)
                loadReferences()
                onSuccess()
            }
        }
    }

    fun deleteReference(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteReference(id)
            loadReferences()
            onSuccess()
        }
    }

    fun validateReferenceForm(title: String, url: String, courseName: String): ReferenceFormErrors {
        val validationError = service.validateReferenceInput(title, url, courseName)
        if (validationError == null) return ReferenceFormErrors()

        val titleErr = if (title.isBlank()) "Judul tidak boleh kosong" else null
        val urlErr = when {
            url.isBlank() -> "URL tidak boleh kosong"
            !service.isValidUrl(url) -> "Format URL tidak valid"
            else -> null
        }
        val courseErr = if (courseName.isBlank()) "Mata kuliah tidak boleh kosong" else null
        return ReferenceFormErrors(
            titleError = titleErr,
            urlError = urlErr,
            courseError = courseErr
        )
    }

    private fun applyFilter(
        references: List<Reference>,
        query: String,
        category: ReferenceCategory?
    ): List<Reference> {
        return references.filter { ref ->
            val matchesCategory = category == null || ref.category == category
            val matchesQuery = query.isBlank() ||
                ref.title.contains(query, ignoreCase = true) ||
                ref.courseName.contains(query, ignoreCase = true) ||
                (ref.notes?.contains(query, ignoreCase = true) == true)
            matchesCategory && matchesQuery
        }
    }
}
