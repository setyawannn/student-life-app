package com.example.student_life_app.features.goal.service

interface GoalService {
    fun validateGoalInput(title: String, deadline: String): String?
    fun calculateProgressPercentage(currentProgress: Int): Float // 0.0f - 1.0f
}

class GoalServiceImpl : GoalService {
    override fun validateGoalInput(title: String, deadline: String): String? {
        if (title.isBlank()) return "Nama target belajar tidak boleh kosong"
        if (deadline.isBlank()) return "Tenggat waktu target wajib diisi"
        return null
    }

    override fun calculateProgressPercentage(currentProgress: Int): Float {
        return (currentProgress.coerceIn(0, 100)) / 100f
    }
}
