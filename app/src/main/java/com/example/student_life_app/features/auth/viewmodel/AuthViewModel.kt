package com.example.student_life_app.features.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.auth.data.repository.AuthRepository
import com.example.student_life_app.features.auth.data.repository.SupabaseAuthRepository
import com.example.student_life_app.features.auth.service.AuthService
import com.example.student_life_app.features.auth.service.AuthServiceImpl
import com.example.student_life_app.features.auth.service.LoginValidationErrors
import com.example.student_life_app.features.auth.service.RegisterValidationErrors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val loginErrors: LoginValidationErrors = LoginValidationErrors(),
    val registerErrors: RegisterValidationErrors = RegisterValidationErrors()
)

class AuthViewModel(
    private val repository: AuthRepository = SupabaseAuthRepository(),
    private val service: AuthService = AuthServiceImpl()
) : ViewModel() {

    // State input TextField memakai mutableStateOf agar posisi kursor tetap stabil
    var loginIdentifier by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set

    var registerFullName by mutableStateOf("")
        private set
    var registerNim by mutableStateOf("")
        private set
    var registerEmail by mutableStateOf("")
        private set
    var registerPassword by mutableStateOf("")
        private set
    var registerConfirmPassword by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isAuthenticated = MutableStateFlow(false)
    private val _loginErrors = MutableStateFlow(LoginValidationErrors())
    private val _registerErrors = MutableStateFlow(RegisterValidationErrors())

    val uiState: StateFlow<AuthUiState> = combine(
        _isLoading,
        _errorMessage,
        _isAuthenticated,
        _loginErrors,
        _registerErrors
    ) { isLoading, errorMessage, isAuthenticated, loginErrors, registerErrors ->
        AuthUiState(
            isLoading = isLoading,
            errorMessage = errorMessage,
            isAuthenticated = isAuthenticated,
            loginErrors = loginErrors,
            registerErrors = registerErrors
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AuthUiState()
    )

    fun onLoginIdentifierChange(value: String) {
        loginIdentifier = value
        if (_loginErrors.value.identifierError != null) {
            _loginErrors.value = _loginErrors.value.copy(identifierError = null)
        }
        _errorMessage.value = null
    }

    fun onLoginPasswordChange(value: String) {
        loginPassword = value
        if (_loginErrors.value.passwordError != null) {
            _loginErrors.value = _loginErrors.value.copy(passwordError = null)
        }
        _errorMessage.value = null
    }

    fun onRegisterFullNameChange(value: String) {
        registerFullName = value
        if (_registerErrors.value.fullNameError != null) {
            _registerErrors.value = _registerErrors.value.copy(fullNameError = null)
        }
        _errorMessage.value = null
    }

    fun onRegisterNimChange(value: String) {
        registerNim = value
        if (_registerErrors.value.nimError != null) {
            _registerErrors.value = _registerErrors.value.copy(nimError = null)
        }
        _errorMessage.value = null
    }

    fun onRegisterEmailChange(value: String) {
        registerEmail = value
        if (_registerErrors.value.emailError != null) {
            _registerErrors.value = _registerErrors.value.copy(emailError = null)
        }
        _errorMessage.value = null
    }

    fun onRegisterPasswordChange(value: String) {
        registerPassword = value
        if (_registerErrors.value.passwordError != null) {
            _registerErrors.value = _registerErrors.value.copy(passwordError = null)
        }
        _errorMessage.value = null
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        registerConfirmPassword = value
        if (_registerErrors.value.confirmPasswordError != null) {
            _registerErrors.value = _registerErrors.value.copy(confirmPasswordError = null)
        }
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun login() {
        val validation = service.validateLogin(loginIdentifier, loginPassword)
        _loginErrors.value = validation
        if (validation.hasError) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.signIn(loginIdentifier, loginPassword)) {
                is Resource.Success -> {
                    _isAuthenticated.value = true
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                }
                Resource.Loading -> Unit
            }
            _isLoading.value = false
        }
    }

    fun register() {
        val validation = service.validateRegister(
            fullName = registerFullName,
            nim = registerNim,
            email = registerEmail,
            password = registerPassword,
            confirmPassword = registerConfirmPassword
        )
        _registerErrors.value = validation
        if (validation.hasError) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (
                val result = repository.signUp(
                    fullName = registerFullName,
                    nim = registerNim,
                    email = registerEmail,
                    password = registerPassword
                )
            ) {
                is Resource.Success -> {
                    _isAuthenticated.value = true
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                }
                Resource.Loading -> Unit
            }
            _isLoading.value = false
        }
    }
}
