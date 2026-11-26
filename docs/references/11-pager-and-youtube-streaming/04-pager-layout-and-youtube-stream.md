---
id: ref-modul11-04
title: Pager Layout (Horizontal/Vertical) & Interoperabilitas YouTube Player
module: Modul Pager YouTube Stream & Navigasi Terapan
category: media-and-views
tags:
  - pager-layout
  - horizontal-pager
  - vertical-pager
  - rememberpagerstate
  - androidview
  - youtube-player
  - interoperability
summary: Implementasi tata letak halaman bergeser (HorizontalPager/VerticalPager) dan integrasi library klasik Android YouTube Player menggunakan jembatan AndroidView.
related:
  - docs/references/11-pager-and-youtube-streaming/01-type-safe-navigation-and-backstack-leak.md
  - docs/references/11-pager-and-youtube-streaming/03-return-back-data-savedstatehandle-serializable.md
  - docs/references/02-lazy-layouts/01-lazy-lists-column-and-row.md
---

# 📺 Pager Layout (Horizontal/Vertical) & Interoperabilitas YouTube Player

[← Sebelumnya: Return-Back Data via SavedStateHandle](./03-return-back-data-savedstatehandle-serializable.md) | [Berikutnya: Glosarium & Kamus Istilah Terpadu →](../GLOSSARY.md)

---

## 1. Konsep Pager Layout & Interoperabilitas `AndroidView`

1. **Pager Layout (`HorizontalPager` & `VerticalPager`)**:  
   Komponen antarmuka deklaratif yang memungkinkan pengguna menggeser antarhalaman data secara mulus menggunakan gestur sentuhan. Dikelola oleh state reaktif `rememberPagerState(pageCount = { ... })`.
2. **Interoperabilitas dengan `AndroidView`**:  
   Karena ekosistem Android memiliki banyak pustaka SDK pihak ketiga berbasis Android View klasik (seperti *YouTube Player*, *Google Maps SDK*, *ExoPlayer SurfaceView*), Compose menyediakan komponen **`AndroidView`** sebagai jembatan pembungkus (*bridge wrapper*) agar View klasik tersebut dapat berjalan harmonis di dalam pohon Composable.

---

## 2. Dependensi Gradle & Izin Internet

### A. `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### B. `app/build.gradle.kts`
```kotlin
dependencies {
    // Android YouTube Player Library oleh Pierfrancesco Soffritti
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:13.0.0")
}
```

---

## 3. Pemodelan Data & Rute Pager

### A. Model Video (`model/YouTubeStreamItem.kt`)
```kotlin
package com.example.appnavigasi.model

data class YouTubeStreamItem(
    val id: Int,
    val title: String,
    val videoId: String // ID video dari URL YouTube
)
```

### B. Rute Navigasi di `navigation/Destinations.kt`
```kotlin
package com.example.appnavigasi.navigation

import kotlinx.serialization.Serializable

@Serializable
object YouTubeStreamRoute

@Serializable
object AboutUsRoute
```

---

## 4. Pembungkus Interoperabilitas Composable YouTube (`YouTubePlayerComposable.kt`)

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubePlayerComposable(
    videoId: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f), // Rasio standar video 16:9
        factory = { context ->
            // Blok factory menginstansiasi View klasik Android
            YouTubePlayerView(context).apply {
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        // Mempersiapkan video YouTube tanpa langsung memutar otomatis
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })
            }
        }
    )
}
```

---

## 5. Implementasi Layar Pager Video (`YouTubeStreamPagerScreen.kt`)

```kotlin
@Composable
fun YouTubeStreamPagerScreen(
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val videoList = remember {
        listOf(
            YouTubeStreamItem(1, "Google I/O Compose Overview", "FgAL6T_KILw"),
            YouTubeStreamItem(2, "Jetpack Compose Navigation", "0-S5a0eXPoc"),
            YouTubeStreamItem(3, "Android State Management", "j5E3_El_8Js")
        )
    }

    val pagerState = rememberPagerState(pageCount = { videoList.size })

    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stream Video Pager",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Komponen Pager Geser Horizontal
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val video = videoList[page]
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        YouTubePlayerComposable(videoId = video.videoId)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Indikator Posisi Halaman Dinamis
            Text(
                text = "Halaman ${pagerState.currentPage + 1} dari ${videoList.size}",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = onNext,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Next >")
            }
        }
    }
}
```

---

## 6. Pembahasan Pertanyaan Pemahaman 4.1 s.d 4.3

### ❓ 4.1: Bagaimana `rememberPagerState` mencatat indeks halaman aktif?
`rememberPagerState` menyimpan snapshot state internal posisi scroll. Ketika pengguna melakukan swipe gesture pada layar, gesture controller menghitung persentase perpindahan offset scroll. Jika offset melewati ambang batas snap (biasanya 50% lebar layar), `pagerState.currentPage` akan diperbarui secara otomatis, memicu rekomendasi teks indikator halaman (`"Halaman X dari Y"`).

### ❓ 4.2: Eksperimen Transformasi ke Layout TikTok / YouTube Shorts (`VerticalPager`)
- **Perubahan Gesture**: Jika `HorizontalPager` diganti menjadi `VerticalPager`, arah gesture geser berubah dari horizontal (kiri-kanan) menjadi **vertikal (atas-bawah / swipe up and down)**.
- **Contoh Aplikasi Populer**: TikTok, Instagram Reels, dan YouTube Shorts menggunakan arsitektur `VerticalPager` untuk navigasi antar konten video layar penuh.

### ❓ 4.3: Mengapa Butuh `AndroidView` dan Apa Peran Blok `factory`?
- **Penyebab**: Pustaka `android-youtube-player` dikembangkan menggunakan sistem Android View lama (`android.view.View`), yang menggunakan kanvas rendering `SurfaceView / WebView` bawaan platform. Komponen ini tidak bisa langsung dirender oleh engine deklaratif Compose.
- **Peran Blok `factory = { context -> ... }`**: Bertindak sebagai konstruktor inisialisasi satu kali (*one-time instantiation*). Compose mengeksekusi lambda `factory` untuk membuat objek View fisik menggunakan `Context` Android, lalu menempelkannya ke dalam hierarki jendela aplikasi.

---
[← Sebelumnya: Return-Back Data via SavedStateHandle](./03-return-back-data-savedstatehandle-serializable.md) | [Berikutnya: Glosarium & Kamus Istilah Terpadu →](../GLOSSARY.md)
