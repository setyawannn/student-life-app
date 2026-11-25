package com.example.student_life_app.features.profile.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.profile.data.datasource.ProfileRemoteDataSource
import com.example.student_life_app.features.profile.data.datasource.ProfileRemoteDataSourceImpl
import com.example.student_life_app.features.profile.data.dto.ProfileDto
import com.example.student_life_app.features.profile.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ProfileWithEmail(
    val profile: UserProfile,
    val email: String
)

interface ProfileRepository {
    suspend fun getCurrentProfile(): Resource<ProfileWithEmail>
    suspend fun updateProfile(fullName: String, nim: String): Resource<UserProfile>
    suspend fun signOut(): Resource<Unit>
}

class SupabaseProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource = ProfileRemoteDataSourceImpl()
) : ProfileRepository {

    override suspend fun getCurrentProfile(): Resource<ProfileWithEmail> =
        withContext(Dispatchers.IO) {
            try {
                val userId = SupabaseManager.ensureUserId()
                val email = remoteDataSource.getCurrentUserEmail()
                val dto = remoteDataSource.getProfile(userId)
                    ?: ProfileDto(id = userId, fullName = "Mahasiswa", nim = "")
                Resource.Success(ProfileWithEmail(profile = dto.toDomain(), email = email))
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Gagal memuat profil.", e)
            }
        }

    override suspend fun updateProfile(fullName: String, nim: String): Resource<UserProfile> =
        withContext(Dispatchers.IO) {
            try {
                val userId = SupabaseManager.ensureUserId()
                val existing = remoteDataSource.getProfile(userId)
                val updated = remoteDataSource.upsertProfile(
                    ProfileDto(
                        id = userId,
                        fullName = fullName.trim(),
                        nim = nim.trim(),
                        avatarUrl = existing?.avatarUrl
                    )
                )
                Resource.Success(updated.toDomain())
            } catch (e: Exception) {
                val raw = e.message.orEmpty()
                val message = if (
                    raw.contains("idx_profiles_nim_unique", ignoreCase = true) ||
                    raw.contains("duplicate key", ignoreCase = true)
                ) {
                    "NIM sudah digunakan oleh akun lain."
                } else {
                    raw.ifBlank { "Gagal memperbarui profil." }
                }
                Resource.Error(message, e)
            }
        }

    override suspend fun signOut(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal keluar dari akun.", e)
        }
    }
}
