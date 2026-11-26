---
id: ref-modul03-02
title: Event Handling, Compose State, & Recomposition
module: Modul 03 - Event, State & Implisit Intent
category: state-management
tags:
  - event-handling
  - mutablestateof
  - remember
  - recomposition
  - state-driven-ui
summary: Cara kerja event handling di Jetpack Compose, perbedaan variabel lokal biasa vs mutableStateOf dalam memicu rekomposisi, dan peran remember.
related:
  - docs/references/09-event-state-and-implicit-intent/01-imperative-vs-declarative-ui.md
  - docs/references/09-event-state-and-implicit-intent/03-property-delegation-by-keyword.md
  - docs/references/09-event-state-and-implicit-intent/05-lab-experiments-and-recomposition-analysis.md
---

# ⚡ Event Handling, Compose State, & Recomposition

[← Sebelumnya: Imperatif XML vs Deklaratif Compose](./01-imperative-vs-declarative-ui.md) | [Berikutnya: Property Delegation (by Keyword) →](./03-property-delegation-by-keyword.md)

---

## 1. Definisi Event dalam Jetpack Compose

**Event** adalah sinyal aksi yang terjadi akibat interaksi pengguna dengan perangkat atau sistem, contohnya:
- Pengguna menekan tombol: memicu lambda `onClick = { ... }`.
- Pengguna mengetik teks di formulir: memicu lambda `onValueChange = { ... }`.
- Pengguna memilih item dari daftar atau menggeser layar (*gesture*).

---

## 2. Mengapa Variabel Biasa Tidak Memperbarui Tampilan UI?

Perhatikan eksperimen berikut:

```kotlin
@Composable
fun CounterGagal() {
    var count = 0 // Variabel lokal Kotlin biasa

    Column {
        Text("Counter: $count")
        Button(onClick = {
            count++
            Log.d("TEST", "Nilai count bertambah: $count")
        }) {
            Text("Tambah")
        }
    }
}
```

### Hasil Analisis Eksperimen:
- Ketika tombol "Tambah" diklik, nilai variabel `count` **benar-benar bertambah** di memori (terbukti dari Logcat: `Counter berubah: 1, 2, 3...`).
- Namun, **tampilan Text di layar HP tetap menampilkan `Counter: 0` tanpa pernah berubah!**
- **Penyebab**: Compiler Jetpack Compose tidak memiliki mekanisme untuk mengamati mutasi variabel Kotlin primitif biasa. Compose tidak mengetahui bahwa data telah berubah, sehingga tidak menjadwalkan penggambaran ulang antarmuka.

---

## 3. Solusi: `mutableStateOf` dan `remember`

Agar Compose mengetahui bahwa suatu data telah berubah dan UI perlu diperbarui, data tersebut harus dibungkus dalam objek yang dapat diamati (*observable*):

```kotlin
var count by remember { mutableStateOf(0) }
```

```
[Pengguna Klik Tombol] 
          │
          ▼
    count++ dipanggil
          │
          ▼
[mutableStateOf mendeteksi mutasi nilai]
          │
          ▼
[Compose Runtime Menjadwalkan RECOMPOSITION]
          │
          ▼
Fungsi Composable dipanggil ulang
Text("Counter: $count") menggambar angka baru di layar!
```

### Peran Kritis `remember { ... }`:
Jika Anda hanya menulis `mutableStateOf(0)` tanpa `remember`:
```kotlin
// SALAH: Tanpa remember
var count by mutableStateOf(0)
```
Setiap kali rekomposisi terjadi (saat nilai count berubah dari 0 ke 1), seluruh fungsi composable akan dieksekusi ulang dari baris paling atas, yang menyebabkan `count` **diinisialisasi ulang kembali menjadi 0**!  
Fungsi **`remember`** bertindak sebagai brankas memori lokal yang menyimpan nilai state agar tetap bertahan selama composable masih berada di dalam hierarki komposisi.

---
[← Sebelumnya: Imperatif XML vs Deklaratif Compose](./01-imperative-vs-declarative-ui.md) | [Berikutnya: Property Delegation (by Keyword) →](./03-property-delegation-by-keyword.md)
