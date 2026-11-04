package com.example.student_life_app.features.auth.model

data class UserSession(
    val userId: String,
    val email: String,
    val accessToken: String? = null
)
