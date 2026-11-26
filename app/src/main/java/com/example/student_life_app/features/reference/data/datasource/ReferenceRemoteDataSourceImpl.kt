package com.example.student_life_app.features.reference.data.datasource

import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.reference.data.dto.ReferenceDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class ReferenceWritePayload(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("category") val category: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("is_favorite") val isFavorite: Boolean = false
)

@Serializable
internal data class FavoriteWritePayload(
    @SerialName("is_favorite") val isFavorite: Boolean
)

private fun ReferenceDto.toPayload() = ReferenceWritePayload(
    id = id ?: UUID.randomUUID().toString(),
    userId = userId,
    title = title,
    url = url,
    category = category,
    courseName = courseName,
    notes = notes,
    isFavorite = isFavorite
)

class ReferenceRemoteDataSourceImpl(
    private val client: SupabaseClient = SupabaseManager.client
) : ReferenceRemoteDataSource {

    private companion object {
        const val TABLE = "references"
    }

    override suspend fun getReferences(userId: String): List<ReferenceDto> =
        client.postgrest.from(TABLE).select {
            filter { eq("user_id", userId) }
            order("created_at", Order.DESCENDING)
        }.decodeList<ReferenceDto>()

    override suspend fun insertReference(dto: ReferenceDto): ReferenceDto =
        client.postgrest.from(TABLE).insert(dto.toPayload()) {
            select()
        }.decodeSingle<ReferenceDto>()

    override suspend fun updateReference(dto: ReferenceDto) {
        val payload = dto.toPayload()
        client.postgrest.from(TABLE).update(payload) {
            filter { eq("id", payload.id) }
        }
    }

    override suspend fun toggleFavorite(referenceId: String, isFavorite: Boolean) {
        client.postgrest.from(TABLE).update(FavoriteWritePayload(isFavorite)) {
            filter { eq("id", referenceId) }
        }
    }

    override suspend fun deleteReference(referenceId: String) {
        client.postgrest.from(TABLE).delete {
            filter { eq("id", referenceId) }
        }
    }
}
