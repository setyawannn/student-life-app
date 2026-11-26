---
id: ref-lazy-02
title: Lazy Grids di Jetpack Compose (Fixed vs Adaptive)
module: Modul 05 - Lazy Layouts & Lists
category: ui-layout
tags:
  - lazy-grid
  - lazy-vertical-grid
  - gridcells-fixed
  - gridcells-adaptive
  - responsive-layout
summary: Panduan penggunaan Lazy Grids untuk menampilkan data berformat kisi/tabel secara efisien dengan konfigurasi kolom Fixed maupun Adaptive responsif.
related:
  - docs/references/02-lazy-layouts/01-lazy-lists-column-and-row.md
  - docs/references/02-lazy-layouts/03-lab-experiments-and-performance.md
---

# 🍱 Lazy Grids di Jetpack Compose (Fixed vs Adaptive)

[← Sebelumnya: Lazy Lists (Column & Row)](./01-lazy-lists-column-and-row.md) | [Berikutnya: Lab Praktikum & Performa Lazy Layouts →](./03-lab-experiments-and-performance.md)

---

## 1. Konsep Lazy Grids

Ketika data perlu disajikan dalam format multikolom (kisi-kisi / matriks foto / galeri produk), Jetpack Compose menyediakan komponen:
- `LazyVerticalGrid`: Item disusun dalam kolom-kolom dan digulirkan secara vertikal.
- `LazyHorizontalGrid`: Item disusun dalam baris-baris dan digulirkan secara horizontal.

Sama seperti `LazyColumn`, komponen Grid hanya merender sel-sel yang saat ini terlihat di layar (*lazy loading*).

---

## 2. Pengaturan Kolom (*Cells Strategy*)

Pada `LazyVerticalGrid`, penataan kolom dikontrol menggunakan parameter `columns`. Terdapat dua strategi utama:

```
+------------------------------------+    +------------------------------------+
|         GridCells.Fixed(2)         |    |     GridCells.Adaptive(150.dp)     |
|   Layar Portrait: 2 Kolom Lebar    |    |   Layar Portrait: 2 Kolom (150dp)  |
|  Layar Landscape: 2 Kolom Melebar  |    |  Layar Landscape: 4-5 Kolom Rapi   |
+------------------------------------+    +------------------------------------+
```

### A. `GridCells.Fixed(count: Int)`
Menetapkan jumlah kolom secara kaku dan tetap, tidak peduli seberapa lebar layar perangkat.
- **Karakteristik**: Jumlah kolom selalu sama baik di layar HP kecil, HP besar, mode portrait, maupun mode landscape / tablet.
- **Konsekuensi**: Pada layar lebar (landscape / tablet), setiap sel akan meregang sangat lebar (*stretched*), yang sering kali merusak estetika antarmuka.

```kotlin
LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(8.dp),
    modifier = Modifier.fillMaxSize()
) {
    items(gadgets) { gadget ->
        GadgetItem(gadget)
    }
}
```

### B. `GridCells.Adaptive(minSize: Dp)`
Menentukan lebar minimum yang diizinkan untuk setiap sel (*item*). Sistem Compose akan menghitung secara dinamis berapa banyak kolom yang dapat dimuat ke dalam layar.
- **Karakteristik**: Responsif terhadap orientasi layar dan ukuran layar yang bervariasi.
- **Contoh Perilaku**: Jika lebar minimum adalah `150.dp`:
  - Di layar portrait selebar 360dp: Compose menampilkan **2 kolom**.
  - Di layar landscape selebar 720dp: Compose otomatis menampilkan **4 kolom** tanpa perlu menulis logika ukuran manual!

```kotlin
LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 150.dp),
    contentPadding = PaddingValues(8.dp),
    modifier = Modifier.fillMaxSize()
) {
    items(gadgets) { gadget ->
        GadgetItem(gadget)
    }
}
```

---

## 3. Matriks Perbandingan Ringkas

| Fitur | `GridCells.Fixed` | `GridCells.Adaptive` |
| :--- | :--- | :--- |
| **Jumlah Kolom** | Tetap (*Hardcoded*) | Fleksibel & Dinamis |
| **Responsivitas Layar** | Rendah (bisa terlalu lebar di tablet) | Sangat Tinggi (otomatis menyesuaikan) |
| **Kasus Penggunaan Tepat** | Komparasi 2 produk berdampingan, grid catur/papan permainan tetap | Katalog belanja, galeri wallpaper, kartu profil |

---
[← Sebelumnya: Lazy Lists (Column & Row)](./01-lazy-lists-column-and-row.md) | [Berikutnya: Lab Praktikum & Performa Lazy Layouts →](./03-lab-experiments-and-performance.md)
