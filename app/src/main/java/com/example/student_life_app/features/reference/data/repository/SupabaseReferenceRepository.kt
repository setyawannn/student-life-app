package com.example.student_life_app.features.reference.data.repository

import android.util.Log
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.reference.data.datasource.ReferenceRemoteDataSource
import com.example.student_life_app.features.reference.data.datasource.ReferenceRemoteDataSourceImpl
import com.example.student_life_app.features.reference.data.dto.toDomain
import com.example.student_life_app.features.reference.data.dto.toDto
import com.example.student_life_app.features.reference.model.Reference
import com.example.student_life_app.features.reference.model.ReferenceCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Repository Referensi Materi yang terhubung ke Supabase (Postgrest).
 * Cache di-scope per userId agar data tidak bocor saat berganti akun.
 */
class SupabaseReferenceRepository(
    private val remote: ReferenceRemoteDataSource = ReferenceRemoteDataSourceImpl()
) : ReferenceRepository {

    private companion object {
        const val TAG = "ReferenceRepo"
        val cache = MutableStateFlow<List<Reference>>(emptyList())
        var loadedUserId: String? = null
    }

    private fun Exception.toUserMessage(): String =
        "Gagal terhubung ke server: ${message ?: "kesalahan tidak diketahui"}"

    override fun getReferences(userId: String): Flow<Resource<List<Reference>>> =
        flow {
            emit(Resource.Loading)
            try {
                val uid = SupabaseManager.ensureUserId()
                if (loadedUserId != uid) {
                    cache.value = emptyList()
                    cache.value = remote.getReferences(uid).map { it.toDomain() }
                    loadedUserId = uid
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gagal memuat referensi", e)
                emit(Resource.Error(e.toUserMessage(), e))
                return@flow
            }
            emitAll(cache.map { list -> Resource.Success(list) })
        }

    override fun getReferencesByCategory(
        userId: String,
        category: ReferenceCategory
    ): Flow<Resource<List<Reference>>> =
        getReferences(userId).map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.filter { it.category == category })
                else -> resource
            }
        }

    override suspend fun createReference(reference: Reference): Resource<Unit> =
        try {
            val uid = SupabaseManager.ensureUserId()
            val saved = remote.insertReference(reference.toDto(uid)).toDomain()
            cache.update { listOf(saved) + it }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membuat referensi", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun updateReference(reference: Reference): Resource<Unit> =
        try {
            val uid = SupabaseManager.ensureUserId()
            val updated = reference.copy(userId = uid)
            remote.updateReference(updated.toDto(uid))
            cache.update { list -> list.map { if (it.id == updated.id) updated else it } }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal memperbarui referensi", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun toggleFavorite(referenceId: String, isFavorite: Boolean): Resource<Unit> =
        try {
            SupabaseManager.ensureUserId()
            remote.toggleFavorite(referenceId, isFavorite)
            cache.update { list ->
                list.map { if (it.id == referenceId) it.copy(isFavorite = isFavorite) else it }
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengubah favorit referensi", e)
            Resource.Error(e.toUserMessage(), e)
        }

    override suspend fun deleteReference(referenceId: String): Resource<Unit> =
        try {
            SupabaseManager.ensureUserId()
            remote.deleteReference(referenceId)
            cache.update { list -> list.filterNot { it.id == referenceId } }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menghapus referensi", e)
            Resource.Error(e.toUserMessage(), e)
        }
}
