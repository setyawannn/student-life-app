# 🎓 StudentLife — Personal Academic Organizer

Aplikasi mobile Android modern berbasis **Kotlin** dan **Jetpack Compose** yang dirancang untuk membantu mahasiswa mengelola seluruh aktivitas akademiknya dalam satu platform terpadu: manajemen tugas, jadwal perkuliahan, catatan belajar (lengkap dengan foto lampiran), target pencapaian belajar, serta repositori tautan referensi materi kuliah.

Aplikasi ini menggunakan backend *cloud real-time* **Supabase** (PostgreSQL, Auth, dan Storage) dengan penerapan standar industri **Feature-Packaged Layered Clean Architecture**.

---

## 👥 Identitas Tim & Pembagian Fitur Utama

| No | NIM | Nama Lengkap | Peran | Fitur Utama yang Dikerjakan |
|:--:|:---|:---|:---:|:---|
| 1 | `245150401111015` | **Muhammad Abdillah** | **Ketua Tim** | **Fitur 2: Manajemen Jadwal Kuliah** (`HorizontalPager` + `LazyColumn`) |
| 2 | `245150407111022` | **Gangsar Wijayanto** | Anggota Tim | **Fitur 1: Manajemen Tugas Kuliah** (`LazyColumn`) |
| 3 | `245150401111021` | **Prayoga Adi Setyawan** | Anggota Tim | **Fitur 3: Catatan Belajar & Upload Gambar** (`LazyVerticalGrid` + Supabase Storage) |
| 4 | `245150407111030` | **Tubagus Fatahudin Mulkan** | Anggota Tim | **Fitur 4: Manajemen Target Belajar** (`LazyColumn` + Progress Indicator) |
| 5 | `245150401111030` | **Qoid Kafi** | Anggota Tim | **Fitur 5: Referensi & Link Materi** (Tab Pager + `LazyRow` Chips + Implicit Intent) |

**Dosen Pengampu / Evaluator:** Muhammad Aminul Akbar, S.Kom., M.T.

---

## 🛠️ Tech Stack & Library

* **Bahasa Pemrograman:** [Kotlin](https://kotlinlang.org/) `2.2.10` (Coroutines, StateFlow, Serialization)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (BOM `2026.02.01`) + Material 3 (*Material You*)
* **Arsitektur:** Feature-Packaged Layered Clean Architecture (Database, Service, Controller, UI, Utils)
* **Backend as a Service:** [Supabase](https://supabase.com/) (`postgrest-kt`, `auth-kt`, `storage-kt`)
* **Pemuat Gambar:** [Coil Compose](https://coil-kt.github.io/coil/) `2.7.0` (Memory & Disk Cache)
* **Build System:** Gradle 9.5 & Android Gradle Plugin (AGP) 9.3.3 (Version Catalog)
* **Target OS:** Android Min SDK 24 (Android 7.0+) / Target SDK 35

---

## 💻 Persyaratan Perangkat Lunak (Prerequisites)

Sebelum menjalankan proyek, pastikan komputer Anda telah terpasang:
1. **Git:** Terpasang dan terkonfigurasi (`git --version`).
2. **Android Studio:** Disarankan versi terbaru (**Ladybug / Meerkat** atau yang lebih baru).
3. **Java Development Kit (JDK):** JDK 17 atau JDK 21 (Secara default sudah terpasang di Android Studio JBR: `jbr-17` atau `jbr-21`).
4. **Android SDK:** Platform SDK 34 / 35 dan Android SDK Build-Tools.
5. **Perangkat Pengujian:** Emulator Android (API 26+) atau perangkat fisik Android yang telah mengaktifkan *USB Debugging*.

---

## 🚀 Panduan Instalasi & Menjalankan di Lokal (Step-by-Step)

Ikuti langkah-langkah berikut untuk meng-clone dan menjalankan proyek di laptop Anda:

### Langkah 1: Clone Repositori
Buka terminal / PowerShell dan clone repositori ini:
```bash
git clone <URL_REPOSITORI_GITHUB_ANDA>
cd studentlifeapp
```

### Langkah 2: Setup File Environment (`.env`)
Proyek ini menggunakan file `.env` untuk menyimpan kredensial sensitif agar tidak bocor ke Git:
```bash
# Untuk Windows Command Prompt:
copy .env.example .env

# Untuk Windows PowerShell / Linux / macOS:
cp .env.example .env
```
Buka file `.env` yang baru dibuat menggunakan text editor, lalu masukkan kredensial Supabase tim:
```env
# Kredensial Supabase
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-supabase-anon-key-here

# Figma Access (Opsional untuk integrasi desain)
FIGMA_API_KEY=your_figma_token_here
```
> [!NOTE]
> File `.env` sudah dimasukkan ke `.gitignore` sehingga aman dan tidak akan pernah ter-commit ke GitHub.

### Langkah 3: Buka di Android Studio
1. Buka aplikasi **Android Studio**.
2. Pilih **Open** (atau **File > Open**).
3. Arahkan ke folder `studentlifeapp` tempat Anda meng-clone proyek ini.
4. Tunggu Android Studio selesai mengunduh dependensi dan melakukan **Gradle Sync** (pastikan laptop terhubung ke internet).

### Langkah 4: Menjalankan Aplikasi
1. Hubungkan HP fisik Anda via kabel USB (aktifkan *USB Debugging*) ATAU pilih salah satu **Android Virtual Device (AVD / Emulator)**.
2. Klik tombol hijau **Run 'app'** (ikon segitiga ▶) di toolbar atas Android Studio, atau tekan pintasan:
   $$\mathbf{Shift + F10}$$
3. Aplikasi akan otomatis ter-build dan terbuka di emulator/ponsel Anda dengan Bottom Navigation 5 menu!

---

## 🌿 Panduan Alur Kerja Tim (Git Workflow)

Agar seluruh 5 anggota tim dapat bekerja bersamaan tanpa pernah mengalami **Git Merge Conflict**, ikuti aturan ini:

### 1. Struktur Percabangan (*Branching*)
* `main`: Branch rilis utama yang stabil (hanya untuk demo / rilis ke dosen).
* `develop`: Branch integrasi bersama seluruh fitur.
* `feature/<nama_fitur>-<nama_anggota>`: Branch kerja pribadi setiap anggota.

Contoh nama branch tiap anggota:
* Gangsar: `feature/task-gangsar`
* Abdillah: `feature/schedule-abdillah`
* Prayoga: `feature/note-prayoga`
* Tubagus: `feature/goal-tubagus`
* Qoid: `feature/reference-qoid`

### 2. Alur Pengerjaan Fitur
1. Selalu mulai dari branch `develop` terbaru:
   ```bash
   git checkout develop
   git pull origin develop
   ```
2. Buat branch fitur baru Anda:
   ```bash
   git checkout -b feature/task-gangsar
   ```
3. Kerjakan kode **hanya di dalam folder fitur Anda** (`features/<fitur_anda>/`).
4. Lakukan commit dengan pesan yang jelas (*Conventional Commits*):
   ```bash
   git add .
   git commit -m "feat(task): implement task remote data source and repository"
   ```
5. Push branch fitur Anda ke GitHub:
   ```bash
   git push -u origin feature/task-gangsar
   ```
6. Buat **Pull Request (PR)** dari branch fitur Anda ke branch `develop` di web GitHub.
7. Diskusikan dan mintalah Ketua Tim (`Muhammad Abdillah`) untuk me-review dan melakukan *merge*.

---

## 📁 Struktur Pemisahan Kode Berlapis (*Architecture Guide*)

Setiap fitur dikelompokkan ke dalam 6 lapisan terpisah:
```text
features/<nama_fitur>/
├── data/
│   ├── datasource/    # Query Supabase Postgrest & upload berkas Storage
│   ├── dto/           # Model data JSON (DTO) dengan kotlinx.serialization
│   └── repository/    # Abstraksi data & mapping ke domain model
├── service/           # Logika bisnis, validasi form, & kalkulasi data
├── controller/        # ViewModel (StateFlow) & penanganan event antarmuka
├── model/             # Domain entity murni & Enums status
├── ui/                # Composable Screen (List, Detail, Form) & custom cards
└── utils/             # Fungsi pembantu / helper khusus fitur
```

Baca dokumentasi lengkap arsitektur dan sistem desain di:
* [📖 **ARCHITECTURE.md**](ARCHITECTURE.md): Panduan lengkap sistem desain, konvensi penamaan, dan standar Material 3.
* [🗄️ **docs/03_SKEMA_DATABASE_SUPABASE.md**](docs/03_SKEMA_DATABASE_SUPABASE.md): Skrip SQL DDL tabel basis data dan kebijakan RLS Supabase.
* [👨‍💻 **docs/04_PANDUAN_FITUR_TIM.md**](docs/04_PANDUAN_FITUR_TIM.md): Panduan teknis mendalam dan ceklis tugas per anggota.

---

## 🤝 Kontribusi & Bantuan

Jika mengalami kendala build Gradle atau eror dependensi saat pertama kali clone:
1. Pastikan koneksi internet stabil saat Gradle Sync pertama.
2. Pastikan file `.env` sudah dibuat dengan menyalin `.env.example`.
3. Di Android Studio, lakukan **File > Invalidate Caches... > Invalidate and Restart**.
