---
id: ref-modul11-01
title: Navigasi Dasar Type-Safe, Navigation Hoisting, & Back Stack Leak
module: Modul Pager YouTube Stream & Navigasi Terapan
category: navigation
tags:
  - type-safe-navigation
  - navigation-hoisting
  - back-stack-management
  - memory-leak-test
  - popbackstack-vs-navigate
summary: Konsep arsitektur navigasi Compose 2.8+, bahaya penumpukan Back Stack saat navigasi kembali, dan implementasi rute Type-Safe dasar.
related:
  - docs/references/11-pager-and-youtube-streaming/02-forward-data-passing-checkbox-lazycolumn.md
  - docs/references/11-pager-and-youtube-streaming/03-return-back-data-savedstatehandle-serializable.md
  - docs/references/03-navigation/01-navigation-pillars-and-architecture.md
---

# 🧭 Navigasi Dasar Type-Safe, Navigation Hoisting, & Back Stack Leak

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Forward Data Passing & Checkbox →](./02-forward-data-passing-checkbox-lazycolumn.md)

---

## 1. Arsitektur Tiga Pilar Navigasi & Pengelolaan Back Stack

Sistem navigasi Jetpack Compose bertugas mengelola perpindahan antartampilan (*Screen*) serta memelihara riwayat perjalanan pengguna (*User Journey*):
1. **`NavController`**: Objek pusat pengendali ("otak" navigasi) yang melacak rute aktif dan tumpukan memori (*Back Stack*). Dibuat di level teratas UI menggunakan `rememberNavController()`.
2. **`NavHost`**: Kontainer visual yang menampilkan layar aktif berdasarkan instruksi `NavController`.
3. **`Destination`**: Tampilan composable individual yang diwakili oleh data object atau data class Kotlin beranotasi `@Serializable`.

---

## 2. Bahaya Penumpukan Back Stack (*Back Stack Accumulation / Memory Leak*)

Android mengelola riwayat navigasi dengan struktur data **LIFO (*Last In, First Out*)**:

```
[Maju Normal]:
Home ──> navigate(Detail) ──> Back Stack: [Home, Detail]

[Kembali yang BENAR via popBackStack()]:
Detail ──> popBackStack() ──> Back Stack: [Home]  (Detail dihancurkan dari memori!)

[Kembali yang SALAH via navigate(Home)]:
Detail ──> navigate(Home) ──> Back Stack: [Home, Detail, Home]  (Layar menumpuk terus!)
```

> [!CAUTION]
> **Praktik Buruk**: Menggunakan `navController.navigate(Home)` untuk tombol "Kembali" adalah kesalahan fatal. Hal ini tidak menghapus layar detail, melainkan membuat instance baru di atas tumpukan. Jika diulang 5 kali, akan ada 10 layar di memori, menyebabkan pemborosan RAM (*memory leak*) dan pengguna harus menekan tombol Back fisik HP sebanyak 10 kali untuk keluar dari aplikasi!

---

## 3. Prinsip Clean Architecture: Navigation Hoisting

Navigation Hoisting adalah penerapan Unidirectional Data Flow (UDF) pada alur navigasi:
- **DILARANG KERAS** melewatkan referensi `navController: NavHostController` langsung ke dalam parameter fungsi Composable layar UI (seperti `P1HomeScreen`).
- **SEHARUSNYA**: Layar UI dibuat berstatus **stateless** dan hanya menerima lambda callback aksi navigasi sederhana (contoh: `onNavigateToDetail: () -> Unit`).

---

## 4. Implementasi Percobaan 1: Navigasi Dasar

### A. Rute di `Destinations.kt`
```kotlin
package com.example.appnavigasi.navigation

import kotlinx.serialization.Serializable

@Serializable
object P1HomeRoute

@Serializable
object P1DetailRoute
```

### B. Composable Stateless Screen
```kotlin
@Composable
fun P1HomeScreen(onNavigateToDetail: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("P1: Halaman Utama", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToDetail) {
                Text("Ke Detail Screen (P1) >")
            }
        }
    }
}

@Composable
fun P1DetailScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("P1: Halaman Detail", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("< Kembali")
            }
        }
    }
}
```

### C. Entry Point NavHost (`MainActivity.kt`)
```kotlin
@Composable
fun AppNavigationP1() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = P1HomeRoute
    ) {
        composable<P1HomeRoute> {
            P1HomeScreen(onNavigateToDetail = {
                navController.navigate(P1DetailRoute)
            })
        }
        composable<P1DetailRoute> {
            P1DetailScreen(onBack = {
                // Gunakan popBackStack() untuk membersihkan memori
                navController.popBackStack()
            })
        }
    }
}
```

---

## 5. Pembahasan Pertanyaan Pemahaman 1.1 & 1.2

### ❓ Pertanyaan 1.1: Hasil Eksperimen Penumpukan Back Stack
- **Berapa kali harus menekan tombol Back fisik HP sebelum aplikasi tertutup?**  
  Jika proses bolak-balik diulang 5 kali dengan `navigate(P1HomeRoute)`, maka ada 10 layer instance rute di stack. Pengguna harus menekan tombol Back sistem sebanyak **10 kali** sebelum aplikasi tertutup!
- **Perbedaan Mekanisme**:  
  `popBackStack()` menghapus dan mendestruksi layar paling atas dari memori RAM. Sebaliknya, `navigate()` membuat instance rute baru dan menumpuknya di atas stack.

### ❓ Pertanyaan 1.2: Mengapa memasukkan `navController` ke Composable layar dilarang dalam Clean Code?
1. **Merusak Reusability**: Composable terikat kaku (*tightly coupled*) pada pustaka Navigation Compose dan tidak bisa dipanggil di preview Android Studio (`@Preview`).
2. **Sulit Diuji (*Untestable*)**: Composable tidak bisa diuji dengan unit test tanpa melakukan mock rumit terhadap seluruh instance `NavHostController`.
3. **Melanggar UDF**: Komponen UI seharusnya hanya menerima state visual dan memancarkan event murni via lambda.

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Forward Data Passing & Checkbox →](./02-forward-data-passing-checkbox-lazycolumn.md)
