---
id: ref-mvvm-05
title: Panduan Praktikum Mandiri MVVM (Counter, Mahasiswa, REST API Posts)
module: Modul Panduan MVVM Modern
category: lab-guide
tags:
  - lab-guide
  - counter-mvvm
  - mahasiswa-crud
  - retrofit-posts
  - reactive-filtering
summary: Langkah implementasi lengkap empat studi kasus praktikum MVVM Jetpack Compose dari counter dasar hingga pengambilan API publik dengan pencarian reaktif.
related:
  - docs/references/05-mvvm-best-practices/01-architecture-overview-and-udf.md
  - docs/references/05-mvvm-best-practices/03-reactive-ui-state-combine-and-statein.md
  - docs/references/05-mvvm-best-practices/04-remote-data-retrofit-and-serialization.md
  - docs/references/05-mvvm-best-practices/06-troubleshooting-and-architecture-rules.md
---

# 🛠️ Panduan Praktikum Mandiri MVVM: 4 Studi Kasus

[← Sebelumnya: Integrasi REST API Modern](./04-remote-data-retrofit-and-serialization.md) | [Berikutnya: Troubleshooting & Aturan Arsitektur →](./06-troubleshooting-and-architecture-rules.md)

---

## 1. Praktik 1 — MVVM Paling Sederhana: Counter

**Tujuan**: Membuktikan bahwa nilai variabel di dalam `ViewModel` tetap bertahan saat layar diputar, sedangkan nilai pada `remember` biasa akan kembali ke 0.

### A. ViewModel (`CounterViewModel.kt`)
```kotlin
package com.example.mvvmpraktikum.counter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    var count by mutableIntStateOf(0)
        private set

    fun increment() { count++ }
    fun decrement() { if (count > 0) count-- }
    fun reset() { count = 0 }
}
```

### B. UI Pembanding (`CounterScreen.kt`)
```kotlin
@Composable
fun CounterScreen(
    modifier: Modifier = Modifier,
    viewModel: CounterViewModel = viewModel()
) {
    // Variabel lokal pembanding (musnah saat rotasi layar)
    var localCount by remember { mutableIntStateOf(0) }

    CounterContent(
        count = viewModel.count,
        localCount = localCount,
        onIncrement = {
            viewModel.increment()
            localCount++
        },
        onDecrement = viewModel::decrement,
        onReset = viewModel::reset,
        modifier = modifier
    )
}
```

---

## 2. Praktik 2 & 3 — Pengelolaan Data Mahasiswa (CRUD & Reaktif)

### A. Model & Simulasi Server (`MahasiswaRepository.kt`)
```kotlin
data class Mahasiswa(val nim: String, val nama: String, val prodi: String)

class MahasiswaRepository {
    private var dataServer = listOf(
        Mahasiswa("235001", "Andi Saputra", "Teknik Informatika"),
        Mahasiswa("235002", "Budi Santoso", "Sistem Informasi"),
        Mahasiswa("235003", "Citra Lestari", "Teknik Informatika")
    )
    private var counterNim = 4

    private val _mahasiswa = MutableStateFlow<List<Mahasiswa>>(emptyList())
    val mahasiswa: StateFlow<List<Mahasiswa>> = _mahasiswa.asStateFlow()

    suspend fun muatData(simulasiGagal: Boolean = false) {
        delay(2000)
        if (simulasiGagal) throw Exception("Gagal terhubung ke server (simulasi)")
        _mahasiswa.value = dataServer
    }

    suspend fun tambah(nama: String) {
        delay(500)
        if (nama.contains("gagal", ignoreCase = true)) throw Exception("Server menolak nama \"$nama\"")
        val nim = "235%03d".format(counterNim++)
        dataServer = dataServer + Mahasiswa(nim, nama, "Teknik Informatika")
        _mahasiswa.value = dataServer
    }

    suspend fun hapus(nim: String) {
        delay(300)
        dataServer = dataServer.filterNot { it.nim == nim }
        _mahasiswa.value = dataServer
    }
}
```

### B. Versi Reaktif Terbaik (Praktik 3 dengan `combine` + `stateIn`)
```kotlin
data class MahasiswaUiState(
    val daftar: List<Mahasiswa> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

private data class Status(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class MahasiswaViewModel(
    private val repository: MahasiswaRepository = MahasiswaRepository()
) : ViewModel() {
    var nama by mutableStateOf("")
        private set

    private val _status = MutableStateFlow(Status())

    val uiState: StateFlow<MahasiswaUiState> = combine(
        repository.mahasiswa, _status
    ) { daftar, status ->
        MahasiswaUiState(
            daftar = daftar,
            isLoading = status.isLoading,
            isSaving = status.isSaving,
            errorMessage = status.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MahasiswaUiState(isLoading = true)
    )

    init { muatData() }

    fun onNamaChange(nilai: String) { nama = nilai }

    fun muatData(simulasiGagal: Boolean = false) {
        viewModelScope.launch {
            _status.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.muatData(simulasiGagal)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _status.update { it.copy(errorMessage = e.message ?: "Terjadi kesalahan") }
            } finally {
                _status.update { it.copy(isLoading = false) }
            }
        }
    }

    fun tambah() {
        if (nama.isBlank()) {
            _status.update { it.copy(errorMessage = "Nama tidak boleh kosong") }
            return
        }
        viewModelScope.launch {
            _status.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                repository.tambah(nama.trim())
                nama = "" // Input dibersihkan HANYA jika operasi sukses
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _status.update { it.copy(errorMessage = e.message ?: "Gagal menyimpan") }
            } finally {
                _status.update { it.copy(isSaving = false) }
            }
        }
    }

    fun hapus(nim: String) {
        viewModelScope.launch {
            try {
                repository.hapus(nim)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _status.update { it.copy(errorMessage = e.message ?: "Gagal menghapus") }
            }
        }
    }

    fun tutupError() {
        _status.update { it.copy(errorMessage = null) }
    }
}
```

---

## 3. Praktik 4 — Membaca API Publik dengan Pencarian Reaktif

Pada studi kasus ini, teks pencarian (`query`) digabungkan dengan data API melalui operator `combine` dan fungsi `snapshotFlow`:

```kotlin
class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {
    var query by mutableStateOf("")
        private set

    private val _status = MutableStateFlow(PostUiState())

    // snapshotFlow memancarkan event setiap kali nilai query berubah
    val uiState: StateFlow<PostUiState> = combine(
        _status, snapshotFlow { query }
    ) { status, q ->
        status.copy(posts = saring(status.posts, q))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PostUiState(isLoading = true)
    )

    init { loadPosts() }

    fun onQueryChange(nilai: String) { query = nilai }

    private fun saring(data: List<Post>, query: String): List<Post> =
        if (query.isBlank()) data
        else data.filter { it.title.contains(query, ignoreCase = true) }
}
```

> [!TIP]
> **Mengapa pencarian kata tidak memanggil API kembali?**  
> Karena seluruh 100 data artikel sudah berada di dalam memori `_status.posts`. Operasi filter dilakukan secara lokal di perangkat klien menggunakan memori RAM tanpa membebani kuota atau server!

---
[← Sebelumnya: Integrasi REST API Modern](./04-remote-data-retrofit-and-serialization.md) | [Berikutnya: Troubleshooting & Aturan Arsitektur →](./06-troubleshooting-and-architecture-rules.md)
