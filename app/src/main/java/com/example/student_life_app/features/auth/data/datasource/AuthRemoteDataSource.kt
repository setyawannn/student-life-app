package com.example.student_life_app.features.auth.data.datasource

import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.auth.model.UserSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface AuthRemoteDataSource {
    val sessionStatus: Flow<SessionStatus>
    fun getCurrentSession(): UserSession?
    suspend fun signIn(identifier: String, password: String): UserSession
    suspend fun signUp(fullName: String, nim: String, email: String, password: String): UserSession
    suspend fun signOut()
}

class AuthRemoteDataSourceImpl(
    private val client: SupabaseClient = SupabaseManager.client
) : AuthRemoteDataSource {

    override val sessionStatus: Flow<SessionStatus>
        get() = client.auth.sessionStatus

    override fun getCurrentSession(): UserSession? {
        val session = client.auth.currentSessionOrNull() ?: return null
        val user = client.auth.currentUserOrNull() ?: return null
        return UserSession(
            userId = user.id,
            email = user.email.orEmpty(),
            accessToken = session.accessToken
        )
    }

    override suspend fun signIn(identifier: String, password: String): UserSession {
        val trimmedIdentifier = identifier.trim()
        val resolvedEmail = if (trimmedIdentifier.contains("@")) {
            trimmedIdentifier
        } else {
            val rpcResult = client.postgrest.rpc(
                function = "get_email_by_nim",
                parameters = buildJsonObject {
                    put("p_nim", trimmedIdentifier)
                }
            )
            val foundEmail = rpcResult.decodeAsOrNull<String>()?.trim()
            if (foundEmail.isNullOrBlank()) {
                throw IllegalArgumentException("NIM atau password salah.")
            }
            foundEmail
        }

        client.auth.signInWith(Email) {
            this.email = resolvedEmail
            this.password = password
        }

        return getCurrentSession()
            ?: throw IllegalStateException("Gagal memuat sesi pengguna setelah login.")
    }

    override suspend fun signUp(
        fullName: String,
        nim: String,
        email: String,
        password: String
    ): UserSession {
        client.auth.signUpWith(Email) {
            this.email = email.trim()
            this.password = password
            this.data = buildJsonObject {
                put("full_name", fullName.trim())
                put("nim", nim.trim())
            }
        }

        return getCurrentSession()
            ?: throw IllegalStateException(
                "Akun berhasil dibuat. Jika belum otomatis masuk, silakan login menggunakan email/NIM Anda."
            )
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }
}
