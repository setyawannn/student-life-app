package com.example.student_life_app.features.goal.model

import kotlinx.serialization.Serializable

@Serializable
data class GoalMilestone(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isCompleted: Boolean = false
)

data class StudyGoal(
    val id: String,
    val userId: String,
    val title: String,
    val category: String = "Pemrograman Mobile",
    val priority: String = "Tinggi",
    val description: String? = null,
    val deadline: String,
    val progress: Int = 0,
    val milestones: List<GoalMilestone> = emptyList(),
    val strategy: String? = null,
    val weeklyReminder: Boolean = true
)
