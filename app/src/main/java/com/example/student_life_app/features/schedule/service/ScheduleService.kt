package com.example.student_life_app.features.schedule.service

import com.example.student_life_app.features.schedule.model.Schedule

interface ScheduleService {
    fun validateScheduleInput(courseName: String, room: String, lecturer: String): String?
    fun validateTimeRange(startTime: String, endTime: String): String?
    fun calculateDurationMinutes(startTime: String, endTime: String): Int
    fun formatDuration(totalMinutes: Int): String
    fun sortSchedulesByTime(schedules: List<Schedule>): List<Schedule>
}

class ScheduleServiceImpl : ScheduleService {

    private val timeRegex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

    override fun validateScheduleInput(courseName: String, room: String, lecturer: String): String? {
        if (courseName.isBlank()) return "Nama mata kuliah tidak boleh kosong"
        if (room.isBlank()) return "Ruang kelas tidak boleh kosong"
        if (lecturer.isBlank()) return "Nama dosen pengampu tidak boleh kosong"
        return null
    }

    override fun validateTimeRange(startTime: String, endTime: String): String? {
        if (!timeRegex.matches(startTime)) return "Jam mulai harus berformat HH:mm, contoh 08:00"
        if (!timeRegex.matches(endTime)) return "Jam selesai harus berformat HH:mm, contoh 10:30"
        if (endTime <= startTime) return "Jam selesai harus setelah jam mulai"
        return null
    }

    override fun calculateDurationMinutes(startTime: String, endTime: String): Int {
        return try {
            val (sh, sm) = startTime.split(":").map { it.toInt() }
            val (eh, em) = endTime.split(":").map { it.toInt() }
            (eh * 60 + em) - (sh * 60 + sm)
        } catch (e: Exception) {
            0
        }
    }

    override fun formatDuration(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours > 0 && minutes > 0 -> "$hours jam $minutes menit"
            hours > 0 -> "$hours jam"
            else -> "$minutes menit"
        }
    }

    override fun sortSchedulesByTime(schedules: List<Schedule>): List<Schedule> {
        return schedules.sortedBy { it.startTime }
    }
}