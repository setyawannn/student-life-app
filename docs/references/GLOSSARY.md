---
id: ref-glossary
title: Glosarium & Kamus Istilah Terpadu Android Jetpack Compose & MVVM
module: Master Reference Shelf
category: glossary
tags:
  - glossary
  - terminology
  - quick-lookup
  - cheatsheet
summary: Glosarium komprehensif istilah-istilah kunci Android Modern, Gradle, Lifecycle, UI Layouts, Navigation Type-Safe, Coroutines, dan MVVM.
---

# 📖 Glosarium & Kamus Istilah Terpadu

[← Kembali ke Rak Referensi Utama](./README.md)

Dokumen ini memuat rangkuman definisi resmi dan analogi untuk seluruh istilah kunci yang digunakan di seluruh modul praktikum dan referensi arsitektur.

---

## 🔤 A - C

### Activity Lifecycle
Rangkaian tahapan siklus hidup komponen Activity Android dari inisialisasi awal (`onCreate`), terlihat dan berinteraksi (`onResume`), jeda sementara (`onPause`), berada di background (`onStop`), hingga dihancurkan oleh sistem (`onDestroy`).  
🔗 *Referensi: [08. Transisi Siklus Hidup Activity](./08-activity-lifecycle-and-basic-ui/01-activity-lifecycle-transitions.md) & [04. ViewModel Lifecycle](./04-viewmodel/01-viewmodel-core-and-lifecycle.md)*

### AndroidView
Komponen Composable yang bertindak sebagai jembatan interoperabilitas untuk menampilkan View Android klasik (seperti YouTubePlayerView, MapView, atau SurfaceView) di dalam hierarki antarmuka Compose.  
🔗 *Referensi: [11. Pager Layout & Interoperabilitas YouTube Player](./11-pager-and-youtube-streaming/04-pager-layout-and-youtube-stream.md)*

### Back Stack
Struktur data tumpukan (*Last In, First Out*) yang melacak riwayat layar (*destinations*) yang telah dikunjungi oleh pengguna. Layar paling atas adalah layar yang sedang aktif dan akan dibuang (*popped*) saat tombol kembali ditekan.  
🔗 *Referensi: [03. Tiga Pilar Navigasi](./03-navigation/01-navigation-pillars-and-architecture.md) & [11. Navigasi Dasar & Back Stack Leak](./11-pager-and-youtube-streaming/01-type-safe-navigation-and-backstack-leak.md)*

### Box
Komponen tata letak (*layout composable*) untuk menumpuk elemen-elemen anak berdasarkan lapisan kedalaman atau sumbu Z (*z-index*).  
🔗 *Referensi: [08. Tata Letak Dasar Compose (Column, Row, Box)](./08-activity-lifecycle-and-basic-ui/02-composable-layouts-column-row-box.md)*

### Breakpoint
Titik henti sementara yang ditempatkan pada baris kode di Android Studio untuk menghentikan program saat runtime, guna memeriksa nilai variabel memori dan mengidentifikasi *logic error*.  
🔗 *Referensi: [07. Teknik Debugging (Logcat & Breakpoints)](./07-gradle-setup-and-debugging/03-debugging-logcat-and-breakpoints.md)*

### CancellationException
Eksepsi internal Kotlin Coroutines yang digunakan untuk membatalkan coroutine secara kooperatif.  
> [!IMPORTANT]
> **Jangan pernah menelan exception ini** dalam blok `catch (e: Exception)` karena akan merusak pembatalan siklus hidup.  
🔗 *Referensi: [05. Integrasi REST API & Error Handling](./05-mvvm-best-practices/04-remote-data-retrofit-and-serialization.md)*

### clip()
Fungsi `Modifier` yang digunakan untuk memotong batasan visual elemen composable sesuai bentuk tertentu (seperti `CircleShape` atau `RoundedCornerShape`). Urutan penulisannya sangat menentukan hasil rendering (*Order Matters*).  
🔗 *Referensi: [08. Modifier Chaining (Order Matters)](./08-activity-lifecycle-and-basic-ui/03-modifier-chaining-order-matters.md)*

### Column & Row
- **Column**: Layout penampung untuk menyusun elemen anak secara vertikal dari atas ke bawah.
- **Row**: Layout penampung untuk menyusun elemen anak secara horizontal dari kiri ke kanan.

### compileSdk, minSdk, targetSdk
- **compileSdk**: Versi API Android yang digunakan saat proses kompilasi kode.
- **minSdk**: Versi API minimum Android yang diizinkan untuk menginstal dan menjalankan aplikasi.
- **targetSdk**: Versi API Android yang dijadikan acuan perilaku sistem terhadap aplikasi.  
*Hubungan wajib: $\text{minSdk} \le \text{targetSdk} \le \text{compileSdk}$.*  
🔗 *Referensi: [07. Konfigurasi Versi SDK pada Gradle](./07-gradle-setup-and-debugging/01-gradle-sdk-configuration.md)*

### Composable
Fungsi deklaratif dalam Jetpack Compose yang ditandai dengan anotasi `@Composable`, digunakan untuk mendefinisikan elemen antarmuka pengguna (UI).

### Coroutines
Unit tugas asinkron yang sangat ringan (*lightweight*) yang dikelola oleh Kotlin Runtime untuk menjalankan tugas berat tanpa memblokir Main Thread (UI).  
🔗 *Referensi: [06. Fondasi Coroutine](./06-coroutines-compose-deepdive/01-coroutine-fundamentals-and-dispatchers.md)*

---

## 🔤 D - L

### Destination
Tujuan akhir dalam grafik navigasi Jetpack Compose, diwakili oleh objek atau data class beranotasi `@Serializable`.  
🔗 *Referensi: [03. Tiga Pilar Navigasi](./03-navigation/01-navigation-pillars-and-architecture.md)*

### Dispatchers
Pengatur alokasi thread pool tempat Coroutine dijalankan:
- `Dispatchers.Main`: Thread UI untuk render tampilan.
- `Dispatchers.IO`: Thread pool untuk operasi disk, jaringan, dan database.
- `Dispatchers.Default`: Thread pool untuk kalkulasi berat komputasi CPU.  
🔗 *Referensi: [06. Fondasi Coroutine & Dispatchers](./06-coroutines-compose-deepdive/01-coroutine-fundamentals-and-dispatchers.md)*

### GridCells (Fixed vs Adaptive)
- **Fixed**: Mematok jumlah kolom kisi secara kaku (misal 2 kolom).
- **Adaptive**: Menentukan ukuran lebar minimum per item; sistem otomatis menghitung jumlah kolom yang muat berdasarkan lebar layar perangkat secara responsif.  
🔗 *Referensi: [02. Lazy Grids (Fixed vs Adaptive)](./02-lazy-layouts/02-lazy-grids-fixed-vs-adaptive.md)*

### Higher-Order Function
Fungsi yang menerima fungsi lain sebagai parameter atau mengembalikan fungsi sebagai hasilnya. Merupakan fondasi pengiriman event callback di Compose.  
🔗 *Referensi: [10. Higher-Order Functions di Compose](./10-kotlin-lambdas-and-hoisting/02-higher-order-functions-in-compose.md)*

### Intent (Explicit vs Implicit)
Objek pesan sistem Android untuk berkomunikasi antar komponen.
- **Explicit Intent**: Membuka activity tertentu di dalam aplikasi internal.
- **Implicit Intent**: Meminta sistem Android membuka aplikasi luar yang sesuai dengan kombinasi **Action** (`ACTION_VIEW`, `ACTION_DIAL`, `ACTION_SENDTO`) dan **Data URI** (`https:`, `tel:`, `mailto:`, `geo:`).  
🔗 *Referensi: [09. Integrasi Implisit Intent](./09-event-state-and-implicit-intent/04-implicit-intent-actions-and-uris.md)*

### it (Kata Kunci Lambda)
Nama panggilan implisit otomatis untuk satu-satunya parameter masukan dalam fungsi lambda satu argumen di Kotlin.  
🔗 *Referensi: [10. Trailing Lambda & Keyword 'it'](./10-kotlin-lambdas-and-hoisting/03-trailing-lambda-and-it-keyword.md)*

### LaunchedEffect
Fungsi Composable pembungkus yang meluncurkan coroutine yang terikat pada siklus hidup composable. Otomatis dibatalkan jika composable keluar dari komposisi atau jika parameter `key` berubah.  
🔗 *Referensi: [01. Side-Effects & LaunchedEffect](./01-state-and-lifecycle/03-side-effects-launchedeffect.md)*

### Lazy Layouts (LazyColumn & LazyRow)
Komponen daftar cerdas yang hanya merender item yang sedang terlihat (*visible*) di layar, setara dengan `RecyclerView` pada Android XML.  
🔗 *Referensi: [02. Lazy Lists (Column & Row)](./02-lazy-layouts/01-lazy-lists-column-and-row.md)*

---

## 🔤 M - P

### Modifier & Modifier Chaining
- **Modifier**: Objek dekorasi untuk mengatur ukuran, padding, background, klik, dan tata letak composable.
- **Modifier Chaining**: Rangkaian fungsi modifier yang dipanggil berurutan, di mana urutannya sangat menentukan hasil akhir (*order matters*).  
🔗 *Referensi: [08. Modifier Chaining (Order Matters)](./08-activity-lifecycle-and-basic-ui/03-modifier-chaining-order-matters.md)*

### NavController & NavHost
- **NavController**: Pengontrol pusat yang mengelola navigasi aplikasi, melacak tumpukan layar (*back stack*), dan mengeksekusi perpindahan rute.
- **NavHost**: Wadah visual dalam UI yang menampilkan composable aktif berdasarkan instruksi `NavController`.  
🔗 *Referensi: [03. Tiga Pilar Navigasi](./03-navigation/01-navigation-pillars-and-architecture.md)*

### Pager Layout (HorizontalPager & VerticalPager)
Komponen tata letak geser halaman (kiri-kanan atau atas-bawah) dengan pemuatan lazy, dikontrol via `rememberPagerState`.  
🔗 *Referensi: [11. Pager Layout & Interoperabilitas YouTube Player](./11-pager-and-youtube-streaming/04-pager-layout-and-youtube-stream.md)*

### Property Delegation (`by`)
Mekanisme Kotlin untuk mendelegasikan getter dan setter variabel ke objek lain (seperti `MutableState<T>`), menghilangkan kebutuhan mengetik `.value`.  
🔗 *Referensi: [09. Property Delegation (by Keyword)](./09-event-state-and-implicit-intent/03-property-delegation-by-keyword.md)*

---

## 🔤 R - S

### remember vs rememberSaveable
- `remember`: Menjaga nilai state bertahan selama proses rekomendasi (*recomposition*), namun hilang saat rotasi layar (*Activity destroyed*).
- `rememberSaveable`: Menjaga nilai state bertahan saat rekomposisi DAN saat terjadi perubahan konfigurasi (rotasi layar) dengan menyimpannya ke dalam `Bundle`.  
🔗 *Referensi: [01. Configuration Change & Persistence](./01-state-and-lifecycle/02-configuration-change-and-persistence.md)*

### SavedStateHandle
Media penyimpanan pasangan kunci-nilai (*key-value map*) yang menempel pada setiap entri back stack. Digunakan untuk menyimpan state atau mengembalikan data hasil (*return-back result*) dari layar editor ke layar pemanggil secara *loose-coupled*.  
🔗 *Referensi: [03. SavedStateHandle & Result Passing](./03-navigation/03-savedstatehandle-and-result-passing.md) & [11. Return-Back Data via SavedStateHandle](./11-pager-and-youtube-streaming/03-return-back-data-savedstatehandle-serializable.md)*

### Single Source of Truth (SSoT)
Prinsip arsitektur di mana suatu entitas data hanya memiliki satu pemilik otoritas tunggal (misalnya ViewModel atau Repository) guna mencegah inkonsistensi data antar layar.  
🔗 *Referensi: [01. State Hoisting Fundamentals](./01-state-and-lifecycle/01-state-hoisting-fundamentals.md)*

### State Hoisting
Pola arsitektur mengangkat variabel state dari composable anak ke pemanggilnya (*caller*), mengubah komponen menjadi *stateless*, mudah diuji (*testable*), dan dapat digunakan kembali (*reusable*).  
🔗 *Referensi: [10. Implementasi State Hoisting dengan Lambda](./10-kotlin-lambdas-and-hoisting/04-state-hoisting-with-lambdas.md)*

### StateFlow
Aliran data reaktif (*observable state holder flow*) dari Kotlin Coroutines yang selalu menyimpan nilai terkini dan memancarkan pembaruan nilai baru ke para pengamat (*collectors*).  
🔗 *Referensi: [05. StateFlow vs mutableStateOf](./05-mvvm-best-practices/02-state-management-stateflow-vs-mutablestate.md)*

---

## 🔤 T - V

### Trailing Lambda
Aturan sintaksis Kotlin di mana jika parameter terakhir fungsi adalah lambda, kurung kurawal `{ ... }` dapat ditulis di luar tanda kurung biasa `()`.  
🔗 *Referensi: [10. Trailing Lambda & Keyword 'it'](./10-kotlin-lambdas-and-hoisting/03-trailing-lambda-and-it-keyword.md)*

### Type-Safe Navigation
Pendekatan navigasi modern (Navigation Compose 2.8.0+) yang mendefinisikan rute menggunakan Objek atau Data Class Kotlin beranotasi `@Serializable`, menggantikan String URL manual yang rawan crash *typo*.  
🔗 *Referensi: [03. Type-Safe Navigation Guide](./03-navigation/02-type-safe-navigation-guide.md)*

### Unidirectional Data Flow (UDF)
Pola aliran data satu arah di mana status mengalir ke bawah (*state down*) dari ViewModel ke View, dan aksi/kejadian pengguna mengalir ke atas (*event up*) dari View ke ViewModel.  
🔗 *Referensi: [05. Arsitektur MVVM & UDF Modern Android](./05-mvvm-best-practices/01-architecture-overview-and-udf.md)*

### Version Catalog (`libs.versions.toml`)
Fitur standar Android modern untuk mengelola seluruh pustaka dan plugin eksternal secara terpusat, konsisten, dan minim duplikasi.  
🔗 *Referensi: [07. Version Catalog (TOML) Guide](./07-gradle-setup-and-debugging/02-version-catalog-toml-guide.md)*

### ViewModel
Komponen Jetpack yang dirancang sebagai pemegang state UI (*State Holder*) yang sadar siklus hidup (*lifecycle-aware*), tetap bertahan hidup melintasi rotasi layar.  
🔗 *Referensi: [04. ViewModel Architecture & Lifecycle](./04-viewmodel/01-viewmodel-core-and-lifecycle.md)*

---
[← Kembali ke Rak Referensi Utama](./README.md)
