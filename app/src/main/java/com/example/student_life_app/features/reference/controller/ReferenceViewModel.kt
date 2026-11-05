package com.example.student_life_app.features.reference.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.reference.data.repository.FakeReferenceRepository
import com.example.student_life_app.features.reference.data.repository.ReferenceRepository
import com.example.student_life_app.features.reference.model.Reference
import com.example.student_life_app.features.reference.model.ReferenceCategory
import com.example.student_life_app.features.reference.service.ReferenceService
import com.example.student_life_app.features.reference.service.ReferenceServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val _uiState = MutableStateFlow(ReferenceUiState())
    val uiState: StateFlow<ReferenceUiState> = _uiState.asStateFlow()

    init {
        loadReferences("default_user")
    }

    fun loadReferences(userId: String = "default_user") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getReferences(userId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val refs = resource.data
                        val total = refs.size
                        val favCount = refs.count { it.isFavorite }

                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                allReferences = refs,
                                totalCount = total,
                                favoriteCount = favCount,
                                filteredReferences = applyFilter(refs, current.searchQuery, current.selectedCategory)
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
                filteredReferences = applyFilter(current.allReferences, query, current.selectedCategory)
            )
        }
    }

    fun onCategorySelect(category: ReferenceCategory?) {
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                filteredReferences = applyFilter(current.allReferences, current.searchQuery, category)
            )
        }
    }

    fun toggleFavorite(referenceId: String) {
        viewModelScope.launch {
            val currentItem = _uiState.value.allReferences.firstOrNull { it.id == referenceId }
            val currentStatus = currentItem?.isFavorite ?: false
            repository.toggleFavorite(referenceId, !currentStatus)
            loadReferences()
        }
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
