package com.example.student_life_app.features.goal.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.goal.model.StudyGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementasi Dummy Repository Target Belajar.
 * Memisahkan penyediaan data sasaran belajar dari layer View.
 */
class FakeGoalRepository : GoalRepository {

    private val sampleGoals = mutableListOf(
        StudyGoal(
            id = "1",
            userId = "user_default",
            title = "Kuasai Jetpack Compose & StateFlow",
            description = "Membuat modular UI dan reactive unidirectional data flow.",
            deadline = "2026-11-20",
            progress = 80
        ),
        StudyGoal(
            id = "2",
            userId = "user_default",
            title = "Selesaikan Modul Database 1NF-3NF",
            description = "Latihan normalisasi tabel skema relasional kompleks.",
            deadline = "2026-11-15",
            progress = 100
        ),
        StudyGoal(
            id = "3",
            userId = "user_default",
            title = "Membaca 5 Paper Usability Heuristics",
            description = "Materi literatur untuk tugas review IMK dan perbaikan UX.",
            deadline = "2026-11-30",
            progress = 40
        ),
        StudyGoal(
            id = "4",
            userId = "user_default",
            title = "Mempersiapkan Ujian Sertifikasi Kotlin",
            description = "Menyelesaikan kursus Android Kotlin Developer Associate.",
            deadline = "2026-12-10",
            progress = 60
        )
    )

    override fun getGoals(userId: String): Flow<Resource<List<StudyGoal>>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(sampleGoals.toList()))
    }

    override suspend fun createGoal(goal: StudyGoal): Resource<Unit> {
        sampleGoals.add(0, goal)
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
