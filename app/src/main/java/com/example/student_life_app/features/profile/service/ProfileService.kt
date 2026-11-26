package com.example.student_life_app.features.profile.service

data class ProfileValidationErrors(
    val fullNameError: String? = null,
    val nimError: String? = null
) {
    val hasError: Boolean
        get() = fullNameError != null || nimError != null
}

interface ProfileService {
    fun validateProfile(fullName: String, nim: String): ProfileValidationErrors
}

class ProfileServiceImpl : ProfileService {
    override fun validateProfile(fullName: String, nim: String): ProfileValidationErrors {
        val trimmedName = fullName.trim()
        val trimmedNim = nim.trim()

        val fullNameError = if (trimmedName.isEmpty()) {
            "Nama lengkap tidak boleh kosong"
        } else {
            null
        }
        val nimError = if (trimmedNim.isNotEmpty() && trimmedNim.length < 5) {
            "NIM minimal 5 karakter"
        } else {
            null
        }
        return ProfileValidationErrors(
            fullNameError = fullNameError,
            nimError = nimError
        )
    }
}
