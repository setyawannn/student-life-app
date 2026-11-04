package com.example.student_life_app.features.note.model

data class Note(
    val id: String,
    val userId: String,
    val title: String,
    val courseName: String,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: String? = null
)
