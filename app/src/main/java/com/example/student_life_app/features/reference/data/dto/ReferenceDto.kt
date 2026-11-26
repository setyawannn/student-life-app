package com.example.student_life_app.features.reference.data.dto

import com.example.student_life_app.features.reference.model.Reference
import com.example.student_life_app.features.reference.model.ReferenceCategory
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

fun ReferenceDto.toDomain(): Reference = Reference(
    id = id.orEmpty(),
    userId = userId,
    title = title,
    url = url,
    category = try {
        ReferenceCategory.valueOf(category.uppercase())
    } catch (_: Exception) {
        ReferenceCategory.JURNAL
    },
    courseName = courseName,
    notes = notes,
    isFavorite = isFavorite
)

fun Reference.toDto(userId: String): ReferenceDto = ReferenceDto(
    id = id.ifBlank { null },
    userId = userId,
    title = title,
    url = url,
    category = category.name,
    courseName = courseName,
    notes = notes,
    isFavorite = isFavorite
)
