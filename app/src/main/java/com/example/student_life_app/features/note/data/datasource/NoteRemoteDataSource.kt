package com.example.student_life_app.features.note.data.datasource

import com.example.student_life_app.features.note.data.dto.NoteDto

interface NoteRemoteDataSource {
    suspend fun getNotes(userId: String): List<NoteDto>
    suspend fun insertNote(dto: NoteDto): NoteDto
    suspend fun updateNote(dto: NoteDto)
    suspend fun deleteNote(noteId: String)
    suspend fun uploadNoteImage(userId: String, imageBytes: ByteArray): String // Returns image public URL
}
