---
id: ref-nav-03
title: Komunikasi Dua Arah & SavedStateHandle di Navigation Compose
module: Modul 06 - Navigation Compose
category: navigation
tags:
  - savedstatehandle
  - return-back-data
  - loose-coupling
  - parcelable
  - unidirectional-data-flow
summary: Teknik pengembalian data dari layar tujuan ke layar pemanggil (return-back result) secara reaktif menggunakan SavedStateHandle tanpa shared ViewModel.
related:
  - docs/references/03-navigation/02-type-safe-navigation-guide.md
  - docs/references/03-navigation/04-lab-experiments-travel-and-profile.md
  - docs/references/04-viewmodel/01-viewmodel-core-and-lifecycle.md
---

# 🔄 Komunikasi Dua Arah & SavedStateHandle di Navigation Compose

[← Sebelumnya: Type-Safe Navigation Guide](./02-type-safe-navigation-guide.md) | [Berikutnya: Lab Praktikum & Analisis Modul 6 →](./04-lab-experiments-travel-and-profile.md)

---

## 1. Masalah: Aliran Maju vs Aliran Mundur

Dalam arsitektur navigasi mobile:
- **Aliran Maju (*Forward Navigation*)**: Sangat mudah dilakukan dengan menyisipkan argumen ke rute tujuan (misal: mengirim ID produk dari katalog ke layar detail produk).
- **Aliran Mundur (*Return-Back Data*)**: Kebutuhan umum saat pengguna membuka layar pemilih (*picker*), form editor, atau filter, lalu data hasil pilihan tersebut harus **dikembalikan ke layar sebelumnya** (misal: pilih kontak, pilih foto, atau edit nama pengguna).

Menggunakan shared ViewModel global atau static variable melanggar prinsip modularitas dan membuat komponen saling terikat erat (*tight coupling*).

---

## 2. Solusi: Mekanisme `SavedStateHandle`

`SavedStateHandle` adalah media penyimpanan berbasis pasangan kunci-nilai (*key-value map*) yang menempel langsung pada setiap entri di **Back Stack**. Data di dalamnya aman dari perubahan konfigurasi (*rotation*) maupun *process death*.

```
[Layar B: Pengirim / Editor]
        │
        │ 1. Tulis data ke previousBackStackEntry.savedStateHandle
        │ 2. Panggil popBackStack() untuk menutup diri
        ▼
[SavedStateHandle Kotak Pos Layar A]
        │
        │ 3. Memancarkan perubahan data baru (StateFlow / LiveData)
        ▼
[Layar A: Penerima / Profil] (Mengamati perubahan secara reaktif)
```

### Analogi Papan Tulis Ruang Tamu:
- **Layar A** adalah Anda yang sedang menunggu di ruang tamu.
- **Layar B** adalah teman Anda yang pergi belanja ke pasar.
- **SavedStateHandle** adalah papan tulis di ruang tamu.  
Layar B tidak perlu mencari Anda secara langsung. Layar B cukup menuliskan *"Total Belanja: Rp 50.000"* di papan tulis, lalu pergi (*popBackStack*). Saat Anda kembali melihat papan tulis, Anda langsung membaca nilainya dan memperbarui catatan Anda.

---

## 3. Tiga Keunggulan Teoritis Arsitektur Ini

1. **Loose Coupling (Keterikatan Sangat Rendah)**:  
   Layar B (Editor) tidak perlu tahu siapa yang memanggilnya. Layar B dapat dipakai ulang oleh layar mana pun di seluruh aplikasi.
2. **Unidirectional Data Flow (UDF)**:  
   Menjaga prinsip dasar Compose: data mengalir ke bawah melalui argumen, dan event hasil navigasi kembali mengalir ke atas.
3. **Lifecycle-Aware**:  
   Data tidak akan musnah jika ponsel dirotasi tepat di saat proses pengiriman data berlangsung.

---

## 4. Implementasi Kode Pengiriman Data

### A. Di Layar Penerima (Layar A / Profile)
Mengamati data dari `SavedStateHandle` miliknya sendiri secara reaktif:

```kotlin
composable<Profile> { backStackEntry ->
    // Mengamati data menggunakan StateFlow (atau LiveData)
    val updatedName = backStackEntry.savedStateHandle
        .getStateFlow<String>("new_name", "Guest")
        .collectAsState()

    ProfileScreen(
        currentName = updatedName.value,
        onEditClick = { navController.navigate(EditUsername) }
    )
}
```

### B. Di Layar Pengirim (Layar B / EditUsername)
Menaruh data ke `previousBackStackEntry` dan menutup layar:

```kotlin
composable<EditUsername> {
    EditUsernameScreen(onSave = { newName ->
        // 1. Taruh data ke SavedStateHandle milik layar sebelumnya
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("new_name", newName)

        // 2. Tutup layar saat ini
        navController.popBackStack()
    })
}
```

---

## 5. Mengembalikan Data Kelas Kustom (`Parcelable`)

Jika data yang dikembalikan lebih dari sekadar tipe primitif (misal: data kelas `UserProfile` yang memiliki banyak atribut), gunakan anotasi `@Parcelize`:

```kotlin
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val name: String,
    val bio: String,
    val themeColor: Int
) : Parcelable
```

### Mengirim Objek Parcelable:
```kotlin
navController.previousBackStackEntry
    ?.savedStateHandle
    ?.set("user_profile_data", UserProfile(name, bio, 0xFF6200EE.toInt()))

navController.popBackStack()
```

### Mengamati Objek Parcelable di NavHost:
```kotlin
composable<Profile> { backStackEntry ->
    val profileState = backStackEntry.savedStateHandle
        .getStateFlow<UserProfile?>("user_profile_data", null)
        .collectAsState()

    ProfileScreen(user = profileState.value)
}
```

> [!TIP]
> **Menghapus State Sekali Pakai (*One-time Event*)**:  
> Jika data hanya ditujukan untuk diproses satu kali (seperti menampilkan pesan Toast atau SnackBar konfirmasi), segera hapus kuncinya setelah dibaca:  
> `backStackEntry.savedStateHandle.remove<String>("new_name")`

---
[← Sebelumnya: Type-Safe Navigation Guide](./02-type-safe-navigation-guide.md) | [Berikutnya: Lab Praktikum & Analisis Modul 6 →](./04-lab-experiments-travel-and-profile.md)
