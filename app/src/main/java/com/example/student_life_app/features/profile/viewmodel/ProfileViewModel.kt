package com.example.student_life_app.features.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.profile.data.repository.ProfileRepository
import com.example.student_life_app.features.profile.data.repository.SupabaseProfileRepository
import com.example.student_life_app.features.profile.service.ProfileService
import com.example.student_life_app.features.profile.service.ProfileServiceImpl
import com.example.student_life_app.features.profile.service.ProfileValidationErrors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLoggingOut: Boolean = false,
    val savedFullName: String = "",
    val savedNim: String = "",
    val email: String = "",
    val validationErrors: ProfileValidationErrors = ProfileValidationErrors(),
    val snackbarMessage: String? = null,
    val loggedOut: Boolean = false
)

class ProfileViewModel(
    private val repository: ProfileRepository = SupabaseProfileRepository(),
    private val service: ProfileService = ProfileServiceImpl()
) : ViewModel() {

    // State input TextField memakai mutableStateOf agar posisi kursor tetap stabil
    var fullNameInput by mutableStateOf("")
        private set
    var nimInput by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _isSaving = MutableStateFlow(false)
    private val _isLoggingOut = MutableStateFlow(false)
    private val _savedFullName = MutableStateFlow("")
    private val _savedNim = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _validationErrors = MutableStateFlow(ProfileValidationErrors())
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    private val _loggedOut = MutableStateFlow(false)

    private val _statusTriple = combine(_isLoading, _isSaving, _isLoggingOut) { l, s, o ->
        Triple(l, s, o)
    }
    private val _profileTriple = combine(_savedFullName, _savedNim, _email) { name, nim, mail ->
        Triple(name, nim, mail)
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        _statusTriple,
        _profileTriple,
        _validationErrors,
        _snackbarMessage,
        _loggedOut
    ) { (isLoading, isSaving, isLoggingOut), (savedName, savedNim, email), errors, snackbar, loggedOut ->
        ProfileUiState(
            isLoading = isLoading,
            isSaving = isSaving,
            isLoggingOut = isLoggingOut,
            savedFullName = savedName,
            savedNim = savedNim,
            email = email,
            validationErrors = errors,
            snackbarMessage = snackbar,
            loggedOut = loggedOut
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState(isLoading = true)
    )

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getCurrentProfile()) {
                is Resource.Success -> {
                    val data = result.data
                    _savedFullName.value = data.profile.fullName
                    _savedNim.value = data.profile.nim
                    _email.value = data.email
                    fullNameInput = data.profile.fullName
                    nimInput = data.profile.nim
                }
                is Resource.Error -> {
                    _snackbarMessage.value = result.message
                }
                Resource.Loading -> Unit
            }
            _isLoading.value = false
        }
    }

    fun onFullNameChange(value: String) {
        fullNameInput = value
        if (_validationErrors.value.fullNameError != null) {
            _validationErrors.value = _validationErrors.value.copy(fullNameError = null)
        }
    }

    fun onNimChange(value: String) {
        nimInput = value
        if (_validationErrors.value.nimError != null) {
            _validationErrors.value = _validationErrors.value.copy(nimError = null)
        }
    }

    fun consumeSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun saveProfile() {
        val validation = service.validateProfile(fullNameInput, nimInput)
        _validationErrors.value = validation
        if (validation.hasError) return

        viewModelScope.launch {
            _isSaving.value = true
            when (val result = repository.updateProfile(fullNameInput, nimInput)) {
                is Resource.Success -> {
                    _savedFullName.value = result.data.fullName
                    _savedNim.value = result.data.nim
                    fullNameInput = result.data.fullName
                    nimInput = result.data.nim
                    _snackbarMessage.value = "Profil berhasil diperbarui"
                }
                is Resource.Error -> {
                    _snackbarMessage.value = result.message
                }
                Resource.Loading -> Unit
            }
            _isSaving.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggingOut.value = true
            when (val result = repository.signOut()) {
                is Resource.Success -> {
                    _loggedOut.value = true
                }
                is Resource.Error -> {
                    _snackbarMessage.value = result.message
                }
                Resource.Loading -> Unit
            }
            _isLoggingOut.value = false
        }
    }
}
