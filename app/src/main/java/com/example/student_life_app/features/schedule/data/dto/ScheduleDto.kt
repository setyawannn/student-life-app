package com.example.student_life_app.features.schedule.data.dto

import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.model.Schedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Bentuk data seperti yang dikirim/diterima tabel `schedules` di Supabase. */
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
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/** Database mengembalikan jam "08:00:00", aplikasi memakai "08:00". */
fun ScheduleDto.toDomain(): Schedule = Schedule(
    id = id.orEmpty(),
    userId = userId,
    courseName = courseName,
    dayOfWeek = DayOfWeekEnum.entries.firstOrNull { it.name == dayOfWeek } ?: DayOfWeekEnum.SENIN,
    startTime = startTime.take(5),
    endTime = endTime.take(5),
    room = room,
    lecturerName = lecturerName
)

fun Schedule.toDto(ownerId: String): ScheduleDto = ScheduleDto(
    id = id,
    userId = ownerId,
    courseName = courseName,
    dayOfWeek = dayOfWeek.name,
    startTime = startTime,
    endTime = endTime,
    room = room,
    lecturerName = lecturerName
)

