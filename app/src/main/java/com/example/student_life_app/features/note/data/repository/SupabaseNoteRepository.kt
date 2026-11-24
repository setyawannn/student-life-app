package com.example.student_life_app.features.note.data.repository

import android.util.Log
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.note.data.datasource.NoteRemoteDataSource
import com.example.student_life_app.features.note.data.datasource.NoteRemoteDataSourceImpl
import com.example.student_life_app.features.note.data.dto.toDomain
import com.example.student_life_app.features.note.data.dto.toDto
import com.example.student_life_app.features.note.model.Note
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Repository Catatan Belajar yang terhubung ke Supabase (Postgrest + Storage).
 * Menggunakan `cache` bersama agar layar List, Detail, dan Form selalu melihat data terbaru.
 */
class SupabaseNoteRepository(
    private val remote: NoteRemoteDataSource = NoteRemoteDataSourceImpl()
) : NoteRepository {

    private companion object {
        const val TAG = "NoteRepo"
        val cache = MutableStateFlow<List<Note>>(emptyList())
        var loaded = false
    }

    private fun Exception.toUserMessage(): String =
        "Gagal terhubung ke server: ${message ?: "kesalahan tidak diketahui"}"

    override fun getNotes(userId: String): Flow<Resource<List<Note>>> =
        flow {
            emit(Resource.Loading)
            if (!loaded) {
                try {
                    val uid = SupabaseManager.ensureUserId()
                    cache.value = remote.getNotes(uid).map { it.toDomain() }
                    loaded = true
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal memuat catatan", e)
                    emit(Resource.Error(e.toUserMessage(), e))
                    return@flow
                }
            }
            emitAll(cache.map { list -> Resource.Success(list) })
        }

    override suspend fun createNoteWithImage(note: Note, imageBytes: ByteArray?): Resource<Unit> =
        try {
            val uid = SupabaseManager.ensureUserId()
            val uploadedUrl = if (imageBytes != null) {
                remote.uploadNoteImage(uid, imageBytes)
            } else {
                note.imageUrl
            }
            val finalNote = note.copy(
                imageUrl = uploadedUrl,
                badgeType = if (uploadedUrl != null) note.badgeType ?: "Photo" else null
            )
            val saved = remote.insertNote(finalNote.toDto(uid)).toDomain()
            cache.update { listOf(saved) + it }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membuat catatan", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun updateNoteWithImage(note: Note, imageBytes: ByteArray?): Resource<Unit> =
        try {
            val uid = SupabaseManager.ensureUserId()
            val uploadedUrl = if (imageBytes != null) {
                remote.uploadNoteImage(uid, imageBytes)
            } else {
                note.imageUrl
            }
            val finalNote = note.copy(
                userId = uid,
                imageUrl = uploadedUrl,
                badgeType = if (uploadedUrl != null) note.badgeType ?: "Photo" else null
            )
            remote.updateNote(finalNote.toDto(uid))
            cache.update { list -> list.map { if (it.id == finalNote.id) finalNote else it } }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal memperbarui catatan", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun deleteNote(noteId: String): Resource<Unit> =
        try {
            SupabaseManager.ensureUserId()
            remote.deleteNote(noteId)
            cache.update { list -> list.filterNot { it.id == noteId } }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menghapus catatan", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun toggleBookmark(noteId: String): Resource<Unit> =
        try {
            SupabaseManager.ensureUserId()
            val current = cache.value.firstOrNull { it.id == noteId }
                ?: return Resource.Error("Catatan tidak ditemukan")
            val nextState = !current.isBookmarked
            remote.updateBookmark(noteId, nextState)
            cache.update { list ->
                list.map { if (it.id == noteId) it.copy(isBookmarked = nextState) else it }
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengubah bookmark", e)
            Resource.Error(e.toUserMessage(), e)
        }
}
