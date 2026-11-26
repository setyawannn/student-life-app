---
id: ref-nav-02
title: Type-Safe Navigation di Jetpack Compose (Modern Way)
module: Modul 06 - Navigation Compose
category: navigation
tags:
  - type-safe-navigation
  - kotlin-serialization
  - route-arguments
  - compile-time-safety
summary: Panduan transisi dari String-based navigation ke Type-Safe navigation menggunakan Kotlin Serialization di Navigation Compose 2.8.0+.
related:
  - docs/references/03-navigation/01-navigation-pillars-and-architecture.md
  - docs/references/03-navigation/03-savedstatehandle-and-result-passing.md
  - docs/references/03-navigation/04-lab-experiments-travel-and-profile.md
---

# 🛡️ Type-Safe Navigation di Jetpack Compose (Modern Way)

[← Sebelumnya: Tiga Pilar Navigasi](./01-navigation-pillars-and-architecture.md) | [Berikutnya: SavedStateHandle & Result Passing →](./03-savedstatehandle-and-result-passing.md)

---

## 1. Evolusi: String-Based (Lama) vs Type-Safe (Baru)

Sebelum pustaka **Navigation Compose versi 2.8.0**, perutean (*routing*) antar layar menggunakan pendekatan **berbasis String**, mirip URL website:
```kotlin
// PENDEKATAN LAMA (String-based)
navController.navigate("detail_screen/5/mobile")
```

### Masalah Pendekatan String-Based:
- **Rawan Typo**: Jika Anda salah menulis `"detail_screen"` menjadi `"details_screen"`, compiler tidak akan memberi tahu. Aplikasi langsung mengalami **crash saat runtime**.
- **Parsing Data Manual**: Nilai integer atau boolean harus diparsing manual dari String di tujuan rute.
- **Data Kompleks Sulit**: Sangat repot mengirim lebih dari 2 argumen.

### Solusi: Type-Safe Navigation (Navigation 2.8.0+)
Menggunakan **Class / Objek Asli Kotlin** yang dianotasi `@Serializable`.

```kotlin
// PENDEKATAN BARU (Type-Safe)
navController.navigate(Details(locationId = 5, locationName = "Tokyo"))
```

---

## 2. Tabel Perbandingan Lengkap

| Fitur | String-Based (Lama) | Type-Safe (Baru) |
| :--- | :--- | :--- |
| **Keamanan Kode** | Rendah (rawan salah ketik / typo) | **Tinggi (Dicek langsung oleh Kotlin Compiler)** |
| **Pengecekan Error** | Saat aplikasi dijalankan (*runtime crash*) | **Saat kode diketik / dicompile (*compile-time*)** |
| **Parsing Tipe Data** | Manual (String to Int, Boolean, dll) | **Otomatis via `toRoute<T>()`** |
| **Refactoring Nama Variabel** | Berisiko tinggi (string tidak ikut berubah) | **Sangat aman (fitur Rename Android Studio otomatis mengupdate semua)** |
| **Kemudahan Pemeliharaan** | Sulit jika argumen bertambah banyak | **Sangat mudah & rapi** |

---

## 3. Konfigurasi Gradle untuk Type-Safe Navigation

### A. Module `build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Wajib untuk Type-Safe Navigation (Kotlin 2.0+)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

dependencies {
    // Navigation Compose 2.8.0+
    val nav_version = "2.8.0"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Kotlinx Serialization JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
}
```

---

## 4. Cara Mendefinisikan Rute & Ekstraksi Data

### A. Definisi di `Destinations.kt`
```kotlin
import kotlinx.serialization.Serializable

// Rute tanpa argumen -> gunakan object
@Serializable
object Home

// Rute dengan argumen -> gunakan data class
@Serializable
data class Details(
    val locationId: Int,
    val locationName: String,
    val isPremium: Boolean = false // Mendukung default value!
)
```

### B. Konfigurasi `NavHost` dan Ekstraksi Otomatis
```kotlin
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Home) {
        
        // 1. Layar Home
        composable<Home> {
            HomeScreen(onNavigateToDetails = { id, name ->
                navController.navigate(
                    Details(locationId = id, locationName = name)
                )
            })
        }

        // 2. Layar Details dengan Ekstraksi Type-Safe
        composable<Details> { backStackEntry ->
            // Ekstraksi seluruh objek Details secara otomatis tanpa boilerplate!
            val args = backStackEntry.toRoute<Details>()
            
            DetailScreen(
                locationId = args.locationId,
                locationName = args.locationName,
                isPremium = args.isPremium,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
```

---

## 5. Bagaimana Alur Kerjanya di Balik Layar?

```
[UI Trigger] 
navController.navigate(Details(id=5, name="Tokyo"))
         │
         ▼
[Kotlinx Serialization] 
Mengonversi objek Details menjadi rute String terstruktur di latar belakang
         │
         ▼
[NavHost Engine] 
Mencari deklarasi composable<Details> yang cocok di grafik navigasi
         │
         ▼
[BackStackEntry.toRoute<Details>()] 
Membaca argumen Bundle dan merekonstruksi ulang objek Details asli
         │
         ▼
[Destination Screen] 
Menerima properti strongly-typed (Int, String, dsb) secara aman
```

---
[← Sebelumnya: Tiga Pilar Navigasi](./01-navigation-pillars-and-architecture.md) | [Berikutnya: SavedStateHandle & Result Passing →](./03-savedstatehandle-and-result-passing.md)
