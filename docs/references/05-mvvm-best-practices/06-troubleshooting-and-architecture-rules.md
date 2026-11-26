---
id: ref-mvvm-06
title: Troubleshooting, Masalah Umum, & Checklist Arsitektur MVVM
module: Modul Panduan MVVM Modern
category: debugging
tags:
  - troubleshooting
  - common-errors
  - retrofit-errors
  - serialization-errors
  - architecture-checklist
summary: Panduan pemecahan masalah umum (Retrofit, Serialization, Permission), checklist kriteria penilaian kode profesional, dan latihan pengayaan.
related:
  - docs/references/05-mvvm-best-practices/04-remote-data-retrofit-and-serialization.md
  - docs/references/05-mvvm-best-practices/05-practical-labs-counter-mahasiswa-posts.md
---

# 🩺 Troubleshooting, Masalah Umum, & Checklist Arsitektur MVVM

[← Sebelumnya: Praktikum Hands-on](./05-practical-labs-counter-mahasiswa-posts.md) | [Berikutnya: Coroutine Deep Dive & Dispatchers →](../06-coroutines-compose-deepdive/01-coroutine-fundamentals-and-dispatchers.md)

---

## 1. Tabel Diagnosis Masalah & Solusi Cepat

| Gejala Error | Penyebab Utama | Solusi Pasti |
| :--- | :--- | :--- |
| `java.lang.SecurityException: Permission denied (missing INTERNET permission?)` | Tag izin internet belum dideklarasikan di manifest. | Tambahkan `<uses-permission android:name="android.permission.INTERNET" />` di luar tag `<application>` pada `AndroidManifest.xml`, lalu **uninstall aplikasi lama** dan install ulang. |
| `Serializer for class 'Post' is not found` | Plugin Kotlin Serialization belum aktif atau lupa memberi anotasi `@Serializable`. | 1. Berikan `@Serializable` di atas deklarasi data class.<br>2. Pastikan `alias(libs.plugins.kotlin.serialization)` terpasang di `build.gradle.kts` Project dan Module.<br>3. Lakukan **Sync Now** dan **Rebuild Project**. |
| `java.lang.IllegalArgumentException: baseUrl must end in /` | URL dasar Retrofit tidak diakhiri garis miring `/`. | Ubah `"https://jsonplaceholder.typicode.com"` menjadi `"https://jsonplaceholder.typicode.com/"`. |
| `JsonDecodingException: Unexpected JSON token at offset... Unknown key 'xyz'` | Server API mengembalikan properti JSON yang tidak dideklarasikan di Data Class Kotlin. | Konfigurasikan parser JSON dengan mengaktifkan pengabaian kunci tak dikenal:<br>`val json = Json { ignoreUnknownKeys = true }`. |
| Error koneksi di emulator padahal laptop terkoneksi internet | DNS emulator mengalami cache korup atau emulator hang. | Buka Android Studio **Device Manager** -> Klik titik tiga pada emulator -> Pilih **Cold Boot Now**. |

---

## 2. Checklist Kriteria Kualitas Arsitektur (*Quality Gate*)

Gunakan checklist ini untuk menguji apakah kode Anda sudah memenuhi standar industri modern:

- [ ] **Bebas Logika di Composable**: Tidak ada instansiasi langsung `RetrofitClient` atau pemanggilan method `repository.*` di dalam fungsi Composable.
- [ ] **State Encapsulation**: State mutabel di ViewModel (`MutableStateFlow`) berstatus `private`, dan hanya diekspos keluar sebagai `StateFlow` read-only (`val uiState = _uiState.asStateFlow()`).
- [ ] **Lifecycle-Safe Observation**: Composable UI membaca state menggunakan `collectAsStateWithLifecycle()` dari library `lifecycle-runtime-compose`.
- [ ] **UI State Robustness**: Layar memiliki representasi visual lengkap untuk 3 kondisi:
  1. Kondisi Loading (ProgressBar / Skeleton).
  2. Kondisi Sukses (Daftar data tampil).
  3. Kondisi Error (Pesan error ramah pengguna + Tombol Coba Lagi / Retry).
- [ ] **Tahan Rotasi Layar**: Data dan posisi formulir tidak reset saat ponsel dirotasi ke posisi landscape.
- [ ] **Safe Coroutines**: Tidak ada blok `catch` yang menelan `CancellationException`.

---

## 3. Latihan Mandiri & Tantangan Pengayaan

1. **Detail Artikel (Wajib)**:  
   Ketika salah satu kartu artikel diklik, buka layar detail baru yang mengambil data via endpoint `getPostById(id)`. Karena halaman detail awalnya kosong, gunakan pemodelan **`sealed interface UiState { object Loading; data class Success; data class Error }`**.
2. **Komentar Artikel (Wajib)**:  
   Tambahkan endpoint `@GET("posts/{id}/comments")` dan model `Comment(postId, id, name, email, body)`. Tampilkan daftar komentar pengguna di bawah detail artikel.
3. **Penggantian Sumber API (Pengayaan)**:  
   Gantikan sumber data dengan endpoint publik lain (seperti `https://dummyjson.com/products`). Buktikan bagaimana pola MVVM memungkinkan pergantian API hanya dengan mengubah layer data tanpa merusak Composable UI!

---
[← Sebelumnya: Praktikum Hands-on](./05-practical-labs-counter-mahasiswa-posts.md) | [Berikutnya: Coroutine Deep Dive & Dispatchers →](../06-coroutines-compose-deepdive/01-coroutine-fundamentals-and-dispatchers.md)
