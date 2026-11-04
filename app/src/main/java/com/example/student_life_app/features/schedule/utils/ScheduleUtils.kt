package com.example.student_life_app.features.schedule.utils

import com.example.student_life_app.features.schedule.model.DayOfWeekEnum

object ScheduleUtils {
    fun parseDay(value: String): DayOfWeekEnum {
        return try {
            DayOfWeekEnum.valueOf(value.uppercase())
        } catch (_: Exception) {
            DayOfWeekEnum.SENIN
        }
    }
}
