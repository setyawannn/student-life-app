package com.example.student_life_app.features.note.model

data class Note(
    val id: String,
    val userId: String = "default_user",
    val title: String,
    val courseName: String,
    val content: String,
    val imageUrl: String? = null,
    val badgeType: String? = null, // "Diag" atau "Photo"
    val dateText: String = "28 Okt 2026",
    val isBookmarked: Boolean = false,
    val createdAt: String? = null
)
