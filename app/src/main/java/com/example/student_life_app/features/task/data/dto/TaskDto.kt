package com.example.student_life_app.features.task.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("description") val description: String? = null,
    @SerialName("due_date") val dueDate: String,
    @SerialName("status") val status: String = "TODO",
    @SerialName("created_at") val createdAt: String? = null
)
