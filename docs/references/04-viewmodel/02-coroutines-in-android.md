---
id: ref-vm-02
title: Dasar Coroutines dalam Pemrograman Android
module: Modul 07 - ViewModel & Coroutine
category: concurrency
tags:
  - coroutines
  - suspend-function
  - dispatchers
  - viewmodelscope
  - non-blocking
summary: Konsep konkurensi asinkron di Android menggunakan Kotlin Coroutines, fungsi penangguhan (suspend), dan peran Dispatchers (Main, IO, Default).
related:
  - docs/references/04-viewmodel/01-viewmodel-core-and-lifecycle.md
  - docs/references/04-viewmodel/03-lab-experiments-calculator-and-threading.md
  - docs/references/06-coroutines-compose-deepdive/01-coroutine-fundamentals-and-dispatchers.md
---

# 🚀 Dasar Coroutines dalam Pemrograman Android

[← Sebelumnya: ViewModel Architecture & Lifecycle](./01-viewmodel-core-and-lifecycle.md) | [Berikutnya: Lab Praktikum & Analisis Modul 7 →](./03-lab-experiments-calculator-and-threading.md)

---

## 1. Masalah Utama: UI Freezing & ANR

Aplikasi Android menjalankan proses antarmuka pengguna pada **Main Thread (UI Thread)**.  
Jika sebuah tugas berat dieksekusi secara sinkron di Main Thread (misalnya: mengambil data API via HTTP atau query ribuan baris database), thread UI akan **terblokir (*blocked*)**.

Akibatnya:
- Tampilan aplikasi membeku (*freeze* / *jank*).
- Sentuhan layar tidak direspons.
- Sistem operasi Android akan memunculkan dialog kegagalan fatal: **ANR (Application Not Responding)**.

---

## 2. Apa itu Kotlin Coroutines?

**Coroutine** adalah pola desain konkurensi asinkron yang bersifat *non-blocking*. Coroutine memungkinkan kode asinkron ditulis dengan gaya berurutan (*sequential*) yang bersih tanpa jebakan *callback hell*.

### Karakteristik Penting:
1. **Sangat Ringan (*Lightweight*)**: Ribuan coroutine dapat dijalankan secara bersamaan pada satu thread tunggal tanpa membebani memori sistem (berbeda dengan Thread OS yang berat).
2. **Suspending Functions (`suspend`)**: Fungsi dengan kata kunci `suspend` dapat **ditangguhkan (*pause*)** saat menunggu I/O selesai, dan **dilanjutkan kembali (*resume*)** tanpa memblokir thread yang mendasarinya.
3. **Structured Concurrency**: Eksekusi coroutine terikat pada cakupan (*Scope*). Jika scope dihancurkan, seluruh coroutine anak otomatis dibatalkan.

---

## 3. Komponen Inti Coroutines

```
                      +-------------------+
                      |   CoroutineScope  | (Wadah Siklus Hidup)
                      +-------------------+
                                │
               ┌────────────────┴────────────────┐
               ▼                                 ▼
      +-----------------+               +-----------------+
      |  scope.launch   |               |   scope.async   |
      | (Fire & Forget) |               | (Mengembalikan  |
      |                 |               |  Deferred hasil)|
      +-----------------+               +-----------------+
               │                                 │
               └────────────────┬────────────────┘
                                ▼
                      +-------------------+
                      |    Dispatchers    | (Jalur Thread Pool)
                      +-------------------+
                                │
       ┌────────────────────────┼────────────────────────┐
       ▼                        ▼                        ▼
Dispatchers.Main         Dispatchers.IO          Dispatchers.Default
(Update Antarmuka UI)    (Jaringan & Database)   (Kalkulasi Berat CPU)
```

### Tabel Rincian Dispatchers

| Dispatcher | Target Penggunaan | Contoh Pekerjaan |
| :--- | :--- | :--- |
| `Dispatchers.Main` | Thread Utama Android | Memperbarui teks UI, menampilkan Toast, animasi |
| `Dispatchers.IO` | Thread Pool I/O (Input/Output) | Panggilan REST API Retrofit, baca/tulis file disk, Room Database |
| `Dispatchers.Default` | Thread Pool Berdasarkan Inti CPU | Parsing JSON raksasa, kompresi gambar, pengurutan list jutaan data |

---

## 4. Contoh Implementasi

### A. Memanggil API Jaringan (Retrofit)
```kotlin
// Di dalam Activity atau ViewModel
viewModelScope.launch {
    try {
        // suspend function dieksekusi di background tanpa memblokir UI
        val response = apiService.getData()
        updateUI(response)
    } catch (e: Exception) {
        showError(e.message)
    }
}
```

### B. Operasi Database dengan `Dispatchers.IO`
```kotlin
viewModelScope.launch(Dispatchers.IO) {
    // Menjalankan query insert database di thread IO yang aman
    database.userDao().insertUser(user)
}
```

---
[← Sebelumnya: ViewModel Architecture & Lifecycle](./01-viewmodel-core-and-lifecycle.md) | [Berikutnya: Lab Praktikum & Analisis Modul 7 →](./03-lab-experiments-calculator-and-threading.md)
