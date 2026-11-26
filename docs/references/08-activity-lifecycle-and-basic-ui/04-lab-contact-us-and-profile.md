---
id: ref-modul02-04
title: Lembar Percobaan, Tugas Mandiri, & Evaluasi Modul 2
module: Modul 02 - Activity Lifecycle, Composable Layout & Modifier
category: lab-guide
tags:
  - lab-guide
  - contact-us-screen
  - profil-mahasiswa
  - whatsapp-intent
  - modifier-chaining-test
  - lifecycle-analysis
summary: Dokumentasi langkah eksperimen praktikum Modul 2, pengujian intent WhatsApp, pembuatan komponen profil mahasiswa, dan analisis siklus hidup aplikasi.
related:
  - docs/references/08-activity-lifecycle-and-basic-ui/01-activity-lifecycle-transitions.md
  - docs/references/08-activity-lifecycle-and-basic-ui/02-composable-layouts-column-row-box.md
  - docs/references/08-activity-lifecycle-and-basic-ui/03-modifier-chaining-order-matters.md
---

# 🧪 Lembar Percobaan, Tugas Mandiri, & Evaluasi Modul 2

[← Sebelumnya: Modifier Chaining (Order Matters)](./03-modifier-chaining-order-matters.md) | [Berikutnya: Modul 3 - Event, State & Implisit Intent →](../09-event-state-and-implicit-intent/01-imperative-vs-declarative-ui.md)

---

## 1. Latihan Praktik 1: `ContactUsScreen.kt` & Intent WhatsApp

Membuat antarmuka tombol bantuan yang memanggil aplikasi eksternal WhatsApp:

```kotlin
package com.example.appnavigasi.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactUsScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Halaman Bantuan", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Membuka tautan API WhatsApp menggunakan Implicit Intent
            val url = "https://wa.me/6281234567890"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }) {
            Text("Hubungi via WhatsApp")
        }
    }
}
```

---

## 2. Latihan Praktik 2: `ProfilMahasiswa.kt` dengan Box Layering

Menggabungkan Avatar Person dengan ikon pensil Edit di pojok kanan atas (`Alignment.TopEnd`):

```kotlin
@Composable
fun ProfilMahasiswa() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Box digunakan untuk menumpuk ikon Person dan Edit
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Ikon Profil",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)      // Memotong bulat
                    .background(Color.Cyan) // Mewarnai bidang bulat
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Ikon Edit",
                modifier = Modifier.size(15.dp),
                tint = Color.DarkGray
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "Nama: Setyawan", fontWeight = FontWeight.Bold)
            Text(text = "NIM: 24515020000000")
        }
    }
}
```

---

## 3. Tugas Praktik 1.5: Penggabungan `HalamanUtama.kt`

Menggabungkan komponen profil dan tombol kontak WhatsApp ke dalam satu layar:

```kotlin
@Composable
fun HalamanUtama() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Memanggil komponen ProfilMahasiswa
        ProfilMahasiswa()

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Tombol Hubungi WhatsApp
        Button(onClick = {
            val url = "https://wa.me/6281234567890"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }) {
            Text("Hubungi via WhatsApp")
        }
    }
}
```

---

## 4. Pembahasan Pertanyaan Evaluasi

### ❓ Pertanyaan 1: Mengapa kedua teks menumpuk di pojok kiri atas pada awal latihan?
Karena fungsi Compose merender elemen secara default pada koordinat `(0,0)` jika tidak dibungkus oleh tata letak layout seperti `Column` (vertikal) atau `Row` (horizontal).

### ❓ Pertanyaan 2: Mengapa memisahkan Composable ke file terpisah sangat dianjurkan?
Mencegah `MainActivity.kt` menjadi **"God Object"** yang menumpuk ribuan baris kode, meningkatkan *readability*, mempermudah *teamwork*, dan memungkinkan komponen diuji atau di-preview secara independen.

### ❓ Pertanyaan 3: Ketika berpindah ke WhatsApp, fungsi lifecycle mana yang tepat untuk menyimpan draft?
Method **`onPause()`** atau **`onStop()`**. Ketika Activity tertutup oleh WhatsApp, aplikasi memasuki status stopped. Jika kita menunggu `onDestroy()`, ada risiko data hilang karena sistem operasi Android dapat mematikan proses di background seketika untuk menghemat RAM (*Low Memory Killer*) tanpa memanggil `onDestroy()`.

---
[← Sebelumnya: Modifier Chaining (Order Matters)](./03-modifier-chaining-order-matters.md) | [Berikutnya: Modul 3 - Event, State & Implisit Intent →](../09-event-state-and-implicit-intent/01-imperative-vs-declarative-ui.md)
