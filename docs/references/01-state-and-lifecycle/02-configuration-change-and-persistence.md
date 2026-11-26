---
id: ref-state-02
title: Perubahan Konfigurasi dan Retensi State di Jetpack Compose
module: Modul 04 - State & Lifecycle
category: state-management
tags:
  - remember
  - rememberSaveable
  - configuration-change
  - bundle
  - saver
  - activity-recreation
summary: Analisis perbedaan antara remember dan rememberSaveable dalam mempertahankan state saat recomposition vs Activity destruction/recreation akibat rotasi layar.
related:
  - docs/references/01-state-and-lifecycle/01-state-hoisting-fundamentals.md
  - docs/references/01-state-and-lifecycle/03-side-effects-launchedeffect.md
  - docs/references/04-viewmodel/01-viewmodel-core-and-lifecycle.md
---

# 🔄 Perubahan Konfigurasi & Retensi State di Jetpack Compose

[← Sebelumnya: State Hoisting Fundamentals](./01-state-hoisting-fundamentals.md) | [Berikutnya: Side Effects & LaunchedEffect →](./03-side-effects-launchedeffect.md)

---

## 1. Masalah: Recomposition vs Configuration Change

Dalam siklus hidup UI Jetpack Compose:
1. **Rekomposisi (*Recomposition*)**: Terjadi ketika nilai state yang dibaca oleh Composable berubah. Composable fungsi akan dipanggil ulang untuk memperbarui elemen visual.
2. **Perubahan Konfigurasi (*Configuration Change*)**: Terjadi saat pengguna memutar layar (rotasi portrait ↔ landscape), mengubah bahasa sistem, atau beralih ke mode multi-window. Pada saat ini, OS Android **menghancurkan (*destroy*) Activity** dan membuatnya kembali dari awal (*recreation*).

---

## 2. Perbandingan `remember` vs `rememberSaveable`

| Karakteristik | `remember { ... }` | `rememberSaveable { ... }` |
| :--- | :--- | :--- |
| **Bertahan saat Rekomposisi** | ✅ Ya | ✅ Ya |
| **Bertahan saat Rotasi Layar** | ❌ Tidak (kembali ke nilai awal) | ✅ Ya (disimpan ke instance state) |
| **Bertahan saat Process Death (Killed by OS)** | ❌ Tidak | ✅ Ya (jika data muat di Bundle) |
| **Bertahan jika Aplikasi Ditutup Manual oleh User** | ❌ Tidak | ❌ Tidak |
| **Mekanisme Penyimpanan** | Disimpan di Composition Tree lokal | Disimpan di objek `Bundle` Android |
| **Tipe Data yang Didukung** | Semua objek Kotlin | Tipe primitif Bundle atau class dengan `Saver` / `@Parcelize` |

---

## 3. Implementasi Kode

### A. Menggunakan `rememberSaveable` untuk Input Teks

```kotlin
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun NameInput() {
    // State ini tetap bertahan meskipun layar diputar!
    var name by rememberSaveable { mutableStateOf("") }

    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Enter your name") }
    )
}
```

---

## 4. Kasus Khusus & Penanganan Objek Kompleks

Secara default, `rememberSaveable` hanya dapat menyimpan tipe data yang didukung oleh `android.os.Bundle` (seperti `Int`, `String`, `Boolean`, `Float`, `ArrayList`, dll).

Jika Anda ingin menyimpan objek custom / kompleks:
1. **Parcelize**: Beri anotasi `@Parcelize` dan implementasikan interface `Parcelable`.
2. **Custom Saver**: Gunakan `Saver` kustom untuk mendefinisikan bagaimana objek di-serialize menjadi tipe yang kompatibel dengan Bundle dan di-restore kembali.

```kotlin
import androidx.compose.runtime.saveable.mapSaver

// Contoh custom mapSaver untuk objek non-primitif
val CitySaver = run {
    val nameKey = "Name"
    val countryKey = "Country"
    mapSaver(
        save = { mapOf(nameKey to it.name, countryKey to it.country) },
        restore = { City(it[nameKey] as String, it[countryKey] as String) }
    )
}
```

---

## 5. Kapan Menggunakan `rememberSaveable` vs `ViewModel`?

- **Gunakan `rememberSaveable` di Composable untuk**:
  - UI state murni yang tidak berhubungan dengan logika bisnis server (contoh: status ekspansi dropdown, posisi scroll sementara, teks formulir sederhana yang belum disubmit).
- **Gunakan `ViewModel` untuk**:
  - State bisnis yang diakses dari jaringan atau database.
  - State yang membutuhkan proses transformasi asinkron (misal: Coroutines, Flow).
  - Untuk perbandingan mendalam, pelajari [Panduan Penyimpanan State (StateFlow vs mutableStateOf)](../05-mvvm-best-practices/02-state-management-stateflow-vs-mutablestate.md).

---
[← Sebelumnya: State Hoisting Fundamentals](./01-state-hoisting-fundamentals.md) | [Berikutnya: Side Effects & LaunchedEffect →](./03-side-effects-launchedeffect.md)
