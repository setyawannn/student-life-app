package com.example.student_life_app.features.schedule.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Repository Dummy Jadwal Kuliah (dipakai pada Progress 2: UI dengan data dummy).
 * Data disimpan di companion object supaya semua layar (List, Detail, Form)
 * melihat data yang sama, dan setiap perubahan otomatis tampil di layar lain.
 * Pada Progress 3 class ini diganti repository Supabase.
 */
class FakeScheduleRepository : ScheduleRepository {

    companion object {
        private val store = MutableStateFlow(
            listOf(
                Schedule("1", "user_default", "Pemrograman Mobile", DayOfWeekEnum.SENIN,
                    "08:00", "10:30", "Lab Komputasi 2", "Aryo Pinandito, S.T., M.MT."),
                Schedule("2", "user_default", "Kecerdasan Buatan", DayOfWeekEnum.SENIN,
                    "13:00", "15:30", "Gedung F RK 3.2", "Dr. Eng. Herman Tolle, S.T., M.T."),
                Schedule("3", "user_default", "Basis Data Lanjut", DayOfWeekEnum.SELASA,
                    "09:30", "12:00", "Lab Basis Data", "Suprapto, S.T., M.T."),
                Schedule("4", "user_default", "Interaksi Manusia & Komputer", DayOfWeekEnum.SELASA,
                    "13:00", "14:40", "Gedung G RK 2.1", "Muhammad Aminul Akbar, S.Kom., M.T."),
                Schedule("5", "user_default", "Jaringan Komputer", DayOfWeekEnum.RABU,
                    "07:30", "10:00", "Lab Jaringan", "Eko Sakti P., S.Kom., M.Kom."),
                Schedule("6", "user_default", "Metodologi Penelitian", DayOfWeekEnum.KAMIS,
                    "10:00", "11:40", "Gedung H RK 1.1", "Prof. Dr. Ir. Wayan Firdaus Mahmudy"),
                Schedule("7", "user_default", "Etika Profesi & Kewirausahaan", DayOfWeekEnum.JUMAT,
                    "08:30", "10:10", "Auditorium FILKOM", "Drs. M. Ali Fauzi, M.Kom.")
            )
        )
    }

    override fun getSchedules(userId: String): Flow<Resource<List<Schedule>>> =
        store.map { list -> Resource.Success(list) }

    override fun getSchedulesByDay(userId: String, day: DayOfWeekEnum): Flow<Resource<List<Schedule>>> =
        store.map { list -> Resource.Success(list.filter { it.dayOfWeek == day }) }

    override suspend fun createSchedule(schedule: Schedule): Resource<Unit> {
        store.update { it + schedule }
        return Resource.Success(Unit)
    }

    override suspend fun updateSchedule(schedule: Schedule): Resource<Unit> {
        store.update { list -> list.map { if (it.id == schedule.id) schedule else it } }
        return Resource.Success(Unit)
    }

    override suspend fun deleteSchedule(scheduleId: String): Resource<Unit> {
        store.update { list -> list.filterNot { it.id == scheduleId } }
        return Resource.Success(Unit)
    }
}