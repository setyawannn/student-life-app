package com.example.student_life_app.features.task.utils

import com.example.student_life_app.features.task.model.TaskStatus

object TaskUtils {
    fun parseStatus(value: String): TaskStatus {
        return try {
            TaskStatus.valueOf(value.uppercase())
        } catch (_: Exception) {
            TaskStatus.TODO
        }
    }
}
