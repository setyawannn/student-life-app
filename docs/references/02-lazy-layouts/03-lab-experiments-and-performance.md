---
id: ref-lazy-03
title: Lembar Percobaan dan Analisis Modul 5 (Lazy Layouts)
module: Modul 05 - Lazy Layouts & Lists
category: lab-guide
tags:
  - lab-guide
  - lazy-layouts
  - lazy-column
  - lazy-row
  - lazy-grid
  - orientation-change
summary: Dokumentasi langkah kode implementasi data dummy Gadget, perbandingan LazyColumn, LazyRow, dan LazyVerticalGrid saat rotasi layar landscape.
related:
  - docs/references/02-lazy-layouts/01-lazy-lists-column-and-row.md
  - docs/references/02-lazy-layouts/02-lazy-grids-fixed-vs-adaptive.md
---

# 🧪 Lembar Percobaan & Analisis Modul 5 (Lazy Layouts)

[← Sebelumnya: Lazy Grids (Fixed vs Adaptive)](./02-lazy-grids-fixed-vs-adaptive.md) | [Berikutnya: Modul 6 - Navigasi Compose →](../03-navigation/01-navigation-pillars-and-architecture.md)

---

## 1. Setup Data Model Dummy

Buat file `DataModel.kt`:
```kotlin
data class Gadget(
    val id: Int,
    val name: String,
    val description: String
)
```

Dataset dummy di `MainActivity.kt`:
```kotlin
val gadgets = listOf(
    Gadget(1, "Smartphone", "Perangkat komunikasi genggam"),
    Gadget(2, "Laptop", "Komputer portabel untuk bekerja"),
    Gadget(3, "Tablet", "Perangkat layar sentuh antara hp dan laptop"),
    Gadget(4, "Smartwatch", "Jam tangan pintar pelacak kesehatan"),
    Gadget(5, "Earbuds", "Perangkat audio nirkabel")
)
```

---

## 2. Pembuatan Komponen Item Card (`GadgetItem`)

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GadgetItem(gadget: Gadget) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = gadget.name,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = gadget.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

---

## 3. Implementasi 3 Varian Layout

### A. Varian 1: Daftar Vertikal (`LazyColumn`)
```kotlin
@Composable
fun GadgetList(gadgets: List<Gadget>) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(gadgets) { gadget ->
            GadgetItem(gadget)
        }
    }
}
```

### B. Varian 2: Daftar Horizontal (`LazyRow`)
```kotlin
@Composable
fun GadgetHorizontalList(gadgets: List<Gadget>) {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(gadgets) { gadget ->
            GadgetItem(gadget)
        }
    }
}
```

### C. Varian 3: Kisi Grid Responsif (`LazyVerticalGrid`)
```kotlin
@Composable
fun GadgetGrid(gadgets: List<Gadget>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(gadgets) { gadget ->
            GadgetItem(gadget)
        }
    }
}
```

---

## 4. Pembahasan Pertanyaan Analisis Praktikum

### ❓ Pertanyaan 1: Perbedaan tampilan dan perilaku scroll antara `LazyColumn` dan `LazyRow`?
- **LazyColumn**: Item disusun dari atas ke bawah (sumbu Y). Arah scroll adalah vertikal. Lebar kartu secara default memenuhi lebar layar (`fillMaxWidth`), sangat optimal untuk daftar berita, pesan obrolan, atau feed media sosial.
- **LazyRow**: Item disusun dari kiri ke kanan (sumbu X). Arah scroll adalah horizontal. Cocok untuk carousel banner, kategori filter instan, atau daftar cerita (*story*).

### ❓ Pertanyaan 2: Perbedaan tampilan antara `LazyColumn` dan `LazyVerticalGrid`?
- **LazyColumn**: Hanya menampilkan tepat **1 item per baris horizontal**. Layout berbentuk satu jalur linear.
- **LazyVerticalGrid**: Mengizinkan **banyak item berada berdampingan dalam satu baris** sesuai konfigurasi kolom (`columns`). Layout berbentuk matriks dua dimensi.

### ❓ Pertanyaan 3: Perbedaan tampilan antara `Adaptive` dan `Fixed` saat layar dirotasi ke landscape?
- **Pada `GridCells.Fixed(2)`**: Saat diputar ke landscape, jumlah kolom tetap dipaksa berjumlah 2. Akibatnya, masing-masing kartu meregang sangat lebar secara horizontal dan menyisakan banyak ruang kosong yang tidak proporsional.
- **Pada `GridCells.Adaptive(minSize = 150.dp)`**: Saat diputar ke landscape di mana lebar layar meningkat dari misalnya 400dp ke 800dp, sistem otomatis menghitung ulang dan menambah kolom menjadi 4 atau 5 kolom. Ukuran kartu tetap proporsional dan tidak terdistorsi.

---
[← Sebelumnya: Lazy Grids (Fixed vs Adaptive)](./02-lazy-grids-fixed-vs-adaptive.md) | [Berikutnya: Modul 6 - Navigasi Compose →](../03-navigation/01-navigation-pillars-and-architecture.md)
