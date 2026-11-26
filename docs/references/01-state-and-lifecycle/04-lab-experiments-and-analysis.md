---
id: ref-state-04
title: Lembar Percobaan dan Analisis Modul 4 (State Hoisting, Konfigurasi, Side-Effect)
module: Modul 04 - State & Lifecycle
category: lab-guide
tags:
  - lab-guide
  - state-hoisting
  - rememberSaveable
  - LaunchedEffect
  - snackbar
  - orientation-change
summary: Dokumentasi langkah kerja, kode percobaan, pertanyaan evaluasi, dan pembahasan mendalam untuk Modul 4 Jetpack Compose.
related:
  - docs/references/01-state-and-lifecycle/01-state-hoisting-fundamentals.md
  - docs/references/01-state-and-lifecycle/02-configuration-change-and-persistence.md
  - docs/references/01-state-and-lifecycle/03-side-effects-launchedeffect.md
---

# 🧪 Lembar Percobaan & Analisis Modul 4

[← Sebelumnya: Side Effects & LaunchedEffect](./03-side-effects-launchedeffect.md) | [Berikutnya: Modul 5 - Lazy Lists →](../02-lazy-layouts/01-lazy-lists-column-and-row.md)

---

## 1. Percobaan 4.5.1: State Hoisting

### A. Kode Awal (State Lokal / Stateful)
```kotlin
@Composable
fun CounterScreen(modifier: Modifier) {
    var number by remember { mutableStateOf(0) }
    val label by remember { mutableStateOf("Increment") }
    Column(modifier) {
        Text(text = "$number", fontSize = 72.sp)
        Button(onClick = { number++ }) {
            Text(text = "$label")
        }
    }
}
```

### B. Refaktorisasi State & Event Hoisting
Komponen diubah agar menerima status dari luar (*Stateless*):
```kotlin
@Composable
fun CounterScreen(
    modifier: Modifier,
    number: Int,
    label: String,
    onButtonClick: () -> Unit
) {
    Column(modifier) {
        Text(text = "$number", fontSize = 72.sp)
        Button(onClick = onButtonClick) {
            Text(text = label)
        }
    }
}
```

Pemanggilan di dalam `onCreate()` (MainActivity):
```kotlin
var number by remember { mutableStateOf(0) }

CounterScreen(
    modifier = Modifier.padding(32.dp),
    number = number,
    label = "Increment",
    onButtonClick = { number++ }
)
```

### C. Modifikasi Logika Bisnis: Tombol "Double"
Ketika kebutuhan berubah menjadi menggandakan nilai awal (1 -> 2 -> 4 -> 8...):
```kotlin
var number by remember { mutableStateOf(1) }

CounterScreen(
    modifier = Modifier.padding(32.dp),
    number = number,
    label = "Double",
    onButtonClick = { number *= 2 } // Menggandakan nilai
)
```

### 🧠 Analisis & Evaluasi:
- **Di mana logika perubahan nilai dilakukan?** Dilakukan pada lambda pemanggil di `onCreate` (induk), bukan di dalam composable `CounterScreen`.
- **Apakah Composable `CounterScreen` perlu diubah saat logika perhitungan diganti dari pertambahan menjadi perkalian?** **Tidak sama sekali!** Composable `CounterScreen` sepenuhnya terbebas dari aturan bisnis. Inilah esensi utama dari *reusability* dan *separation of concerns*.

---

## 2. Percobaan 4.5.2: Perubahan Konfigurasi (Rotasi Layar)

### A. Skenario Uji
1. Aplikasi dijalankan pada orientasi portrait, tombol ditekan hingga nilai berubah.
2. Perangkat dirotasi menjadi landscape:
   - **Hasil dengan `remember`**: Nilai kembali ke nilai awal (0 atau 1) karena Activity dibuat ulang (*destroyed and recreated*).
3. Kode diubah menggunakan `rememberSaveable`:
   ```kotlin
   var number by rememberSaveable { mutableStateOf(1) }
   ```
4. Dirotasi kembali menjadi landscape:
   - **Hasil dengan `rememberSaveable`**: Nilai tetap bertahan sesuai angka sebelum rotasi layar karena nilai tersimpan di `Bundle`.

---

## 3. Percobaan 4.5.3: Side-Effect dengan `LaunchedEffect`

### A. Integrasi SnackBar & `LaunchedEffect(number)`
Di dalam `onCreate()`:
```kotlin
val snackbarHostState = remember { SnackbarHostState() }

Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
) { innerPadding ->
    // Composable Screen
    CounterScreen(
        modifier = Modifier.padding(innerPadding),
        number = number,
        label = "Double",
        onButtonClick = { number *= 2 }
    )

    // Side-Effect: Terpicu setiap nilai 'number' berubah
    LaunchedEffect(number) {
        snackbarHostState.showSnackbar("Number $number is shown!")
    }
}
```

### B. Simulasi Pengambilan Data Jaringan (`Repo.kt`)
Membuat class Repository simulasi:
```kotlin
import kotlinx.coroutines.delay
import kotlin.random.Random

class Repo {
    companion object {
        suspend fun getData(): Int {
            delay(2000) // Simulasikan delay network 2 detik
            return Random.nextInt(100, 1000)
        }
    }
}
```

Menambahkan `LaunchedEffect(Unit)` untuk inisialisasi awal:
```kotlin
LaunchedEffect(Unit) {
    number = Repo.getData()
}
```

### 🧠 Analisis Komparatif:
1. **Mengapa pesan SnackBar tampil saat aplikasi pertama kali dibuka tanpa klik pengguna?**  
   Karena saat pertama kali masuk ke komposisi (*initial composition*), `LaunchedEffect(number)` selalu dieksekusi dengan nilai awal `number`.
2. **Apa perbedaan antara `LaunchedEffect(state)` vs `LaunchedEffect(Unit)`?**  
   - `LaunchedEffect(Unit)` hanya dieksekusi 1 kali saat layar dimuat, ideal untuk memuat data awal (*fetch data*).  
   - `LaunchedEffect(number)` dieksekusi setiap kali nilai `number` berubah, cocok untuk mendeteksi mutasi data dan memperbarui efek luar seperti SnackBar/Notifikasi.

---
[← Sebelumnya: Side Effects & LaunchedEffect](./03-side-effects-launchedeffect.md) | [Berikutnya: Modul 5 - Lazy Lists →](../02-lazy-layouts/01-lazy-lists-column-and-row.md)
