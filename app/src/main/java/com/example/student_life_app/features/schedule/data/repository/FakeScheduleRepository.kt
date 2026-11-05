package com.example.student_life_app.features.schedule.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementasi Dummy Repository Jadwal Kuliah.
 * Memisahkan penyediaan data jadwal perkuliahan dari layer View.
 */
class FakeScheduleRepository : ScheduleRepository {

    private val sampleSchedules = mutableListOf(
        Schedule(
            id = "1",
            userId = "user_default",
            courseName = "Pemrograman Mobile",
            dayOfWeek = DayOfWeekEnum.SENIN,
            startTime = "08:00",
            endTime = "10:30",
            room = "Lab Komputasi 2",
            lecturerName = "Aryo Pinandito, S.T., M.MT."
        ),
        Schedule(
            id = "2",
            userId = "user_default",
            courseName = "Kecerdasan Buatan",
            dayOfWeek = DayOfWeekEnum.SENIN,
            startTime = "13:00",
            endTime = "15:30",
            room = "Gedung F RK 3.2",
            lecturerName = "Dr. Eng. Herman Tolle, S.T., M.T."
        ),
        Schedule(
            id = "3",
            userId = "user_default",
            courseName = "Basis Data Lanjut",
            dayOfWeek = DayOfWeekEnum.SELASA,
            startTime = "09:30",
            endTime = "12:00",
            room = "Lab Basis Data",
            lecturerName = "Suprapto, S.T., M.T."
        ),
        Schedule(
            id = "4",
            userId = "user_default",
            courseName = "Interaksi Manusia & Komputer",
            dayOfWeek = DayOfWeekEnum.SELASA,
            startTime = "13:00",
            endTime = "14:40",
            room = "Gedung G RK 2.1",
            lecturerName = "Muhammad Aminul Akbar, S.Kom., M.T."
        ),
        Schedule(
            id = "5",
            userId = "user_default",
            courseName = "Jaringan Komputer",
            dayOfWeek = DayOfWeekEnum.RABU,
            startTime = "07:30",
            endTime = "10:00",
            room = "Lab Jaringan",
            lecturerName = "Eko Sakti P., S.Kom., M.Kom."
        ),
        Schedule(
            id = "6",
            userId = "user_default",
            courseName = "Metodologi Penelitian",
            dayOfWeek = DayOfWeekEnum.KAMIS,
            startTime = "10:00",
            endTime = "11:40",
            room = "Gedung H RK 1.1",
            lecturerName = "Prof. Dr. Ir. Wayan Firdaus Mahmudy"
        ),
        Schedule(
            id = "7",
            userId = "user_default",
            courseName = "Etika Profesi & Kewirausahaan",
            dayOfWeek = DayOfWeekEnum.JUMAT,
            startTime = "08:30",
            endTime = "10:10",
            room = "Auditorium FILKOM",
            lecturerName = "Drs. M. Ali Fauzi, M.Kom."
        )
    )

    override fun getSchedules(userId: String): Flow<Resource<List<Schedule>>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(sampleSchedules.toList()))
    }

    override fun getSchedulesByDay(userId: String, day: DayOfWeekEnum): Flow<Resource<List<Schedule>>> = flow {
        emit(Resource.Loading)
        val filtered = sampleSchedules.filter { it.dayOfWeek == day }
        emit(Resource.Success(filtered))
    }

    override suspend fun createSchedule(schedule: Schedule): Resource<Unit> {
        sampleSchedules.add(schedule)
        return Resource.Success(Unit)
    }

    override suspend fun deleteSchedule(scheduleId: String): Resource<Unit> {
        sampleSchedules.removeAll { it.id == scheduleId }
        return Resource.Success(Unit)
    }
}
