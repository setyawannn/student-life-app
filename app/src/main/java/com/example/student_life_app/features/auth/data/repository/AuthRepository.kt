package com.example.student_life_app.features.auth.data.repository

import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.auth.data.datasource.AuthRemoteDataSource
import com.example.student_life_app.features.auth.data.datasource.AuthRemoteDataSourceImpl
import com.example.student_life_app.features.auth.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AuthRepository {
    suspend fun signIn(identifier: String, password: String): Resource<UserSession>
    suspend fun signUp(fullName: String, nim: String, email: String, password: String): Resource<UserSession>
    suspend fun signOut(): Resource<Unit>
    fun getCurrentSession(): UserSession?
}

class SupabaseAuthRepository(
    private val remoteDataSource: AuthRemoteDataSource = AuthRemoteDataSourceImpl()
) : AuthRepository {

    override suspend fun signIn(identifier: String, password: String): Resource<UserSession> =
        withContext(Dispatchers.IO) {
            try {
                val session = remoteDataSource.signIn(identifier, password)
                Resource.Success(session)
            } catch (e: Exception) {
                Resource.Error(mapAuthError(e), e)
            }
        }

    override suspend fun signUp(
        fullName: String,
        nim: String,
        email: String,
        password: String
    ): Resource<UserSession> = withContext(Dispatchers.IO) {
        try {
            val session = remoteDataSource.signUp(fullName, nim, email, password)
            Resource.Success(session)
        } catch (e: Exception) {
            Resource.Error(mapAuthError(e), e)
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

    override fun getCurrentSession(): UserSession? = remoteDataSource.getCurrentSession()

    private fun mapAuthError(e: Exception): String {
        val raw = e.message.orEmpty()
        return when {
            raw.contains("Invalid login credentials", ignoreCase = true) ->
                "Email/NIM atau password salah."
            raw.contains("User already registered", ignoreCase = true) ->
                "Email sudah terdaftar. Silakan masuk."
            raw.contains("idx_profiles_nim_unique", ignoreCase = true) ||
                raw.contains("duplicate key", ignoreCase = true) ->
                "NIM sudah terdaftar pada akun lain."
            raw.contains("Email not confirmed", ignoreCase = true) ->
                "Email belum dikonfirmasi. Matikan 'Confirm email' di Supabase Auth."
            raw.contains("rate limit", ignoreCase = true) ->
                "Batas pengiriman email tercapai. Matikan 'Confirm email' di Supabase Auth atau coba lagi nanti."
            raw.contains("Unable to resolve host", ignoreCase = true) ||
                raw.contains("timeout", ignoreCase = true) ->
                "Koneksi internet bermasalah. Periksa jaringan Anda."
            raw.isNotBlank() -> raw
            else -> "Terjadi kesalahan autentikasi."
        }
    }
}
