package com.example.student_life_app.features.profile.data.datasource

import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.profile.data.dto.ProfileDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest

interface ProfileRemoteDataSource {
    suspend fun getProfile(userId: String): ProfileDto?
    suspend fun upsertProfile(profile: ProfileDto): ProfileDto
    fun getCurrentUserEmail(): String
    suspend fun signOut()
}

class ProfileRemoteDataSourceImpl(
    private val client: SupabaseClient = SupabaseManager.client
) : ProfileRemoteDataSource {

    override suspend fun getProfile(userId: String): ProfileDto? {
        return client.postgrest
            .from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<ProfileDto>()
    }

    override suspend fun upsertProfile(profile: ProfileDto): ProfileDto {
        return client.postgrest
            .from("profiles")
            .upsert(profile) {
                select()
            }
            .decodeSingle<ProfileDto>()
    }

    override fun getCurrentUserEmail(): String {
        return client.auth.currentUserOrNull()?.email.orEmpty()
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }
}
