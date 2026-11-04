package com.example.student_life_app.features.reference.data.datasource

import com.example.student_life_app.features.reference.data.dto.ReferenceDto

interface ReferenceRemoteDataSource {
    suspend fun getReferences(userId: String): List<ReferenceDto>
    suspend fun insertReference(dto: ReferenceDto): ReferenceDto
    suspend fun updateReference(dto: ReferenceDto)
    suspend fun toggleFavorite(referenceId: String, isFavorite: Boolean)
    suspend fun deleteReference(referenceId: String)
}
