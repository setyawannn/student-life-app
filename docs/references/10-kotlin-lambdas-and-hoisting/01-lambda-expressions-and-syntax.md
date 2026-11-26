---
id: ref-modul10-01
title: Sintaks Dasar Fungsi Lambda di Kotlin (Lambda Expressions)
module: Panduan Lengkap Fungsi Lambda & State Hoisting
category: kotlin-language
tags:
  - kotlin-lambda
  - anonymous-function
  - arrow-syntax
  - lambda-expressions
  - higher-order-functions
summary: Pengenalan dasar fungsi lambda (anonymous function) pada Kotlin, aturan tanda panah (->), dan perbandingannya dengan fungsi reguler.
related:
  - docs/references/10-kotlin-lambdas-and-hoisting/02-higher-order-functions-in-compose.md
  - docs/references/10-kotlin-lambdas-and-hoisting/03-trailing-lambda-and-it-keyword.md
  - docs/references/10-kotlin-lambdas-and-hoisting/04-state-hoisting-with-lambdas.md
---

# 🏹 Sintaks Dasar Fungsi Lambda di Kotlin (Lambda Expressions)

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Higher-Order Functions in Compose →](./02-higher-order-functions-in-compose.md)

---

## 1. Definisi Fungsi Lambda

**Fungsi Lambda** (*Anonymous Function*) adalah fungsi yang tidak memiliki nama dan dapat diperlakukan layaknya sebuah variabel:
- Dapat disimpan ke dalam variabel.
- Dapat dikirimkan sebagai argumen parameter ke fungsi lain.
- Dapat dikembalikan (*return value*) dari suatu fungsi.

Dalam pengembangan Jetpack Compose, hampir 100% interaksi antarmuka ditulis menggunakan ekspresi lambda kurung kurawal `{ ... }` karena sintaksnya sangat ringkas, deklaratif, dan bersih.

---

## 2. Perbandingan: Fungsi Biasa vs Fungsi Lambda

| Fitur | Fungsi Biasa (`fun`) | Fungsi Lambda (`{ ... }`) |
| :--- | :--- | :--- |
| **Deklarasi** | Wajib menggunakan kata kunci `fun` dan memiliki nama fungsi | Menggunakan kurung kurawal `{ }` dan tidak memiliki nama |
| **Return Value** | Menggunakan kata kunci `return` secara eksplisit | **Otomatis mengembalikan nilai dari baris terakhir** di dalam `{ }` |
| **Tujuan Penggunaan** | Logika yang panjang dan dipanggil berulang kali di banyak tempat | Aksi singkat yang disisipkan langsung ke dalam parameter event |

---

## 3. Aturan Tanda Panah (`->`) dalam Lambda

Tanda panah `->` berfungsi sebagai **"tembok pembatas"** yang memisahkan antara **parameter input** (di sebelah kiri) dengan **badan/isi fungsi** (di sebelah kanan).

```
val namaVariabel = { param1: Tipe, param2: Tipe ->
    // Badan fungsi (Body)
    // Baris terakhir otomatis menjadi nilai kembalian
}
```

### Kapan Tanda Panah Muncul atau Hilang?

1. **Wajib Muncul (Jika parameter diberi nama manual)**:
   ```kotlin
   val sapa = { nama: String -> println("Halo $nama") }
   ```
2. **Hilang (Jika lambda tidak membutuhkan parameter apa pun)**:
   ```kotlin
   val aksiKlik = { println("Tombol ditekan!") }
   ```
3. **Hilang (Jika menggunakan kata kunci `it`)**:
   Jika lambda hanya menerima tepat satu parameter input, deklarasi parameter dan tanda `->` dapat dihapus:
   ```kotlin
   val sapaSingkat: (String) -> Unit = { println("Halo $it") }
   ```

---

## 4. Contoh Variasi Jumlah Parameter

### A. Lambda dengan 1 Parameter
```kotlin
// Fungsi biasa:
fun sapa(nama: String) {
    println("Halo, $nama!")
}

// Fungsi lambda setara:
val sapaLambda = { nama: String -> println("Halo, $nama!") }
```

### B. Lambda dengan 2 Parameter
Jika memiliki dua parameter atau lebih, pisahkan dengan koma di sebelah kiri tanda panah `->`:
```kotlin
val tambah = { a: Int, b: Int ->
    val hasil = a + b
    hasil // Baris terakhir otomatis menjadi return value (Int)
}

// Cara pemanggilan:
val output = tambah(5, 3) // Output: 8
```

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Higher-Order Functions in Compose →](./02-higher-order-functions-in-compose.md)
