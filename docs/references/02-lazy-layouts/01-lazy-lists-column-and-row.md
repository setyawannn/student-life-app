---
id: ref-lazy-01
title: Lazy Lists di Jetpack Compose (LazyColumn & LazyRow)
module: Modul 05 - Lazy Layouts & Lists
category: ui-layout
tags:
  - lazy-layouts
  - lazy-column
  - lazy-row
  - recyclerview
  - memory-efficiency
summary: Konsep dasar Lazy Layouts pada Jetpack Compose, perbedaan performa dengan Column/Row biasa, dan penggunaan DSL item & items.
related:
  - docs/references/02-lazy-layouts/02-lazy-grids-fixed-vs-adaptive.md
  - docs/references/02-lazy-layouts/03-lab-experiments-and-performance.md
---

# 📜 Lazy Lists di Jetpack Compose (LazyColumn & LazyRow)

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Lazy Grids (Fixed vs Adaptive) →](./02-lazy-grids-fixed-vs-adaptive.md)

---

## 1. Mengapa Butuh Lazy Layouts?

Pada Jetpack Compose standar, tata letak seperti `Column` atau `Row` biasa akan **merender seluruh item sekaligus**, meskipun item tersebut berada jauh di luar area tampilan layar (*viewport*).

```kotlin
// TIDAK DIANJURKAN untuk kumpulan data besar:
@Composable
fun MessageList(messages: List<Message>) {
    Column {
        messages.forEach { message ->
            MessageRow(message)
        }
    }
}
```

Jika daftar berisi ratusan atau ribuan data, pendekatan `Column` biasa akan:
- Menghabiskan memori dalam jumlah besar (*High Memory Usage / Out of Memory*).
- Menghambat CPU saat rendering awal sehingga aplikasi mengalami patah-patah (*UI jank / lag*).

### Solusi: Lazy Layouts
**Lazy Layouts** hanya membuat, mengukur, dan merender item yang **sedang terlihat (*visible*)** di layar oleh pengguna.
- **Efisiensi Memori**: Item yang di luar jangkauan layar akan dihancurkan atau tidak diinstansiasi sampai diperlukan.
- **Efisiensi CPU**: Mengurangi beban rendering dan pengukuran layout secara drastis.
- **Analogi Android Klasik**: Konsep ini ekuivalen modern dari `RecyclerView` pada Android XML View System.

---

## 2. LazyColumn vs LazyRow

Jetpack Compose menyediakan dua implementasi daftar satu dimensi:
- `LazyColumn`: Membuat daftar vertikal yang dapat digulir (*scroll*).
- `LazyRow`: Membuat daftar horizontal yang dapat digulir ke samping.

---

## 3. Komponen Utama & DSL (Domain Specific Language)

Di dalam blok konten `LazyColumn` atau `LazyRow`, Anda tidak memanggil composable biasa secara langsung, melainkan menggunakan DSL khusus:

```kotlin
LazyColumn {
    // 1. item: Menambahkan satu item tunggal (misal: header atau banner)
    item {
        HeaderSection()
    }

    // 2. items(count): Menambahkan sejumlah item berdasarkan angka integer
    items(count = 5) { index ->
        Text("Item nomor: $index")
    }

    // 3. items(list): Menambahkan item dari koleksi List (paling sering digunakan)
    items(myGadgetList) { gadget ->
        GadgetItem(gadget)
    }
}
```

---

## 4. Pola Best Practice: Penentuan Key yang Unik

Saat menampilkan data dinamis yang bisa ditambah, dihapus, atau diubah posisinya, selalu sertakan parameter `key`:

```kotlin
LazyColumn {
    items(
        items = gadgets,
        key = { gadget -> gadget.id } // Kunci unik menjaga state item saat list diacak/dihapus
    ) { gadget ->
        GadgetItem(gadget)
    }
}
```

Menyediakan `key` yang stabil memungkinkan Compose mengenali item mana yang bergeser tanpa harus merender ulang seluruh daftar dari awal.

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Lazy Grids (Fixed vs Adaptive) →](./02-lazy-grids-fixed-vs-adaptive.md)
