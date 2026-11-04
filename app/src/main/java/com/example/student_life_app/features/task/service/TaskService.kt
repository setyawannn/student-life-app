package com.example.student_life_app.features.task.service

import com.example.student_life_app.features.task.model.Task

interface TaskService {
    fun validateTaskInput(title: String, courseName: String): String?
    fun sortTasksByDeadline(tasks: List<Task>): List<Task>
}

class TaskServiceImpl : TaskService {
    override fun validateTaskInput(title: String, courseName: String): String? {
        if (title.isBlank()) return "Judul tugas tidak boleh kosong"
        if (courseName.isBlank()) return "Nama mata kuliah tidak boleh kosong"
        return null
    }

    override fun sortTasksByDeadline(tasks: List<Task>): List<Task> {
        return tasks.sortedBy { it.dueDate }
    }
}
