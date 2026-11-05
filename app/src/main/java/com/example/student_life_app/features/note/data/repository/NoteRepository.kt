package com.example.student_life_app.features.note.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.note.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(userId: String): Flow<Resource<List<Note>>>
    suspend fun createNoteWithImage(note: Note, imageBytes: ByteArray?): Resource<Unit>
    suspend fun deleteNote(noteId: String): Resource<Unit>
    suspend fun toggleBookmark(noteId: String): Resource<Unit>
}
