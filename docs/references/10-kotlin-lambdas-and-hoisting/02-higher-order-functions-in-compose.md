---
id: ref-modul10-02
title: Parameter Bertipe Fungsi (Higher-Order Functions) di Compose
module: Panduan Lengkap Fungsi Lambda & State Hoisting
category: kotlin-language
tags:
  - higher-order-functions
  - function-types
  - callback-lambdas
  - function-reference
  - jetpack-compose
summary: Konsep fungsi tingkat tinggi (Higher-Order Functions) di Kotlin, sintaks tipe fungsi, dan pengiriman event callback pada arsitektur Jetpack Compose.
related:
  - docs/references/10-kotlin-lambdas-and-hoisting/01-lambda-expressions-and-syntax.md
  - docs/references/10-kotlin-lambdas-and-hoisting/03-trailing-lambda-and-it-keyword.md
  - docs/references/10-kotlin-lambdas-and-hoisting/04-state-hoisting-with-lambdas.md
---

# 🧩 Parameter Bertipe Fungsi (Higher-Order Functions) di Compose

[← Sebelumnya: Sintaks Dasar Fungsi Lambda](./01-lambda-expressions-and-syntax.md) | [Berikutnya: Trailing Lambda & Keyword 'it' →](./03-trailing-lambda-and-it-keyword.md)

---

## 1. Konsep Higher-Order Functions

Di Kotlin, fungsi dianggap sebagai warga kelas satu (*first-class citizens*). Sebuah fungsi dapat menerima fungsi lain sebagai parameternya atau mengembalikan fungsi sebagai hasilnya. Konsep ini dinamakan **Higher-Order Function**.

Inilah fondasi utama yang memungkinkan pembuatan UI deklaratif di Jetpack Compose (seperti parameter `onClick` pada `Button` atau `onValueChange` pada `TextField`).

### Format Penulisan Tipe Fungsi:
```kotlin
namaParameter: (TipeInput1, TipeInput2) -> TipeOutput
```

---

## 2. Apakah Parameter Ini Wajib Diisi dengan Lambda?

Jawabannya: **TIDAK**.  
Meskipun lambda adalah opsi paling populer, Anda dapat mengirimkan **fungsi biasa** ke dalam parameter fungsi tersebut menggunakan **referensi fungsi (`::`)**, asalkan tipe parameter input dan tipe kembaliannya cocok persis:

```kotlin
@Composable
fun TombolKustom(teks: String, aksiKlik: () -> Unit) {
    Button(onClick = aksiKlik) {
        Text(teks)
    }
}

// Fungsi biasa di luar Composable
fun simpanDataKeDatabase() {
    println("Data berhasil disimpan!")
}

@Composable
fun ContohPemanggilan() {
    // 1. Menggunakan Lambda langsung:
    TombolKustom(teks = "Simpan", aksiKlik = { println("Tersimpan via Lambda!") })

    // 2. Menggunakan Referensi Fungsi Biasa (tanda ::):
    TombolKustom(teks = "Simpan", aksiKlik = ::simpanDataKeDatabase)
}
```

---

## 3. Tiga Pola Parameter Fungsi yang Paling Sering Digunakan

### Pola 1: Tanpa Input `() -> Unit` (Aksi Sederhana)
Fungsi meminta sebuah aksi tanpa butuh data masukan dan tidak mengembalikan nilai apa pun (`Unit` setara `void` di Java).
```kotlin
@Composable
fun TombolSederhana(aksiKlik: () -> Unit) {
    Button(onClick = aksiKlik) {
        Text("Klik Saya")
    }
}
```

### Pola 2: Satu Input `(String) -> Unit` (Melaporkan Data Balik)
Digunakan oleh komponen anak (*child*) untuk melaporkan perubahan data input pengguna kembali ke komponen induk (*parent*).
```kotlin
@Composable
fun InputPesan(onPesanKetik: (String) -> Unit) {
    TextField(
        value = "",
        onValueChange = { teksBaru -> onPesanKetik(teksBaru) }
    )
}
```

### Pola 3: Dua Input atau Lebih `(String, String) -> Unit` (Kombinasi Data)
Umum dipakai pada form formulir yang mengirimkan pasangan data secara bersamaan (seperti form Login: `username` dan `password`):

```kotlin
@Composable
fun LoginForm(onLoginSubmit: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column {
        TextField(value = username, onValueChange = { username = it })
        TextField(value = password, onValueChange = { password = it })
        Button(onClick = {
            // Mengirim 2 data sekaligus ke callback induk
            onLoginSubmit(username, password)
        }) {
            Text("Login")
        }
    }
}

// Pemanggilan di Komponen Induk:
@Composable
fun LoginScreen() {
    LoginForm(
        onLoginSubmit = { user, pass ->
            println("Login dengan User: $user, Pass: $pass")
        }
    )
}
```

---
[← Sebelumnya: Sintaks Dasar Fungsi Lambda](./01-lambda-expressions-and-syntax.md) | [Berikutnya: Trailing Lambda & Keyword 'it' →](./03-trailing-lambda-and-it-keyword.md)
