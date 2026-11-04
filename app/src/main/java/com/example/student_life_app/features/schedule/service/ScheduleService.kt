package com.example.student_life_app.features.schedule.service

import com.example.student_life_app.features.schedule.model.Schedule

interface ScheduleService {
    fun validateScheduleInput(courseName: String, room: String, lecturer: String): String?
    fun sortSchedulesByTime(schedules: List<Schedule>): List<Schedule>
}

class ScheduleServiceImpl : ScheduleService {
    override fun validateScheduleInput(courseName: String, room: String, lecturer: String): String? {
        if (courseName.isBlank()) return "Nama mata kuliah tidak boleh kosong"
        if (room.isBlank()) return "Ruang kelas tidak boleh kosong"
        if (lecturer.isBlank()) return "Nama dosen pengampu tidak boleh kosong"
        return null
    }

    override fun sortSchedulesByTime(schedules: List<Schedule>): List<Schedule> {
        return schedules.sortedBy { it.startTime }
    }
}
