# DOKUMENTASI RESMI PENGEMBANGAN APLIKASI STUDENTLIFE

Selamat datang di pusat dokumentasi resmi pengembangan aplikasi mobile **StudentLife**. Proyek ini dikembangkan menggunakan **Kotlin**, **Jetpack Compose**, **Android Architecture Components (ViewModel, StateFlow)**, dan **Supabase** sebagai *backend-as-a-service* *real-time*.

---

## 📌 Indeks Dokumen Panduan

Silakan pelajari dokumen-dokumen berikut sesuai dengan kebutuhan peran dan pengerjaan fitur Anda:

| Dokumen | Deskripsi Utama | Pembaca Utama |
|:---|:---|:---|
| 🏛️ [ARCHITECTURE.md (Spesifikasi Standar)](../ARCHITECTURE.md) | **Dokumen Standar Arsitektur Inti**: Identifikasi tech stack, file management, mekanisme penyimpanan gambar, konvensi penamaan, prinsip DRY, reusable design system, dan panduan styling Material 3. | Seluruh Tim Pengembang |
| 📄 [01. Proposal Lengkap Proyek](01_PROPOSAL_LENGKAP.md) | Dokumen proposal resmi, profil aplikasi, rincian 5 fitur anggota, aturan pengerjaan, dan **Lembar Ceklis Validasi Evaluator / Dosen**. | Seluruh Tim & Dosen Evaluator |
| 🏗️ [02. Arsitektur & Struktur Folder](02_ARSITEKTUR_DAN_STRUKTUR_FOLDER.md) | Standar arsitektur *Feature-First Clean Architecture*, panduan pencegahan konflik Git, standar penamaan file, dan pola Unidirectional Data Flow (UDF). | Seluruh Pengembang |
| 🗄️ [03. Skema Basis Data & Supabase](03_SKEMA_DATABASE_SUPABASE.md) | Skrip SQL DDL tabel (`tasks`, `schedules`, `notes`, `study_goals`, `references`), aturan Row Level Security (RLS), konfigurasi Supabase Storage, dan model DTO Kotlin. | Seluruh Pengembang |
| 👨‍💻 [04. Panduan Fitur Per Anggota](04_PANDUAN_FITUR_TIM.md) | Panduan teknis mendalam per fitur anggota (Gangsar, Abdillah, Prayoga, Tubagus, Qoid), rincian komponen UI (LazyColumn, HorizontalPager, LazyVerticalGrid, LazyRow), alur navigasi multi-screen, dan ceklis mandiri asistensi. | Masing-masing Penanggung Jawab Fitur |
| ⚙️ [05. Setup Gradle & Dependensi](05_SETUP_DAN_DEPENDENCY_GRADLE.md) | Panduan konfigurasi `libs.versions.toml`, `build.gradle.kts`, izin `AndroidManifest.xml`, dan inisialisasi singleton `SupabaseClient`. | Technical Lead / Seluruh Anggota |

---

## 👥 Ringkasan Tim & Pembagian Fitur Utama

| No | NIM | Nama Anggota | Fitur Utama | Komponen UI Wajib | Tabel Database / Storage |
|:--:|:---|:---|:---|:---|:---|
| 1 | `245150407111022` | **Gangsar Wijayanto** | Manajemen Tugas Kuliah | `LazyColumn` | `tasks` |
| 2 | `245150401111015` | **Muhammad Abdillah** (Ketua) | Manajemen Jadwal Kuliah | `HorizontalPager` + `LazyColumn` | `schedules` |
| 3 | `245150401111021` | **Prayoga Adi Setyawan** | Catatan Belajar | `LazyVerticalGrid` | `notes` + Bucket `note_attachments` |
| 4 | `245150407111030` | **Tubagus Fatahudin Mulkan** | Manajemen Target Belajar | `LazyColumn` + Progress Indicator | `study_goals` |
| 5 | `245150401111030` | **Qoid Kafi** | Referensi & Link Materi | `HorizontalPager` + `LazyColumn` + `LazyRow` | `references` |

---

## 🚀 Alur Navigasi Standar Tiap Fitur

Semua fitur mengimplementasikan pola navigasi 3 layar terstruktur:
$$\text{Daftar Data (List/Grid/Pager)} \longrightarrow \text{Rincian Data (Detail)} \longrightarrow \text{Formulir (Input / Edit)}$$

Setiap anggota bertanggung jawab merawat layarnya sendiri di dalam folder `features/<nama_fitur>/` tanpa mengubah layar anggota lain.
