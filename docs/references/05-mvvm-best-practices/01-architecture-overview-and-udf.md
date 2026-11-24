---
id: ref-mvvm-01
title: Arsitektur MVVM & Unidirectional Data Flow (UDF) Modern Android
module: Modul Panduan MVVM Modern
category: architecture
tags:
  - mvvm
  - architecture
  - unidirectional-data-flow
  - clean-architecture
  - four-layers
summary: Panduan arsitektur modern Android berbasis MVVM 4 lapisan (View, ViewModel, Repository, Data Source) dan prinsip Unidirectional Data Flow (UDF).
related:
  - docs/references/05-mvvm-best-practices/02-state-management-stateflow-vs-mutablestate.md
  - docs/references/05-mvvm-best-practices/03-reactive-ui-state-combine-and-statein.md
  - docs/references/05-mvvm-best-practices/05-practical-labs-counter-mahasiswa-posts.md
---

# 🏛️ Arsitektur MVVM & Unidirectional Data Flow (UDF) Modern Android

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: StateFlow vs mutableStateOf →](./02-state-management-stateflow-vs-mutablestate.md)

---

## 1. Mengapa Aplikasi Android Butuh Arsitektur?

Tanpa arsitektur yang terstruktur, pengembang cenderung menempatkan semua kode (rendering UI, logika bisnis, validasi form, hingga pemanggilan API jaringan) di dalam satu file `MainActivity.kt`. Hal ini berujung pada antipattern **"God Object"** dengan dampak negatif:
- **Kode Sulit Dibaca & Dirawat**: Ribuan baris kode bercampur aduk.
- **Sulit Diuji (*Untestable*)**: Logika bisnis tidak dapat diuji via Unit Test tanpa menjalankan emulator Android.
- **Kehilangan State**: Data musnah setiap kali pengguna memutar layar (*orientation change*) karena Activity dihancurkan ulang.

Pola **Model-View-ViewModel (MVVM)** memecah tanggung jawab ke dalam lapisan-lapisan yang independen.

---

## 2. Struktur Arsitektur 4 Lapisan (*Four-Tier Architecture*)

```
┌────────────────────────────────────────────────────────┐
│                   VIEW (@Composable)                   │
│   - Menampilkan status UI visual                       │
│   - Menerima interaksi pengguna & mengirim Event       │
└───────────────────────────┬────────────────────────────┘
        ▲                   │
        │ State (StateFlow) │ Event (Panggilan Fungsi)
        │                   ▼
┌────────────────────────────────────────────────────────┐
│               VIEWMODEL (State Holder)                 │
│   - Menyimpan state UI & bertahan dari rotasi layar   │
│   - Memproses logika presentasi                        │
│   - Mengelola Coroutines via viewModelScope            │
└───────────────────────────┬────────────────────────────┘
        ▲                   │
        │ Data / Flow       │ Memanggil suspend fun
        │                   ▼
┌────────────────────────────────────────────────────────┐
│               REPOSITORY (Pintu Data)                  │
│   - Satu-satunya sumber kebenaran data aplikasi        │
│   - Mengabstraksi sumber data (Dummy / REST / Database)│
└───────────────────────────┬────────────────────────────┘
        ▲                   │
        │ Raw Response      │ Query / Request
        │                   ▼
┌────────────────────────────────────────────────────────┐
│               DATA SOURCE (Sumber Data)                │
│   - REST API (Retrofit) / Database Lokal (Room) / List │
└────────────────────────────────────────────────────────┘
```

### Tanggung Jawab & Batasan Setiap Lapisan

| Komponen | Implementasi di Android | Tanggung Jawab Utama | **DILARANG KERAS** |
| :--- | :--- | :--- | :--- |
| **Model / Data** | `data class`, Room Entity, DTO | Menyimpan struktur data mentah | Mengetahui kode UI atau Composable |
| **Repository** | Kelas `Repository` | Mengambil, memvalidasi, & mengolah data | Menyimpan referensi ViewModel atau UI |
| **ViewModel** | Subclass `androidx.lifecycle.ViewModel` | Menyimpan UI State, menerima event, memanggil Repository | **Menyimpan `Context`, `Activity`, atau `@Composable`** |
| **View** | Fungsi `@Composable` (Screen & Content) | Menggambar antarmuka, meneruskan input user ke ViewModel | **Mengambil data jaringan langsung atau memproses logika bisnis** |

---

## 3. Alur Data Satu Arah (*Unidirectional Data Flow / UDF*)

Prinsip dasar UDF menjamin aliran data terjadi secara linear dan terkontrol:

1. **State Mengalir Turun (*State Down*)**: ViewModel mengekspos state yang tidak dapat diubah (*immutable*). View membaca state tersebut dan menggambar ulang UI (*recomposition*).
2. **Event Mengalir Naik (*Event Up*)**: Pengguna mengklik tombol atau mengetik teks -> View memicu fungsi di ViewModel sebagai aksi (*event*).
3. **Pembaruan Terpusat**: ViewModel memproses event (memanggil Repository jika butuh data baru) lalu memperbarui state.
4. **Prediktibilitas Tinggi**: Karena state hanya bermutasi di satu tempat (ViewModel), aplikasi menjadi sangat mudah di-debug dan bebas dari kondisi balapan (*race condition*).

---

## 4. Pemisahan Layar: Pattern `Screen` vs `Content`

Salah satu penerapan terbaik State Hoisting pada arsitektur MVVM adalah memisahkan satu layar menjadi dua fungsi Composable:

```kotlin
// 1. SCREEN: Composable yang memegang ViewModel (Stateful)
@Composable
fun MahasiswaScreen(
    modifier: Modifier = Modifier,
    viewModel: MahasiswaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MahasiswaContent(
        state = uiState,
        nama = viewModel.nama,
        onNamaChange = viewModel::onNamaChange,
        onTambah = viewModel::tambah,
        modifier = modifier
    )
}

// 2. CONTENT: Composable murni visual (Stateless) -> Mudah di-Preview & di-Test!
@Composable
fun MahasiswaContent(
    state: MahasiswaUiState,
    nama: String,
    onNamaChange: (String) -> Unit,
    onTambah: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Elemen visual murni tanpa ketergantungan pada ViewModel
    }
}
```

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: StateFlow vs mutableStateOf →](./02-state-management-stateflow-vs-mutablestate.md)
