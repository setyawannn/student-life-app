package com.example.student_life_app.features.schedule.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.model.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedules(userId: String): Flow<Resource<List<Schedule>>>
    fun getSchedulesByDay(userId: String, day: DayOfWeekEnum): Flow<Resource<List<Schedule>>>
    suspend fun createSchedule(schedule: Schedule): Resource<Unit>
    suspend fun deleteSchedule(scheduleId: String): Resource<Unit>
}
