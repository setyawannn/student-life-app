package com.example.student_life_app.features.schedule.data.repository

import android.util.Log
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.schedule.data.datasource.ScheduleRemoteDataSource
import com.example.student_life_app.features.schedule.data.datasource.ScheduleRemoteDataSourceImpl
import com.example.student_life_app.features.schedule.data.dto.toDomain
import com.example.student_life_app.features.schedule.data.dto.toDto
import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.model.Schedule
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Repository Jadwal yang terhubung ke Supabase.
 * Data disimpan juga di `cache` bersama supaya layar List, Detail, dan Form
 * selalu melihat data yang sama tanpa memuat ulang dari internet setiap saat.
 */
class SupabaseScheduleRepository(
    private val remote: ScheduleRemoteDataSource = ScheduleRemoteDataSourceImpl()
) : ScheduleRepository {

    private companion object {
        const val TAG = "ScheduleRepo"
        val cache = MutableStateFlow<List<Schedule>>(emptyList())
        var loaded = false
    }

    private fun Exception.toUserMessage(): String =
        "Gagal terhubung ke server: ${message ?: "kesalahan tidak diketahui"}"

    override fun getSchedules(userId: String): Flow<Resource<List<Schedule>>> =
        flow<Resource<List<Schedule>>> {
            emit(Resource.Loading)
            if (!loaded) {
                try {
                    val uid = SupabaseManager.ensureUserId()
                    cache.value = remote.getSchedules(uid).map { it.toDomain() }
                    loaded = true
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal memuat jadwal", e)
                    emit(Resource.Error(e.toUserMessage(), e))
                    return@flow
                }
            }
            emitAll(cache.map { list -> Resource.Success(list) })
        }

    override fun getSchedulesByDay(userId: String, day: DayOfWeekEnum): Flow<Resource<List<Schedule>>> =
        getSchedules(userId).map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.filter { it.dayOfWeek == day })
                else -> resource
            }
        }

    override suspend fun createSchedule(schedule: Schedule): Resource<Unit> =
        try {
            val uid = SupabaseManager.ensureUserId()
            val saved = remote.insertSchedule(schedule.toDto(uid)).toDomain()
            cache.update { it + saved }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menambah jadwal", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun updateSchedule(schedule: Schedule): Resource<Unit> =
        try {
            val uid = SupabaseManager.ensureUserId()
            remote.updateSchedule(schedule.toDto(uid))
            cache.update { list -> list.map { if (it.id == schedule.id) schedule.copy(userId = uid) else it } }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengubah jadwal", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun deleteSchedule(scheduleId: String): Resource<Unit> =
        try {
            SupabaseManager.ensureUserId()
            remote.deleteSchedule(scheduleId)
            cache.update { list -> list.filterNot { it.id == scheduleId } }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menghapus jadwal", e)
            Resource.Error(e.toUserMessage(), e)
        }
}