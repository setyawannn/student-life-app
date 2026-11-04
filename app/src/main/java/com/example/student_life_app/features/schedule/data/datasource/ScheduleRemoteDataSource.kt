package com.example.student_life_app.features.schedule.data.datasource

import com.example.student_life_app.features.schedule.data.dto.ScheduleDto

interface ScheduleRemoteDataSource {
    suspend fun getSchedules(userId: String): List<ScheduleDto>
    suspend fun insertSchedule(dto: ScheduleDto): ScheduleDto
    suspend fun updateSchedule(dto: ScheduleDto)
    suspend fun deleteSchedule(scheduleId: String)
}
