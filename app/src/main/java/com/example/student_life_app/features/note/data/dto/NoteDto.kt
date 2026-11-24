package com.example.student_life_app.features.note.data.dto

import com.example.student_life_app.features.note.model.Note
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class NoteDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("content") val content: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("badge_type") val badgeType: String? = null,
    @SerialName("is_bookmarked") val isBookmarked: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

private fun formatCreatedDate(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Baru saja"
    return try {
        val localeId = Locale.forLanguageTag("id-ID")
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val output = SimpleDateFormat("dd MMM yyyy", localeId)
        val parsed = input.parse(createdAt.take(19))
        if (parsed != null) output.format(parsed) else createdAt.take(10)
    } catch (_: Exception) {
        createdAt.take(10)
    }
}

fun NoteDto.toDomain(): Note = Note(
    id = id.orEmpty(),
    userId = userId,
    title = title,
    courseName = courseName,
    content = content,
    imageUrl = imageUrl,
    badgeType = badgeType,
    dateText = formatCreatedDate(createdAt),
    isBookmarked = isBookmarked,
    createdAt = createdAt
)

fun Note.toDto(ownerId: String): NoteDto = NoteDto(
    id = id,
    userId = ownerId,
    title = title,
    courseName = courseName,
    content = content,
    imageUrl = imageUrl,
    badgeType = badgeType,
    isBookmarked = isBookmarked,
    createdAt = createdAt
)
