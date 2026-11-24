package com.example.student_life_app.core.database

import com.example.student_life_app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Pintu masuk tunggal ke Supabase untuk seluruh aplikasi.
 * URL dan kunci dibaca dari file .env lewat BuildConfig (lihat app/build.gradle.kts).
 */
object SupabaseManager {

    val client: SupabaseClient by lazy {
        require(BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) {
            "SUPABASE_URL / SUPABASE_ANON_KEY masih kosong. Isi file .env lalu Rebuild Project."
        }
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    private val authMutex = Mutex()

    /**
     * Memastikan ada pengguna yang sedang masuk, lalu mengembalikan id-nya.
     * Sementara belum ada layar Login, aplikasi masuk sebagai pengguna anonim
     * (perlu "Allow anonymous sign-ins" aktif di dashboard Supabase).
     * Saat fitur Login sudah ada, cukup ganti isi fungsi ini.
     */
    suspend fun ensureUserId(): String = authMutex.withLock {
        val auth = client.auth
        auth.awaitInitialization()
        if (auth.currentSessionOrNull() == null) {
            auth.signInAnonymously()
        }
        auth.currentUserOrNull()?.id
            ?: error("Gagal mendapatkan pengguna. Cek pengaturan Auth di Supabase.")
    }
}