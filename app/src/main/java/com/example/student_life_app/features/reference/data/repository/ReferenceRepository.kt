package com.example.student_life_app.features.reference.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.reference.model.Reference
import com.example.student_life_app.features.reference.model.ReferenceCategory
import kotlinx.coroutines.flow.Flow

interface ReferenceRepository {
    fun getReferences(userId: String): Flow<Resource<List<Reference>>>
    fun getReferencesByCategory(userId: String, category: ReferenceCategory): Flow<Resource<List<Reference>>>
    suspend fun createReference(reference: Reference): Resource<Unit>
    suspend fun toggleFavorite(referenceId: String, isFavorite: Boolean): Resource<Unit>
    suspend fun deleteReference(referenceId: String): Resource<Unit>
}
