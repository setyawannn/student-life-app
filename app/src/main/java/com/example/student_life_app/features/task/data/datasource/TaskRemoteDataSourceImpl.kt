package com.example.student_life_app.features.task.data.datasource

import android.util.Log
import com.example.student_life_app.core.database.SupabaseClientProvider
import com.example.student_life_app.features.task.data.dto.TaskDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class TaskRemoteDataSourceImpl : TaskRemoteDataSource {

    private val client = SupabaseClientProvider.client

    override suspend fun getTasks(userId: String): List<TaskDto> {
        val authUserId = client.auth.currentUserOrNull()?.id
        val validUserId = authUserId ?: userId.takeIf { it.length == 36 && it.contains("-") }

        return try {
            client.from("tasks")
                .select {
                    if (validUserId != null) {
                        filter {
                            eq("user_id", validUserId)
                        }
                    }
                }
                .decodeList<TaskDto>()
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error getTasks (userId=$userId): ${e.message}", e)
            throw e
        }
    }

    override suspend fun insertTask(dto: TaskDto): TaskDto {
        val authUserId = client.auth.currentUserOrNull()?.id
        val sanitizedUserId = authUserId ?: dto.userId?.takeIf { it.length == 36 && it.contains("-") }
        val finalDto = dto.copy(userId = sanitizedUserId)
        Log.d("SUPABASE_TASK", "Memproses insertTask dengan userId=$sanitizedUserId, DTO=$finalDto")

        return try {
            client.from("tasks")
                .insert(finalDto) {
                    select()
                }
                .decodeSingle<TaskDto>()
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error insertTask (DTO=$finalDto): ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateTask(dto: TaskDto) {
        val id = dto.id ?: return
        val authUserId = client.auth.currentUserOrNull()?.id
        val sanitizedUserId = authUserId ?: dto.userId?.takeIf { it.length == 36 && it.contains("-") }
        val finalDto = dto.copy(userId = sanitizedUserId)

        try {
            client.from("tasks")
                .update(finalDto) {
                    filter {
                        eq("id", id)
                    }
                }
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error updateTask (DTO=$finalDto): ${e.message}", e)
            throw e
        }
    }

    override suspend fun deleteTask(taskId: String) {
        try {
            client.from("tasks")
                .delete {
                    filter {
                        eq("id", taskId)
                    }
                }
        } catch (e: Exception) {
            Log.e("SUPABASE_TASK", "Error deleteTask (id=$taskId): ${e.message}", e)
            throw e
        }
    }
}