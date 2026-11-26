---
id: ref-coroutine-02
title: Integrasi Coroutine di Jetpack Compose (LaunchedEffect vs rememberCoroutineScope)
module: Modul Coroutine Compose V4 Deep Dive
category: concurrency
tags:
  - launched-effect
  - remember-coroutine-scope
  - lifecycle-bound
  - event-handlers
  - withcontext
summary: Praktikum dan panduan perbandingan antara LaunchedEffect untuk aksi berbasis siklus hidup dan rememberCoroutineScope untuk aksi berbasis klik pengguna.
related:
  - docs/references/06-coroutines-compose-deepdive/01-coroutine-fundamentals-and-dispatchers.md
  - docs/references/06-coroutines-compose-deepdive/03-case-study-countdown-timer-and-analysis.md
  - docs/references/01-state-and-lifecycle/03-side-effects-launchedeffect.md
---

# 🎨 Integrasi Coroutine di Jetpack Compose: LaunchedEffect vs rememberCoroutineScope

[← Sebelumnya: Fondasi Coroutine & Dispatchers](./01-coroutine-fundamentals-and-dispatchers.md) | [Berikutnya: Studi Kasus Countdown Timer & Analisis →](./03-case-study-countdown-timer-and-analysis.md)

---

## 1. Tantangan Coroutine pada Siklus Hidup Compose

Jetpack Compose bekerja dengan siklus pembacaan ulang antarmuka yang dinamis (**Rekomposisi**).  
Fungsi Composable dapat dipanggil berulang kali kapan saja saat ada state yang berubah. Jika coroutine diluncurkan tanpa diikat ke siklus hidup composable:
- Setiap rekomposisi akan memicu duplikasi coroutine baru.
- Terjadi kebocoran memori (*memory leak*) ketika layar ditutup saat coroutine masih mengunduh data.

Compose menyediakan **Effect APIs** untuk menjamin coroutine aman terhadap siklus hidup UI.

---

## 2. Kapan Menggunakan `LaunchedEffect` vs `rememberCoroutineScope`?

```
                        Bagaimana coroutine ingin dipicu?
                                       │
                    ┌──────────────────┴──────────────────┐
                    ▼                                     ▼
        Secara Otomatis saat Layar Muncul           Melalui Aksi Pengguna
        atau Parameter State Berubah               (Klik Tombol, Gestur Geser)
                    │                                     │
           Gunakan LaunchedEffect               Gunakan rememberCoroutineScope
```

| Karakteristik | `LaunchedEffect` | `rememberCoroutineScope` |
| :--- | :--- | :--- |
| **Pemicu Eksekusi** | Masuk ke komposisi atau perubahan nilai `key` | Dipanggil manual di dalam event handler (misal `onClick`) |
| **Siklus Hidup** | Dibatalkan otomatis jika Composable keluar komposisi | Terikat pada siklus hidup Composable pembuat scope |
| **Lokasi Pemanggilan** | Harus di dalam body Composable langsung | Scope dibuat di body Composable, `scope.launch` dipanggil di dalam lambda callback |

---

## 3. Praktik 1: Pengambilan Data Otomatis (`LaunchedEffect`)

Menampilkan data pengguna dari internet begitu halaman dibuka:

```kotlin
@Composable
fun FetchDataScreen(userId: String) {
    var isLoading by remember { mutableStateOf(true) }
    var resultText by remember { mutableStateOf("") }

    // Coroutine dimulai otomatis di Main Thread saat composable masuk ke layar
    LaunchedEffect(key1 = userId) {
        isLoading = true
        // Memanggil fungsi suspend yang berpindah ke IO Thread
        resultText = fetchDataFromNetwork(userId)
        isLoading = false
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
            Text("Sedang mengambil data...")
        } else {
            Text(text = resultText)
        }
    }
}

// Fungsi suspend independen dengan pemindahan Dispatcher IO
suspend fun fetchDataFromNetwork(id: String): String {
    return withContext(Dispatchers.IO) {
        delay(2000) // Simulasi latensi jaringan 2 detik
        "Data berhasil dimuat untuk User ID: $id"
    }
}
```

---

## 4. Praktik 2: Aksi Tombol Manual (`rememberCoroutineScope`)

Menjalankan proses transaksi saat tombol diklik:

```kotlin
@Composable
fun ProcessButtonScreen() {
    // 1. Dapatkan scope coroutine yang sadar siklus hidup composable
    val scope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("Siap") }
    var isProcessing by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                // 2. Luncurkan coroutine dari dalam lambda klik tombol
                scope.launch {
                    isProcessing = true
                    statusText = "Memproses transaksi..."
                    
                    // Eksekusi fungsi suspend di background thread
                    statusText = processTransactionData()
                    
                    isProcessing = false
                }
            },
            enabled = !isProcessing
        ) {
            Text("Mulai Proses")
        }

        Text(
            text = statusText,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

suspend fun processTransactionData(): String {
    return withContext(Dispatchers.IO) {
        delay(3000) // Simulasi proses simpan database 3 detik
        "Transaksi Selesai!"
    }
}
```

---
[← Sebelumnya: Fondasi Coroutine & Dispatchers](./01-coroutine-fundamentals-and-dispatchers.md) | [Berikutnya: Studi Kasus Countdown Timer & Analisis →](./03-case-study-countdown-timer-and-analysis.md)
