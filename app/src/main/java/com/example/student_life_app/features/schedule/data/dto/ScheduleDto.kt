package com.example.student_life_app.features.schedule.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("day_of_week") val dayOfWeek: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    @SerialName("room") val room: String,
    @SerialName("lecturer_name") val lecturerName: String,
    @SerialName("created_at") val createdAt: String? = null
)
