package com.example.student_life_app.features.note.data.datasource

import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.note.data.dto.NoteDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Payload yang DIKIRIM ke tabel `notes` (tanpa created_at,
 * karena diisi otomatis oleh default `now()` di Supabase).
 */
@Serializable
internal data class NoteWritePayload(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("content") val content: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("badge_type") val badgeType: String? = null,
    @SerialName("is_bookmarked") val isBookmarked: Boolean = false
)

@Serializable
internal data class BookmarkWritePayload(
    @SerialName("is_bookmarked") val isBookmarked: Boolean
)

private fun NoteDto.toPayload() = NoteWritePayload(
    id = id ?: UUID.randomUUID().toString(),
    userId = userId,
    title = title,
    courseName = courseName,
    content = content,
    imageUrl = imageUrl,
    badgeType = badgeType,
    isBookmarked = isBookmarked
)

class NoteRemoteDataSourceImpl(
    private val client: SupabaseClient = SupabaseManager.client
) : NoteRemoteDataSource {

    private companion object {
        const val TABLE = "notes"
        const val BUCKET = "note-images"
    }

    override suspend fun getNotes(userId: String): List<NoteDto> =
        client.postgrest.from(TABLE).select {
            filter { eq("user_id", userId) }
            order("created_at", Order.DESCENDING)
        }.decodeList<NoteDto>()

    override suspend fun insertNote(dto: NoteDto): NoteDto =
        client.postgrest.from(TABLE).insert(dto.toPayload()) {
            select()
        }.decodeSingle<NoteDto>()

    override suspend fun updateNote(dto: NoteDto) {
        val payload = dto.toPayload()
        client.postgrest.from(TABLE).update(payload) {
            filter { eq("id", payload.id) }
        }
    }

    override suspend fun updateBookmark(noteId: String, isBookmarked: Boolean) {
        client.postgrest.from(TABLE).update(BookmarkWritePayload(isBookmarked)) {
            filter { eq("id", noteId) }
        }
    }

    override suspend fun deleteNote(noteId: String) {
        client.postgrest.from(TABLE).delete {
            filter { eq("id", noteId) }
        }
    }

    override suspend fun uploadNoteImage(userId: String, imageBytes: ByteArray): String {
        val path = "$userId/${UUID.randomUUID()}.jpg"
        val bucket = client.storage.from(BUCKET)
        bucket.upload(path, imageBytes) {
            upsert = true
        }
        return bucket.publicUrl(path)
    }
}
