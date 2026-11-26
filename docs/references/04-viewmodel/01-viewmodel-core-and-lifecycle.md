---
id: ref-vm-01
title: ViewModel Architecture & Lifecycle di Android Jetpack
module: Modul 07 - ViewModel & Coroutine
category: architecture
tags:
  - viewmodel
  - lifecycle-aware
  - mvvm
  - single-source-of-truth
  - state-holder
summary: Peran ViewModel sebagai pemegang status (State Holder) yang sadar siklus hidup, bertahan saat rotasi layar, dan memisahkan logika UI dari logika bisnis.
related:
  - docs/references/04-viewmodel/02-coroutines-in-android.md
  - docs/references/04-viewmodel/03-lab-experiments-calculator-and-threading.md
  - docs/references/05-mvvm-best-practices/01-architecture-overview-and-udf.md
---

# 🏛️ ViewModel Architecture & Lifecycle di Android Jetpack

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Coroutines in Android →](./02-coroutines-in-android.md)

---

## 1. Definisi dan Peran ViewModel

`ViewModel` adalah komponen arsitektur Android Jetpack yang dirancang untuk **menyimpan dan mengelola data UI secara *lifecycle-aware*** (sadar akan siklus hidup komponen visual seperti Activity atau Fragment).

### Fungsi Utama ViewModel:
1. **Mempertahankan Status UI (*State Survival*)**:  
   Menyimpan data agar tidak musnah ketika Activity/Fragment dihancurkan dan dibuat ulang akibat rotasi layar (*orientation change*).
2. **Memisahkan Logika Bisnis (*Separation of Concerns*)**:  
   Bertindak sebagai perantara yang menyiapkan, memproses, dan memformat data dari Repository atau Model sebelum ditampilkan di View.
3. **Pilar Pola MVVM**:  
   Menjadi inti dari arsitektur *Model-View-ViewModel* yang direkomendasikan Google untuk aplikasi Android kelas enterprise.

```
+--------------------------------------------------------+
|                          VIEW                          |
|         (Activity / Fragment / Composable Screen)      |
|         - Menampilkan elemen grafis                    |
|         - Mengirim aksi pengguna (Event ↑)             |
+--------------------------------------------------------+
                         ▲          │
         State Teramati  │          │ Panggilan Fungsi
         (StateFlow ↓)   │          │ (Event ↑)
                         │          ▼
+--------------------------------------------------------+
|                       VIEWMODEL                        |
|         - Mengelola state bisnis                       |
|         - Menjalankan Coroutines di viewModelScope     |
+--------------------------------------------------------+
                         ▲          │
           Pembalikan    │          │ Panggilan
           Data Hasil    │          │ suspend fun
                         │          ▼
+--------------------------------------------------------+
|                   MODEL / REPOSITORY                   |
|         - Sumber Data (Room Database / REST API)       |
+--------------------------------------------------------+
```

---

## 2. Siklus Hidup ViewModel

Rentang hidup (*scope*) `ViewModel` dimulai saat Activity pertama kali dibuat dan **terus bertahan hidup di memori** meskipun Activity mengalami siklus `onDestroy()` dan `onCreate()` berulang kali akibat perubahan konfigurasi:

```
Activity onCreate() ─────> [ViewModel Dibuat]
Activity Berjalan                │
Activity Rotasi (onDestroy)      │ (ViewModel Tetap Hidup di Memori!)
Activity Dibuat Ulang (onCreate) │
Activity Ditutup Pengguna (Finish)
Activity onDestroy() ────> [ViewModel onCleared() -> Coroutines Batal]
```

> [!CAUTION]
> **Larangan Keras di ViewModel**:  
> Jangan pernah menyimpan referensi ke `Context`, `Activity`, `View`, atau fungsi `@Composable` di dalam `ViewModel`! Hal ini akan menyebabkan **Memory Leak parah** karena Activity yang telah dihancurkan tidak dapat dibersihkan oleh Garbage Collector.

---

## 3. Implementasi Dasar ViewModel dengan StateFlow

```kotlin
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {
    // 1. Private mutable state: Hanya dapat dimutasi dari dalam ViewModel
    private val _userName = MutableStateFlow("Nama Awal")

    // 2. Public read-only state: Diekspos ke UI sebagai StateFlow
    val userName: StateFlow<String> = _userName.asStateFlow()

    // 3. Fungsi bisnis yang dipanggil oleh UI saat event terjadi
    fun updateName(newName: String) {
        _userName.value = newName
    }
}
```

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Coroutines in Android →](./02-coroutines-in-android.md)
