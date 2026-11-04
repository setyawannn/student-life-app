package com.example.student_life_app.features.task.model

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED
}

data class Task(
    val id: String,
    val userId: String,
    val title: String,
    val courseName: String,
    val description: String? = null,
    val dueDate: String,
    val status: TaskStatus = TaskStatus.TODO
)
