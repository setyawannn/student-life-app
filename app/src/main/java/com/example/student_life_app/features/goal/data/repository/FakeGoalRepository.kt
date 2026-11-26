package com.example.student_life_app.features.goal.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.model.StudyGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * In-Memory Repository untuk Target Belajar (Tanpa data dummy awal).
 */
class FakeGoalRepository : GoalRepository {

    companion object {
        const val DEFAULT_USER_ID = "00000000-0000-0000-0000-000000000001"
        private val sampleGoals = mutableListOf<StudyGoal>()
    }

    override fun getGoals(userId: String): Flow<Resource<List<StudyGoal>>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(sampleGoals.toList()))
    }

    override suspend fun createGoal(goal: StudyGoal): Resource<Unit> {
        val index = sampleGoals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            sampleGoals[index] = goal
        } else {
            sampleGoals.add(0, goal)
        }
        return Resource.Success(Unit)
    }

    override suspend fun updateGoal(goal: StudyGoal): Resource<Unit> {
        val index = sampleGoals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            sampleGoals[index] = goal
        } else {
            sampleGoals.add(0, goal)
        }
        return Resource.Success(Unit)
    }

    override suspend fun updateGoalProgress(goalId: String, progress: Int): Resource<Unit> {
        val index = sampleGoals.indexOfFirst { it.id == goalId }
        if (index != -1) {
            val item = sampleGoals[index]
            sampleGoals[index] = item.copy(progress = progress.coerceIn(0, 100))
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteGoal(goalId: String): Resource<Unit> {
        sampleGoals.removeAll { it.id == goalId }
        return Resource.Success(Unit)
    }
}
