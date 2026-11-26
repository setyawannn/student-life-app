---
id: ref-coroutine-03
title: Studi Kasus Praktikum Countdown Timer & Analisis Coroutine Mendalam
module: Modul Coroutine Compose V4 Deep Dive
category: case-study
tags:
  - case-study
  - countdown-timer
  - launchedeffect-keys
  - coroutine-cancellation
  - delay-reset
  - thread-execution
summary: Analisis kode komprehensif praktikum Countdown Timer multi-key, mekanisme pembatalan saat jeda, efek penambahan state dinamis, dan eksekusi thread.
related:
  - docs/references/06-coroutines-compose-deepdive/01-coroutine-fundamentals-and-dispatchers.md
  - docs/references/06-coroutines-compose-deepdive/02-compose-coroutine-effects.md
  - docs/references/01-state-and-lifecycle/03-side-effects-launchedeffect.md
---

# ⏱️ Studi Kasus Praktikum Countdown Timer & Analisis Coroutine Mendalam

[← Sebelumnya: Compose Coroutine Effects](./02-compose-coroutine-effects.md) | [Berikutnya: Glosarium & Kamus Istilah Terpadu →](../GLOSSARY.md)

---

## 1. Kode Lengkap Aplikasi Countdown Timer (`CountdownTimerFullApp.kt`)

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CountdownTimerFullApp() {
    var timeLeft by remember { mutableIntStateOf(10) }
    var isRunning by remember { mutableStateOf(false) }

    // LaunchedEffect dipicu setiap kali isRunning ATAU timeLeft berubah
    LaunchedEffect(key1 = isRunning, key2 = timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        } else if (timeLeft == 0) {
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sisa Waktu:", style = MaterialTheme.typography.titleMedium)
        Text("$timeLeft Detik", style = MaterialTheme.typography.displayLarge)

        // Penanda Waktu Habis
        if (timeLeft == 0) {
            Text(
                text = "Waktu Habis!",
                color = Color.Red,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Kontrol Utama
        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { isRunning = true },
                enabled = !isRunning && timeLeft > 0
            ) {
                Text("Mulai")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { isRunning = false },
                enabled = isRunning
            ) {
                Text("Jeda")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    isRunning = false
                    timeLeft = 10
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Tambah Waktu Dinamis
        Button(
            onClick = { timeLeft += 5 },
            enabled = timeLeft > 0
        ) {
            Text("+5 Detik")
        }
    }
}
```

---

## 2. Analisis Kritis & Bedah Kode Coroutine

### 🧐 1. Analisis Kunci (*Key*) `LaunchedEffect`
> **Pertanyaan**: Mengapa `LaunchedEffect` menggunakan dua key: `key1 = isRunning` dan `key2 = timeLeft`? Apa yang terjadi jika `key2 = timeLeft` dihapus dan hanya menyisakan `key1 = isRunning`?

**Pembahasan Mendalam**:
- Pola di atas mengandalkan siklus: `LaunchedEffect` menunggu 1 detik (`delay(1000)`), lalu mengubah `timeLeft -= 1`.  
- Karena `timeLeft` dijadikan kunci kedua (`key2`), pengurangan nilai tersebut memicu **relaunch** `LaunchedEffect` untuk detik berikutnya. Inilah yang menciptakan efek perulangan detak jam (*clock tick loop*).
- **Jika `key2 = timeLeft` dihapus**:  
  Saat tombol "Mulai" ditekan, `isRunning` berubah dari `false` ke `true`. `LaunchedEffect` dieksekusi **tepat satu kali**: menunggu 1 detik, menurunkan waktu dari 10 menjadi 9, lalu **berhenti total**. Timer tidak akan pernah menghitung mundur ke angka 8, 7, 6, dst., karena tidak ada pemicu baru untuk mengulangi blok coroutine tersebut!

---

### 🛑 2. Perilaku Pembatalan (*Cancellation*) saat Tombol "Jeda" Ditekan
> **Pertanyaan**: Saat timer sedang berjalan di pertengahan detik ke-5 (misal pada milidetik ke-400 dari fungsi `delay(1000)`) dan pengguna menekan tombol "Jeda" (`isRunning = false`), apa yang terjadi pada fungsi `delay` di dalam `LaunchedEffect`?

**Pembahasan Mendalam**:
- Fungsi `delay()` dalam Kotlin Coroutines adalah fungsi penangguhan kooperatif (*cooperative suspending function*) yang peka terhadap pembatalan.
- Ketika tombol "Jeda" ditekan, nilai `key1` (`isRunning`) berubah menjadi `false`.  
- Jetpack Compose segera **membatalkan (*cancel*) CoroutineScope** dari `LaunchedEffect` yang sedang aktif saat itu dengan melempar `CancellationException` di balik layar.
- Fungsi `delay(1000)` **langsung dihentikan seketika tanpa menyelesaikan sisa 600 milidetik**, dan baris `timeLeft -= 1` **tidak dieksekusi**. Waktu berhenti tepat pada detik tersebut secara presisi.

---

### ⏳ 3. Perilaku Penambahan State Dinamis (+5 Detik)
> **Pertanyaan**: Ketika tombol "+5 Detik" ditekan saat timer sedang berjalan aktif, bagaimana `LaunchedEffect` merespons? Apakah durasi `delay(1000)` yang sedang berjalan akan diulang dari awal detik atau tidak?

**Pembahasan Mendalam**:
- Ketika tombol ditekan, nilai `timeLeft` bertambah 5 (misal dari 7 menjadi 12).
- Karena `timeLeft` adalah `key2` dari `LaunchedEffect`, perubahan nilai ini menyebabkan coroutine yang sedang berjalan **dibatalkan secara instan**.
- Compose meluncurkan coroutine baru dari awal blok `LaunchedEffect`.
- **Akibatnya**: Durasi `delay(1000)` yang sedang berlangsung **diulang dari awal detik baru (0 ms)** dengan nilai 12 detik. Hal ini merupakan konsekuensi logis dari restart lifecycle coroutine berbasis key.

---

### 🧵 4. Analisis Eksekusi Thread & Mutasi UI
> **Pertanyaan**: Blok `LaunchedEffect` di atas tidak menentukan `Dispatchers` secara eksplisit:
> 1. Di thread mana kode `timeLeft -= 1` dieksekusi?
> 2. Mengapa perubahan nilai `timeLeft` di dalam coroutine tersebut bisa langsung memperbarui UI antarmuka secara instan tanpa perlu `withContext(Dispatchers.Main)`?

**Pembahasan Mendalam**:
1. **Thread Eksekusi**:  
   Kode dieksekusi pada **Main Thread (UI Thread)** Android. Berdasarkan aturan pewarisan (*dispatcher inheritance*), default dispatcher untuk `LaunchedEffect` dalam Compose Runtime adalah `Dispatchers.Main`.
2. **Pembaruan UI Tanpa `withContext`**:  
   Karena coroutine sudah secara alami berada di `Dispatchers.Main`, mutasi terhadap Compose State (`mutableIntStateOf`) aman dilakukan secara langsung. Sistem *Snapshot state* Compose mendeteksi penulisan nilai pada Main Thread dan langsung menjadwalkan rekomendasi (*recomposition*) untuk memperbarui widget `Text("$timeLeft Detik")` di frame berikutnya tanpa terjadi exception thread.

---
[← Sebelumnya: Compose Coroutine Effects](./02-compose-coroutine-effects.md) | [Berikutnya: Glosarium & Kamus Istilah Terpadu →](../GLOSSARY.md)
