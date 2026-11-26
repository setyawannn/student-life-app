package com.example.student_life_app.features.auth.service

import android.util.Patterns

data class LoginValidationErrors(
    val identifierError: String? = null,
    val passwordError: String? = null
) {
    val hasError: Boolean
        get() = identifierError != null || passwordError != null
}

data class RegisterValidationErrors(
    val fullNameError: String? = null,
    val nimError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val hasError: Boolean
        get() = fullNameError != null ||
            nimError != null ||
            emailError != null ||
            passwordError != null ||
            confirmPasswordError != null
}

interface AuthService {
    fun validateLogin(identifier: String, password: String): LoginValidationErrors
    fun validateRegister(
        fullName: String,
        nim: String,
        email: String,
        password: String,
        confirmPassword: String
    ): RegisterValidationErrors
}

class AuthServiceImpl : AuthService {

    override fun validateLogin(identifier: String, password: String): LoginValidationErrors {
        val trimmedIdentifier = identifier.trim()
        val identifierError = when {
            trimmedIdentifier.isEmpty() -> "Email atau NIM wajib diisi"
            trimmedIdentifier.contains("@") &&
                !Patterns.EMAIL_ADDRESS.matcher(trimmedIdentifier).matches() ->
                "Format email tidak valid"
            else -> null
        }
        val passwordError = when {
            password.isEmpty() -> "Password wajib diisi"
            password.length < 6 -> "Password minimal 6 karakter"
            else -> null
        }
        return LoginValidationErrors(
            identifierError = identifierError,
            passwordError = passwordError
        )
    }

    override fun validateRegister(
        fullName: String,
        nim: String,
        email: String,
        password: String,
        confirmPassword: String
    ): RegisterValidationErrors {
        val trimmedName = fullName.trim()
        val trimmedNim = nim.trim()
        val trimmedEmail = email.trim()

        val fullNameError = if (trimmedName.isEmpty()) "Nama lengkap wajib diisi" else null
        val nimError = when {
            trimmedNim.isEmpty() -> "NIM wajib diisi"
            trimmedNim.length < 5 -> "NIM minimal 5 karakter"
            else -> null
        }
        val emailError = when {
            trimmedEmail.isEmpty() -> "Email wajib diisi"
            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> "Format email tidak valid"
            else -> null
        }
        val passwordError = when {
            password.isEmpty() -> "Password wajib diisi"
            password.length < 6 -> "Password minimal 6 karakter"
            else -> null
        }
        val confirmPasswordError = when {
            confirmPassword.isEmpty() -> "Konfirmasi password wajib diisi"
            confirmPassword != password -> "Konfirmasi password tidak cocok"
            else -> null
        }

        return RegisterValidationErrors(
            fullNameError = fullNameError,
            nimError = nimError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )
    }
}
