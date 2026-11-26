package com.example.student_life_app.features.task.data.repository

import android.util.Log
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.task.data.datasource.TaskRemoteDataSource
import com.example.student_life_app.features.task.data.datasource.TaskRemoteDataSourceImpl
import com.example.student_life_app.features.task.data.dto.TaskDto
import com.example.student_life_app.features.task.model.Task
import com.example.student_life_app.features.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseTaskRepository(
    private val remoteDataSource: TaskRemoteDataSource = TaskRemoteDataSourceImpl()
) : TaskRepository {

    override fun getTasks(userId: String): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading)
        try {
            val dtoList = remoteDataSource.getTasks(userId)
            val tasks = dtoList.map { dtoToDomain(it) }
            emit(Resource.Success(tasks))
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error in SupabaseTaskRepository.getTasks: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Gagal mengambil data tugas dari Supabase"))
        }
    }

    override suspend fun getTaskById(taskId: String): Resource<Task?> {
        return try {
            val dtoList = remoteDataSource.getTasks("")
            val taskDto = dtoList.find { it.id == taskId }
            Resource.Success(taskDto?.let { dtoToDomain(it) })
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error in SupabaseTaskRepository.getTaskById (id=$taskId): ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Gagal mengambil detail tugas")
        }
    }

    override suspend fun createTask(task: Task): Resource<Unit> {
        return try {
            val dto = domainToDto(task, isInsert = true)
            remoteDataSource.insertTask(dto)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error in SupabaseTaskRepository.createTask: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Gagal membuat tugas di Supabase")
        }
    }

    override suspend fun updateTask(task: Task): Resource<Unit> {
        return try {
            val dto = domainToDto(task, isInsert = false)
            remoteDataSource.updateTask(dto)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error in SupabaseTaskRepository.updateTask: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Gagal memperbarui tugas")
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Resource<Unit> {
        return try {
            val dtoList = remoteDataSource.getTasks("")
            val existingDto = dtoList.find { it.id == taskId }
            if (existingDto != null) {
                val updatedDto = existingDto.copy(status = status.name)
                remoteDataSource.updateTask(updatedDto)
                Resource.Success(Unit)
            } else {
                Resource.Error("Tugas tidak ditemukan")
            }
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error in SupabaseTaskRepository.updateTaskStatus: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Gagal memperbarui status tugas")
        }
    }

    override suspend fun deleteTask(taskId: String): Resource<Unit> {
        return try {
            remoteDataSource.deleteTask(taskId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error in SupabaseTaskRepository.deleteTask: ${e.message}", e)
            Resource.Error(e.localizedMessage ?: "Gagal menghapus tugas")
        }
    }

    private fun dtoToDomain(dto: TaskDto): Task {
        val status = try {
            TaskStatus.valueOf(dto.status)
        } catch (e: Exception) {
            TaskStatus.TODO
        }
        val displayDueDate = if (dto.dueDate.length >= 10) dto.dueDate.substring(0, 10) else dto.dueDate
        return Task(
            id = dto.id ?: "",
            userId = dto.userId ?: "",
            title = dto.title,
            courseName = dto.courseName,
            description = dto.description,
            dueDate = displayDueDate,
            status = status
        )
    }

    private fun domainToDto(task: Task, isInsert: Boolean = false): TaskDto {
        return TaskDto(
            id = if (isInsert || task.id.isBlank()) null else task.id,
            userId = if (task.userId.isBlank() || task.userId == "default_user") null else ensureValidUuid(task.userId),
            title = task.title,
            courseName = task.courseName,
            description = task.description,
            dueDate = ensureIsoTimestamp(task.dueDate),
            status = task.status.name,
            createdAt = null,
            updatedAt = null
        )
    }

    private fun ensureIsoTimestamp(dateStr: String): String {
        val trimmed = dateStr.trim()
        if (trimmed.isBlank()) return "2026-11-25T00:00:00Z"
        return if (trimmed.length == 10 && trimmed.contains("-")) {
            "${trimmed}T00:00:00Z"
        } else {
            trimmed
        }
    }

    private fun ensureValidUuid(id: String): String? {
        if (id.isBlank() || id == "default_user") return null
        return try {
            java.util.UUID.fromString(id)
            id
        } catch (e: Exception) {
            null
        }
    }
}
