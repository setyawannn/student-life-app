# PROPOSAL PROYEK AKHIR PENGEMBANGAN APLIKASI MOBILE

**Mata Kuliah:** Pemrograman Aplikasi Mobile  
**Fokus:** Konsep Aplikasi, Rincian Fitur Utama, & Ceklis Validasi Tugas Individu  
**Dosen Pengampu / Evaluator:** Muhammad Aminul Akbar, S.Kom., M.T.

---

## 1. Konsep & Profil Aplikasi yang Dibangun

- **Nama Aplikasi (Rencana):** StudentLife
- **Kategori Aplikasi:** Produktivitas / Manajemen Akademik Mahasiswa (*Personal Academic Organizer*)
- **Deskripsi Singkat Aplikasi:**  
  **StudentLife** adalah aplikasi mobile berbasis Android yang membantu mahasiswa mengelola aktivitas akademik dalam satu platform terpadu, mulai dari manajemen tugas kuliah, jadwal perkuliahan, catatan belajar (termasuk fitur unggah dan lihat gambar), target belajar pribadi, hingga penyimpanan referensi serta tautan materi kuliah. Seluruh data tersimpan secara persisten dan dapat diakses kembali secara *real-time* berbasis *cloud database* melalui **Supabase**.

---

## 2. Informasi Kelompok & Identitas Tim

| No | NIM | Nama Lengkap Anggota | Peran / Jabatan | Nama Fitur Utama yang Ditangani |
|:--:|:---|:---|:---|:---|
| 1 | `245150407111022` | **Gangsar Wijayanto** | Anggota Tim | Fitur 1: Manajemen Tugas Kuliah |
| 2 | `245150401111015` | **Muhammad Abdillah** | **Ketua Kelompok** | Fitur 2: Manajemen Jadwal Kuliah |
| 3 | `245150401111021` | **Prayoga Adi Setyawan** | Anggota Tim | Fitur 3: Catatan Belajar (*Image Upload & Storage*) |
| 4 | `245150407111030` | **Tubagus Fatahudin Mulkan** | Anggota Tim | Fitur 4: Manajemen Target Belajar |
| 5 | `245150401111030` | **Qoid Kafi** | Anggota Tim | Fitur 5: Referensi & Link Materi |

---

## 3. Ketentuan & Aturan Pengerjaan Fitur

Seluruh pengerjaan aplikasi kelompok wajib mengacu pada ketentuan teknis berikut:

1. **Fitur Dasar Aplikasi:**
   - Fitur Login/Register (*Supabase Authentication*) dan Profil Pengguna (*User Profile*) wajib tersedia sebagai fondasi aplikasi.
   - Fitur dasar ini dikerjakan bersama / menjadi infrastruktur umum dan **bukan** merupakan porsi pembagian tugas fitur utama per anggota.
   - Wajib ada minimal 1 fitur yang menerapkan **upload gambar dan dapat dibaca kembali** (pada proyek ini diterapkan pada **Fitur 3: Catatan Belajar** menggunakan Supabase Storage).
2. **Kesesuaian Fitur:**
   - Jumlah fitur utama yang dikembangkan persis sama dengan jumlah anggota kelompok (**1 anggota = 1 fitur utama**), yaitu 5 fitur untuk 5 anggota.
3. **Komponen Data List:**
   - Setiap fitur anggota wajib menyajikan data list menggunakan komponen Modern Jetpack Compose: `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, atau `HorizontalPager` / `ViewPager`.
4. **Navigasi Halaman:**
   - Setiap fitur memiliki alur perpindahan halaman (*multi-screen*) yang jelas dan terstruktur, dengan alur standar:
     $$\text{List Screen} \longrightarrow \text{Detail Screen} \longrightarrow \text{Form Screen (Input / Edit)}$$
5. **Integrasi Supabase / Database (CRUD):**
   - Setiap fitur mengelola data secara langsung ke Supabase:
     - **Create (Insert):** Menambah data baru.
     - **Read:** Membaca data milik pengguna aktif (*filtered by authenticated user*).
     - **Update:** Mengubah data yang sudah tersimpan.
     - **Delete:** Menghapus data.
6. **Arsitektur State & ViewModel:**
   - Manajemen data dan *state UI* wajib menggunakan komponen `ViewModel` Android Architecture Components sesuai standar Modern Android Development (MAD).
7. **Independensi Modul (*Loose Coupling*):**
   - Pengerjaan fitur dilakukan secara mandiri oleh masing-masing anggota.
   - Kolom nama mata kuliah pada setiap fitur dibuat berupa **input teks bebas**, sehingga tidak terjadi dependensi atau *blocking* antar tabel basis data mahasiswa.

---

## 4. Rincian Fitur Utama Per Anggota

### A. Fitur 1 — Penanggung Jawab: Gangsar Wijayanto (NIM: 245150407111022)
* **Nama Fitur:** Manajemen Tugas Kuliah (*Task Management*)
* **Gambaran Fitur:**  
  Pengguna dapat mencatat dan mengelola tugas kuliah berdasarkan nama tugas, mata kuliah (teks bebas), deskripsi, tenggat waktu (*deadline*), dan status pengerjaan (*belum dikerjakan*, *sedang dikerjakan*, *selesai*). Pengguna dapat melihat daftar tugas, menambahkan tugas baru, memperbarui informasi serta status pengerjaan tugas, dan menghapus tugas yang sudah tidak diperlukan.
* **Bentuk Tampilan List:**  
  Menggunakan `LazyColumn` (kartu tugas berisi judul tugas, mata kuliah, tenggat waktu, dan badge status pengerjaan).
* **Alur Navigasi Screen:**
  1. **Halaman Daftar Tugas (`TaskListScreen`):** Menampilkan daftar tugas beserta mata kuliah, tenggat waktu, dan status pengerjaan.
  2. **Halaman Detail Tugas (`TaskDetailScreen`):** Menampilkan rincian tugas secara lengkap (deskripsi, deadline, status) dengan opsi cepat ubah status, tombol navigasi edit, dan hapus.
  3. **Halaman Form (`TaskFormScreen`):** Formulir input untuk menambahkan tugas baru maupun mengedit data tugas yang sudah ada.
* **Aksi Data Supabase (CRUD):**
  * **Read:** Mengambil daftar tugas milik pengguna yang sedang login dari tabel Supabase.
  * **Insert:** Menambah data tugas baru ke basis data.
  * **Update:** Memperbarui data detail tugas atau mengganti status pengerjaan tugas.
  * **Delete:** Menghapus tugas dari basis data.

---

### B. Fitur 2 — Penanggung Jawab: Muhammad Abdillah (NIM: 245150401111015)
* **Nama Fitur:** Manajemen Jadwal Kuliah (*Class Schedule Management*)
* **Gambaran Fitur:**  
  Pengguna dapat mencatat jadwal perkuliahan berdasarkan nama mata kuliah, hari perkuliahan, jam mulai-selesai, ruang kelas/gedung, dan nama dosen pengampu. Pengguna dapat melihat jadwal per hari secara terstruktur dan melakukan pembaruan jika ada perubahan jam atau ruangan.
* **Bentuk Tampilan List:**  
  Kombinasi `HorizontalPager` (tab per hari: Senin s.d. Sabtu) dan `LazyColumn` (daftar mata kuliah pada hari yang sedang aktif dipilih).
* **Alur Navigasi Screen:**
  1. **Halaman Daftar Jadwal (`ScheduleListScreen`):** Pager tab hari dengan list kartu mata kuliah pada hari tersebut.
  2. **Halaman Detail Jadwal (`ScheduleDetailScreen`):** Menampilkan detail perkuliahan lengkap (hari, jam, ruangan, dosen pengampu) dengan opsi edit dan hapus.
  3. **Halaman Form (`ScheduleFormScreen`):** Formulir untuk menambahkan jadwal baru atau mengubah data jadwal yang sudah ada.
* **Aksi Data Supabase (CRUD):**
  * **Read:** Mengambil data jadwal kuliah milik pengguna yang sedang login dari tabel Supabase.
  * **Insert:** Menyimpan jadwal perkuliahan baru ke basis data.
  * **Update:** Memperbarui data jadwal (perubahan jam/ruangan/dosen).
  * **Delete:** Menghapus jadwal dari basis data.

---

### C. Fitur 3 — Penanggung Jawab: Prayoga Adi Setyawan (NIM: 245150401111021)
* **Nama Fitur:** Catatan Belajar (*Study Notes & Image Upload*)
* **Gambaran Fitur:**  
  Pengguna dapat menyimpan catatan materi perkuliahan berdasarkan judul, mata kuliah, isi catatan (*rich content* / teks penjelasan), dan tanggal pembuatan. Fitur ini merupakan fitur khusus yang menerapkan **unggah gambar ke Supabase Storage dan menampilkannya kembali** (misal: foto catatan tangan, slide papan tulis dosen, atau diagram materi).
* **Bentuk Tampilan List:**  
  Menggunakan `LazyVerticalGrid` (tampilan grid kartu catatan yang dilengkapi pratinjau / *thumbnail* gambar).
* **Alur Navigasi Screen:**
  1. **Halaman Daftar Catatan (`NoteListScreen`):** Grid kartu catatan dengan judul, mata kuliah, tanggal, dan thumbnail gambar.
  2. **Halaman Detail Catatan (`NoteDetailScreen`):** Menampilkan judul, mata kuliah, isi catatan lengkap, dan gambar beresolusi penuh dengan opsi edit dan hapus.
  3. **Halaman Form (`NoteFormScreen`):** Formulir input judul, mata kuliah, isi catatan, serta integrasi pemilih gambar (*Image Picker* galeri/kamera) dan tombol simpan yang mengunggah berkas ke Supabase Storage.
* **Aksi Data Supabase (CRUD & Storage):**
  * **Read:** Mengambil data catatan dari database dan memuat URL gambar dari Supabase Storage.
  * **Insert:** Menyimpan metadata catatan ke tabel database dan mengunggah berkas gambar fisik ke bucket Supabase Storage.
  * **Update:** Memperbarui konten catatan atau mengganti berkas gambar lampiran.
  * **Delete:** Menghapus entri catatan di database dan membersihkan berkas gambar terkait di Storage.

---

### D. Fitur 4 — Penanggung Jawab: Tubagus Fatahudin Mulkan (NIM: 245150407111030)
* **Nama Fitur:** Manajemen Target Belajar (*Study Goals & Milestones*)
* **Gambaran Fitur:**  
  Pengguna dapat menyusun dan memantau target pencapaian belajar pribadi, seperti menuntaskan materi bab tertentu, menguasai topik algoritma, atau mempersiapkan ujian sertifikasi/semester. Setiap target memiliki deskripsi target, tenggat waktu, dan persentase progres capaian (0% - 100%).
* **Bentuk Tampilan List:**  
  Menggunakan `LazyColumn` (kartu target dilengkapi bar progress interaktif/visual persentase capaian dan tanggal tenggat).
* **Alur Navigasi Screen:**
  1. **Halaman Daftar Target (`GoalListScreen`):** Menampilkan daftar target belajar berurutan beserta tenggat dan progress bar capaian.
  2. **Halaman Detail Target (`GoalDetailScreen`):** Menampilkan rincian target belajar, status pencapaian, indikator progress, serta opsi edit dan hapus.
  3. **Halaman Form (`GoalFormScreen`):** Formulir untuk membuat target belajar baru atau mengubah rincian target serta memperbarui persentase progress capaiannya.
* **Aksi Data Supabase (CRUD):**
  * **Read:** Mengambil seluruh daftar target belajar milik pengguna yang sedang login.
  * **Insert:** Menambahkan target belajar baru ke basis data.
  * **Update:** Memperbarui isi deskripsi target, tenggat waktu, atau nilai progress pencapaian.
  * **Delete:** Menghapus target belajar dari basis data.

---

### E. Fitur 5 — Penanggung Jawab: Qoid Kafi (NIM: 245150401111030)
* **Nama Fitur:** Referensi & Link Materi (*Academic References & Links*)
* **Gambaran Fitur:**  
  Pengguna dapat menyimpan tautan materi pendukung perkuliahan (jurnal ilmiah, repositori Google Drive, video YouTube/kuliah tamu, atau website modul) dengan rincian: judul referensi, URL tautan, kategori, mata kuliah, dan catatan singkat. Mendukung fitur pencarian judul, filter chip mata kuliah, tombol buka link langsung ke aplikasi eksternal (*Implicit Intent*), serta status favorit (*bookmark*).
* **Bentuk Tampilan List:**  
  Kombinasi `HorizontalPager` (tab kategori: Jurnal, Drive/Dokumen, Video) dengan `LazyColumn` di setiap halaman tab, ditambah `LazyRow` pada bagian atas untuk barisan *chip filter* mata kuliah.
* **Alur Navigasi Screen:**
  1. **Halaman Daftar Referensi (`ReferenceListScreen`):** Tab pager kategori, search bar, row chip filter mata kuliah, dan list referensi.
  2. **Halaman Detail Referensi (`ReferenceDetailScreen`):** Menampilkan informasi lengkap tautan, tombol aksi buka link eksternal (*Implicit Intent* `ACTION_VIEW`), tombol toggle favorit, opsi edit, dan tombol hapus.
  3. **Halaman Form (`ReferenceFormScreen`):** Formulir judul, input URL (dengan validasi format tautan yang valid), pilihan dropdown kategori, nama mata kuliah, dan catatan singkat.
* **Aksi Data Supabase (CRUD):**
  * **Read:** Mengambil seluruh tautan referensi milik pengguna yang login, dengan filter kategori dan status favorit.
  * **Insert:** Menyimpan data tautan referensi baru ke basis data.
  * **Update:** Memperbarui informasi referensi dan melakukan toggle status favorit (*is_favorite*).
  * **Delete:** Menghapus data tautan referensi dari basis data.

---

## 5. Lembar Ceklis Validasi Pengerjaan Individu (Khusus Evaluator / Dosen)

Lembar ceklis ini disiapkan untuk digunakan evaluator/dosen pada saat demonstrasi dan asistensi proyek untuk memeriksa pemenuhan rubrik teknis secara objektif.

**Catatan Evaluator / Dosen:**  
`________________________________________________________________________________________________`  
`________________________________________________________________________________________________`

### A. Ceklis Validasi — Gangsar Wijayanto
* **NIM:** `245150407111022`
* **Fitur:** Fitur 1 — Manajemen Tugas Kuliah

| No | Ketentuan Teknis Proyek | Status Validasi | Catatan Evaluator |
|:--:|:---|:---:|:---|
| 1 | Bukan fitur dasar Login / Profil | [ ] Valid &nbsp; [ ] Belum | Fitur utama independen: Manajemen Tugas |
| 2 | Menampilkan Data List (`LazyList` / `Grid` / `ViewPager`) | [ ] Valid &nbsp; [ ] Belum | Komponen: `LazyColumn` (`TaskListScreen`) |
| 3 | Terdapat Navigasi Halaman *Multi-Screen* | [ ] Valid &nbsp; [ ] Belum | Route: `task_list`, `task_detail/{id}`, `task_form?id={id}` |
| 4 | Mengelola Data Supabase (Read, Insert/Update/Delete) | [ ] Valid &nbsp; [ ] Belum | Tabel: `tasks` |
| 5 | Menggunakan `ViewModel` dalam arsitektur kode | [ ] Valid &nbsp; [ ] Belum | ViewModel Class: `TaskViewModel` |
| 6 | Dikerjakan secara mandiri dalam tim kelompok | [ ] Valid &nbsp; [ ] Belum | Implementasi mandiri pada package `feature/task` |

---

### B. Ceklis Validasi — Muhammad Abdillah
* **NIM:** `245150401111015`
* **Fitur:** Fitur 2 — Manajemen Jadwal Kuliah

| No | Ketentuan Teknis Proyek | Status Validasi | Catatan Evaluator |
|:--:|:---|:---:|:---|
| 1 | Bukan fitur dasar Login / Profil | [ ] Valid &nbsp; [ ] Belum | Fitur utama independen: Jadwal Perkuliahan |
| 2 | Menampilkan Data List (`LazyList` / `Grid` / `ViewPager`) | [ ] Valid &nbsp; [ ] Belum | Komponen: `HorizontalPager` (Hari) + `LazyColumn` |
| 3 | Terdapat Navigasi Halaman *Multi-Screen* | [ ] Valid &nbsp; [ ] Belum | Route: `schedule_list`, `schedule_detail/{id}`, `schedule_form?id={id}` |
| 4 | Mengelola Data Supabase (Read, Insert/Update/Delete) | [ ] Valid &nbsp; [ ] Belum | Tabel: `schedules` |
| 5 | Menggunakan `ViewModel` dalam arsitektur kode | [ ] Valid &nbsp; [ ] Belum | ViewModel Class: `ScheduleViewModel` |
| 6 | Dikerjakan secara mandiri dalam tim kelompok | [ ] Valid &nbsp; [ ] Belum | Implementasi mandiri pada package `feature/schedule` |

---

### C. Ceklis Validasi — Prayoga Adi Setyawan
* **NIM:** `245150401111021`
* **Fitur:** Fitur 3 — Catatan Belajar (*Image Upload & Storage*)

| No | Ketentuan Teknis Proyek | Status Validasi | Catatan Evaluator |
|:--:|:---|:---:|:---|
| 1 | Bukan fitur dasar Login / Profil | [ ] Valid &nbsp; [ ] Belum | Fitur utama: Catatan Belajar + Upload Gambar |
| 2 | Menampilkan Data List (`LazyList` / `Grid` / `ViewPager`) | [ ] Valid &nbsp; [ ] Belum | Komponen: `LazyVerticalGrid` (`NoteListScreen`) |
| 3 | Terdapat Navigasi Halaman *Multi-Screen* | [ ] Valid &nbsp; [ ] Belum | Route: `note_list`, `note_detail/{id}`, `note_form?id={id}` |
| 4 | Mengelola Data Supabase (Read, Insert/Update/Delete) | [ ] Valid &nbsp; [ ] Belum | Tabel: `notes` & Bucket: `note_attachments` |
| 5 | Menggunakan `ViewModel` dalam arsitektur kode | [ ] Valid &nbsp; [ ] Belum | ViewModel Class: `NoteViewModel` |
| 6 | Dikerjakan secara mandiri dalam tim kelompok | [ ] Valid &nbsp; [ ] Belum | Implementasi mandiri pada package `feature/note` |

---

### D. Ceklis Validasi — Tubagus Fatahudin Mulkan
* **NIM:** `245150407111030`
* **Fitur:** Fitur 4 — Manajemen Target Belajar

| No | Ketentuan Teknis Proyek | Status Validasi | Catatan Evaluator |
|:--:|:---|:---:|:---|
| 1 | Bukan fitur dasar Login / Profil | [ ] Valid &nbsp; [ ] Belum | Fitur utama independen: Target & Milestone Belajar |
| 2 | Menampilkan Data List (`LazyList` / `Grid` / `ViewPager`) | [ ] Valid &nbsp; [ ] Belum | Komponen: `LazyColumn` (`GoalListScreen`) |
| 3 | Terdapat Navigasi Halaman *Multi-Screen* | [ ] Valid &nbsp; [ ] Belum | Route: `goal_list`, `goal_detail/{id}`, `goal_form?id={id}` |
| 4 | Mengelola Data Supabase (Read, Insert/Update/Delete) | [ ] Valid &nbsp; [ ] Belum | Tabel: `study_goals` |
| 5 | Menggunakan `ViewModel` dalam arsitektur kode | [ ] Valid &nbsp; [ ] Belum | ViewModel Class: `GoalViewModel` |
| 6 | Dikerjakan secara mandiri dalam tim kelompok | [ ] Valid &nbsp; [ ] Belum | Implementasi mandiri pada package `feature/goal` |

---

### E. Ceklis Validasi — Qoid Kafi
* **NIM:** `245150401111030`
* **Fitur:** Fitur 5 — Referensi & Link Materi

| No | Ketentuan Teknis Proyek | Status Validasi | Catatan Evaluator |
|:--:|:---|:---:|:---|
| 1 | Bukan fitur dasar Login / Profil | [ ] Valid &nbsp; [ ] Belum | Fitur utama independen: Referensi & Link Materi |
| 2 | Menampilkan Data List (`LazyList` / `Grid` / `ViewPager`) | [ ] Valid &nbsp; [ ] Belum | Komponen: `HorizontalPager` + `LazyColumn` + `LazyRow` |
| 3 | Terdapat Navigasi Halaman *Multi-Screen* | [ ] Valid &nbsp; [ ] Belum | Route: `reference_list`, `reference_detail/{id}`, `reference_form?id={id}` |
| 4 | Mengelola Data Supabase (Read, Insert/Update/Delete) | [ ] Valid &nbsp; [ ] Belum | Tabel: `references` |
| 5 | Menggunakan `ViewModel` dalam arsitektur kode | [ ] Valid &nbsp; [ ] Belum | ViewModel Class: `ReferenceViewModel` |
| 6 | Dikerjakan secara mandiri dalam tim kelompok | [ ] Valid &nbsp; [ ] Belum | Implementasi mandiri pada package `feature/reference` |

---

## 6. Lembar Persetujuan & Tanda Tangan

| Ketua Kelompok | Dosen Pengampu / Evaluator |
|:---:|:---:|
| <br><br><br> **Muhammad Abdillah**<br>NIM: `245150401111015` | <br><br><br> **Muhammad Aminul Akbar, S.Kom., M.T.**<br>NIP/NIDN: `___________________________` |
