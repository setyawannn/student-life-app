package com.example.student_life_app.features.goal.data.datasource

import com.example.student_life_app.features.goal.data.dto.GoalDto

interface GoalRemoteDataSource {
    suspend fun getGoals(userId: String): List<GoalDto>
    suspend fun insertGoal(dto: GoalDto): GoalDto
    suspend fun updateGoal(dto: GoalDto)
    suspend fun deleteGoal(goalId: String)
}
