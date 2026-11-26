---
id: ref-modul02-02
title: Tata Letak Dasar Jetpack Compose (Column, Row, Box) & @Preview
module: Modul 02 - Activity Lifecycle, Composable Layout & Modifier
category: ui-layout
tags:
  - compose-layouts
  - column
  - row
  - box
  - preview
  - setcontent
summary: Panduan komponen tata letak dasar Jetpack Compose (Column, Row, Box), peran setContent, dan pemanfaatan anotasi @Preview.
related:
  - docs/references/08-activity-lifecycle-and-basic-ui/01-activity-lifecycle-transitions.md
  - docs/references/08-activity-lifecycle-and-basic-ui/03-modifier-chaining-order-matters.md
  - docs/references/08-activity-lifecycle-and-basic-ui/04-lab-contact-us-and-profile.md
---

# 📐 Tata Letak Dasar Jetpack Compose (Column, Row, Box) & @Preview

[← Sebelumnya: Transisi Siklus Hidup Activity](./01-activity-lifecycle-transitions.md) | [Berikutnya: Modifier Chaining (Order Matters) →](./03-modifier-chaining-order-matters.md)

---

## 1. Peran `setContent { }` dan Anotasi `@Composable`

Dalam aplikasi modern Android berbasis Compose:
- **`setContent { ... }`**: Adalah method ekstensi pada `ComponentActivity` yang bertindak sebagai jembatan pembuka hierarki UI Compose ke dalam sistem Activity Android.
- **Anotasi `@Composable`**: Memberitahu compiler Kotlin bahwa fungsi ini dimaksudkan untuk mengubah data menjadi elemen antarmuka (*UI emitting function*).
  > **Mengapa error jika `@Composable` dihapus?**  
  > Karena fungsi-fungsi UI Compose (seperti `Text`, `Button`, `Column`) hanya dapat dipanggil dari dalam konteks fungsi lain yang juga memiliki anotasi `@Composable`.

---

## 2. Mengapa Teks Menumpuk Tanpa Wadah Layout?

Jika Anda menulis dua composable `Text` berturut-turut tanpa pembungkus:

```kotlin
@Composable
fun ProfilMahasiswa() {
    Text(text = "Nama: Budi Santoso")
    Text(text = "NIM: 24515020000000")
}
```

**Hasil Tampilan**:  
Kedua teks akan **saling bertumpuk (*overlap*) di koordinat `(0,0)` pojok kiri atas**.  
Compose tidak mengasumsikan susunan vertikal atau horizontal secara otomatis. Anda wajib menentukan tata letak menggunakan `Column`, `Row`, atau `Box`.

---

## 3. Tiga Komponen Layout Inti

```
    ┌───────────┐         ┌─────────────────────────┐         ┌───────────┐
    │  Item 1   │         │ Item 1 │ Item 2│ Item 3 │         │ ┌───────┐ │
    ├───────────┤         └─────────────────────────┘         │ │Item 2 │ │ (Z-Index /
    │  Item 2   │                    ROW                      │ └───────┘ │  Layering)
    ├───────────┤         (Penyusunan Horizontal X)           │  Item 1   │
    │  Item 3   │                                             └───────────┘
    └───────────┘                                                  BOX
        COLUMN
(Penyusunan Vertikal Y)
```

### A. `Column`: Menyusun Elemen ke Bawah (Vertikal)
```kotlin
Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("Baris 1")
    Text("Baris 2")
}
```

### B. `Row`: Menyusun Elemen ke Samping (Horizontal)
```kotlin
Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    Icon(Icons.Default.Person, contentDescription = null)
    Text("Nama Mahasiswa")
}
```

### C. `Box`: Menumpuk Elemen Berdasarkan Kedalaman (Sumbu Z / Layering)
Sangat berguna untuk menaruh badge notifikasi, tombol edit di atas avatar, atau gradient overlay di atas foto:
```kotlin
Box(contentAlignment = Alignment.TopEnd) {
    // Layer 1: Avatar Utama di Bawah
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Ikon Profil",
        modifier = Modifier.size(48.dp)
    )
    // Layer 2: Ikon Pensil Edit Menumpuk di Pojok Kanan Atas
    Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = "Ikon Edit",
        modifier = Modifier.size(15.dp)
    )
}
```

---

## 4. Pratinjau Instan dengan `@Preview`

Anotasi `@Preview` memungkinkan Anda melihat tampilan UI secara instan di tab **Split / Design** Android Studio tanpa perlu menjalankan emulator:

```kotlin
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfilMahasiswa() {
    ProfilMahasiswa()
}
```

---
[← Sebelumnya: Transisi Siklus Hidup Activity](./01-activity-lifecycle-transitions.md) | [Berikutnya: Modifier Chaining (Order Matters) →](./03-modifier-chaining-order-matters.md)
