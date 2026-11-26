---
id: ref-modul10-03
title: Fitur Sintaksis Kotlin Trailing Lambda dan Kata Kunci 'it'
module: Panduan Lengkap Fungsi Lambda & State Hoisting
category: kotlin-language
tags:
  - trailing-lambda
  - it-keyword
  - kotlin-syntax
  - clean-code
  - compose-dsl
summary: Panduan fitur sintaksis Trailing Lambda dan kata kunci it pada Kotlin untuk menghasilkan struktur pohon antarmuka Compose yang rapi dan mudah dibaca.
related:
  - docs/references/10-kotlin-lambdas-and-hoisting/02-higher-order-functions-in-compose.md
  - docs/references/10-kotlin-lambdas-and-hoisting/04-state-hoisting-with-lambdas.md
---

# ✨ Fitur Sintaksis Kotlin: Trailing Lambda dan Kata Kunci `it`

[← Sebelumnya: Higher-Order Functions in Compose](./02-higher-order-functions-in-compose.md) | [Berikutnya: State Hoisting dengan Lambda Callback →](./04-state-hoisting-with-lambdas.md)

---

## 1. Apa itu Trailing Lambda?

**Trailing Lambda** adalah konvensi sintaksis resmi dalam bahasa Kotlin:  
> *"Jika parameter terakhir dari suatu fungsi adalah sebuah fungsi lambda, maka tanda kurung kurawal `{ ... }` dari lambda tersebut dapat dituliskan **di luar tanda kurung lengkung biasa `()`**."*

Fitur inilah yang membuat deklarasi komponen Jetpack Compose terlihat seperti struktur pohon UI hierarkis yang indah dan bukan sekadar pemanggilan fungsi Java biasa.

### Perbandingan Sintaks:

```kotlin
// ❌ Tanpa Trailing Lambda (Kurang rapi, mirip fungsi Java konvensional)
Button(
    onClick = { aksiKlik() },
    content = { Text("Klik Saya") }
)

// ✅ Dengan Trailing Lambda (Bersih, mencerminkan hierarki visual)
Button(onClick = { aksiKlik() }) {
    Text("Klik Saya")
}
```

Jika lambda adalah **satu-satunya parameter** dari fungsi tersebut, tanda kurung biasa `()` bahkan boleh dihilangkan seluruhnya:
```kotlin
// Alih-alih: Column() { ... }
Column {
    Text("Item 1")
    Text("Item 2")
}
```

---

## 2. Kata Kunci `it` pada Lambda

Kata kunci **`it`** adalah nama panggilan otomatis (*implicit parameter*) yang disediakan oleh Kotlin untuk mewakili parameter di dalam fungsi lambda, **dengan syarat mutlak: lambda tersebut hanya menerima tepat satu parameter input**.

### Perbandingan Penulisan:

```kotlin
// 1. Ditulis Manual (Menyertakan deklarasi nama dan tanda ->)
TextField(
    value = userName,
    onValueChange = { teksBaru -> userName = teksBaru }
)

// 2. Menggunakan Kata Kunci 'it' (Jauh lebih ringkas dan idiomatis)
TextField(
    value = userName,
    onValueChange = { userName = it }
)
```

### ⚠️ Batasan Penggunaan `it`
> [!CAUTION]
> Kata kunci `it` **TIDAK BISA digunakan** jika fungsi lambda menerima 2 parameter atau lebih.  
> Pada kasus 2 parameter (seperti form login), Anda **wajib** mendefinisikan nama parameter dan menggunakan tanda panah `->` secara manual:  
> `{ username, password -> ... }`

---
[← Sebelumnya: Higher-Order Functions in Compose](./02-higher-order-functions-in-compose.md) | [Berikutnya: State Hoisting dengan Lambda Callback →](./04-state-hoisting-with-lambdas.md)
