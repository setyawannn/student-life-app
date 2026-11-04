package com.example.student_life_app.features.goal.model

data class StudyGoal(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val deadline: String,
    val progress: Int = 0 // 0 s.d. 100
)
