package com.example.student_life_app.features.goal.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.data.datasource.GoalRemoteDataSource
import com.example.student_life_app.features.goal.data.datasource.GoalRemoteDataSourceImpl
import com.example.student_life_app.features.goal.data.dto.GoalDto
import com.example.student_life_app.features.goal.model.StudyGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseGoalRepositoryImpl(
    private val remoteDataSource: GoalRemoteDataSource = GoalRemoteDataSourceImpl(),
    private val localBackupRepository: GoalRepository = FakeGoalRepository()
) : GoalRepository {

    override fun getGoals(userId: String): Flow<Resource<List<StudyGoal>>> = flow {
        emit(Resource.Loading)
        try {
            val dtoList = remoteDataSource.getGoals(userId)
            val domainList = dtoList.map { it.toDomain() }
            emit(Resource.Success(domainList))
        } catch (e: Exception) {
            localBackupRepository.getGoals(userId).collect { res ->
                emit(res)
            }
        }
    }

    override suspend fun createGoal(goal: StudyGoal): Resource<Unit> {
        return try {
            val dto = GoalDto.fromDomain(goal)
            remoteDataSource.insertGoal(dto)
            localBackupRepository.createGoal(goal)
            Resource.Success(Unit)
        } catch (_: Exception) {
            localBackupRepository.createGoal(goal)
        }
    }

    override suspend fun updateGoal(goal: StudyGoal): Resource<Unit> {
        return try {
            val dto = GoalDto.fromDomain(goal)
            remoteDataSource.updateGoal(dto)
            localBackupRepository.updateGoal(goal)
            Resource.Success(Unit)
        } catch (_: Exception) {
            localBackupRepository.updateGoal(goal)
        }
    }

    override suspend fun updateGoalProgress(goalId: String, progress: Int): Resource<Unit> {
        return try {
            var targetGoal: StudyGoal? = null
            localBackupRepository.getGoals(FakeGoalRepository.DEFAULT_USER_ID).collect { res ->
                if (res is Resource.Success) {
                    targetGoal = res.data.firstOrNull { it.id == goalId }
                }
            }
            if (targetGoal != null) {
                val updated = targetGoal!!.copy(progress = progress)
                remoteDataSource.updateGoal(GoalDto.fromDomain(updated))
            }
            localBackupRepository.updateGoalProgress(goalId, progress)
            Resource.Success(Unit)
        } catch (_: Exception) {
            localBackupRepository.updateGoalProgress(goalId, progress)
        }
    }

    override suspend fun deleteGoal(goalId: String): Resource<Unit> {
        return try {
            remoteDataSource.deleteGoal(goalId)
            localBackupRepository.deleteGoal(goalId)
            Resource.Success(Unit)
        } catch (_: Exception) {
            localBackupRepository.deleteGoal(goalId)
        }
    }
}
