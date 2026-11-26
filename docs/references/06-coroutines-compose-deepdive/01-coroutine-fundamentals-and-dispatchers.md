---
id: ref-coroutine-01
title: Fondasi Coroutine, Aturan Dispatcher Bawaan, & withContext
module: Modul Coroutine Compose V4 Deep Dive
category: concurrency
tags:
  - coroutines
  - dispatchers
  - withcontext
  - thread-vs-coroutine
  - suspending-function
summary: Analisis mendalam cara kerja Kotlin Coroutines, perbedaan Thread vs Coroutine, pewarisan default Dispatchers, dan perpindahan thread via withContext.
related:
  - docs/references/06-coroutines-compose-deepdive/02-compose-coroutine-effects.md
  - docs/references/06-coroutines-compose-deepdive/03-case-study-countdown-timer-and-analysis.md
  - docs/references/04-viewmodel/02-coroutines-in-android.md
---

# ⚙️ Fondasi Coroutine, Aturan Dispatcher Bawaan, & withContext

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Compose Coroutine Effects →](./02-compose-coroutine-effects.md)

---

## 1. Analogi Koki Restoran

Untuk memahami cara kerja Coroutine secara intuitif, perhatikan analogi koki dapur berikut:

```
+-------------------------------------------------------------------------+
| TANPA COROUTINE (SINKRON / MEMBLOKIR)                                   |
| Koki merebus air untuk pasta -> Berdiri diam menunggu air mendidih      |
| selama 5 menit -> Tidak melayani pesanan kasir -> Restoran macet & ANR! |
+-------------------------------------------------------------------------+

+-------------------------------------------------------------------------+
| DENGAN COROUTINE (ASINKRON / MENANGGUHKAN / SUSPEND)                    |
| Koki menyalakan kompor merebus air -> Tugas merebus DITANGGUHKAN        |
| (Pause) -> Koki melayani pesanan kasir lain -> Saat air mendidih, koki  |
| kembali memasukkan pasta ke panci (Resume). Sistem tetap lancar!       |
+-------------------------------------------------------------------------+
```

### Perbedaan Utama: Thread OS vs Kotlin Coroutine

| Sifat | Thread Konvensional | Kotlin Coroutine |
| :--- | :--- | :--- |
| **Pengelola** | Dikelola langsung oleh Sistem Operasi (Kernel OS) | Dikelola oleh **Kotlin Runtime** di atas thread pool |
| **Konsumsi Memori** | Berat (setiap thread memakan ~1 MB stack memori) | **Sangat ringan** (beberapa byte per instance coroutine) |
| **Kapasitas Skalabilitas** | Terbatas (ratusan thread dapat membuat sistem Out of Memory) | **Puluhan ribu** coroutine dapat berjalan berdampingan secara mulus |
| **Operasi Blokir** | Memblokir thread fisik tempat ia berjalan | **Menangguhkan (*suspend*)** tanpa memblokir thread fisik |

---

## 2. Istilah Kunci Coroutine

1. **`suspend`**: Penanda fungsi bahwa eksekusinya dapat dijeda (*pause*) dan dilanjutkan kembali (*resume*) di kemudian hari tanpa memblokir thread pemanggil. Hanya bisa dipanggil dari dalam blok coroutine atau fungsi `suspend` lainnya.
2. **`CoroutineScope`**: Wadah siklus hidup. Jika scope dibatalkan, semua coroutine yang berjalan di dalamnya otomatis mati.
3. **Coroutine Builder**:
   - `launch`: Pola *"Jalankan dan lupakan"* (*Fire and Forget*). Digunakan saat Anda tidak membutuhkan hasil kembalian (misal hanya memicu pembaruan UI).
   - `async`: Pola *"Jalankan dan minta hasilnya nanti"*. Menghasilkan objek `Deferred<T>` yang hasilnya diambil menggunakan fungsi `.await()`.
4. **`Dispatchers`**: Pengatur jalur thread tempat coroutine dieksekusi:
   - `Dispatchers.Main`: Thread Utama (UI). Khusus tugas visual cepat.
   - `Dispatchers.IO`: Dioptimalkan untuk operasi Input/Output (jaringan HTTP, pembacaan file disk, Room Database).
   - `Dispatchers.Default`: Dioptimalkan untuk tugas pemrosesan CPU tinggi (algoritma kompleks, pengurutan ribuan data, encoding).

---

## 3. Aturan Bawaan: "Jika Dispatcher Tidak Ditulis, Mana yang Akan Berjalan?"

Jika Anda tidak menentukan `Dispatcher` secara eksplisit saat membuat coroutine, coroutine tersebut akan **mewarisi (*inherit*) Dispatcher dari CoroutineScope tempat ia dipanggil**:

```
1. Di Jetpack Compose (LaunchedEffect & rememberCoroutineScope):
   DEFAULT: Dispatchers.Main
   * Catatan Kritis: Jangan lakukan I/O di sini tanpa withContext! UI akan beku.

2. Di ViewModel (viewModelScope):
   DEFAULT: Dispatchers.Main.immediate
   * Catatan Kritis: Berjalan di UI Thread secara bawaan.

3. Di Scope Mandiri (CoroutineScope(Job())):
   DEFAULT: Dispatchers.Default (Thread Pool Komputasi CPU).
```

---

## 4. Berpindah Thread dengan `withContext`

Untuk mengeksekusi operasi berat tanpa memblokir UI, gunakan `withContext`. Coroutine akan otomatis **berpindah thread**, menunggu proses selesai, lalu **otomatis kembali ke Main Thread**:

```kotlin
scope.launch {
    // 1. Berjalan di Dispatchers.Main (UI Thread)
    showLoadingProgressBar()

    // 2. PINDAH ke Dispatchers.IO untuk mengunduh data dari internet
    val data = withContext(Dispatchers.IO) {
        apiService.fetchBigPayload() // Operasi jaringan berat
    }

    // 3. Otomatis KEMBALI ke Dispatchers.Main (UI Thread) secara aman
    hideLoadingProgressBar()
    renderResultOnScreen(data)
}
```

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Compose Coroutine Effects →](./02-compose-coroutine-effects.md)
