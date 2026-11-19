package com.example.student_life_app.features.goal.data.dto

import com.example.student_life_app.features.goal.model.GoalMilestone
import com.example.student_life_app.features.goal.model.StudyGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val jsonFormatter = Json { ignoreUnknownKeys = true; coerceInputValues = true }

@Serializable
data class GoalMetadataPayload(
    val category: String? = null,
    val priority: String? = null,
    val notes: String? = null,
    val milestones: List<GoalMilestone> = emptyList(),
    val strategy: String? = null,
    val weeklyReminder: Boolean? = true
)

@Serializable
data class GoalDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("deadline") val deadline: String,
    @SerialName("progress") val progress: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    fun toDomain(): StudyGoal {
        var parsedCategory = "Pemrograman Mobile"
        var parsedPriority = "Tinggi"
        var parsedNotes = description
        var parsedMilestones: List<GoalMilestone> = emptyList()
        var parsedStrategy: String? = null
        var parsedReminder = true

        val desc = description
        if (!desc.isNullOrBlank() && desc.startsWith("{") && desc.endsWith("}")) {
            runCatching {
                val payload = jsonFormatter.decodeFromString<GoalMetadataPayload>(desc)
                parsedCategory = payload.category ?: parsedCategory
                parsedPriority = payload.priority ?: parsedPriority
                parsedNotes = payload.notes
                parsedMilestones = payload.milestones
                parsedStrategy = payload.strategy
                parsedReminder = payload.weeklyReminder ?: true
            }
        }

        return StudyGoal(
            id = id.orEmpty(),
            userId = userId,
            title = title,
            category = parsedCategory,
            priority = parsedPriority,
            description = parsedNotes,
            deadline = deadline,
            progress = progress,
            milestones = parsedMilestones,
            strategy = parsedStrategy,
            weeklyReminder = parsedReminder
        )
    }

    companion object {
        fun fromDomain(goal: StudyGoal): GoalDto {
            val payload = GoalMetadataPayload(
                category = goal.category,
                priority = goal.priority,
                notes = goal.description,
                milestones = goal.milestones,
                strategy = goal.strategy,
                weeklyReminder = goal.weeklyReminder
            )
            val jsonDescription = jsonFormatter.encodeToString(GoalMetadataPayload.serializer(), payload)
            val timeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val nowTime = timeFormatter.format(Date())

            return GoalDto(
                id = goal.id.ifBlank { null },
                userId = goal.userId,
                title = goal.title,
                description = jsonDescription,
                deadline = goal.deadline,
                progress = goal.progress,
                createdAt = nowTime,
                updatedAt = nowTime
            )
        }
    }
}
