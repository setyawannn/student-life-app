package com.example.student_life_app.features.note.service

interface NoteService {
    fun validateNoteInput(title: String, courseName: String, content: String): String?
}

class NoteServiceImpl : NoteService {
    override fun validateNoteInput(title: String, courseName: String, content: String): String? {
        if (title.isBlank()) return "Judul catatan tidak boleh kosong"
        if (courseName.isBlank()) return "Nama mata kuliah tidak boleh kosong"
        if (content.isBlank()) return "Isi catatan tidak boleh kosong"
        return null
    }
}
