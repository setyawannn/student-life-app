package com.example.student_life_app.features.goal.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("deadline") val deadline: String,
    @SerialName("progress") val progress: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)
