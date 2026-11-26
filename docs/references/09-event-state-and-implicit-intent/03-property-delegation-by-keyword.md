---
id: ref-modul03-03
title: Property Delegation (by Keyword) pada State Compose
module: Modul 03 - Event, State & Implisit Intent
category: language-features
tags:
  - property-delegation
  - by-keyword
  - getvalue
  - setvalue
  - compose-syntax
summary: Panduan penggunaan sintaks delegasi properti (by) di Kotlin untuk menyederhanakan pembacaan dan penulisan Compose State tanpa .value.
related:
  - docs/references/09-event-state-and-implicit-intent/02-event-handling-and-compose-state.md
  - docs/references/09-event-state-and-implicit-intent/05-lab-experiments-and-recomposition-analysis.md
---

# 🪄 Property Delegation (`by` Keyword) pada State Compose

[← Sebelumnya: Event Handling & Compose State](./02-event-handling-and-compose-state.md) | [Berikutnya: Implisit Intent (Actions & URI Schemes) →](./04-implicit-intent-actions-and-uris.md)

---

## 1. Apa itu Property Delegation?

**Property Delegation** adalah fitur bahasa pemrograman Kotlin yang memungkinkan developer "menitipkan" logika operasi pembacaan (*getter*) dan penulisan (*setter*) sebuah variabel ke objek lain menggunakan kata kunci **`by`**.

Dalam Jetpack Compose, fitur ini digunakan untuk menyederhanakan akses ke `MutableState<T>` sehingga variabel dapat diperlakukan seperti variabel biasa.

---

## 2. Perbandingan Sintaks: Tanpa `by` vs Menggunakan `by`

### A. Tanpa `by` (Menggunakan Tipe Data Asli `MutableState<T>`)
Developer harus selalu memanggil properti `.value`:
```kotlin
// Deklarasi: Tipe variabel adalah MutableState<Int>
val count: MutableState<Int> = remember { mutableStateOf(0) }

// Membaca nilai: Wajib menyertakan .value
Text("Hitungan: ${count.value}")

// Mengubah nilai: Wajib menugaskan ke .value
Button(onClick = { count.value++ }) {
    Text("Tambah")
}
```

### B. Menggunakan `by` (Delegasi Properti yang Bersih)
Kotlin compiler secara transparan menangani pemanggilan `.value` di belakang layar:
```kotlin
// Deklarasi: Tipe variabel diekstrak langsung sebagai Int
var count: Int by remember { mutableStateOf(0) }

// Membaca nilai: Langsung sebutkan nama variabel
Text("Hitungan: $count")

// Mengubah nilai: Langsung mutasi variabel
Button(onClick = { count++ }) {
    Text("Tambah")
}
```

---

## 3. Import Wajib yang Diperlukan

Jika Anda menggunakan kata kunci `by` dan Android Studio menandai error warna merah pada variabel state, hal itu disebabkan karena fungsi ekstensi operator delegasi belum diimpor:

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
```

> [!NOTE]
> Jika state dideklarasikan sebagai `val` (hanya dibaca / read-only), Anda hanya membutuhkan import `getValue`.  
> Jika state dideklarasikan sebagai `var` (dapat dimutasi / ditulis), Anda wajib mengimpor kedua method: `getValue` dan `setValue`.

---
[← Sebelumnya: Event Handling & Compose State](./02-event-handling-and-compose-state.md) | [Berikutnya: Implisit Intent (Actions & URI Schemes) →](./04-implicit-intent-actions-and-uris.md)
