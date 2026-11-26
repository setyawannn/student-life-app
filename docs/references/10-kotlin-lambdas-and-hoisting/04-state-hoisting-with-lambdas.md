---
id: ref-modul10-04
title: Implementasi State Hoisting dengan Lambda Callbacks
module: Panduan Lengkap Fungsi Lambda & State Hoisting
category: state-management
tags:
  - state-hoisting
  - stateless-ui
  - lambda-callbacks
  - uicontrol
  - single-source-of-truth
summary: Penerapan praktis lambda callbacks untuk mengangkat state (State Hoisting) dari composable anak ke induk guna menciptakan komponen stateless.
related:
  - docs/references/10-kotlin-lambdas-and-hoisting/02-higher-order-functions-in-compose.md
  - docs/references/10-kotlin-lambdas-and-hoisting/03-trailing-lambda-and-it-keyword.md
  - docs/references/01-state-and-lifecycle/01-state-hoisting-fundamentals.md
---

# 🏗️ Implementasi State Hoisting dengan Lambda Callbacks

[← Sebelumnya: Trailing Lambda & Keyword 'it'](./03-trailing-lambda-and-it-keyword.md) | [Berikutnya: Modul Pager & YouTube Streaming →](../11-pager-and-youtube-streaming/01-type-safe-navigation-and-backstack-leak.md)

---

## 1. Hubungan Antara Lambda dan State Hoisting

Setelah menguasai konsep **Fungsi Lambda**, **Higher-Order Functions**, dan **Trailing Lambda**, seluruh konsep tersebut bermuara pada satu pola arsitektur utama di Jetpack Compose: **State Hoisting**.

State Hoisting adalah proses memindahkan state dari dalam composable anak (*child*) ke composable pemanggilnya (*parent*). Untuk menghubungkan keduanya, kita memanfaatkan:
1. **Parameter Biasa**: Mengalirkan data turun dari parent ke child (*State Down*).
2. **Parameter Bertipe Fungsi Lambda**: Mengirimkan event ke atas dari child ke parent (*Event Up*).

---

## 2. Studi Kasus Komparasi: Sebelum vs Sesudah Hoisting

### A. Tanpa Hoisting (Komponen Stateful)
Komponen menyimpan dan mengelola datanya sendiri secara internal. Induk tidak memiliki akses untuk membaca teks yang diketik pengguna:

```kotlin
@Composable
fun NameInputStateful() {
    // State terperangkap di dalam komponen ini
    var name by remember { mutableStateOf("") }

    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Masukkan Nama") }
    )
}
```

---

### B. Dengan State Hoisting (Komponen Stateless)
Kita "mengangkat" (*hoist*) variabel state ke luar. Composable ini sekarang murni hanya menerima data dan menembakkan event perubahan melalui lambda:

```kotlin
@Composable
fun NameInputStateless(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Masukkan Nama") },
        modifier = modifier
    )
}
```

---

### C. Pemanggilan oleh Komponen Induk (`RegistrationScreen`)
Komponen induk memegang kendali penuh atas data dan dapat menampilkan nilai tersebut di tempat lain (misal di widget `Text("Halo, $userName!")`):

```kotlin
@Composable
fun RegistrationScreen() {
    // State diangkat dan dikelola terpusat di sini
    var userName by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        NameInputStateless(
            name = userName,
            onNameChange = { newName -> userName = newName }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Halo, $userName!", style = MaterialTheme.typography.headlineSmall)
    }
}
```

---

## 3. Kesimpulan Arsitektur
Dengan memisahkan state dari komponen input, `NameInputStateless` sekarang dapat digunakan kembali di berbagai form aplikasi (form pendaftaran, form profil, form checkout) tanpa ketergantungan pada logika bisnis tertentu.

---
[← Sebelumnya: Trailing Lambda & Keyword 'it'](./03-trailing-lambda-and-it-keyword.md) | [Berikutnya: Modul Pager & YouTube Streaming →](../11-pager-and-youtube-streaming/01-type-safe-navigation-and-backstack-leak.md)
