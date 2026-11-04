package com.example.student_life_app.features.note.utils

import java.util.UUID

object NoteUtils {
    fun generateStoragePath(userId: String): String {
        return "$userId/notes/${UUID.randomUUID()}.jpg"
    }
}
