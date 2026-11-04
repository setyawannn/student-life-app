package com.example.student_life_app.features.reference.model

enum class ReferenceCategory {
    JURNAL,
    DRIVE,
    VIDEO
}

data class Reference(
    val id: String,
    val userId: String,
    val title: String,
    val url: String,
    val category: ReferenceCategory,
    val courseName: String,
    val notes: String? = null,
    val isFavorite: Boolean = false
)
