# 🏛️ ARSITEKTUR SISTEM & STANDAR PENGEMBANGAN — STUDENTLIFE

Dokumen ini merupakan panduan arsitektur teknis definitif (*Single Source of Truth*) untuk seluruh pengembang aplikasi **StudentLife**. Dokumen ini merinci identifikasi *tech stack*, tata kelola berkas (*file management*), mekanisme penyimpanan media/gambar, konvensi penamaan, prinsip DRY (*Don't Repeat Yourself*), sistem komponen pakai ulang (*reusable design system*), serta panduan *styling* berbasis Material 3.

---

## 1. Identifikasi Teknologi & Tech Stack Proyek

| Kategori | Teknologi / Library | Versi | Peran & Alasan Pemilihan |
|:---|:---|:---:|:---|
| **Platform Target** | Android OS | Min SDK 24 / Target SDK 35 | Kompatibel dengan 95%+ perangkat Android aktif modern. |
| **Bahasa Utama** | Kotlin | `2.2.10` | Bahasa resmi Google untuk Android; mendukung konkurensi Coroutines, Flow, dan type safety penuh. |
| **UI Toolkit** | Jetpack Compose (BOM) | `2026.02.01` | Deklaratif UI modern, menghilangkan XML layout, reaktif, dan performa render tinggi. |
| **Design System** | Material 3 (Material You) | Terintegrasi BOM | Standar desain Google terkini; mendukung Dynamic Color dan dark mode. |
| **Arsitektur Inti** | Feature-First Clean Architecture + MVVM/UDF | - | Isolasi fitur total untuk kolaborasi 5 pengembang tanpa konflik Git. |
| **State Management** | Android Lifecycle ViewModel + StateFlow | `2.8.7` | Manajemen state yang *lifecycle-aware*, bertahan saat orientasi layar berubah (*configuration change*). |
| **Asinkron & Aliran Data** | Kotlin Coroutines & Flow | `1.9.0` | Menangani operasi I/O dan jaringan di thread latar belakang (*background thread*) secara non-blocking. |
| **Navigasi** | Jetpack Navigation Compose | `2.8.5` | Navigasi deklaratif berbasis rute (*route-based*) antar halaman (*multi-screen*). |
| **Backend & Database** | Supabase (Postgrest-kt, Auth-kt) | `3.0.3` | Basis data PostgreSQL *real-time cloud* dengan Row Level Security (RLS). |
| **Penyimpanan Media** | Supabase Storage (`storage-kt`) | `3.0.3` | Object storage untuk mengunggah dan melayani berkas foto catatan kuliah. |
| **HTTP Client Engine** | Ktor Client Android | `3.0.3` | Engine jaringan resmi berbobot ringan untuk konektivitas Supabase-kt. |
| **Pemuat Gambar (Image Loader)**| Coil Compose | `2.7.0` | Library pemuat gambar berbasis Kotlin Coroutines; mendukung memory & disk caching. |
| **Serialisasi Data** | `kotlinx.serialization` (JSON) | `1.7.3` | Serializer resmi Kotlin, *reflection-free*, cepat, dan hemat memori. |
| **Build Tooling** | Gradle & Android Gradle Plugin (AGP) | Gradle 9.5 / AGP 9.3.3 | Version Catalog (`libs.versions.toml`) tersentralisasi. |

---

## 2. Pola Arsitektur: *Feature-Packaged Layered Clean Architecture*

Aplikasi StudentLife mengadopsi standar pemisahan kode berlapis (**Database**, **Service**, **Controller**, **UI**, dan **Utils**) yang dikemas ke dalam masing-masing modul fitur (**Feature-Packaged Layering**). Pendekatan ini menggabungkan pemisahan tanggung jawab (*Separation of Concerns*) tingkat enterprise dengan proteksi isolasi Git antar anggota tim.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION / UI LAYER                         │
│                                                                        │
│   ┌──────────────────────────┐         ┌───────────────────────────┐   │
│   │   Stateless Screen UI    │         │      Stateful Route       │   │
│   │   (LazyColumn, Grid,     │ ◄─────  │  (NavHost integration,    │   │
│   │    Pager, Reusable Card) │         │   ViewModel injection)    │   │
│   └─────────────┬────────────┘         └─────────────▲─────────────┘   │
│                 │                                    │                 │
│                 │ (1. User Actions / Callbacks)      │ (StateFlow)     │
│                 ▼                                    │                 │
│   ┌──────────────────────────────────────────────────┴─────────────┐   │
│   │                 CONTROLLER LAYER (ViewModel)                   │   │
│   │     (State Hoisting, Coroutines, UiState update, Flow)         │   │
│   └──────────────────────────────────┬─────────────────────────────┘   │
└──────────────────────────────────────┼─────────────────────────────────┘
                                       │ (2. Delegasi Logika Bisnis & Validasi)
                                       ▼
┌────────────────────────────────────────────────────────────────────────┐
│                 SERVICE / DOMAIN LAYER (Business Rules)                │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │           Service Interface & Implementation                   │   │
│   │     (Validasi Input, Kalkulasi Progress, Pengurutan Data)      │   │
│   └─────────────────┬───────────────────────────────▲──────────────┘   │
│                     │                               │                  │
│                     ▼ (3. Permintaan Mutasi Data)   │ (Domain Models)  │
│   ┌─────────────────────────────────────────────────┴──────────────┐   │
│   │                   DOMAIN MODEL & ENUMS                         │   │
│   │            (Task, Schedule, Note, StudyGoal, Reference)        │   │
│   └──────────────────────────────────▲─────────────────────────────┘   │
└──────────────────────────────────────┼─────────────────────────────────┘
                                       │ (4. Repository Interface Flow)
                                       ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   DATABASE & DATA LAYER (Persistence)                  │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │                      Repository Implementation                 │   │
│   │          (DTO to Domain Mapping, Resource<T> Handling)         │   │
│   └──────────────────────────────────┬─────────────────────────────┘   │
│                                      │                                 │
│                                      ▼                                 │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │                    Remote DataSource                           │   │
│   │        (Direct Queries Postgrest, Storage SDK & JSON DTO)      │   │
│   └─────────────────┬───────────────────────────────┬──────────────┘   │
│                     │                               │                  │
│                     ▼                               ▼                  │
│   ┌─────────────────────────────────┐   ┌──────────────────────────┐   │
│   │    Supabase Postgrest Client    │   │  Supabase Storage Client │   │
│   │      (Database PostgreSQL)      │   │    (Media Bucket Upload) │   │
│   └─────────────────────────────────┘   └──────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘
                                       ▲
                                       │
┌──────────────────────────────────────┴─────────────────────────────────┐
│                      UTILS & HELPER LAYER                              │
│       (DateFormatter, ImageCompressor, ReferenceUtils, TaskUtils)      │
└────────────────────────────────────────────────────────────────────────┘
```

### Mengapa Memilih *Feature-Packaged Layering* (Opsi 1)?
1. **Pemisahan Tanggung Jawab Nyata:** Kode teknis database (`datasource`, `dto`), logika bisnis (`service`), orkestrator antarmuka (`controller`), tampilan murni (`ui`), dan pembantu (`utils`) tidak bercampur dalam satu file atau satu folder.
2. **Zero Merge Conflicts:** Masing-masing dari 5 pengembang mengelola layer fiturnya sendiri di dalam folder `features/<nama_fitur>/`. Tidak ada developer yang saling mengunci file di Git.
3. **Kemudahan Pengujian (Testability):** Layer `service` dan `repository` bebas dari ketergantungan UI sehingga dapat diuji (*Unit Testing*) secara terisolasi dengan sangat cepat.

---

## 3. Detail Tata Kelola Berkas (*File Management & Folder Structure*)

Berikut adalah pemetaan pohon direktori lengkap berbasis **Feature-Packaged Layering**:

```text
app/src/main/java/com/example/student_life_app/
│
├── StudentLifeApp.kt                     // Application class (Inisialisasi Supabase & logging)
├── MainActivity.kt                       // Single Activity, container MainAppContainer & Theme
│
├── core/                                 // SHARED KERNEL (Komponen & Utilitas Lintas Fitur)
│   ├── common/
│   │   ├── Resource.kt                   // Sealed interface Result (Loading, Success, Error)
│   │   ├── Constants.kt                  // Konstanta global aplikasi
│   │   └── DispatcherProvider.kt         // Provider coroutine dispatchers (IO, Default, Main)
│   │
│   ├── database/
│   │   └── SupabaseClient.kt             // Singleton instance Supabase Client (Auth, Postgrest, Storage)
│   │
│   ├── designsystem/                     // REUSABLE UI SYSTEM (Prinsip DRY)
│   │   ├── components/
│   │   │   ├── AppButton.kt              // Tombol utama dan outline Material 3
│   │   │   ├── AppTextField.kt           // Input text seragam dengan error state & label
│   │   │   ├── AppTopBar.kt              // TopAppBar standar dengan tombol navigasi Back
│   │   │   ├── EmptyStateView.kt         // Tampilan saat list data kosong (ikon + pesan + aksi)
│   │   │   ├── ConfirmDialog.kt          // Dialog konfirmasi universal (Hapus / Ubah)
│   │   │   └── StatusBadge.kt            // Chip badge status semantik (TODO, COMPLETED, dll.)
│   │   └── theme/
│   │       ├── Color.kt                  // Definisi palet warna resmi Figma StudentLife UI
│   │       ├── Theme.kt                  // Konfigurasi tema Light & Dark Mode
│   │       └── Type.kt                   // Standar tipografi teks Inter
│   │
│   ├── navigation/
│   │   ├── AppNavigation.kt              // NavHost pusat pengatur seluruh navigasi aplikasi
│   │   ├── BottomNavBar.kt               // Komponen Bottom Navigation Bar 5 menu (Tugas, Jadwal, Catatan, Target, Referensi)
│   │   └── ScreenRoute.kt                // Sealed class seluruh route dan argumen navigasi
│   │
│   └── utils/
│       ├── DateFormatter.kt              // Utilitas konversi tanggal & waktu lokal Indonesia
│       ├── ImageCompressor.kt            // Utilitas kompresi foto klien sebelum diunggah (Anti-OOM)
│       └── IntentHelper.kt               // Utilitas peluncur Android Implicit Intent
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
    │   │   │   └── TaskRemoteDataSource.kt// Query langsung Postgrest (select, insert, update, delete)
    │   │   ├── dto/
    │   │   │   └── TaskDto.kt            // Serialisasi data JSON Supabase
    │   │   └── repository/
    │   │       └── TaskRepository.kt     // Interface & implementasi repositori data tugas
    │   ├── service/                      // [LAYER LOGIKA BISNIS & VALIDASI]
    │   │   └── TaskService.kt            // Validasi input form & pengurutan deadline tugas
    │   ├── model/                        // [LAYER DOMAIN MODEL]
    │   │   ├── Task.kt                   // Data class Task murni
    │   │   └── TaskStatus.kt             // Enum: TODO, IN_PROGRESS, COMPLETED
    │   ├── controller/                   // [LAYER CONTROLLER / VIEWMODEL]
    │   │   └── TaskViewModel.kt          // StateFlow & orkestrator event tugas
    │   ├── ui/                           // [LAYER TAMPILAN / COMPOSE]
    │   │   ├── TaskListScreen.kt         // IMPLEMENTASI: LazyColumn
    │   │   └── components/               // Kartu custom tugas
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
```

---

## 4. Mekanisme Penyimpanan Media & Gambar (*Image Storage Architecture*)

Fitur 3 (Catatan Belajar) wajib menerapkan unggah gambar dan menampilkan gambar kembali. Alur teknis penyimpanan media dirancang agar hemat bandwidth, anti-OOM (*Out Of Memory*), dan aman:

```
[Pengguna] ──► Pilih Gambar (Galeri / Kamera)
                     │
                     ▼
             [ImageCompressor.kt] 
             (Kompresi Bitmap: Max 1280px, WebP/JPEG 80% Quality)
                     │
                     ▼
             [Supabase Storage Client] 
             (Upload ke Bucket 'note_attachments' di folder '{userId}/')
                     │
                     ▼ Mendapatkan Public URL
             [Supabase Postgrest (Tabel notes)] 
             (Simpan URL teks ke kolom 'image_url')
                     │
                     ▼
             [Coil AsyncImage pada LazyVerticalGrid / Detail]
             (Otomatis Cache Memori & Cache Disk)
```

### A. Kompresi Gambar Klien Sebelum Unggah (`ImageCompressor.kt`)
Kamera Android modern menghasilkan foto berukuran 5MB - 20MB. Mengunggah berkas mentah akan menyebabkan koneksi lambat dan boros kuota penyimpanan. Wajib dikompresi di sisi klien:

```kotlin
object ImageCompressor {
    fun compressImage(context: Context, imageUri: Uri, maxDimension: Int = 1280, quality: Int = 80): ByteArray {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        
        // Skala proporsional
        val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        if (originalBitmap.width > originalBitmap.height) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }
        
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
}
```

### B. Konvensi Naming Path File di Supabase Storage
Format nama berkas di Supabase Storage bucket `note_attachments`:
$$\text{Path} = \texttt{\{userId\}/notes/\{UUID.randomUUID()\}.jpg}$$
* **Mengapa menyertakan `userId` di awal?** Memudahkan penerapan kebijakan RLS Supabase Storage agar pengguna hanya dapat memodifikasi berkas dalam foldernya sendiri.

### C. Standard Komponen Penampil Gambar (Coil `AsyncImage`)
Selalu sediakan *placeholder* saat memuat gambar dan gambar *fallback* jika koneksi terputus:

```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(note.imageUrl)
        .crossfade(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build(),
    contentDescription = "Lampiran gambar catatan ${note.title}",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)
        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
    placeholder = painterResource(R.drawable.img_placeholder_shimmer),
    error = painterResource(R.drawable.ic_broken_image)
)
```

---

## 5. Standarisasi Penamaan (*Naming Conventions*)

Keseragaman penamaan wajib ditaati oleh seluruh anggota tim:

### A. Kode Kotlin & Jetpack Compose
| Entitas | Format Penamaan | Contoh Benar | Contoh Salah (Dilarang) |
|:---|:---|:---|:---|
| **Paket / Package** | `lowercase`, tanpa pemisah | `features.task.ui` | `features.task_management.UI` |
| **Kelas / Interface** | `PascalCase` | `TaskRepository`, `GoalViewModel` | `task_repository`, `goalVM` |
| **DTO Data Class** | `<Entity>Dto` (`PascalCase`) | `TaskDto`, `ScheduleDto` | `TaskData`, `ScheduleResponse` |
| **Domain Model** | `<Entity>` (`PascalCase`) | `Task`, `Schedule`, `Note` | `TaskModel`, `MTask` |
| **Screen Composable** | `<Feature><Action>Screen` | `TaskListScreen`, `NoteFormScreen` | `ViewTasks`, `FormNoteActivity` |
| **Route Composable** | `<Feature><Action>Route` | `TaskListRoute`, `NoteFormRoute` | `TaskRouteScreen` |
| **Custom Component** | `<NamaKomponen>` (`PascalCase` kata benda) | `TaskCard`, `StatusBadge`, `DayTabBar` | `renderTaskCard`, `drawBadge` |
| **StateFlow Backing Field** | `_uiState` (private) & `uiState` (public) | `private val _uiState = MutableStateFlow(...)`<br>`val uiState = _uiState.asStateFlow()` | `val state`, `val myUiState` |
| **Fungsi Event UI** | `on<Action>` (camelCase) | `onTaskClick`, `onDeleteConfirm`, `onFilterChanged` | `handleTaskClick`, `clickTask` |
| **Fungsi ViewModel** | Kata kerja deskriptif (`camelCase`) | `loadTasks()`, `createTask()`, `updateProgress()` | `tasks()`, `doTask()` |

### B. Basis Data Supabase (PostgreSQL)
* **Nama Tabel:** Huruf kecil jamak (*snake_case*): `tasks`, `schedules`, `notes`, `study_goals`, `references`, `profiles`.
* **Nama Kolom:** Huruf kecil (*snake_case*): `user_id`, `course_name`, `due_date`, `is_favorite`, `created_at`.
* **Primary Key:** Kolom `id` bertipe `UUID` dengan default `uuid_generate_v4()`.
* **Foreign Key:** `<nama_tabel_singular>_id` (contoh: `user_id`).

### C. Version Control (Git)
* **Branch Fitur:** `feature/<nama-fitur>-<nama-anggota>` (contoh: `feature/task-gangsar`, `feature/schedule-abdillah`).
* **Format Commit (Conventional Commits):**
  - `feat(task): implement status change dropdown on detail screen`
  - `fix(note): handle empty image uri on note form submission`
  - `style(designsystem): update primary color token to indigo-600`
  - `refactor(schedule): extract DayTabBar into reusable component`
  - `docs: update database schema documentation`

---

## 6. Prinsip DRY (*Don't Repeat Yourself*) & Reusable Components

Dilarang membuat ulang komponen dasar di dalam folder fitur jika sudah ada di dalam `core/designsystem/components/`.

```
                        core/designsystem/components/
                                    │
    ┌──────────────┬────────────────┼──────────────┬──────────────┐
    ▼              ▼                ▼              ▼              ▼
[AppTopBar]   [AppButton]     [AppTextField]  [EmptyStateView] [StatusBadge]
    │              │                │              │              │
    └──────────────┴────────────────┴──────────────┴──────────────┘
                                    │
               Digunakan Bersama oleh 5 Fitur Anggota:
      (features/task, schedule, note, goal, reference, auth, profile)
```

### Standar Komponen Inti Pakai Ulang:

1. **`AppTopBar.kt`:**
   Menjamin konsistensi judul halaman, warna latar belakang, dan aksi tombol navigasi kembali (*back arrow*):
   ```kotlin
   @OptIn(ExperimentalMaterial3Api::class)
   @Composable
   fun AppTopBar(
       title: String,
       showBackButton: Boolean = false,
       onBackClick: () -> Unit = {},
       actions: @Composable RowScope.() -> Unit = {}
   ) {
       CenterAlignedTopAppBar(
           title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
           navigationIcon = {
               if (showBackButton) {
                   IconButton(onClick = onBackClick) {
                       Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                   }
               }
           },
           actions = actions
       )
   }
   ```

2. **`EmptyStateView.kt`:**
   Menghindari tampilan layar kosong tanpa petunjuk ketika data list mahasiswa masih kosong:
   ```kotlin
   @Composable
   fun EmptyStateView(
       icon: ImageVector,
       title: String,
       description: String,
       actionButtonText: String? = null,
       onActionClick: () -> Unit = {},
       modifier: Modifier = Modifier
   ) {
       Column(
           modifier = modifier.fillMaxSize().padding(24.dp),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Icon(icon, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.outline)
           Spacer(modifier = Modifier.height(16.dp))
           Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
           Spacer(modifier = Modifier.height(8.dp))
           Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
           if (actionButtonText != null) {
               Spacer(modifier = Modifier.height(20.dp))
               AppButton(text = actionButtonText, onClick = onActionClick)
           }
       }
   }
   ```

3. **`ConfirmDialog.kt`:**
   Dialog konfirmasi seragam sebelum pengguna menghapus data:
   ```kotlin
   @Composable
   fun ConfirmDialog(
       show: Boolean,
       title: String = "Konfirmasi Hapus",
       message: String = "Apakah Anda yakin ingin menghapus data ini?",
       confirmText: String = "Hapus",
       dismissText: String = "Batal",
       onConfirm: () -> Unit,
       onDismiss: () -> Unit
   ) {
       if (show) {
           AlertDialog(
               onDismissRequest = onDismiss,
               title = { Text(title) },
               text = { Text(message) },
               confirmButton = {
                   TextButton(onClick = onConfirm) {
                       Text(confirmText, color = MaterialTheme.colorScheme.error)
                   }
               },
               dismissButton = {
                   TextButton(onClick = onDismiss) { Text(dismissText) }
               }
           )
       }
   }
   ```

---

## 7. Sistem Desain, Styling, & UI Guidelines (Material 3)

### A. Palet Warna Resmi (Extracted directly from Figma: StudentLife UI)
Aplikasi menggunakan sistem warna semantik resmi yang diambil langsung dari desain Figma **StudentLife UI**:

* **Primary Brand (`#004AC6` / `Royal Blue`):** Warna identitas utama aplikasi, tombol aksi primer, Floating Action Button (FAB), dan ikon navigasi aktif.
* **Primary Container (`#DBE1FF` / `Soft Ice Blue`):** Latar belakang tombol ringan, badge info, dan aksen navigasi.
* **Screen Canvas Background (`#FAF8FF`):** Warna latar belakang utama seluruh layar aplikasi (*clean, modern, distraction-free*).
* **Surface Card (`#FFFFFF`):** Latar belakang kartu item tugas, jadwal, catatan, target, dan referensi.
* **Surface Variant (`#EFF6FF`):** Latar kartu informatif, banner catatan, atau seksi form.
* **Text Primary (`#131B2E` / `Deep Navy Black`):** Teks judul layar, nama mata kuliah, dan heading utama dengan tingkat kontras tinggi.
* **Text Secondary (`#434655`):** Teks deskripsi, subtitle, dan label form input.
* **Text Muted / Caption (`#64748B` / `Slate`):** Informasi tanggal, jam, catatan kecil, dan metadata sekunder.
* **Border & Outline (`#C3C6D7` / `#E2E8F0`):** Garis batas kartu, outline input field, dan garis pembatas (*divider*).

#### Status Semantik (Badge Tugas, Target, & Prioritas):
* **Status Success (`#00714D` / Container `#6FFBBE`):** Status tugas "Selesai / COMPLETED", target capaian 100%.
* **Status Warning (`#784B00` / Container `#FFDDB8`):** Status tugas "Sedang Dikerjakan / IN_PROGRESS", target aktif.
* **Status Error / Danger (`#BA1A1A` / Container `#FFDAD6`):** Status tugas "Belum Dikerjakan / TODO", tenggat waktu terlewat (*overdue*), tombol aksi hapus data.
* **Status Info (`#004AC6` / Container `#EAEDFF`):** Kategori referensi materi (Jurnal, Drive, Video).

### B. Skala Spasi Grid 8-Point (*Spacing System*)
Dilarang menggunakan nilai pixel atau padding acak (misal: `13.dp`, `19.dp`). Wajib mematuhi skala kelipatan 4 & 8:
* `Extra Small (4.dp)`: Jarak antar teks dan ikon kecil.
* `Small (8.dp)`: Spasi antar chip filter, jarak vertikal antar teks judul dan subtitle.
* `Medium (12.dp)`: Spasi antar kartu di dalam `LazyColumn` / `LazyVerticalGrid`.
* `Default (16.dp)`: Padding standar batas kiri dan kanan layar (*horizontal screen padding*).
* `Large (24.dp)`: Jarak antar seksi form, margin dialog modal.
* `Extra Large (32.dp - 48.dp)`: Header banner, margin atas empty state.

### C. Standar Sudut Bentuk (*Shapes*)
* **Item Kartu (Card):** `RoundedCornerShape(16.dp)`
* **Input Text & Button:** `RoundedCornerShape(12.dp)`
* **Chip Filter & Status Badge:** `RoundedCornerShape(8.dp)` atau `CircleShape`
* **Dialog / BottomSheet:** `RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)`

### D. Aksesibilitas (*Accessibility*)
* Seluruh elemen yang dapat diklik (*clickable targets*) wajib memiliki ukuran area sentuh minimal **$48 \times 48\text{ dp}$**.
* Setiap `Icon` dan `Image` wajib memiliki atribut `contentDescription` yang jelas (atau `null` jika hanya berupa dekorasi).

---

## 8. Standar Alur Data & State Management (UDF Implementation)

Setiap pengembang mengimplementasikan pola Unidirectional Data Flow pada layarnya:

```kotlin
// 1. DATA LAYER (Repository)
class TaskRepositoryImpl(
    private val supabase: SupabaseClient
) : TaskRepository {
    override fun getTasks(userId: String): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading)
        try {
            val dtoList = supabase.from("tasks")
                .select { filter { eq("user_id", userId) } }
                .decodeList<TaskDto>()
            val domainList = dtoList.map { it.toDomain() }
            emit(Resource.Success(domainList))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "Terjadi kesalahan memuat tugas.", throwable = e))
        }
    }.flowOn(Dispatchers.IO)
}

// 2. UI STATE DEFINITION
data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)

// 3. VIEWMODEL
class TaskViewModel(
    private val repository: TaskRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            repository.getTasks(currentUserId).collect { resource ->
                _uiState.update { state ->
                    when (resource) {
                        is Resource.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Resource.Success -> state.copy(isLoading = false, tasks = resource.data)
                        is Resource.Error -> state.copy(isLoading = false, errorMessage = resource.message)
                    }
                }
            }
        }
    }
}
```

---

## 9. Matriks Validasi Kepatuhan Arsitektur

Sebelum mengajukan *Pull Request* ke branch `develop`, pastikan kode Anda lolos validasi berikut:

- [ ] Kode berada di dalam folder fitur masing-masing (`features/<feature_name>/`).
- [ ] Tidak mengedit file milik anggota tim lain tanpa koordinasi.
- [ ] Menggunakan komponen UI list sesuai mandat proposal:
  - Gangsar: `LazyColumn`
  - Muhammad Abdillah: `HorizontalPager` + `LazyColumn`
  - Prayoga: `LazyVerticalGrid` + Supabase Storage upload
  - Tubagus: `LazyColumn` + Progress Indicator
  - Qoid: `HorizontalPager` + `LazyColumn` + `LazyRow` + Implicit Intent
- [ ] Semua panggilan Supabase dijalankan di latar belakang (*Dispatcher IO*) melalui Repository.
- [ ] Komponen tampilan menggunakan token warna, spasi, dan komponen pakai ulang dari `core/designsystem/`.
- [ ] Tidak ada hardcoded warna heksadesimal atau ukuran margin anomali di dalam Screen Composable.
- [ ] Berhasil melewati kompilasi `./gradlew assembleDebug` tanpa peringatan/error sintaks.
