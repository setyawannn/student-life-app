# STANDAR LAYOUT, SCAFFOLD, DAN DESAIN SISTEM UI APLIKASI STUDENTLIFE

Dokumen ini merupakan **standar resmi antarmuka pengguna (UI Layout & Design System)** untuk seluruh pengembang aplikasi **StudentLife**. Seluruh anggota tim diwajibkan mengikuti panduan ini saat membuat atau memodifikasi layar (*screen*) agar aplikasi memiliki tampilan yang konsisten, rapi, dan presisi sesuai acuan desain di **Figma**.

---

## 1. Filosofi & Arsitektur Layout Master

Aplikasi StudentLife mengadopsi pola layout **Fixed Chrome with Dynamic Middle Content**:
* **Fixed Top Bar (Header)**: Berada di posisi paling atas layar, selalu terlihat (*sticky/pinned*), mendukung status bar transparan (*edge-to-edge*).
* **Fixed Bottom Navigation Bar**: Terkunci di posisi paling bawah layar dengan 5 tab utama, mendukung gesture navigasi sistem (*navigation bar insets*).
* **Dynamic Middle Slot (Content Viewport)**: Bagian tengah antara header dan navbar merupakan area dinamis yang fleksibel, baik untuk konten yang dapat di-scroll (*scrollable* seperti `LazyColumn`, `LazyVerticalGrid`, `HorizontalPager`) maupun konten statis (*non-scrollable* seperti formulir atau *empty state*).

```
+-----------------------------------------------------------+
| [Badge] Catatan Belajar               (🔔)  (Avatar)      | <- Fixed Top Bar (StudentLifeHeader)
+-----------------------------------------------------------+
|                                                           |
|  [🔍 Cari judul atau isi catatan...            (⚙)]        |
|                                                           |
|  (Semua)  (Pemrograman Mobile)  (Basis Data)              |
|                                                           |
|  [Total: 14]      [Foto: 8]       [Matkul: 4]             | <- Dynamic Middle Slot
|                                                           |    (Scrollable / Non-scrollable)
|  +--------------------+  +--------------------+           |
|  | [Img] MVVM & Flow  |  | [Tag] Normalisasi  |           |
|  +--------------------+  +--------------------+           |
|  | [Img] Heuristic 10 |  | [Tag] Query Optim  |      (+)  | <- Floating Action Button (FAB)
|  +--------------------+  +--------------------+           |
+-----------------------------------------------------------+
|   (✓)         (📅)         (📖)        (🎯)        (📁)   | <- Fixed Navbar (Pure Outline Icons)
|  Tugas       Jadwal      Catatan      Target    Referensi |
+-----------------------------------------------------------+
```

---

## 2. Aturan Baku Ikon: WAJIB MENGGUNAKAN IKON OUTLINE

> [!IMPORTANT]
> **ATURAN MUTLAK TIM:**
> Seluruh ikon navigasi, aksi, dan elemen antarmuka **HARUS MENGGUNAKAN VARIAN OUTLINE** (`Icons.Outlined.*` atau `Icons.AutoMirrored.Outlined.*`).
> **DILARANG** menggunakan ikon tipe *Filled* (`Icons.Filled.*` atau `Icons.Default.*`) jika varian *Outlined* tersedia. Hal ini menjaga estetika antarmuka agar tetap modern, ringan, dan elegan sesuai standar Figma.

### Pemetaan Ikon Standar Aplikasi:

| Kebutuhan UI / Modul | Ikon yang Wajib Digunakan | Package Import |
|:---|:---|:---|
| **Tab 1: Tugas** | `Icons.Outlined.CheckCircle` | `androidx.compose.material.icons.outlined.CheckCircle` |
| **Tab 2: Jadwal** | `Icons.Outlined.CalendarMonth` | `androidx.compose.material.icons.outlined.CalendarMonth` |
| **Tab 3: Catatan** | `Icons.AutoMirrored.Outlined.MenuBook` | `androidx.compose.material.icons.automirrored.outlined.MenuBook` |
| **Tab 4: Target** | `Icons.Outlined.TrackChanges` | `androidx.compose.material.icons.outlined.TrackChanges` |
| **Tab 5: Referensi** | `Icons.Outlined.FolderOpen` | `androidx.compose.material.icons.outlined.FolderOpen` |
| **Tombol Tambah (FAB / Action)** | `Icons.Outlined.Add` | `androidx.compose.material.icons.outlined.Add` |
| **Pencarian (Search Bar)** | `Icons.Outlined.Search` | `androidx.compose.material.icons.outlined.Search` |
| **Filter / Setting (Search Bar)** | `Icons.Outlined.Tune` | `androidx.compose.material.icons.outlined.Tune` |
| **Lonceng Notifikasi** | `Icons.Outlined.Notifications` | `androidx.compose.material.icons.outlined.Notifications` |
| **Avatar / Pengguna** | `Icons.Outlined.Person` | `androidx.compose.material.icons.outlined.Person` |
| **Tombol Kembali (Back)** | `Icons.AutoMirrored.Outlined.ArrowBack` | `androidx.compose.material.icons.automirrored.outlined.ArrowBack` |
| **Bookmark / Simpan** | `Icons.Outlined.BookmarkBorder` | `androidx.compose.material.icons.outlined.BookmarkBorder` |
| **Tanggal / Deadline** | `Icons.Outlined.CalendarToday` | `androidx.compose.material.icons.outlined.CalendarToday` |
| **Kamera / Upload Foto** | `Icons.Outlined.CameraAlt` | `androidx.compose.material.icons.outlined.CameraAlt` |
| **Hapus (Delete)** | `Icons.Outlined.Delete` | `androidx.compose.material.icons.outlined.Delete` |
| **Ubah (Edit)** | `Icons.Outlined.Edit` | `androidx.compose.material.icons.outlined.Edit` |

---

## 3. Komponen Inti Desain Sistem

Semua komponen layout inti telah disediakan di direktori `core/designsystem/components/` dan siap dipanggil oleh seluruh anggota.

### A. `StudentLifeScaffold`
Master scaffold yang membungkus setiap layar. Menjaga struktur Top Bar, Bottom Bar, dan Floating Action Button agar selalu konsisten.

* **Lokasi File**: `core/designsystem/components/StudentLifeScaffold.kt`
* **Definisi Signature**:
```kotlin
@Composable
fun StudentLifeScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
)
```

### B. `StudentLifeHeader`
Top Bar seragam khas StudentLife yang menampilkan badge ikon berwarna biru, judul layar, tombol kembali opsional, lonceng notifikasi, dan avatar profil.

* **Lokasi File**: `core/designsystem/components/StudentLifeHeader.kt`
* **Definisi Signature**:
```kotlin
@Composable
fun StudentLifeHeader(
    title: String,
    badgeIcon: ImageVector,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    hasNotification: Boolean = true,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
)
```
* **Kapan Menggunakan `showBackButton`?**
  * Halaman List Utama: `showBackButton = false` (menampilkan badge kotak biru).
  * Halaman Detail atau Form Tambah/Ubah: `showBackButton = true` (menampilkan tombol panah kembali).

### C. `StudentLifeBottomNavBar`
Bottom navigation bar yang menampung 5 menu modul utama.

* **Lokasi File**: `core/navigation/BottomNavBar.kt`
* **Catatan**: Komponen ini otomatis di-render oleh container navigasi root (`MainAppContainer` pada `AppNavigation.kt`), sehingga anggota tim **tidak perlu** memanggil `StudentLifeBottomNavBar` secara manual di dalam masing-masing layarnya.

---

## 4. Panduan Menangani Slot Konten Dinamis (`innerPadding`)

> [!WARNING]
> **PENTING: JANGAN PERNAH MENGABAIKAN `innerPadding`!**
> Saat Anda memanggil `StudentLifeScaffold { innerPadding -> ... }`, Anda **wajib** meneruskan nilai `innerPadding` ke konten Anda. Jika diabaikan, elemen paling atas akan terpotong oleh Top Bar dan elemen paling bawah akan tertutup oleh Floating Action Button atau Bottom Navigation Bar.

### Pola 1: Layar Scrollable (Grid / List / Pager)
Untuk layar daftar data yang memiliki scroll, terapkan `innerPadding` pada modifier utama dan beri `bottom = 84.dp` pada `contentPadding` agar item terbawah tetap terlihat leluasa di atas FAB.

```kotlin
@Composable
fun MyFeatureListScreen(
    onNavigateToCreate: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = "Nama Fitur",
                badgeIcon = Icons.Outlined.CheckCircle
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Tambah Data",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        // Lazy Column / Grid dengan padding aman
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // <-- PENTING: Mencegah tertutup Top Bar
            contentPadding = PaddingValues(
                start = 16.dp, 
                end = 16.dp, 
                top = 12.dp, 
                bottom = 88.dp // <-- PENTING: Mencegah item terbawah tertutup FAB
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item-item konten Anda di sini...
        }
    }
}
```

### Pola 2: Layar Non-Scrollable (Formulir Sederhana / Empty State)
Jika layar tidak memerlukan scroll panjang (misal pesan belum ada data atau form ringkas):

```kotlin
@Composable
fun MyEmptyScreen(onActionClick: () -> Unit = {}) {
    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = "Daftar Tugas",
                badgeIcon = Icons.Outlined.CheckCircle
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateView(
                icon = Icons.Outlined.CheckCircle,
                title = "Belum Ada Tugas",
                description = "Mulai catat tugas kuliah Anda sekarang.",
                actionButtonText = "Tambah Tugas",
                onActionClick = onActionClick
            )
        }
    }
}
```

---

## 5. Komponen Pelengkap Standar (Design Tokens)

### A. Palet Warna Resmi (`ui/theme/Color.kt`)
| Nama Token | Hex Code | Penggunaan |
|:---|:---|:---|
| `PrimaryBlue` | `#004AC6` | Warna aksen utama, badge header, tombol primer, FAB, tab aktif |
| `ScreenCanvasBackground` | `#FAF8FF` | Latar belakang kanvas seluruh layar |
| `SurfaceCard` | `#FFFFFF` | Latar belakang kartu item, kotak pencarian, modal |
| `TextPrimary` | `#131B2E` | Teks judul utama, angka metrik besar |
| `TextSecondary` | `#434655` | Teks paragraf, cuplikan deskripsi, label filter pasif |
| `TextMuted` | `#64748B` | Teks placeholder, tanggal kecil, ikon sekunder |
| `BorderOutlineVariant` | `#E2E8F0` | Garis tepi (*border*) halus kartu, divider navbar |

### B. Kotak Pencarian Standar (Search Bar)
Gunakan struktur berikut untuk membuat kolom pencarian yang konsisten:
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(SurfaceCard)
        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(14.dp))
        .padding(horizontal = 14.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        imageVector = Icons.Outlined.Search,
        contentDescription = "Cari",
        tint = TextMuted,
        modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = {
            Text("Cari data...", color = TextMuted, fontSize = 14.sp)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        modifier = Modifier.weight(1f)
    )
    IconButton(
        onClick = { /* Filter click */ },
        modifier = Modifier.size(28.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Tune,
            contentDescription = "Filter",
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
```

### C. Barisan Filter Chip Horizontal
Untuk membuat filter kategori horisontal di bawah search bar:
```kotlin
LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
) {
    items(filterList) { filter ->
        val isSelected = filter == selectedFilter
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) PrimaryBlue else SurfaceCard)
                .border(
                    1.dp,
                    if (isSelected) PrimaryBlue else BorderOutlineVariant,
                    RoundedCornerShape(20.dp)
                )
                .clickable { selectedFilter = filter }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = filter,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}
```

### D. Kartu Statistik Ringkasan KPI (`SummaryStatCard`)
Tiga kartu metrik ringkas di atas daftar item:
```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    SummaryStatCard(
        count = "14",
        label = "Total Item",
        icon = Icons.Outlined.CheckCircle,
        indicatorColor = PrimaryBlue,
        modifier = Modifier.weight(1f)
    )
    SummaryStatCard(
        count = "8",
        label = "Selesai",
        icon = Icons.Outlined.CheckCircle,
        indicatorColor = Color(0xFF00714D),
        modifier = Modifier.weight(1f)
    )
    SummaryStatCard(
        count = "6",
        label = "Mendesak",
        icon = Icons.Outlined.Notifications,
        indicatorColor = Color(0xFFBA1A1A),
        modifier = Modifier.weight(1f)
    )
}
```

---

## 6. Template Siap Pakai (Boilerplate Copy-Paste)

### Template 1: Layar Detail Data
Untuk layar rincian (*Detail Screen*) yang diakses saat pengguna menekan salah satu kartu item:

```kotlin
package com.example.student_life_app.features.task.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold

@Composable
fun TaskDetailScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = "Detail Tugas",
                badgeIcon = Icons.Outlined.CheckCircle,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Konten Detail Anda di sini...
        }
    }
}
```

### Template 2: Layar Formulir Tambah / Ubah
Untuk form input yang memiliki tombol simpan:

```kotlin
package com.example.student_life_app.features.task.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.ui.theme.PrimaryBlue

@Composable
fun TaskFormScreen(
    taskId: String? = null,
    onNavigateBack: () -> Unit
) {
    val isEditMode = taskId != null
    var title by remember { mutableStateOf("") }

    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = if (isEditMode) "Ubah Tugas" else "Tambah Tugas Baru",
                badgeIcon = Icons.Outlined.CheckCircle,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Tugas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Simpan ke Repository / ViewModel */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = if (isEditMode) "Simpan Perubahan" else "Tambah Tugas",
                    color = Color.White
                )
            }
        }
    }
}
```

---

## 7. Ceklis Mandiri Sebelum Melakukan Git Push

Sebelum membuat commit dan push ke remote repository (`develop`), pastikan Anda telah mencentang seluruh poin berikut:

- [ ] Seluruh ikon yang digunakan adalah varian **OUTLINE** (`Icons.Outlined.*`), bukan *Filled*.
- [ ] Layar dibungkus dengan `StudentLifeScaffold`.
- [ ] Header menggunakan `StudentLifeHeader` dengan badge yang sesuai modul fitur.
- [ ] Parameter `innerPadding` dari scaffold diteruskan ke container konten utama.
- [ ] Untuk layar scrollable (`LazyColumn` / `LazyVerticalGrid`), `contentPadding` bagian bawah diberi minimal `84.dp` agar item tidak tertutup FAB.
- [ ] Tidak ada file rahasia `.env` atau folder `.agents/` yang masuk ke dalam staging git.
- [ ] Perintah `./gradlew compileDebugKotlin` berhasil tanpa pesan error (*BUILD SUCCESSFUL*).
