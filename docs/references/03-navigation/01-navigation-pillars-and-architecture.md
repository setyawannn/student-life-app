---
id: ref-nav-01
title: Tiga Pilar Utama Navigasi di Jetpack Compose
module: Modul 06 - Navigation Compose
category: navigation
tags:
  - navigation-compose
  - navhost
  - navcontroller
  - destinations
  - backstack
summary: Penjelasan arsitektur tiga pilar navigasi Jetpack Compose (NavController, NavHost, Destination), analogi bioskop, dan pengelolaan back stack.
related:
  - docs/references/03-navigation/02-type-safe-navigation-guide.md
  - docs/references/03-navigation/03-savedstatehandle-and-result-passing.md
  - docs/references/03-navigation/04-lab-experiments-travel-and-profile.md
---

# 🧭 Tiga Pilar Utama Navigasi di Jetpack Compose

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Type-Safe Navigation Guide →](./02-type-safe-navigation-guide.md)

---

## 1. Analogi Studio Bioskop

Untuk memahami bagaimana navigasi bekerja dalam Jetpack Compose, bayangkan sistem ini seperti **sebuah gedung bioskop modern**:

```
+------------------------------------------------------------+
|                       NAVCONTROLLER                        |
|       (Operator Film / Supir / Remote Navigasi)            |
|       - Menerima perintah: "Tayangkan Film Detail!"        |
|       - Mengelola catatan tiket / riwayat (Back Stack)     |
+------------------------------------------------------------+
                              |
                              v
+------------------------------------------------------------+
|                          NAVHOST                           |
|               (Layar Proyektor Studio Bioskop)             |
|       - Wadah statis yang menampilkan tayangan aktif       |
|       - Berganti tampilan sesuai instruksi NavController   |
+------------------------------------------------------------+
                              |
                              v
+------------------------------------------------------------+
|                        DESTINATIONS                        |
|                  (Film-Film yang Diputar)                  |
|       - Layar "Home", Layar "Profile", Layar "Detail"      |
+------------------------------------------------------------+
```

---

## 2. Rincian Tiga Pilar Navigasi

### 1. Destination (Tujuan Layar / "Film")
- Merupakan setiap layar (*Composable*) yang dapat dikunjungi dan dilihat oleh pengguna di dalam aplikasi.
- Setiap Destination memiliki pengidentifikasi rute unik (*Route*).

### 2. NavHost (Kontainer Tampilan / "Layar Proyektor")
- Merupakan wadah visual (*container Composable*) tempat layar-layar tersebut bergantian muncul.
- `NavHost` menghubungkan `NavController` dengan grafik navigasi (*NavGraph*) yang menentukan layar mana saja yang tersedia di dalam aplikasi.
- Memiliki parameter wajib: `navController` dan `startDestination`.

### 3. NavController (Otak & Pengemudi / "Operator Film")
- Merupakan objek pusat kendali yang melacak posisi pengguna saat ini dan mengelola tumpukan riwayat halaman (**Back Stack**).
- Dibuat di level tertinggi pohon UI (biasanya di `MainActivity` menggunakan `rememberNavController()`).
- Bertanggung jawab menjalankan aksi:
  - Maju ke layar baru: `navController.navigate(Route)`
  - Kembali ke layar sebelumnya: `navController.popBackStack()`

---

## 3. Pemahaman Alur Back Stack (Tumpukan Layar)

Android mengelola riwayat navigasi menggunakan struktur data **LIFO (*Last In, First Out*)**:
1. Saat user berada di **Home** -> Back stack: `[Home]`
2. User klik detail produk -> `navigate(Details)` -> Back stack: `[Home, Details]`
3. User membuka galeri gambar -> `navigate(Gallery)` -> Back stack: `[Home, Details, Gallery]`
4. User menekan tombol Back atau memanggil `popBackStack()` -> Layar `Gallery` dibuang (*popped*), tampilan kembali ke `Details`. Back stack: `[Home, Details]`.

### Mencegah Penumpukan Memori Tak Terbatas: `popUpTo`
Jika alur navigasi berbentuk melingkar (contoh: Login -> Home -> Profil -> Logout -> Login), navigasi tanpa pembersihan akan menumpuk puluhan layar di memori. Gunakan `popUpTo`:

```kotlin
navController.navigate(Home) {
    popUpTo(Login) {
        inclusive = true // Menghapus layar Login dari back stack
    }
}
```

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Type-Safe Navigation Guide →](./02-type-safe-navigation-guide.md)
