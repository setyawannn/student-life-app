package com.example.student_life_app.features.reference.service

import android.util.Patterns

interface ReferenceService {
    fun validateReferenceInput(title: String, url: String, courseName: String): String?
    fun isValidUrl(url: String): Boolean
}

class ReferenceServiceImpl : ReferenceService {
    override fun validateReferenceInput(title: String, url: String, courseName: String): String? {
        if (title.isBlank()) return "Judul referensi tidak boleh kosong"
        if (url.isBlank()) return "URL tautan tidak boleh kosong"
        if (!isValidUrl(url)) return "Format URL tidak valid (harus diawali http:// atau https://)"
        if (courseName.isBlank()) return "Nama mata kuliah tidak boleh kosong"
        return null
    }

    override fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
}
