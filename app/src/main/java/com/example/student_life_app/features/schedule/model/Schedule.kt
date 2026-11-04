package com.example.student_life_app.features.schedule.model

enum class DayOfWeekEnum {
    SENIN,
    SELASA,
    RABU,
    KAMIS,
    JUMAT,
    SABTU
}

data class Schedule(
    val id: String,
    val userId: String,
    val courseName: String,
    val dayOfWeek: DayOfWeekEnum,
    val startTime: String,
    val endTime: String,
    val room: String,
    val lecturerName: String
)
