package com.example.student_life_app.features.profile.model

data class UserProfile(
    val id: String,
    val fullName: String,
    val nim: String,
    val avatarUrl: String? = null
)
