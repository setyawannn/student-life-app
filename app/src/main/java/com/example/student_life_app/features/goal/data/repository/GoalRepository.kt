package com.example.student_life_app.features.goal.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.model.StudyGoal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoals(userId: String): Flow<Resource<List<StudyGoal>>>
    suspend fun createGoal(goal: StudyGoal): Resource<Unit>
    suspend fun updateGoalProgress(goalId: String, progress: Int): Resource<Unit>
    suspend fun deleteGoal(goalId: String): Resource<Unit>
}
