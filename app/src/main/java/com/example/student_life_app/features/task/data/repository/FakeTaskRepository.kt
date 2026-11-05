package com.example.student_life_app.features.task.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.task.model.Task
import com.example.student_life_app.features.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementasi Dummy Repository Tugas Mahasiswa.
 * Memisahkan sumber data dummy dari layer View.
 */
class FakeTaskRepository : TaskRepository {

    private val sampleTasks = mutableListOf(
        Task(
            id = "1",
            userId = "user_default",
            title = "Implementasi MVVM & Supabase Client",
            courseName = "Pemrograman Mobile",
            description = "Membuat pemisahan layer controller, repository, dan model sesuai panduan.",
            dueDate = "2026-11-08",
            status = TaskStatus.IN_PROGRESS
        ),
        Task(
            id = "2",
            userId = "user_default",
            title = "Laporan Perancangan Normalisasi 3NF",
            courseName = "Basis Data Lanjut",
            description = "Dekomposisi tabel transaksi dan relasi foreign key.",
            dueDate = "2026-11-10",
            status = TaskStatus.TODO
        ),
        Task(
            id = "3",
            userId = "user_default",
            title = "Analisis Heuristik Nielsen pada Mobile App",
            courseName = "Interaksi Manusia & Komputer",
            description = "Evaluasi 10 prinsip usability pada antarmuka aplikasi perbankan digital.",
            dueDate = "2026-11-12",
            status = TaskStatus.COMPLETED
        ),
        Task(
            id = "4",
            userId = "user_default",
            title = "Konfigurasi Routing OSPF & BGP",
            courseName = "Jaringan Komputer",
            description = "Simulasi topologi jaringan menggunakan Cisco Packet Tracer.",
            dueDate = "2026-11-15",
            status = TaskStatus.TODO
        )
    )

    override fun getTasks(userId: String): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(sampleTasks.toList()))
    }

    override suspend fun createTask(task: Task): Resource<Unit> {
        sampleTasks.add(0, task)
        return Resource.Success(Unit)
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Resource<Unit> {
        val index = sampleTasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val item = sampleTasks[index]
            sampleTasks[index] = item.copy(status = status)
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteTask(taskId: String): Resource<Unit> {
        sampleTasks.removeAll { it.id == taskId }
        return Resource.Success(Unit)
    }
}
