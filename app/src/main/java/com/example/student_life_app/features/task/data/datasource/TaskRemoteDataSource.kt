package com.example.student_life_app.features.task.data.datasource

import com.example.student_life_app.features.task.data.dto.TaskDto

interface TaskRemoteDataSource {
    suspend fun getTasks(userId: String): List<TaskDto>
    suspend fun insertTask(dto: TaskDto): TaskDto
    suspend fun updateTask(dto: TaskDto)
    suspend fun deleteTask(taskId: String)
}
