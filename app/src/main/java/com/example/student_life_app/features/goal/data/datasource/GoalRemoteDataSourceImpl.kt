package com.example.student_life_app.features.goal.data.datasource

import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.goal.data.dto.GoalDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class GoalRemoteDataSourceImpl : GoalRemoteDataSource {

    override suspend fun getGoals(userId: String): List<GoalDto> {
        val authUserId = SupabaseManager.client.auth.currentUserOrNull()?.id
        return try {
            SupabaseManager.client.from("study_goals")
                .select {
                    filter {
                        if (authUserId != null) {
                            eq("user_id", authUserId)
                        } else {
                            eq("user_id", userId)
                        }
                    }
                }.decodeList<GoalDto>()
        } catch (_: Exception) {
            SupabaseManager.client.from("study_goals")
                .select()
                .decodeList<GoalDto>()
        }
    }

    override suspend fun insertGoal(dto: GoalDto): GoalDto {
        val authUserId = SupabaseManager.client.auth.currentUserOrNull()?.id
        val finalDto = if (authUserId != null) dto.copy(userId = authUserId) else dto

        return SupabaseManager.client.from("study_goals")
            .insert(finalDto) {
                select()
            }.decodeSingle<GoalDto>()
    }

    override suspend fun updateGoal(dto: GoalDto) {
        val id = dto.id ?: return
        val authUserId = SupabaseManager.client.auth.currentUserOrNull()?.id
        val finalDto = if (authUserId != null) dto.copy(userId = authUserId) else dto

        SupabaseManager.client.from("study_goals")
            .update(finalDto) {
                filter {
                    eq("id", id)
                }
            }
    }

    override suspend fun deleteGoal(goalId: String) {
        SupabaseManager.client.from("study_goals")
            .delete {
                filter {
                    eq("id", goalId)
                }
            }
    }
}
