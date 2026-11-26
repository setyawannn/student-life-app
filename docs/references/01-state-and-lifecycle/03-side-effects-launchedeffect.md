---
id: ref-state-03
title: Side-Effects dan LaunchedEffect di Jetpack Compose
module: Modul 04 - State & Lifecycle
category: side-effects
tags:
  - side-effects
  - launched-effect
  - coroutines
  - lifecycle-bound
  - memory-leak-prevention
summary: Panduan pemahaman konsep Side-Effect pada fungsi Composable dan implementasi fungsi LaunchedEffect untuk operasi asinkron yang aman dari kebocoran memori.
related:
  - docs/references/01-state-and-lifecycle/01-state-hoisting-fundamentals.md
  - docs/references/01-state-and-lifecycle/04-lab-experiments-and-analysis.md
  - docs/references/06-coroutines-compose-deepdive/02-compose-coroutine-effects.md
---

# ⚡ Side-Effects dan LaunchedEffect di Jetpack Compose

[← Sebelumnya: Configuration Change & Persistence](./02-configuration-change-and-persistence.md) | [Berikutnya: Lab Praktikum & Analisis Modul 4 →](./04-lab-experiments-and-analysis.md)

---

## 1. Apa itu Side-Effect?

Dalam paradigma deklaratif Jetpack Compose, sebuah fungsi composable idealnya adalah **fungsi murni (*pure function*)**:
- Bebas dari efek samping (hanya menerima input dan mengembalikan representasi UI).
- Dapat dieksekusi berkali-kali secara tak terduga (rekomposisi).
- Urutan pemanggilan composable dapat bervariasi atau dieksekusi secara paralel di berbagai thread.

**Side-Effect** adalah operasi apa pun yang:
1. Mempengaruhi state atau sistem di luar fungsi composable (misal: memicu navigasi, menampilkan Snackbar, mencatat analitik, request API).
2. Perlu dipicu berdasarkan perubahan state atau saat composable memasuki/meninggalkan komposisi.
3. Terikat langsung dengan siklus hidup (*lifecycle*) komponen visual tersebut.

> [!WARNING]
> Jangan pernah memanggil operasi asinkron, networking, atau manipulasi state luar secara langsung di dalam body fungsi Composable biasa tanpa Side-Effect Handler! Hal itu akan dieksekusi berulang-ulang pada setiap rekomendasi (*recomposition*), menyebabkan glitch performa, freeze, dan memory leak.

---

## 2. Mekanisme Kerja `LaunchedEffect`

`LaunchedEffect` adalah composable pembungkus yang meluncurkan Coroutine yang terikat (*scoped*) ke dalam siklus hidup Composable tempat ia didefinisikan:

```kotlin
LaunchedEffect(key1, key2, ...) {
    // Blok coroutine asinkron
}
```

### Karakteristik Penting:
1. **Dijalankan Saat Masuk Komposisi**: Pertama kali Composable muncul di layar, blok kode di dalam `LaunchedEffect` akan dieksekusi.
2. **Otomatis Dibatalkan (*Auto Cancel*)**: Jika composable meninggalkan komposisi (*onCleared / disposed*), coroutine yang sedang berjalan akan otomatis dibatalkan, sehingga **mencegah kebocoran memori (*memory leak*)**.
3. **Restart Berdasarkan Kunci (*Key*)**: Jika nilai dari salah satu `key` berubah, coroutine yang lama akan segera dibatalkan (*cancelled*), dan coroutine baru akan dijalankan ulang dengan nilai key yang baru.
4. **Key `Unit` atau `true`**: Digunakan jika Anda hanya ingin efek dijalankan **hanya satu kali** saat composable pertama kali ditampilkan (misal: inisialisasi awal layar).

---

## 3. Contoh Implementasi: Pengambilan Data Otomatis

```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun MyDataScreen(userId: String) {
    var data by remember { mutableStateOf("Loading...") }

    // Efek ini dijalankan saat MyDataScreen pertama kali muncul ke composition
    // Jika userId berganti, coroutine lama dibatalkan dan yang baru dijalankan untuk userId baru
    LaunchedEffect(userId) {
        // Simulasi request jaringan
        delay(2000)
        data = "Data Loaded for user $userId"
    }

    Text(text = data)
}
```

---

## 4. Perbedaan `LaunchedEffect(Unit)` vs `LaunchedEffect(state)`

| Aspek | `LaunchedEffect(Unit)` | `LaunchedEffect(state)` |
| :--- | :--- | :--- |
| **Frekuensi Eksekusi** | Tepat 1 kali selama siklus hidup Composable berada di layar | 1 kali saat inisialisasi + setiap kali nilai `state` berubah |
| **Pemicu Pembatalan** | Hanya saat Composable keluar dari Composition | Saat Composable keluar ATAU saat nilai `state` berubah |
| **Penggunaan Umum** | Inisialisasi awal data, registrasi listener satu kali | Menampilkan Snackbar saat nilai error berubah, filter data saat query berubah |

---

## 5. Referensi Silang Lanjutan
- Untuk analisis komparatif antara `LaunchedEffect` dan `rememberCoroutineScope`, lihat [Compose Coroutine Effects Deep Dive](../06-coroutines-compose-deepdive/02-compose-coroutine-effects.md).
- Untuk studi kasus praktikum Countdown Timer multi-key, lihat [Studi Kasus Timer & Analisis](../06-coroutines-compose-deepdive/03-case-study-countdown-timer-and-analysis.md).

---
[← Sebelumnya: Configuration Change & Persistence](./02-configuration-change-and-persistence.md) | [Berikutnya: Lab Praktikum & Analisis Modul 4 →](./04-lab-experiments-and-analysis.md)
