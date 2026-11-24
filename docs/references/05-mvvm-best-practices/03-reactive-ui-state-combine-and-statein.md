---
id: ref-mvvm-03
title: Pemodelan Reactive UI State dengan combine dan stateIn
module: Modul Panduan MVVM Modern
category: state-management
tags:
  - ui-state
  - combine
  - statein
  - whilesubscribed
  - collectasstatewithlifecycle
  - sealed-interface-vs-dataclass
summary: Pola penurunan UI State reaktif menggunakan combine dan stateIn(WhileSubscribed(5000)) untuk efisiensi siklus hidup dan pencegahan duplikasi data.
related:
  - docs/references/05-mvvm-best-practices/02-state-management-stateflow-vs-mutablestate.md
  - docs/references/05-mvvm-best-practices/04-remote-data-retrofit-and-serialization.md
  - docs/references/05-mvvm-best-practices/05-practical-labs-counter-mahasiswa-posts.md
---

# 🔗 Pemodelan Reactive UI State dengan `combine` dan `stateIn`

[← Sebelumnya: StateFlow vs mutableStateOf](./02-state-management-stateflow-vs-mutablestate.md) | [Berikutnya: Integrasi REST API (Retrofit 3) →](./04-remote-data-retrofit-and-serialization.md)

---

## 1. Pemodelan UI State: `data class` vs `sealed interface`

Standar industri modern mewajibkan **Satu Objek UI State per Layar** (*Single UI State per Screen*). Namun, bagaimana memilih bentuk pemodelannya?

```
                    Apakah berbagai kondisi bisa terjadi bersamaan?
                     (Contoh: Tampilkan daftar data DAN indikator simpan)
                                         │
                    ┌────────────────────┴────────────────────┐
                    ▼                                         ▼
                   YA                                       TIDAK
       Gunakan Data Class                      Gunakan Sealed Interface
     (isLoading, isSaving,                     (Hanya salah satu kondisi:
       data, errorMessage)                      Loading, Success, atau Error)
```

| Tipe Pemodelan | Situasi Penggunaan | Contoh di Modul |
| :--- | :--- | :--- |
| **`data class`** | Data yang ada tetap harus tampil di layar meskipun ada proses loading parsial atau error pada satu aksi (misal gagal tambah data). | Daftar Mahasiswa, Feed Artikel |
| **`sealed interface`** | Layar memiliki fase eksklusif: sebelum data datang layar kosong (*loading fullscreen*), sukses menampilkan layar, atau gagal total. | Halaman Detail Artikel, Splash Screen |

---

## 2. Masalah Koleksi Manual di Blok `init` (Praktik 2)

Pada pendekatan pemula, data dari Repository sering disalin secara manual di dalam blok `init` ViewModel:

```kotlin
// PENDEKATAN LAMA (Manual Collect di init):
init {
    viewModelScope.launch {
        repository.mahasiswa.collect { daftar ->
            _uiState.update { it.copy(daftar = daftar) }
        }
    }
}
```

### Kelemahan Pendekatan Ini:
1. **Boros Daya & Memori di Background**: Coroutine `collect` berjalan terus-menerus selama ViewModel aktif, meskipun aplikasi berada di background dan UI tidak terlihat.
2. **Duplikasi Data (*State Synchronization Debt*)**: Data yang sama tersimpan ganda (di memori Repository dan di memori `_uiState` ViewModel). Semakin banyak sumber data, semakin rawan terjadi inkonsistensi (*out-of-sync*).

---

## 3. Solusi Modern: Operator `combine` + `stateIn` (Praktik 3)

Dengan `combine`, UI State **tidak lagi disalin secara manual**, melainkan **diturunkan secara otomatis (*derived state*)** dari sumber data murni.

```kotlin
// 1. Status internal khusus ViewModel (loading, saving, error)
private data class Status(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class MahasiswaViewModel(
    private val repository: MahasiswaRepository = MahasiswaRepository()
) : ViewModel() {

    private val _status = MutableStateFlow(Status())

    // 2. UI State Final diturunkan secara reaktif via combine + stateIn
    val uiState: StateFlow<MahasiswaUiState> = combine(
        repository.mahasiswa, // Sumber 1: Data dari Repository
        _status               // Sumber 2: Status loading/error ViewModel
    ) { daftar, status ->
        MahasiswaUiState(
            daftar = daftar,
            isLoading = status.isLoading,
            isSaving = status.isSaving,
            errorMessage = status.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MahasiswaUiState(isLoading = true)
    )
}
```

---

## 4. Mengapa Memakai `SharingStarted.WhileSubscribed(5_000)`?

Parameter ini adalah **standar resmi rekomendasi tim Google Android**:
1. **Otomatis Berhenti Saat di Latar Belakang**: Jika pengguna menekan tombol Home atau berpindah aplikasi, UI tidak lagi mengamati Flow. Setelah **5 detik**, aliran upstream akan otomatis dihentikan sementara, menghemat baterai dan siklus CPU.
2. **Tahan Terhadap Rotasi Layar (Timeout 5 Detik)**: Saat layar diputar, Activity dihancurkan dan dibuat ulang dalam waktu kurang dari 500 milidetik. Berkat toleransi buffer 5.000 ms (5 detik), koneksi upstream **TIDAK dibatalkan dan tidak perlu memuat ulang data dari awal**!

---

## 5. Konsumsi di UI: `collectAsStateWithLifecycle()`

Pada Composable Screen, **selalu gunakan** `collectAsStateWithLifecycle()` alih-alih `collectAsState()` biasa:

```kotlin
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MahasiswaScreen(viewModel: MahasiswaViewModel = viewModel()) {
    // Berhenti mengamati StateFlow secara otomatis saat aplikasi minimised di background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    MahasiswaContent(state = uiState, ...)
}
```

---
[← Sebelumnya: StateFlow vs mutableStateOf](./02-state-management-stateflow-vs-mutablestate.md) | [Berikutnya: Integrasi REST API (Retrofit 3) →](./04-remote-data-retrofit-and-serialization.md)
