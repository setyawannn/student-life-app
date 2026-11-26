---
id: ref-mvvm-02
title: State Management di ViewModel (StateFlow vs mutableStateOf)
module: Modul Panduan MVVM Modern
category: state-management
tags:
  - stateflow
  - mutablestateof
  - textfield-pattern
  - snapshotflow
  - state-decision-matrix
summary: Panduan mendalam memilih antara StateFlow dan mutableStateOf di ViewModel, pola penanganan TextField yang bebas glitch, dan matriks lokasi penyimpanan state.
related:
  - docs/references/05-mvvm-best-practices/01-architecture-overview-and-udf.md
  - docs/references/05-mvvm-best-practices/03-reactive-ui-state-combine-and-statein.md
---

# ⚖️ State Management di ViewModel: StateFlow vs mutableStateOf

[← Sebelumnya: Arsitektur MVVM & UDF Modern Android](./01-architecture-overview-and-udf.md) | [Berikutnya: Reactive UI State (combine + stateIn) →](./03-reactive-ui-state-combine-and-statein.md)

---

## 1. Perbandingan Komparatif: StateFlow vs mutableStateOf

| Fitur | `mutableStateOf` (Compose State) | `StateFlow` (Kotlin Coroutines) |
| :--- | :--- | :--- |
| **Ekosistem Asal** | Compose Runtime | Kotlin Coroutines |
| **Kesesuaian Penggunaan** | ViewModel sederhana khusus Compose; input ketikan teks | ViewModel yang menerima Flow dari Repository/Room; mendukung multiplatform & XML |
| **Cara Baca di UI** | Langsung: `viewModel.query` | Reaktif: `viewModel.uiState.collectAsStateWithLifecycle()` |
| **Dukungan Operator Flow** | ❌ Tidak ada | ✅ Lengkap (`combine`, `map`, `stateIn`, `filter`, dll) |
| **Thread Safety** | Terikat ke Compose Snapshot system | Aman dimutasi dari berbagai Coroutine (`_uiState.update`) |

---

## 2. Matriks Keputusan: Di Mana Menyimpan Sebuah Nilai?

Untuk menentukan lokasi penyimpanan data secara tepat, tanyakan:  
> *"Jika nilai ini berubah, apakah ada yang perlu dihitung, divalidasi, disimpan ke server, atau mempengaruhi bagian aplikasi lain?"*

```
               Apakah data diproses logika / validasi / disimpan?
                                       │
                    ┌──────────────────┴──────────────────┐
                    ▼                                     ▼
                TIDAK (UI Murni)                     YA (Logika Bisnis)
                    │                                     │
         Simpan di Composable View                Simpan di ViewModel
         dengan `rememberSaveable`                        │
                                        ┌─────────────────┴─────────────────┐
                                        ▼                                   ▼
                                Ketikan Teks Form                    Data Hasil API / DB
                                `mutableStateOf`                          `StateFlow`
```

### Tabel Rincian Lokasi Penyimpanan State

| Jenis Nilai | Pemilik Asli Nilai | Lokasi Penyimpanan yang Benar |
| :--- | :--- | :--- |
| **Toggle Tampilan Visual** (Buka/tutup dropdown, ekspansi kartu, show/hide password) | UI Composable | `rememberSaveable { mutableStateOf(...) }` di Composable |
| **Teks yang Sedang Diketik & Divalidasi** (Pencarian, form registrasi, input numerik) | Ketikan Pengguna | `mutableStateOf` dengan `private set` di **ViewModel** |
| **Aksi Mutasi Data** (Klik hapus, centang tugas selesai, toggle bookmark) | Server / Database | Panggil fungsi ViewModel → Repository; Hasil dialirkan via **StateFlow** |
| **Koleksi Data Jaringan/DB** (Daftar artikel, profil user, riwayat transaksi) | Repository / Server | **`StateFlow`** di ViewModel |

---

## 3. Pola Standar Penanganan Input `TextField` yang Dianjurkan

### ⚠️ Mengapa Teks Input Tidak Boleh Memakai StateFlow?
Jika ketikan TextField diikat ke `StateFlow` yang dimutasi melalui Coroutine (`viewModelScope.launch`), emisi event asinkron akan memiliki latensi kecil (*delay* mikrodetik). Akibatnya:
- Kursor teks meloncat-loncat ke posisi awal atau akhir secara tak beraturan.
- Huruf yang diketik cepat bisa tertelan atau urutannya terbalik (*typing glitch*).

### ✅ Solusi Rekomendasi Resmi Google:
Gunakan `mutableStateOf` sinkron di ViewModel, lalu gunakan **`snapshotFlow`** jika ingin menggabungkannya ke dalam operator Flow:

```kotlin
class SearchViewModel : ViewModel() {
    // 1. Teks pencarian disimpan secara sinkron menggunakan mutableStateOf
    var query by mutableStateOf("")
        private set

    fun onQueryChange(newQuery: String) {
        query = newQuery // Langsung sinkron dengan ketikan tombol keyboard
    }

    // 2. Jika butuh mengubah nilai query menjadi Flow untuk filter reaktif:
    // val queryFlow = snapshotFlow { query }
}
```

---

## 4. Antipattern yang Wajib Dihindari

1. ❌ **Menyalin Isi Flow ke `mutableStateOf` Secara Manual**:
   ```kotlin
   // SALAH: Jangan lakukan ini!
   repository.data.collect { _myMutableState.value = it }
   ```
   *Gunakan langsung `StateFlow` atau operator `stateIn`.*
2. ❌ **Menyimpan State Ganda (*Duplicated State*)**:  
   Menyimpan salinan daftar yang sama di variabel UI lokal dan di ViewModel secara bersamaan.
3. ❌ **Menyimpan State Validasi Hanya di UI**:  
   Menyimpan teks yang butuh validasi server hanya di `remember` Composable lokal, sehingga ViewModel tidak dapat mereset atau memvalidasi isian saat proses simpan selesai.

---
[← Sebelumnya: Arsitektur MVVM & UDF Modern Android](./01-architecture-overview-and-udf.md) | [Berikutnya: Reactive UI State (combine + stateIn) →](./03-reactive-ui-state-combine-and-statein.md)
