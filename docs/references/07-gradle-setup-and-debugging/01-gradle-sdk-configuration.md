---
id: ref-modul01-01
title: Konfigurasi Versi SDK pada Gradle (compileSdk, minSdk, targetSdk)
module: Modul 01 - Setup Gradle, Debug & MainActivity
category: gradle-configuration
tags:
  - gradle
  - sdk-configuration
  - compilesdk
  - minsdk
  - targetsdk
  - android-build-system
summary: Panduan mendalam konfigurasi compileSdk, minSdk, targetSdk pada Gradle, hubungan ketiganya, dan pengaruhnya terhadap kompatibilitas perangkat Android.
related:
  - docs/references/07-gradle-setup-and-debugging/02-version-catalog-toml-guide.md
  - docs/references/07-gradle-setup-and-debugging/05-lab-experiments-and-task.md
---

# ⚙️ Konfigurasi Versi SDK pada Gradle (compileSdk, minSdk, targetSdk)

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Version Catalog (TOML) Guide →](./02-version-catalog-toml-guide.md)

---

## 1. Struktur Sistem Build Gradle di Android

Sistem build Android mengandalkan **Gradle**, sebuah automation tool yang bertanggung jawab mengelola dependensi (*library*), versi SDK, kompilasi kode Kotlin/Java, serta packaging file APK/AAB aplikasi.

Dalam file `app/build.gradle.kts`, blok konfigurasi utama sistem Android didefinisikan pada scope `android { ... }`.

---

## 2. Tiga Parameter Utama SDK

```
              ┌──────────────────────────────────────────────┐
              │                 compileSdk                   │
              │  (Batas Atas API saat Coding / Kompilasi)    │
              └──────────────────────┬───────────────────────┘
                                     │  Harus >=
              ┌──────────────────────▼───────────────────────┐
              │                 targetSdk                    │
              │  (Perilaku Optimal yang Diuji Pengembang)    │
              └──────────────────────┬───────────────────────┘
                                     │  Harus >=
              ┌──────────────────────▼───────────────────────┐
              │                  minSdk                      │
              │  (Batas Bawah: Versi Minimum HP Pengguna)    │
              └──────────────────────────────────────────────┘
```

### Penjelasan & Peran Masing-Masing:

1. **`compileSdk` (Versi Android untuk Kompilasi)**:
   - Menentukan API level Android yang digunakan compiler saat memverifikasi kode Anda.
   - Fitur Android terbaru hanya dapat digunakan jika `compileSdk` mendukung versi tersebut.
   - Tidak membatasi versi HP pengguna yang dapat menginstal aplikasi.

2. **`minSdk` (Versi Minimum yang Didukung)**:
   - Menentukan API level terendah dari perangkat Android fisik yang diizinkan untuk menginstal dan menjalankan aplikasi.
   - Google Play Store menggunakan nilai ini untuk menyaring pengguna. Jika HP pengguna memiliki API lebih rendah dari `minSdk`, aplikasi tidak akan muncul di Play Store.
   - Semakin tinggi `minSdk`, semakin sedikit perangkat yang didukung, namun semakin banyak fitur modern yang bisa langsung dipakai tanpa pengecekan kompatibilitas.

3. **`targetSdk` (Target Optimal Perilaku Sistem)**:
   - Menentukan API level acuan sistem operasi Android dalam menerapkan fitur keamanan, izin runtime (*runtime permissions*), dan manajemen memori.
   - Menjamin aplikasi tetap berjalan dengan perilaku yang diharapkan meskipun diinstal di versi OS Android yang lebih baru.

---

## 3. Aturan Emas Hubungan Versi SDK

Dalam konfigurasi `build.gradle.kts`, terdapat relasi matematis yang wajib dipatuhi:

$$\text{minSdk} \le \text{targetSdk} \le \text{compileSdk}$$

> [!CAUTION]
> **Apa yang terjadi jika `minSdk > compileSdk`?**  
> Proses kompilasi akan **gagal (Build Error)** karena compiler tidak memiliki referensi API yang memadai untuk mendukung perangkat target minimum.  
> Contoh: Jika `compileSdk = 34` tetapi `minSdk = 36`, Gradle akan menolak build.

---

## 4. Contoh Konfigurasi di `app/build.gradle.kts`

```kotlin
android {
    namespace = "com.example.studentlifeapp"
    compileSdk = 35 // Versi kompilasi modern

    defaultConfig {
        applicationId = "com.example.studentlifeapp"
        minSdk = 24     // Mendukung Android 7.0 (Nougat) ke atas (~95%+ perangkat aktif)
        targetSdk = 34  // Optimal pada Android 14

        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
```

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Version Catalog (TOML) Guide →](./02-version-catalog-toml-guide.md)
