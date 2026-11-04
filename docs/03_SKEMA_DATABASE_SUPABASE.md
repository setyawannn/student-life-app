# SKEMA BASIS DATA & SUPABASE CONFIGURATION

Dokumen ini berisi seluruh skrip SQL Data Definition Language (DDL), konfigurasi **Row Level Security (RLS)**, serta pengaturan **Supabase Storage** untuk aplikasi **StudentLife**. Skema ini dirancang independen (*loosely coupled*) agar tidak ada ketergantungan antar tabel yang dapat menghambat kerja masing-masing anggota.

---

## 1. Skrip SQL DDL Lengkap (PostgreSQL Supabase)

Jalankan perintah SQL berikut pada **Supabase SQL Editor** proyek Anda:

```sql
-- ==============================================================================
-- 0. EXTENSION & ENUM TYPES
-- ==============================================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enum Status Pengerjaan Tugas (Fitur 1)
CREATE TYPE task_status AS ENUM ('TODO', 'IN_PROGRESS', 'COMPLETED');

-- Enum Hari Perkuliahan (Fitur 2)
CREATE TYPE day_name AS ENUM ('SENIN', 'SELASA', 'RABU', 'KAMIS', 'JUMAT', 'SABTU');

-- Enum Kategori Referensi (Fitur 5)
CREATE TYPE reference_category AS ENUM ('JURNAL', 'DRIVE', 'VIDEO');

-- ==============================================================================
-- 1. TABEL DASAR: PROFIL MAHASISWA (Infrastruktur Bersama)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    nim VARCHAR(20) NOT NULL,
    avatar_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

-- ==============================================================================
-- 2. TABEL FITUR 1: MANAJEMEN TUGAS KULIAH (Gangsar Wijayanto)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    course_name VARCHAR(150) NOT NULL, -- Kolom teks bebas (tidak terikat tabel lain)
    description TEXT,
    due_date TIMESTAMPTZ NOT NULL,
    status task_status DEFAULT 'TODO' NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

-- Index untuk mempercepat query per user dan status
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON public.tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON public.tasks(due_date);

-- ==============================================================================
-- 3. TABEL FITUR 2: MANAJEMEN JADWAL KULIAH (Muhammad Abdillah)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.schedules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_name VARCHAR(150) NOT NULL, -- Kolom teks bebas
    day_of_week day_name NOT NULL,
    start_time TIME NOT NULL,           -- Format '07:30:00'
    end_time TIME NOT NULL,             -- Format '10:00:00'
    room VARCHAR(50) NOT NULL,          -- Ruangan / Gedung kelas
    lecturer_name VARCHAR(150) NOT NULL,-- Nama dosen pengampu
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_schedules_user_day ON public.schedules(user_id, day_of_week);

-- ==============================================================================
-- 4. TABEL FITUR 3: CATATAN BELAJAR + GAMBAR (Prayoga Adi Setyawan)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.notes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    course_name VARCHAR(150) NOT NULL, -- Kolom teks bebas
    content TEXT NOT NULL,
    image_url TEXT,                    -- URL berkas di Supabase Storage
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notes_user_id ON public.notes(user_id);

-- ==============================================================================
-- 5. TABEL FITUR 4: TARGET BELAJAR & PROGRESS (Tubagus Fatahudin Mulkan)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.study_goals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    deadline TIMESTAMPTZ NOT NULL,
    progress INT DEFAULT 0 NOT NULL CHECK (progress >= 0 AND progress <= 100),
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_goals_user_id ON public.study_goals(user_id);

-- ==============================================================================
-- 6. TABEL FITUR 5: REFERENSI & LINK MATERI (Qoid Kafi)
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.references (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    url TEXT NOT NULL,
    category reference_category NOT NULL,
    course_name VARCHAR(150) NOT NULL, -- Kolom teks bebas untuk filter chip
    notes TEXT,
    is_favorite BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_references_user_category ON public.references(user_id, category);
CREATE INDEX IF NOT EXISTS idx_references_favorite ON public.references(user_id, is_favorite);
```

---

## 2. Keamanan Data: Row Level Security (RLS)

Seluruh tabel wajib dilindungi dengan **RLS**. Ini memastikan data milik mahasiswa A **tidak dapat dibaca, diubah, atau dihapus** oleh mahasiswa B.

```sql
-- Mengaktifkan RLS pada seluruh tabel
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.schedules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.study_goals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.references ENABLE ROW LEVEL SECURITY;

-- -----------------------------------------------------------------------------
-- Kebijakan RLS: PROFILES
-- -----------------------------------------------------------------------------
CREATE POLICY "Users can view own profile" 
ON public.profiles FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update own profile" 
ON public.profiles FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile" 
ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);

-- -----------------------------------------------------------------------------
-- Kebijakan RLS: TASKS (Gangsar Wijayanto)
-- -----------------------------------------------------------------------------
CREATE POLICY "Users can manage own tasks" 
ON public.tasks FOR ALL 
USING (auth.uid() = user_id) 
WITH CHECK (auth.uid() = user_id);

-- -----------------------------------------------------------------------------
-- Kebijakan RLS: SCHEDULES (Muhammad Abdillah)
-- -----------------------------------------------------------------------------
CREATE POLICY "Users can manage own schedules" 
ON public.schedules FOR ALL 
USING (auth.uid() = user_id) 
WITH CHECK (auth.uid() = user_id);

-- -----------------------------------------------------------------------------
-- Kebijakan RLS: NOTES (Prayoga Adi Setyawan)
-- -----------------------------------------------------------------------------
CREATE POLICY "Users can manage own notes" 
ON public.notes FOR ALL 
USING (auth.uid() = user_id) 
WITH CHECK (auth.uid() = user_id);

-- -----------------------------------------------------------------------------
-- Kebijakan RLS: STUDY_GOALS (Tubagus Fatahudin Mulkan)
-- -----------------------------------------------------------------------------
CREATE POLICY "Users can manage own goals" 
ON public.study_goals FOR ALL 
USING (auth.uid() = user_id) 
WITH CHECK (auth.uid() = user_id);

-- -----------------------------------------------------------------------------
-- Kebijakan RLS: REFERENCES (Qoid Kafi)
-- -----------------------------------------------------------------------------
CREATE POLICY "Users can manage own references" 
ON public.references FOR ALL 
USING (auth.uid() = user_id) 
WITH CHECK (auth.uid() = user_id);
```

---

## 3. Konfigurasi Supabase Storage (Khusus Fitur 3: Catatan Belajar)

Untuk memenuhi rubrik:
> *"Wajib ada 1 fitur yang menerapkan upload gambar dan dapat dibaca kembali (pada proyek ini diterapkan pada fitur Catatan Belajar)"*

### Langkah Konfigurasi Bucket di Supabase:
1. Buka dashboard **Supabase $\rightarrow$ Storage**.
2. Buat bucket baru bernama: `note_attachments` (atau `note-images`).
3. Centang opsi **Public Bucket** agar gambar dapat di-load secara efisien menggunakan library seperti `Coil-compose`.
4. Jalankan policy RLS storage berikut di SQL Editor:

```sql
-- Izinkan authenticated user mengunggah file ke bucket note_attachments
CREATE POLICY "Allow authenticated users to upload note images"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'note_attachments');

-- Izinkan authenticated user memperbarui gambar miliknya
CREATE POLICY "Allow users to update own note images"
ON storage.objects FOR UPDATE
TO authenticated
USING (bucket_id = 'note_attachments' AND owner = auth.uid());

-- Izinkan pengguna membaca gambar catatan
CREATE POLICY "Allow public read access to note images"
ON storage.objects FOR SELECT
USING (bucket_id = 'note_attachments');

-- Izinkan pengguna menghapus file gambar catatan miliknya
CREATE POLICY "Allow users to delete own note images"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'note_attachments' AND owner = auth.uid());
```

---

## 4. Model Data Kotlin (DTO Serialization)

Gunakan library `kotlinx.serialization` untuk memetakan respon Supabase Postgrest ke model Kotlin:

### A. Fitur 1: `TaskDto.kt`
```kotlin
package com.example.student_life_app.features.task.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("description") val description: String? = null,
    @SerialName("due_date") val dueDate: String,
    @SerialName("status") val status: String = "TODO",
    @SerialName("created_at") val createdAt: String? = null
)
```

### B. Fitur 2: `ScheduleDto.kt`
```kotlin
package com.example.student_life_app.features.schedule.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("day_of_week") val dayOfWeek: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    @SerialName("room") val room: String,
    @SerialName("lecturer_name") val lecturerName: String,
    @SerialName("created_at") val createdAt: String? = null
)
```

### C. Fitur 3: `NoteDto.kt`
```kotlin
package com.example.student_life_app.features.note.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("content") val content: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
```

### D. Fitur 4: `GoalDto.kt`
```kotlin
package com.example.student_life_app.features.goal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("deadline") val deadline: String,
    @SerialName("progress") val progress: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)
```

### E. Fitur 5: `ReferenceDto.kt`
```kotlin
package com.example.student_life_app.features.reference.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferenceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("category") val category: String,
    @SerialName("course_name") val courseName: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("is_favorite") val isFavorite: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)
```
