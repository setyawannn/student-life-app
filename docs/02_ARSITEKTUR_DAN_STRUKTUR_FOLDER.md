# ARSITEKTUR SISTEM & STRUKTUR FOLDER (FILE MANAGEMENT)

Dokumen ini memuat panduan arsitektur perangkat lunak, standar struktur direktori (*file management*), serta konvensi pemrograman Kotlin dan Jetpack Compose untuk aplikasi **StudentLife**. Panduan ini dirancang khusus agar **5 pengembang dapat bekerja secara paralel dan kolaboratif tanpa mengalami *git merge conflict***.

---

## 1. Filosofi Arsitektur: *Feature-First Clean Architecture*

Aplikasi StudentLife mengadopsi prinsip **Modern Android Development (MAD)** yang direkomendasikan Google:
1. **Jetpack Compose** untuk deklaratif UI modern.
2. **Unidirectional Data Flow (UDF)**: Interaksi pengguna (*Events*) dikirim ke `ViewModel`, diproses dalam Coroutine, dan menghasilkan *State* baru yang diamati secara reaktif oleh Composable Screen melalui `StateFlow`.
3. **Repository Pattern**: Memisahkan logika antarmuka (*UI layer*) dari sumber data Supabase (*Data layer*).
4. **Feature-First (Package-by-Feature)**: Mengelompokkan kode berdasarkan domain fitur bisnis, bukan berdasarkan tipe file teknis.

```
┌────────────────────────────────────────────────────────┐
│                   UI LAYER (Compose)                   │
│   Screen Composable  ◄───  UiState (StateFlow)         │
│           │                                ▲           │
│           │ (User Events / Intent)         │           │
│           ▼                                │           │
│        ViewModel  ─────────────────────────┘           │
└───────────────────────────┬────────────────────────────┘
                            │ (Coroutine suspend call / Flow)
                            ▼
┌────────────────────────────────────────────────────────┐
│                   DATA LAYER (Supabase)                │
│                 Repository Interface                   │
│                          ▲                             │
│                          │                             │
│                 Repository Impl                        │
│                          │                             │
│       Supabase Client / Postgrest / Storage            │
└────────────────────────────────────────────────────────┘
```

### Mengapa Memilih *Package-by-Feature* untuk Kerja Tim?
* **Zero Merge Conflicts**: Setiap anggota memiliki direktori fiturnya masing-masing (`features/task`, `features/schedule`, dll.). Perubahan kode fitur satu anggota tidak akan menyentuh file milik anggota lain.
* **Independensi Pengerjaan**: Masing-masing anggota bertanggung jawab penuh atas satu folder fitur (`data`, `model`, `ui`, `navigation`).
* **Kemudahan Evaluasi & Asistensi**: Dosen atau asisten praktikum dapat langsung memeriksa folder anggota terkait untuk melihat kelengkapan List, Detail, Form, ViewModel, dan integrasi Supabase.

---

## 2. Struktur Direktori Proyek (*File Management*)

Berikut adalah pemetaan folder pada `app/src/main/java/com/example/student_life_app/`:

```text
com.example.student_life_app/
│
├── StudentLifeApp.kt                     // Application class (inisialisasi Supabase)
├── MainActivity.kt                       // Single Activity, container NavHost & AppNav
│
├── core/                                 // Komponen bersama (Shared Kernel)
│   ├── common/
│   │   ├── Resource.kt                   // Sealed class Success, Error, Loading
│   │   ├── Constants.kt                  // Konstanta global (Supabase URL & Anon Key)
│   │   └── DispatcherProvider.kt         // Coroutine Dispatchers abstraction
│   │
│   ├── database/
│   │   └── SupabaseClient.kt             // Singleton Supabase Client (Auth, Postgrest, Storage)
│   │
│   ├── designsystem/                     // Komponen UI pakai ulang
│   │   ├── components/
│   │   │   ├── AppButton.kt              // Tombol standar StudentLife
│   │   │   ├── AppTextField.kt           // Input text terstandarisasi
│   │   │   ├── EmptyState.kt             // Tampilan saat list kosong
│   │   │   ├── LoadingIndicator.kt       // Loading spinner/shimmer
│   │   │   └── TopAppBar.kt              // Reusable TopBar dengan tombol Back
│   │   └── theme/                        // Palet warna, tipografi, dan tema aplikasi
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   │
│   ├── navigation/                       // Infrastruktur Navigasi Global
│   │   ├── AppNavigation.kt              // Setup NavHost utama
│   │   ├── BottomNavBar.kt               // Komponen Bottom Navigation
│   │   └── ScreenRoute.kt                // Sealed class seluruh route & argumen navigasi
│   │
│   └── utils/                            // Helper & Extension Functions
│       ├── DateFormatter.kt              // Format tanggal & jam
│       ├── ImagePickerHelper.kt          // Helper galeri/kamera (untuk Fitur 3)
│       └── IntentHelper.kt               // Helper buka link browser/intent (untuk Fitur 5)
│
└── features/                             // FEATURE MODULES (Isolasi 5 Fitur Anggota Tim)
    │
    ├── auth/                             // Fondasi Bersama: Login, Registrasi, Sesi Akun
    │   ├── data/                         // Auth Repository & DataSource
    │   ├── service/                      // Validasi input email & password
    │   ├── controller/                   // AuthViewModel & AuthUiState
    │   ├── model/                        // UserSession domain model
    │   └── ui/                           // LoginScreen & RegisterScreen
    │
    ├── profile/                          // Fondasi Bersama: Profil Pengguna & Keluar Akun
    │   ├── data/                         // Profile Repository
    │   ├── service/                      // Validasi edit profil
    │   ├── controller/                   // ProfileViewModel & ProfileUiState
    │   ├── model/                        // UserProfile domain model
    │   └── ui/                           // ProfileScreen
    │
    ├── task/                             // FITUR 1: MANAJEMEN TUGAS KULIAH [Gangsar Wijayanto]
    │   ├── data/                         // [LAYER DATABASE & PERSISTENCE]
    │   │   ├── datasource/
    │   │   │   └── TaskRemoteDataSource.kt// Query langsung Postgrest
    │   │   ├── dto/
    │   │   │   └── TaskDto.kt            // Serialisasi data JSON Supabase
    │   │   └── repository/
    │   │       └── TaskRepository.kt     // Abstraksi repository data tugas
    │   ├── service/                      // [LAYER LOGIKA BISNIS & VALIDASI]
    │   │   └── TaskService.kt            // Validasi input form & kalkulasi deadline tugas
    │   ├── model/                        // [LAYER DOMAIN MODEL]
    │   │   ├── Task.kt                   // Domain entity Task murni
    │   │   └── TaskStatus.kt             // Enum: TODO, IN_PROGRESS, COMPLETED
    │   ├── controller/                   // [LAYER CONTROLLER / VIEWMODEL]
    │   │   └── TaskViewModel.kt          // StateFlow & orkestrator event tugas
    │   ├── ui/                           // [LAYER TAMPILAN / COMPOSE]
    │   │   ├── TaskListScreen.kt         // IMPLEMENTASI: LazyColumn
    │   │   └── components/
    │   └── utils/                        // [LAYER UTILITAS FITUR]
    │       └── TaskUtils.kt              // Helper parsing status
    │
    ├── schedule/                         // FITUR 2: MANAJEMEN JADWAL KULIAH [Muhammad Abdillah]
    │   ├── data/
    │   │   ├── datasource/
    │   │   │   └── ScheduleRemoteDataSource.kt
    │   │   ├── dto/
    │   │   │   └── ScheduleDto.kt
    │   │   └── repository/
    │   │       └── ScheduleRepository.kt
    │   ├── service/
    │   │   └── ScheduleService.kt        // Validasi jam kuliah, ruangan, nama dosen
    │   ├── model/
    │   │   ├── Schedule.kt
    │   │   └── DayOfWeekEnum.kt          // Enum hari kuliah: SENIN s.d. SABTU
    │   ├── controller/
    │   │   └── ScheduleViewModel.kt      // Pengelola tab hari aktif & list jadwal
    │   ├── ui/
    │   │   ├── ScheduleListScreen.kt     // IMPLEMENTASI: HorizontalPager + LazyColumn
    │   │   └── components/
    │   └── utils/
    │       └── ScheduleUtils.kt          // Helper konversi hari
    │
    ├── note/                             // FITUR 3: CATATAN BELAJAR + GAMBAR [Prayoga Adi Setyawan]
    │   ├── data/
    │   │   ├── datasource/
    │   │   │   └── NoteRemoteDataSource.kt // Query Postgrest & upload berkas Supabase Storage
    │   │   ├── dto/
    │   │   │   └── NoteDto.kt
    │   │   └── repository/
    │   │       └── NoteRepository.kt
    │   ├── service/
    │   │   └── NoteService.kt            // Validasi judul & isi catatan
    │   ├── model/
    │   │   └── Note.kt                   // Domain entity catatan (termasuk imageUrl)
    │   ├── controller/
    │   │   └── NoteViewModel.kt          // Pengelola state catatan & status upload foto
    │   ├── ui/
    │   │   ├── NoteListScreen.kt         // IMPLEMENTASI: LazyVerticalGrid (Kartu + Foto)
    │   │   └── components/
    │   └── utils/
    │       └── NoteUtils.kt              // Generator path nama berkas storage unik
    │
    ├── goal/                             // FITUR 4: TARGET BELAJAR & PROGRESS [Tubagus Fatahudin Mulkan]
    │   ├── data/
    │   │   ├── datasource/
    │   │   │   └── GoalRemoteDataSource.kt
    │   │   ├── dto/
    │   │   │   └── GoalDto.kt
    │   │   └── repository/
    │   │       └── GoalRepository.kt
    │   ├── service/
    │   │   └── GoalService.kt            // Validasi target & kalkulasi persentase float (0f - 1f)
    │   ├── model/
    │   │   └── StudyGoal.kt
    │   ├── controller/
    │   │   └── GoalViewModel.kt          // Pengelola pembaruan progres capaian
    │   ├── ui/
    │   │   ├── GoalListScreen.kt         // IMPLEMENTASI: LazyColumn + Progress Indicator
    │   │   └── components/
    │   └── utils/
    │       └── GoalUtils.kt              // Helper format persentase
    │
    └── reference/                        // FITUR 5: REFERENSI & LINK MATERI [Qoid Kafi]
        ├── data/
        │   ├── datasource/
        │   │   └── ReferenceRemoteDataSource.kt
        │   ├── dto/
        │   │   └── ReferenceDto.kt
        │   └── repository/
        │       └── ReferenceRepository.kt
        ├── service/
        │   └── ReferenceService.kt       // Validasi format URL (Regex) & nama matkul
        ├── model/
        │   ├── Reference.kt
        │   └── ReferenceCategory.kt      // Enum: JURNAL, DRIVE, VIDEO
        ├── controller/
        │   └── ReferenceViewModel.kt     // Pengelola filter tab kategori & toggle bintang favorit
        ├── ui/
        │   ├── ReferenceListScreen.kt    // IMPLEMENTASI: Tab Kategori + LazyRow Chip + LazyColumn
        │   └── components/
        └── utils/
            └── ReferenceUtils.kt         // Helper peluncur browser (ACTION_VIEW Intent)
    │           ├── NoteGridCard.kt       // Kartu grid catatan dengan pratinjau gambar
    │           └── ImageAttachmentBox.kt // Komponen pratinjau gambar saat input form
    │
    ├── goal/                             // [Fitur 4 - Tubagus Fatahudin Mulkan]
    │   ├── data/
    │   │   ├── GoalDto.kt
    │   │   └── GoalRepository.kt
    │   ├── model/
    │   │   └── StudyGoal.kt              // Domain Model target & progres
    │   ├── navigation/
    │   │   └── GoalNavigation.kt
    │   └── ui/
    │       ├── GoalListScreen.kt         // Menggunakan LazyColumn + ProgressBar
    │       ├── GoalDetailScreen.kt       // Detail target belajar & milestone
    │       ├── GoalFormScreen.kt         // Form Tambah/Edit target & set progres
    │       ├── GoalViewModel.kt          // ViewModel target belajar
    │       ├── GoalUiState.kt
    │       └── components/
    │           ├── GoalCard.kt           // Kartu target dengan progress indicator
    │           └── ProgressBarWidget.kt  // Visual bar progres persentase (0-100%)
    │
    └── reference/                        // [Fitur 5 - Qoid Kafi]
        ├── data/
        │   ├── ReferenceDto.kt
        │   └── ReferenceRepository.kt
        ├── model/
        │   ├── Reference.kt
        │   └── ReferenceCategory.kt      // Enum: JURNAL, DOKUMEN_DRIVE, VIDEO
        ├── navigation/
        │   └── ReferenceNavigation.kt
        └── ui/
            ├── ReferenceListScreen.kt    // HorizontalPager + LazyColumn + LazyRow
            ├── ReferenceDetailScreen.kt  // Detail, Buka Link (Implicit Intent), Favorit
            ├── ReferenceFormScreen.kt    // Form input referensi & validasi URL
            ├── ReferenceViewModel.kt     // ViewModel referensi & filter
            ├── ReferenceUiState.kt
            └── components/
                ├── ReferenceCard.kt      // Kartu tautan dengan tombol aksi cepat
                └── CourseFilterChips.kt  // LazyRow deretan chip filter mata kuliah
```

---

## 3. Standar Implementasi Kode Kotlin & Jetpack Compose

### A. Pola Response & State Umum (`Resource.kt`)
Untuk memastikan seluruh fitur menangani kondisi *loading*, *success*, dan *error* secara konsisten:

```kotlin
package com.example.student_life_app.core.common

sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}
```

### B. Pola UI State & ViewModel (Unidirectional Data Flow)
Setiap fitur wajib membuat satu data class *UiState* yang merepresentasikan seluruh keadaan layar:

```kotlin
// Contoh pada Fitur 1 (TaskUiState.kt)
package com.example.student_life_app.features.task.ui

import com.example.student_life_app.features.task.model.Task

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)
```

Implementasi ViewModel dengan `StateFlow` dan Coroutines:

```kotlin
// Contoh pada TaskViewModel.kt
package com.example.student_life_app.features.task.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student_life_app.core.common.Resource
import com.example.student_life_app.features.task.data.TaskRepository
import com.example.student_life_app.features.task.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            repository.getTasks().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    is Resource.Success -> _uiState.update { it.copy(isLoading = false, tasks = resource.data) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                }
            }
        }
    }
}
```

### C. Pola Composable Screen (State Hoisting & Separation of Concerns)
Pisahkan Composable yang terikat ViewModel (*Stateful*) dengan Composable murni (*Stateless*) agar mudah dites dan memiliki fitur `@Preview`:

```kotlin
@Composable
fun TaskListRoute(
    viewModel: TaskViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    TaskListScreen(
        uiState = uiState,
        onRefresh = { viewModel.loadTasks() },
        onTaskClick = onNavigateToDetail,
        onAddTaskClick = onNavigateToCreate
    )
}

@Composable
fun TaskListScreen(
    uiState: TaskListUiState,
    onRefresh: () -> Unit,
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(innerPadding))
            uiState.errorMessage != null -> ErrorState(uiState.errorMessage, onRetry = onRefresh)
            uiState.tasks.isEmpty() -> EmptyState(message = "Belum ada tugas kuliah.")
            else -> {
                LazyColumn(
                    modifier = modifier.padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = uiState.tasks, key = { it.id }) { task ->
                        TaskCard(task = task, onClick = { onTaskClick(task.id) })
                    }
                }
            }
        }
    }
}
```

---

## 4. Alur Kerja Tim & Panduan Git (Kolaborasi Tanpa Konflik)

Untuk menjaga integritas kode dan meminimalkan masalah penggabungan (*merge*), tim wajib mematuhi standar berikut:

### A. Strategi Percabangan (*Git Branching Model*)
* `main`: Branch stabil yang siap didemonstrasikan ke dosen / evaluator.
* `develop`: Branch integrasi bersama dari seluruh fitur.
* `feature/<nama-fitur>-<nama-anggota>`: Branch mandiri setiap anggota.
  * `feature/task-gangsar`
  * `feature/schedule-abdillah`
  * `feature/note-prayoga`
  * `feature/goal-tubagus`
  * `feature/reference-qoid`

### B. Aturan Commit (*Conventional Commits*)
Gunakan format pesan commit yang jelas dan seragam:
* `feat(task): implement task detail screen and status update`
* `feat(note): add image picker and supabase storage upload integration`
* `fix(schedule): resolve horizontal pager tab synchronization`
* `style(core): update color palette and theme typography`
* `docs: add supabase database schema documentation`

### C. Alur Penggabungan (*Pull Request & Merge*)
1. Setiap anggota melakukan *pull* dari branch `develop` terlebih dahulu sebelum membuat PR.
2. Pastikan proyek berhasil di-*compile* (`./gradlew assembleDebug`) tanpa error.
3. Buat Pull Request (PR) ke branch `develop` dan mintalah Ketua Tim (`Muhammad Abdillah`) untuk me-review.
4. Setelah branch `develop` diverifikasi dan semua 5 fitur berjalan normal, lakukan *merge* ke branch `main`.

---

## 5. Standar Pemisahan MVVM & ViewModel (*Zero Business Data in View*)

Untuk menjamin kode bersih (*clean code*) dan memudahkan pengujian maupun migrasi backend, seluruh anggota tim wajib mematuhi aturan pemisahan antarmuka dan data berikut:

### A. Aturan Wajib untuk Layer View (`ui/`)
1. **Dilarang keras mendefinisikan data bisnis atau dummy data secara lokal di dalam file Screen.**
   * ❌ *SALAH:* `val sampleNotes = listOf(...)` atau `val filterList = listOf(...)` didefinisikan langsung di dalam `@Composable fun NoteListScreen()`.
   * ✅ *BENAR:* Seluruh list data, filter options, dan stat counts dipasok oleh `ViewModel.uiState`.
2. **Dilarang mengelola state data bisnis dengan `remember { mutableStateOf(...) }` di dalam Screen.**
   * `searchQuery`, `selectedFilter`, dan status item wajib dikelola di `ViewModel`.
   * Layar Composable hanya mengamati state secara reaktif:
     ```kotlin
     val uiState by viewModel.uiState.collectAsStateWithLifecycle()
     ```
   * Event dari pengguna diteruskan langsung ke ViewModel:
     ```kotlin
     TextField(
         value = uiState.searchQuery,
         onValueChange = { viewModel.onSearchQueryChange(it) }
     )
     ```

### B. Aturan untuk Layer Controller / ViewModel (`controller/`)
1. Seluruh state layar dibungkus dalam satu data class `UiState` yang di-expose melalui `StateFlow<UiState>`.
2. ViewModel menggunakan dependensi abstraksi `Repository` interface dengan default instance `Fake<Feature>Repository()`:
   ```kotlin
   class TaskViewModel(
       private val repository: TaskRepository = FakeTaskRepository(),
       private val service: TaskService = TaskServiceImpl()
   ) : ViewModel()
   ```
3. Dengan pola default parameter ini, Composable Screen dapat memanggil `viewModel: TaskViewModel = viewModel()` secara langsung tanpa konfigurasi factory manual yang rumit.

### C. Aturan untuk Layer Data (`data/repository/`)
1. Setiap fitur memiliki `Repository` interface (misal `TaskRepository`, `NoteRepository`).
2. Selama fase pengembangan sebelum backend Supabase aktif (atau saat pengujian offline), sediakan kelas `Fake<Feature>Repository` yang mengimplementasikan interface tersebut dengan data akademik representatif.
3. Saat integrasi Supabase siap, pengembang hanya perlu membuat `Supabase<Feature>RepositoryImpl` dan menyuntikkannya ke ViewModel tanpa perlu mengubah sebaris pun kode di layer `ui/` maupun `controller/`.
