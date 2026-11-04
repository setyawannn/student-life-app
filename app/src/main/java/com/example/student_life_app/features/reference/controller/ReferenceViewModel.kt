package com.example.student_life_app.features.reference.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.reference.data.repository.ReferenceRepository
import com.example.student_life_app.features.reference.model.Reference
import com.example.student_life_app.features.reference.model.ReferenceCategory
import com.example.student_life_app.features.reference.service.ReferenceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReferenceUiState(
    val isLoading: Boolean = false,
    val selectedCategory: ReferenceCategory = ReferenceCategory.JURNAL,
    val references: List<Reference> = emptyList(),
    val errorMessage: String? = null
)

class ReferenceViewModel(
    private val repository: ReferenceRepository,
    private val service: ReferenceService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReferenceUiState())
    val uiState: StateFlow<ReferenceUiState> = _uiState.asStateFlow()

    fun selectCategory(category: ReferenceCategory, userId: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadReferences(userId, category)
    }

    fun loadReferences(userId: String, category: ReferenceCategory = _uiState.value.selectedCategory) {
        viewModelScope.launch {
            repository.getReferencesByCategory(userId, category).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> _uiState.update { it.copy(isLoading = false, references = resource.data) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }

    fun toggleFavorite(referenceId: String, currentStatus: Boolean, userId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(referenceId, !currentStatus)
            loadReferences(userId)
        }
    }
}
