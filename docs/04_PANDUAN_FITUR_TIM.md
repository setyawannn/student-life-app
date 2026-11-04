# PANDUAN TEKNIS IMPLEMENTASI FITUR PER ANGGOTA

Dokumen ini memuat panduan teknis mendalam (*deep-dive implementation guide*) bagi setiap anggota tim untuk mengimplementasikan fiturnya masing-masing secara independen, memenuhi seluruh poin ceklis rubrik dosen, dan siap diuji coba.

---

## 1. Panduan Fitur 1 — Gangsar Wijayanto (NIM: 245150407111022)

### A. Identitas & Spesifikasi Fitur
* **Nama Fitur:** Manajemen Tugas Kuliah
* **Paket Kode:** `com.example.student_life_app.features.task`
* **Nama Kelas ViewModel:** `TaskViewModel`
* **Tabel Supabase:** `public.tasks`
* **Komponen Data List Wajib:** `LazyColumn`
* **Alur Layar (Multi-Screen Navigation):**
  * `task_list` $\rightarrow$ `TaskListScreen`
  * `task_detail/{taskId}` $\rightarrow$ `TaskDetailScreen`
  * `task_form?taskId={taskId}` $\rightarrow$ `TaskFormScreen` (mendukung mode tambah baru atau edit eksisting)

### B. Komponen UI Kunci
1. **Daftar Tugas (`LazyColumn`):**
   * Menggunakan `LazyColumn` dengan `items(tasks, key = { it.id })`.
   * Menampilkan kartu `TaskCard`: judul, mata kuliah (*input teks bebas*), deadline (format rapi via `DateFormatter`), dan badge status (`TODO`, `IN_PROGRESS`, `COMPLETED`).
2. **Detail Tugas:**
   * Menampilkan deskripsi lengkap.
   * Dropdown/tombol cepat ubah status pengerjaan langsung memicu pembaruan ke Supabase.
   * Tombol Navigasi Edit (pindah ke `task_form`) dan Tombol Hapus (dengan dialog konfirmasi).
3. **Form Input / Edit:**
   * Input teks: Judul, Mata Kuliah, Deskripsi.
   * Date & Time Picker untuk tenggat waktu (*due date*).
   * Validasi sederhana (judul & mata kuliah tidak boleh kosong).

### C. Ceklis Kesiapan Asistensi / Demonstrasi
- [ ] Fitur berdiri sendiri, tidak bergantung pada login/profil.
- [ ] List terbukti menggunakan `LazyColumn`.
- [ ] Navigasi 3 halaman (List $\rightarrow$ Detail $\rightarrow$ Form) berjalan mulus.
- [ ] Terbukti melakukan CRUD lengkap ke tabel `tasks` di Supabase.
- [ ] Seluruh state ditangani oleh `TaskViewModel` via `StateFlow`.

---

## 2. Panduan Fitur 2 — Muhammad Abdillah (NIM: 245150401111015)

### A. Identitas & Spesifikasi Fitur
* **Nama Fitur:** Manajemen Jadwal Kuliah
* **Paket Kode:** `com.example.student_life_app.features.schedule`
* **Nama Kelas ViewModel:** `ScheduleViewModel`
* **Tabel Supabase:** `public.schedules`
* **Komponen Data List Wajib:** `HorizontalPager` (Tab Hari) + `LazyColumn`
* **Alur Layar (Multi-Screen Navigation):**
  * `schedule_list` $\rightarrow$ `ScheduleListScreen`
  * `schedule_detail/{scheduleId}` $\rightarrow$ `ScheduleDetailScreen`
  * `schedule_form?scheduleId={scheduleId}` $\rightarrow$ `ScheduleFormScreen`

### B. Komponen UI Kunci
1. **Daftar Jadwal (`HorizontalPager` + `LazyColumn`):**
   * Terdapat Tab bar horizontal hari (Senin s.d. Sabtu) menggunakan `ScrollableTabRow` / `TabRow`.
   * Menggunakan `HorizontalPager(state = pagerState)`.
   * Di dalam setiap halaman pager, terdapat `LazyColumn` yang menampilkan jadwal mata kuliah pada hari yang bersangkutan.
2. **Detail Jadwal:**
   * Informasi jam kuliah (misal: 07:30 - 10:00 WIB), nama ruangan / gedung perkuliahan, dan dosen pengampu.
   * Opsi ubah (*edit*) dan hapus jadwal.
3. **Form Input / Edit:**
   * Input nama mata kuliah (teks bebas).
   * Pilihan hari (dropdown/picker).
   * Time Picker untuk jam mulai dan jam selesai.
   * Input ruangan dan nama dosen.

### C. Ceklis Kesiapan Asistensi / Demonstrasi
- [ ] Tab hari dapat digeser (*swipe*) menggunakan `HorizontalPager`.
- [ ] List jadwal di setiap hari dirender menggunakan `LazyColumn`.
- [ ] Navigasi 3 halaman (List $\rightarrow$ Detail $\rightarrow$ Form) berfungsi penuh.
- [ ] Operasi CRUD berfungsi ke tabel `schedules`.
- [ ] State dikelola oleh `ScheduleViewModel`.

---

## 3. Panduan Fitur 3 — Prayoga Adi Setyawan (NIM: 245150401111021)

### A. Identitas & Spesifikasi Fitur (Fitur Wajib Unggah Gambar)
* **Nama Fitur:** Catatan Belajar (*Notes with Supabase Storage*)
* **Paket Kode:** `com.example.student_life_app.features.note`
* **Nama Kelas ViewModel:** `NoteViewModel`
* **Tabel & Storage Supabase:** Tabel `public.notes` & Bucket `note_attachments`
* **Komponen Data List Wajib:** `LazyVerticalGrid`
* **Alur Layar (Multi-Screen Navigation):**
  * `note_list` $\rightarrow$ `NoteListScreen`
  * `note_detail/{noteId}` $\rightarrow$ `NoteDetailScreen`
  * `note_form?noteId={noteId}` $\rightarrow$ `NoteFormScreen`

### B. Implementasi Unggah & Muat Gambar
1. **Pemilihan Gambar di Form:**
   ```kotlin
   val imagePickerLauncher = rememberLauncherForActivityResult(
       contract = ActivityResultContracts.GetContent()
   ) { uri: Uri? ->
       viewModel.onImageSelected(uri)
   }
   ```
2. **Unggah ke Supabase Storage:**
   * Berkas diunggah ke bucket `note_attachments` dengan path unik: `user_id/notes/${UUID.randomUUID()}.jpg`.
   * Dapatkan *Public URL* dari gambar tersebut dan simpan string URL-nya ke kolom `image_url` pada tabel `notes`.
3. **Menampilkan Gambar Kembali:**
   * Gunakan `AsyncImage` dari library Coil Compose:
     ```kotlin
     AsyncImage(
         model = note.imageUrl,
         contentDescription = "Foto Catatan",
         modifier = Modifier.fillMaxWidth().height(180.dp),
         contentScale = ContentScale.Crop
     )
     ```
4. **Daftar Catatan (`LazyVerticalGrid`):**
   * Menggunakan `LazyVerticalGrid(columns = GridCells.Fixed(2))`.
   * Menampilkan thumbnail gambar di bagian atas kartu catatan, diikuti judul, nama matkul, dan tanggal catatan.

### C. Ceklis Kesiapan Asistensi / Demonstrasi
- [ ] Memenuhi syarat wajib proyek: **Upload gambar ke cloud storage & dibaca kembali**.
- [ ] List catatan disajikan dalam tampilan grid dengan `LazyVerticalGrid`.
- [ ] Navigasi 3 halaman lengkap (List $\rightarrow$ Detail $\rightarrow$ Form).
- [ ] CRUD database dan operasi upload storage berhasil.
- [ ] State dikelola oleh `NoteViewModel`.

---

## 4. Panduan Fitur 4 — Tubagus Fatahudin Mulkan (NIM: 245150407111030)

### A. Identitas & Spesifikasi Fitur
* **Nama Fitur:** Manajemen Target Belajar (*Study Goals*)
* **Paket Kode:** `com.example.student_life_app.features.goal`
* **Nama Kelas ViewModel:** `GoalViewModel`
* **Tabel Supabase:** `public.study_goals`
* **Komponen Data List Wajib:** `LazyColumn` dengan visual *Progress Indicator*
* **Alur Layar (Multi-Screen Navigation):**
  * `goal_list` $\rightarrow$ `GoalListScreen`
  * `goal_detail/{goalId}` $\rightarrow$ `GoalDetailScreen`
  * `goal_form?goalId={goalId}` $\rightarrow$ `GoalFormScreen`

### B. Komponen UI Kunci
1. **Daftar Target (`LazyColumn`):**
   * Setiap item kartu target memuat deskripsi target, sisa hari/deadline, serta bar progres horizontal:
     ```kotlin
     LinearProgressIndicator(
         progress = { goal.progress / 100f },
         modifier = Modifier.fillMaxWidth().height(8.dp),
         color = MaterialTheme.colorScheme.primary,
         trackColor = MaterialTheme.colorScheme.surfaceVariant
     )
     Text(text = "${goal.progress}% Selesai")
     ```
2. **Detail Target:**
   * Rincian sasaran target belajar dan milestone capaian.
   * Opsi slider cepat atau tombol "+10%" untuk mempercepat pembaruan progres dari halaman detail.
   * Opsi edit target dan hapus target.
3. **Form Input / Edit:**
   * Input nama sasaran / topik belajar.
   * Input deskripsi tambahan.
   * Date Picker untuk target penyelesaian.
   * Slider atau input angka untuk mengatur nilai progres awal (0% - 100%).

### C. Ceklis Kesiapan Asistensi / Demonstrasi
- [ ] List target ditampilkan menggunakan `LazyColumn`.
- [ ] Indikator progres capaian tampak jelas dan interaktif.
- [ ] Navigasi 3 halaman (List $\rightarrow$ Detail $\rightarrow$ Form) berjalan lancar.
- [ ] CRUD berfungsi ke tabel `study_goals`.
- [ ] State ditangani oleh `GoalViewModel`.

---

## 5. Panduan Fitur 5 — Qoid Kafi (NIM: 245150401111030)

### A. Identitas & Spesifikasi Fitur
* **Nama Fitur:** Referensi & Link Materi
* **Paket Kode:** `com.example.student_life_app.features.reference`
* **Nama Kelas ViewModel:** `ReferenceViewModel`
* **Tabel Supabase:** `public.references`
* **Komponen Data List Wajib:** `HorizontalPager` (Kategori) + `LazyColumn` + `LazyRow` (Filter Chip)
* **Fitur Spesial:** Validasi format URL, Tombol Buka Link (*Implicit Intent*), Toggle Favorit (*Bookmark*)
* **Alur Layar (Multi-Screen Navigation):**
  * `reference_list` $\rightarrow$ `ReferenceListScreen`
  * `reference_detail/{refId}` $\rightarrow$ `ReferenceDetailScreen`
  * `reference_form?refId={refId}` $\rightarrow$ `ReferenceFormScreen`

### B. Komponen UI Kunci & Fitur Spesial
1. **Daftar Referensi Multi-Komponen:**
   * `HorizontalPager` untuk tab kategori: Jurnal, Drive/Dokumen, Video Pembelajaran.
   * `LazyRow` di bagian header untuk menyajikan chip filter nama mata kuliah:
     ```kotlin
     LazyRow(
         horizontalArrangement = Arrangement.spacedBy(8.dp),
         contentPadding = PaddingValues(horizontal = 16.dp)
     ) {
         items(courses) { course ->
             FilterChip(
                 selected = selectedCourse == course,
                 onClick = { viewModel.filterByCourse(course) },
                 label = { Text(course) }
             )
         }
     }
     ```
   * `LazyColumn` untuk daftar kartu referensi pada tab terpilih.
2. **Detail Referensi & Implicit Intent:**
   * Tombol **Buka Tautan** yang memicu *Android Implicit Intent*:
     ```kotlin
     val context = LocalContext.current
     fun openUrl(url: String) {
         val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
         context.startActivity(intent)
     }
     ```
   * Tombol Bintang / Favorit untuk toggle nilai `is_favorite` langsung ke Supabase.
3. **Form Input / Edit dengan Validasi URL:**
   * Validasi string URL menggunakan `android.util.Patterns.WEB_URL.matcher(url).matches()`.
   * Pilihan kategori (JURNAL, DRIVE, VIDEO).
   * Input teks bebas mata kuliah dan catatan pendukung.

### C. Ceklis Kesiapan Asistensi / Demonstrasi
- [ ] Menggunakan kombinasi `HorizontalPager`, `LazyColumn`, dan `LazyRow`.
- [ ] Fitur *Implicit Intent* (buka browser/YouTube) berhasil berjalan.
- [ ] Validasi URL bekerja saat input form.
- [ ] Fitur toggle favorit dan CRUD ke tabel `references` berjalan baik.
- [ ] State ditangani oleh `ReferenceViewModel`.
