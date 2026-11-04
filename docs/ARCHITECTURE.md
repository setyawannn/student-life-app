# 🏛️ ARSITEKTUR SISTEM & STANDAR PENGEMBANGAN — STUDENTLIFE

Dokumen arsitektur lengkap telah ditempatkan di root repositori: [ARCHITECTURE.md](../ARCHITECTURE.md).

Silakan merujuk ke dokumen tersebut atau ringkasan topik di bawah ini:
1. **Identifikasi Tech Stack**: Kotlin 2.2.10, Jetpack Compose, Material 3, Android Lifecycle ViewModel & StateFlow, Supabase (Postgrest, Auth, Storage), Ktor, Coil Compose, Gradle 9.5 & AGP 9.3.3.
2. **File Management**: Pola Feature-First Clean Architecture (`core/` vs `features/`), pemisahan `data`, `model`, `ui`, `navigation` di setiap modul fitur anggota.
3. **Mekanisme Penyimpanan Media & Gambar**: Kompresi klien `ImageCompressor.kt`, format path Supabase Storage `{userId}/notes/{UUID}.jpg`, penampil gambar reaktif Coil `AsyncImage`.
4. **Standarisasi Penamaan**: Standar PascalCase, camelCase, snake_case untuk Kotlin, Compose, Database Postgrest, dan format commit Git.
5. **Prinsip DRY & Reusable Components**: Komponen bersama di `core/designsystem/components/` (`AppTopBar`, `AppButton`, `AppTextField`, `EmptyStateView`, `ConfirmDialog`, `StatusBadge`).
6. **Sistem Desain & Styling**: Material 3 Design Tokens, Color Palette Semantic, Sistem Spasi Grid 8-Point, dan Aksesibilitas Target Sentuh 48dp.
7. **Pola Alur Data & State Management**: Implementasi Unidirectional Data Flow (UDF) lengkap dengan `Resource<T>`, `UiState`, dan `StateFlow`.
8. **Matriks Validasi Tim**: Ceklis kelayakan kode sebelum penggabungan branch Git.
