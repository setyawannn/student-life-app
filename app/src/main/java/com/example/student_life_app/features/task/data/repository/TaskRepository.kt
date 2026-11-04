package com.example.student_life_app.features.task.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.task.model.Task
import com.example.student_life_app.features.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(userId: String): Flow<Resource<List<Task>>>
    suspend fun createTask(task: Task): Resource<Unit>
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Resource<Unit>
    suspend fun deleteTask(taskId: String): Resource<Unit>
}
