---
id: ref-mvvm-04
title: Integrasi REST API Modern dengan Retrofit 3 & Kotlin Serialization
module: Modul Panduan MVVM Modern
category: networking
tags:
  - retrofit3
  - kotlinx-serialization
  - suspend-api
  - error-handling
  - repository-pattern
  - cancellation-exception
summary: Panduan arsitektur pengambilan data HTTP REST API menggunakan Retrofit 3 suspend function, kotlinx.serialization, dan penanganan error jaringan profesional.
related:
  - docs/references/05-mvvm-best-practices/03-reactive-ui-state-combine-and-statein.md
  - docs/references/05-mvvm-best-practices/05-practical-labs-counter-mahasiswa-posts.md
  - docs/references/05-mvvm-best-practices/06-troubleshooting-and-architecture-rules.md
---

# 🌐 Integrasi REST API Modern dengan Retrofit 3 & Kotlin Serialization

[← Sebelumnya: Reactive UI State (combine + stateIn)](./03-reactive-ui-state-combine-and-statein.md) | [Berikutnya: Praktikum Hands-on (Counter, Mahasiswa, Posts) →](./05-practical-labs-counter-mahasiswa-posts.md)

---

## 1. Setup Dependensi Modern (Gradle Version Catalog)

### A. `gradle/libs.versions.toml`
```toml
[versions]
lifecycle = "2.11.0"
retrofit = "3.0.0"
kotlinxSerialization = "1.9.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

[plugins]
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

### B. Izin Internet di `AndroidManifest.xml`
Tambahkan di atas tag `<application>`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 2. Pemodelan DTO Data (`Post.kt`)

Gunakan anotasi `@Serializable` dari Kotlinx Serialization:

```kotlin
package com.example.mvvmpraktikum.post.data

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
```

---

## 3. Interface API Service (`PostApiService.kt`)

Retrofit 3 mendukung **`suspend fun` secara langsung tanpa Call wrapper**:

```kotlin
package com.example.mvvmpraktikum.post.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {
    // GET https://jsonplaceholder.typicode.com/posts
    @GET("posts")
    suspend fun getPosts(): List<Post>

    // GET https://jsonplaceholder.typicode.com/posts/{id}
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Post
}
```

---

## 4. Konfigurasi Singleton `RetrofitClient.kt`

```kotlin
package com.example.mvvmpraktikum.post.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {
    // PENTING: Base URL wajib diakhiri dengan garis miring '/'
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // ignoreUnknownKeys = true mencegah crash jika server mengirim field tambahan
    private val json = Json { ignoreUnknownKeys = true }

    val api: PostApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PostApiService::class.java)
    }
}
```

---

## 5. Implementasi Repository Abstraction (`PostRepository.kt`)

```kotlin
package com.example.mvvmpraktikum.post.data

class PostRepository(
    private val api: PostApiService = RetrofitClient.api
) {
    suspend fun getPosts(): List<Post> = api.getPosts()
    suspend fun getPostById(id: Int): Post = api.getPostById(id)
}
```

---

## 6. Penanganan Error Jaringan (*Error Handling Best Practices*)

Di dalam ViewModel, panggilan jaringan harus dilindungi oleh `try-catch` terstruktur:

```kotlin
fun loadPosts() {
    viewModelScope.launch {
        _status.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val hasil = repository.getPosts()
            _status.update { it.copy(posts = hasil) }
        } catch (e: CancellationException) {
            // PENTING: Lempar kembali CancellationException! Jangan ditelan!
            throw e
        } catch (e: java.io.IOException) {
            _status.update { it.copy(errorMessage = "Tidak ada koneksi internet.") }
        } catch (e: retrofit2.HttpException) {
            _status.update { it.copy(errorMessage = "Server error (HTTP ${e.code()}).") }
        } catch (e: Exception) {
            _status.update { it.copy(errorMessage = e.message ?: "Terjadi kesalahan tidak terduga.") }
        } finally {
            _status.update { it.copy(isLoading = false) }
        }
    }
}
```

> [!CAUTION]
> **Hukum Utama Coroutines Android**:  
> **JANGAN PERNAH menelan `CancellationException`** di dalam blok `catch (e: Exception)`! Jika coroutine dibatalkan karena pengguna menutup layar, penelanan exception ini akan merusak mekanisme pembatalan siklus hidup dan menimbulkan memory leak.

---
[← Sebelumnya: Reactive UI State (combine + stateIn)](./03-reactive-ui-state-combine-and-statein.md) | [Berikutnya: Praktikum Hands-on (Counter, Mahasiswa, Posts) →](./05-practical-labs-counter-mahasiswa-posts.md)
