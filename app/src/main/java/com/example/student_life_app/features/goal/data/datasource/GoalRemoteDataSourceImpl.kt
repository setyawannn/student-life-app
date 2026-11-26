package com.example.student_life_app.features.goal.data.datasource

import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.goal.data.dto.GoalDto
import io.github.jan.supabase.postgrest.from

class GoalRemoteDataSourceImpl : GoalRemoteDataSource {

    override suspend fun getGoals(userId: String): List<GoalDto> {
        val authUserId = SupabaseManager.ensureUserId()
        return SupabaseManager.client.from("study_goals")
            .select {
                filter {
                    eq("user_id", authUserId)
                }
            }.decodeList<GoalDto>()
    }

    override suspend fun insertGoal(dto: GoalDto): GoalDto {
        val authUserId = SupabaseManager.ensureUserId()
        val finalDto = dto.copy(userId = authUserId)

        return SupabaseManager.client.from("study_goals")
            .insert(finalDto) {
                select()
            }.decodeSingle<GoalDto>()
    }

    override suspend fun updateGoal(dto: GoalDto) {
        val id = dto.id ?: return
        val authUserId = SupabaseManager.ensureUserId()
        val finalDto = dto.copy(userId = authUserId)

        SupabaseManager.client.from("study_goals")
            .update(finalDto) {
                filter {
                    eq("id", id)
                }
            }
    }

    override suspend fun deleteGoal(goalId: String) {
        SupabaseManager.ensureUserId()
        SupabaseManager.client.from("study_goals")
            .delete {
                filter {
                    eq("id", goalId)
                }
            }
    }
}
