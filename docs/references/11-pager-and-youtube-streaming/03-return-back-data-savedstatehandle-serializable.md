---
id: ref-modul11-03
title: Return-Back Data Passing dengan SavedStateHandle & @Serializable
module: Modul Pager YouTube Stream & Navigasi Terapan
category: navigation
tags:
  - return-back-data
  - savedstatehandle
  - serializable-model
  - userprofile
  - activity-recreation
  - ssot
summary: Pola pengembalian data kustom dari layar editor ke profil menggunakan SavedStateHandle pada previousBackStackEntry dan ketahanannya terhadap rotasi layar.
related:
  - docs/references/11-pager-and-youtube-streaming/02-forward-data-passing-checkbox-lazycolumn.md
  - docs/references/11-pager-and-youtube-streaming/04-pager-layout-and-youtube-stream.md
  - docs/references/03-navigation/03-savedstatehandle-and-result-passing.md
---

# 🔄 Return-Back Data Passing dengan SavedStateHandle & @Serializable

[← Sebelumnya: Forward Data Passing & Checkbox](./02-forward-data-passing-checkbox-lazycolumn.md) | [Berikutnya: Pager Layout & YouTube Stream →](./04-pager-layout-and-youtube-stream.md)

---

## 1. Masalah Pengiriman Data Mundur (*Return-Back Data*)

Ketika pengguna membuka layar editor (misal: `EditProfileScreen`), menyunting data nama dan email, lalu menekan tombol "Simpan & Kirim Balik", bagaimana data tersebut dikembalikan ke `ProfileScreen`?

```
[SALAH BESAR]:
EditProfileScreen ──> navController.navigate(ProfileRoute)
Akibat: Menumpuk layar Profile baru di atas stack! Back stack menjadi [Profile, EditProfile, Profile].

[BENAR (UDF & SSoT)]:
EditProfileScreen ──> Tulis ke previousBackStackEntry.savedStateHandle
                  ──> navController.popBackStack()
Akibat: EditProfileScreen dihancurkan, ProfileScreen yang di bawahnya membaca data baru!
```

---

## 2. Pemodelan Objek Kustom (`model/UserProfile.kt`)

```kotlin
package com.example.appnavigasi.model

import java.io.Serializable

data class UserProfile(
    val username: String,
    val email: String,
    val role: String
) : Serializable
```

### Rute Navigasi di `navigation/Destinations.kt`:
```kotlin
package com.example.appnavigasi.navigation

import kotlinx.serialization.Serializable

@Serializable
object ProfileRoute

@Serializable
object EditProfileRoute
```

---

## 3. Implementasi Layar Penerima & Pengirim

### A. Layar Penerima (`ProfileScreen.kt`)
```kotlin
@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Profil Pengguna", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Username: ${userProfile?.username ?: "Belum Diatur"}")
            Text("Email: ${userProfile?.email ?: "-"}")
            Text("Role: ${userProfile?.role ?: "-"}")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onEditClick) {
                Text("Edit Profil (navigate) >")
            }
        }
    }
}
```

### B. Layar Pengirim / Editor (`EditProfileScreen.kt`)
```kotlin
@Composable
fun EditProfileScreen(
    onSave: (UserProfile) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Form Edit Profil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                OutlinedButton(onClick = onCancel) {
                    Text("Batal")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    onSave(UserProfile(username, email, "Administrator"))
                }) {
                    Text("Simpan & Kirim Balik")
                }
            }
        }
    }
}
```

---

## 4. Konfigurasi NavHost dengan `SavedStateHandle` SSoT

```kotlin
@Composable
fun AppNavigationP3() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ProfileRoute
    ) {
        composable<ProfileRoute> { backStackEntry ->
            // Mengamati kembalian data dari EditProfileScreen secara reaktif
            val userProfileState = backStackEntry.savedStateHandle
                .getStateFlow<UserProfile?>("user_profile_key", null)
                .collectAsState()

            ProfileScreen(
                userProfile = userProfileState.value,
                onEditClick = { navController.navigate(EditProfileRoute) }
            )
        }

        composable<EditProfileRoute> {
            EditProfileScreen(
                onSave = { updatedProfile ->
                    // 1. Masukkan data balik ke SavedStateHandle milik layar sebelumnya
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("user_profile_key", updatedProfile)

                    // 2. Tutup layar dan kembali
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
```

---

## 5. Pembahasan Pertanyaan Evaluasi Eksperimen 3.1 - 3.4

### ❓ 3.1: Alur Kerja Return-Back Data Passing
Ketika tombol "Simpan & Kirim Balik" ditekan:
1. Objek `UserProfile` baru dibentuk dari input form.
2. Melalui `previousBackStackEntry?.savedStateHandle?.set("user_profile_key", updatedProfile)`, data disimpan ke dalam entri back stack milik `ProfileScreen`.
3. Fungsi `popBackStack()` dipanggil untuk mengeluarkan `EditProfileScreen`.
4. `ProfileScreen` yang sedang mendengarkan (*observing*) via `getStateFlow` mendeteksi emisi nilai baru dan langsung merender nama pengguna yang diperbarui.
5. **Mengapa tidak boleh `navigate(ProfileRoute)`?** Karena akan melipatgandakan instance `ProfileScreen` di memori dan mengacaukan navigasi tombol Back fisik.

### ❓ 3.2: Ketahanan Terhadap Rotasi Layar (Configuration Change)
- **Hasil Pengamatan**: Setelah layar dirotasi (Ctrl+F11) dari Portrait ke Landscape, **data profil yang baru tetap bertahan dan TIDAK hilang kembali ke "Belum Diatur"**.
- **Penyebab**: `SavedStateHandle` didukung oleh mekanisme *Saved Instance State* Android OS yang secara otomatis mempertahankan map data di dalam `Bundle` saat siklus `onDestroy() -> onCreate()` terjadi akibat perubahan konfigurasi.

### ❓ 3.3: Eksperimen Tombol Batal
Ketika tombol "Batal" ditekan, fungsi hanya memanggil `navController.popBackStack()` tanpa memodifikasi `savedStateHandle`. Karena tidak ada data baru yang ditulis, `ProfileScreen` tetap menampilkan nilai lama tanpa perubahan.

### ❓ 3.4: Uji Penghapusan Anotasi `@Serializable`
Jika penanda serialisasi dihilangkan dari objek data kustom, Kotlin Compiler akan memunculkan error kompilasi karena `SavedStateHandle` dan `Bundle` membutuhkan jaminan bahwa objek tersebut dapat diserialisasi ke dalam bentuk bitstream data untuk disimpan ke memori penyimpanan sementara Android.

---
[← Sebelumnya: Forward Data Passing & Checkbox](./02-forward-data-passing-checkbox-lazycolumn.md) | [Berikutnya: Pager Layout & YouTube Stream →](./04-pager-layout-and-youtube-stream.md)
