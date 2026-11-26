---
id: ref-modul01-02
title: Pengelolaan Dependensi dengan Gradle Version Catalog (libs.versions.toml)
module: Modul 01 - Setup Gradle, Debug & MainActivity
category: gradle-configuration
tags:
  - version-catalog
  - toml
  - dependency-management
  - gradle
  - libs-versions-toml
summary: Panduan lengkap penggunaan Gradle Version Catalog (libs.versions.toml) untuk standarisasi dependensi multi-modul di Android modern.
related:
  - docs/references/07-gradle-setup-and-debugging/01-gradle-sdk-configuration.md
  - docs/references/07-gradle-setup-and-debugging/05-lab-experiments-and-task.md
---

# 📦 Pengelolaan Dependensi dengan Gradle Version Catalog (libs.versions.toml)

[← Sebelumnya: Konfigurasi Versi SDK pada Gradle](./01-gradle-sdk-configuration.md) | [Berikutnya: Debugging, Logcat, & Breakpoints →](./03-debugging-logcat-and-breakpoints.md)

---

## 1. Apa itu Gradle Version Catalog?

**Gradle Version Catalog** adalah fitur standar Android modern yang memusatkan seluruh deklarasi pustaka (*dependencies*) dan plugin ke dalam satu file berkas konfigurasi bernama `gradle/libs.versions.toml`.

### Mengapa Menggantikan Cara Manual Tradisional?
Pada cara manual lama, string library ditulis langsung berulang-ulang di berbagai file `build.gradle`:
```kotlin
// CARA LAMA (Manual Hardcoded String):
implementation("com.squareup.retrofit2:retrofit:3.0.0")
implementation("com.squareup.retrofit2:converter-gson:3.0.0")
```
Cara lama ini rentan terhadap inkonsistensi versi antar modul, menyulitkan proses pembaruan (*upgrade*), dan tidak memiliki bantuan *autocomplete* yang aman dari typo.

---

## 2. Struktur Anatomi File `gradle/libs.versions.toml`

File Version Catalog dibagi menjadi beberapa blok tabel utama:

```toml
[versions]
# 1. Tempat mendefinisikan angka versi pustaka
retrofit = "3.0.0"
coil = "2.7.0"
lifecycle = "2.8.4"

[libraries]
# 2. Tempat mendefinisikan grup, nama library, dan referensi versinya
retrofit-core = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

[plugins]
# 3. Tempat mendefinisikan Gradle Plugin
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version = "2.0.0" }
```

### Rincian Elemen:
- **`[versions]`**: Menyimpan variabel versi (String).
- **`[libraries]`**: Mendefinisikan pustaka eksternal dengan kunci:
  - `group`: domain pengembang/organisasi (misal `com.squareup.retrofit2`).
  - `name`: nama modul library (misal `retrofit`).
  - `version.ref`: merujuk ke variabel versi yang ada pada blok `[versions]`.

---

## 3. Aturan Konversi Penamaan TOML ke Kotlin DSL

Ketika mendeklarasikan library di `libs.versions.toml` dan memanggilnya di file `app/build.gradle.kts`:

> [!IMPORTANT]
> **Aturan Translasi Tanda Strip (`-`) ke Titik (`.`)**:  
> Seluruh tanda minus/strip (`-`) pada nama alias di TOML akan **dikonversi secara otomatis menjadi tanda titik (`.`)** dalam penulisan type-safe accessor di Kotlin DSL.

### Contoh Translasi:
| Penulisan di `libs.versions.toml` | Pemanggilan di `app/build.gradle.kts` |
| :--- | :--- |
| `retrofit-core` | `libs.retrofit.core` |
| `retrofit-gson` | `libs.retrofit.gson` |
| `androidx-core-ktx` | `libs.androidx.core.ktx` |
| `coil-compose` | `libs.coil.compose` |

### Implementasi di `app/build.gradle.kts`:
```kotlin
dependencies {
    // Memanggil library via Version Catalog (Aman & Terpusat)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.coil.compose)
}
```

---

## 4. Keuntungan Menggunakan Version Catalog

1. **Konsistensi Versi**: Jika sebuah pustaka dipakai di 5 modul berbeda, Anda cukup mengganti versinya 1 kali di `libs.versions.toml`.
2. **Autocomplete & Refactoring Aman**: Android Studio memberikan saran ketikan otomatis (*code completion*) saat Anda mengetik `libs.`.
3. **Pemberitahuan Update Terpusat**: IDE dapat langsung menandai jika terdapat versi rilis baru untuk pustaka tertentu langsung di file TOML.

---
[← Sebelumnya: Konfigurasi Versi SDK pada Gradle](./01-gradle-sdk-configuration.md) | [Berikutnya: Debugging, Logcat, & Breakpoints →](./03-debugging-logcat-and-breakpoints.md)
