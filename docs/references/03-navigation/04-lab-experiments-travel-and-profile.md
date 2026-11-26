---
id: ref-nav-04
title: Lembar Percobaan dan Analisis Modul 6 (Navigasi Compose)
module: Modul 06 - Navigation Compose
category: lab-guide
tags:
  - lab-guide
  - travel-journal
  - profile-editor
  - destinations
  - savedstatehandle
  - type-safe-vs-string
summary: Panduan praktikum lengkap Modul 6 Jetpack Compose meliputi Travel Journal Type-Safe navigation, Profile Editor return-back data, dan analisis komparatif.
related:
  - docs/references/03-navigation/01-navigation-pillars-and-architecture.md
  - docs/references/03-navigation/02-type-safe-navigation-guide.md
  - docs/references/03-navigation/03-savedstatehandle-and-result-passing.md
---

# 🧪 Lembar Percobaan & Analisis Modul 6 (Navigasi Compose)

[← Sebelumnya: SavedStateHandle & Result Passing](./03-savedstatehandle-and-result-passing.md) | [Berikutnya: Modul 7 - ViewModel & Coroutine →](../04-viewmodel/01-viewmodel-core-and-lifecycle.md)

---

## 1. Percobaan 6.5.1: NavHost & Destinations (Travel Journal)

### A. Mendefinisikan Rute di `Destinations.kt`
```kotlin
package com.example.app.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Details(
    val locationId: Int,
    val locationName: String,
    val isPremium: Boolean = false // Status destinasi premium
)
```

### B. UI Layar Home dengan Kontrol Switch (`HomeScreen.kt`)
```kotlin
@Composable
fun HomeScreen(
    onNavigateToDetails: (Int, String, Boolean) -> Unit
) {
    val locations = listOf("Paris", "Tokyo", "New York", "London")
    var isPremiumSelected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Travel Journal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Switch status membership / paket premium
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Paket Premium: ")
            Switch(
                checked = isPremiumSelected,
                onCheckedChange = { isPremiumSelected = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        locations.forEachIndexed { index, name ->
            Button(
                onClick = { onNavigateToDetails(index, name, isPremiumSelected) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("Visit $name")
            }
        }
    }
}
```

### C. UI Layar Detail dengan Indikator Warna Emas (`DetailScreen.kt`)
```kotlin
@Composable
fun DetailScreen(
    locationId: Int,
    locationName: String,
    isPremium: Boolean,
    onBack: () -> Unit
) {
    // Ubah warna background menjadi emas (Gold) jika paket premium aktif
    val backgroundColor = if (isPremium) Color(0xFFFFD700) else MaterialTheme.colorScheme.background

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Destination Details", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: $locationName", fontWeight = FontWeight.Bold)
            Text("ID: $locationId")
            if (isPremium) {
                Text("Status: ⭐ VIP Premium Destination", color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack) { Text("Go Back") }
        }
    }
}
```

---

## 2. Percobaan 6.5.2: Komunikasi Balik Layar Profil & Editor

### A. Rute Tambahan di `Destinations.kt`
```kotlin
@Serializable
object Profile

@Serializable
object EditUsername
```

### B. Layar Editor (`EditUsernameScreen.kt`) dengan Tombol Cancel
```kotlin
@Composable
fun EditUsernameScreen(
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var tempName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = tempName,
            onValueChange = { tempName = it },
            label = { Text("Enter New Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = { onSave(tempName) }) {
                Text("Confirm & Return")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}
```

### C. Konfigurasi `NavHost` Dua Arah
```kotlin
composable<Profile> { backStackEntry ->
    // Observe perubahan nama yang dititipkan di SavedStateHandle
    val result = backStackEntry.savedStateHandle
        .getStateFlow("new_name", "Guest")
        .collectAsState()

    ProfileScreen(
        currentName = result.value,
        onEditClick = { navController.navigate(EditUsername) }
    )
}

composable<EditUsername> {
    EditUsernameScreen(
        onSave = { newName ->
            // Simpan nama baru ke SavedStateHandle layar sebelumnya
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("new_name", newName)
            navController.popBackStack()
        },
        onCancel = {
            // Langsung pop tanpa mengubah SavedStateHandle
            navController.popBackStack()
        }
    )
}
```

---

## 3. Pembahasan Pertanyaan Analisis & Teori

### ❓ Pertanyaan 1: Mengapa anotasi `@Serializable` wajib pada objek destinasi?
Anotasi `@Serializable` menghasilkan serializer internal Kotlin yang memungkinkan Compose Navigation menyandikan objek Kotlin ke Bundle/URI string di belakang layar dan merekonstruksinya kembali saat diekstrak. Jika anotasi dihilangkan, kompilasi akan gagal (*compile error: Serializer for class not found*).

### ❓ Pertanyaan 2: Mengapa ProfileScreen tetap mempertahankan nama saat tombol Cancel ditekan?
Karena tombol Cancel hanya mengeksekusi `navController.popBackStack()` tanpa memanggil `savedStateHandle.set(...)`. Akibatnya, nilai pada `SavedStateHandle` milik `ProfileScreen` tidak terisi data baru, sehingga `ProfileScreen` tetap memegang dan menampilkan nilai state nama yang lama.

### ❓ Pertanyaan 3: Kapan menggunakan Type-Safe vs String-Based?
- **Gunakan Type-Safe (Rekomendasi Utama & Standar Modern)**:
  - Untuk semua navigasi internal aplikasi (berpindah antar layar, mengirim ID detail, meneruskan argumen multi-tipe). Menjamin *compile-time safety* dan bebas dari kesalahan fatal *typo*.
- **Gunakan String-Based**:
  - Untuk integrasi **Deep Linking eksternal** yang berasal dari web browser (`https://myapp.com/products/123`) atau notifikasi push eksternal yang format rutenya ditentukan dari luar aplikasi sebagai teks string.

---
[← Sebelumnya: SavedStateHandle & Result Passing](./03-savedstatehandle-and-result-passing.md) | [Berikutnya: Modul 7 - ViewModel & Coroutine →](../04-viewmodel/01-viewmodel-core-and-lifecycle.md)
