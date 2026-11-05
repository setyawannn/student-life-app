package com.example.student_life_app.features.note.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.note.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNoteRepository : NoteRepository {

    private val sampleNotes = mutableListOf(
        Note(
            id = "1",
            userId = "user_default",
            title = "Arsitektur MVVM & StateFlow",
            courseName = "Pemrograman Mobile",
            content = "Pemisahan layer UI, ViewModel, dan Repository untuk menghasilkan kode bersih.",
            imageUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=500&auto=format&fit=crop",
            badgeType = "Diag",
            dateText = "28 Okt 2026",
            isBookmarked = false
        ),
        Note(
            id = "2",
            userId = "user_default",
            title = "Normalisasi Database 1NF-3NF",
            courseName = "Basis Data Lanjut",
            content = "Tahapan dekomposisi tabel untuk menghilangkan anomali redundansi data dan update.",
            imageUrl = null,
            badgeType = null,
            dateText = "27 Okt 2026",
            isBookmarked = true
        ),
        Note(
            id = "3",
            userId = "user_default",
            title = "Heuristic Nielsen 10 Prinsip",
            courseName = "IMK",
            content = "Prinsip evaluasi usability pada antarmuka pengguna digital secara konsisten.",
            imageUrl = "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?w=500&auto=format&fit=crop",
            badgeType = "Photo",
            dateText = "25 Okt 2026",
            isBookmarked = false
        ),
        Note(
            id = "4",
            userId = "user_default",
            title = "Query Optimization & B-Tree",
            courseName = "Basis Data Lanjut",
            content = "Penggunaan index clustering vs non-clustering pada InnoDB engine untuk efisiensi.",
            imageUrl = null,
            badgeType = null,
            dateText = "22 Okt 2026",
            isBookmarked = false
        )
    )

    override fun getNotes(userId: String): Flow<Resource<List<Note>>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(sampleNotes.toList()))
    }

    override suspend fun createNoteWithImage(note: Note, imageBytes: ByteArray?): Resource<Unit> {
        sampleNotes.add(0, note)
        return Resource.Success(Unit)
    }

    override suspend fun deleteNote(noteId: String): Resource<Unit> {
        sampleNotes.removeAll { it.id == noteId }
        return Resource.Success(Unit)
    }

    override suspend fun toggleBookmark(noteId: String): Resource<Unit> {
        val index = sampleNotes.indexOfFirst { it.id == noteId }
        if (index != -1) {
            val item = sampleNotes[index]
            sampleNotes[index] = item.copy(isBookmarked = !item.isBookmarked)
        }
        return Resource.Success(Unit)
    }
}
