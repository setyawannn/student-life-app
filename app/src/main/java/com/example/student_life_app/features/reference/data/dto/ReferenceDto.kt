package com.example.student_life_app.features.reference.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferenceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("category") val category: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("is_favorite") val isFavorite: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)
