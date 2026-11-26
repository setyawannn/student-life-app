package com.example.student_life_app.features.schedule.data.datasource

import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.schedule.data.dto.ScheduleDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Data yang DIKIRIM ke tabel schedules (tanpa created_at/updated_at,
 * karena kolom itu diisi otomatis oleh database).
 */
@Serializable
internal data class ScheduleWritePayload(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("day_of_week") val dayOfWeek: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    @SerialName("room") val room: String,
    @SerialName("lecturer_name") val lecturerName: String
)

private fun String.toPgTime(): String = if (length == 5) "$this:00" else this

private fun ScheduleDto.toPayload() = ScheduleWritePayload(
    id = id ?: UUID.randomUUID().toString(),
    userId = userId,
    courseName = courseName,
    dayOfWeek = dayOfWeek,
    startTime = startTime.toPgTime(),
    endTime = endTime.toPgTime(),
    room = room,
    lecturerName = lecturerName
)

class ScheduleRemoteDataSourceImpl(
    private val client: SupabaseClient = SupabaseManager.client
) : ScheduleRemoteDataSource {

    private companion object {
        const val TABLE = "schedules"
    }

    override suspend fun getSchedules(userId: String): List<ScheduleDto> =
        client.postgrest.from(TABLE).select {
            filter { eq("user_id", userId) }
        }.decodeList<ScheduleDto>()

    override suspend fun insertSchedule(dto: ScheduleDto): ScheduleDto =
        client.postgrest.from(TABLE).insert(dto.toPayload()) {
            select()
        }.decodeSingle<ScheduleDto>()

    override suspend fun updateSchedule(dto: ScheduleDto) {
        val payload = dto.toPayload()
        client.postgrest.from(TABLE).update(payload) {
            filter { eq("id", payload.id) }
        }
    }

    override suspend fun deleteSchedule(scheduleId: String) {
        client.postgrest.from(TABLE).delete {
            filter { eq("id", scheduleId) }
        }
    }
}