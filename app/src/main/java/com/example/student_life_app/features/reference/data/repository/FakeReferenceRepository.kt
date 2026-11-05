package com.example.student_life_app.features.reference.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.reference.model.Reference
import com.example.student_life_app.features.reference.model.ReferenceCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementasi Dummy Repository Referensi & Materi Kuliah.
 * Memisahkan penyediaan data referensi dari layer View.
 */
class FakeReferenceRepository : ReferenceRepository {

    private val sampleReferences = mutableListOf(
        Reference(
            id = "1",
            userId = "user_default",
            title = "Modern Android Architecture with StateFlow",
            url = "https://developer.android.com/topic/architecture",
            category = ReferenceCategory.JURNAL,
            courseName = "Pemrograman Mobile",
            notes = "Panduan resmi Google untuk UDF dan arsitektur MVVM berlapis.",
            isFavorite = true
        ),
        Reference(
            id = "2",
            userId = "user_default",
            title = "Database Normalization Principles: 1NF to BCNF",
            url = "https://acm.org/database-norm",
            category = ReferenceCategory.JURNAL,
            courseName = "Basis Data Lanjut",
            notes = "Paper ilmiah mengenai optimasi skema database relasional.",
            isFavorite = false
        ),
        Reference(
            id = "3",
            userId = "user_default",
            title = "Slide Kuliah Pemrograman Mobile 2026",
            url = "https://drive.google.com/drive/folders/mobile-2026",
            category = ReferenceCategory.DRIVE,
            courseName = "Pemrograman Mobile",
            notes = "Kumpulan materi perkuliahan pertemuan 1 sampai 8.",
            isFavorite = true
        ),
        Reference(
            id = "4",
            userId = "user_default",
            title = "Modul Praktikum Jaringan Komputer",
            url = "https://drive.google.com/drive/folders/jarkom-modul",
            category = ReferenceCategory.DRIVE,
            courseName = "Jaringan Komputer",
            notes = "File panduan simulasi Cisco Packet Tracer dan modul lab.",
            isFavorite = false
        ),
        Reference(
            id = "5",
            userId = "user_default",
            title = "Jetpack Compose Crash Course by Google",
            url = "https://youtube.com/watch?v=compose",
            category = ReferenceCategory.VIDEO,
            courseName = "Pemrograman Mobile",
            notes = "Tutorial interaktif penerapan deklaratif UI dan animation API.",
            isFavorite = false
        ),
        Reference(
            id = "6",
            userId = "user_default",
            title = "Query Optimization in PostgreSQL & MySQL",
            url = "https://youtube.com/watch?v=sql-perf",
            category = ReferenceCategory.VIDEO,
            courseName = "Basis Data Lanjut",
            notes = "Video pembahasan EXPLAIN ANALYZE dan clustering index.",
            isFavorite = true
        )
    )

    override fun getReferences(userId: String): Flow<Resource<List<Reference>>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(sampleReferences.toList()))
    }

    override fun getReferencesByCategory(
        userId: String,
        category: ReferenceCategory
    ): Flow<Resource<List<Reference>>> = flow {
        emit(Resource.Loading)
        val filtered = sampleReferences.filter { it.category == category }
        emit(Resource.Success(filtered))
    }

    override suspend fun createReference(reference: Reference): Resource<Unit> {
        sampleReferences.add(0, reference)
        return Resource.Success(Unit)
    }

    override suspend fun toggleFavorite(referenceId: String, isFavorite: Boolean): Resource<Unit> {
        val index = sampleReferences.indexOfFirst { it.id == referenceId }
        if (index != -1) {
            val item = sampleReferences[index]
            sampleReferences[index] = item.copy(isFavorite = isFavorite)
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteReference(referenceId: String): Resource<Unit> {
        sampleReferences.removeAll { it.id == referenceId }
        return Resource.Success(Unit)
    }
}
