---
id: ref-modul01-03
title: Teknik Debugging Android (Logcat, Class Log, & Breakpoints)
module: Modul 01 - Setup Gradle, Debug & MainActivity
category: debugging
tags:
  - debugging
  - logcat
  - breakpoints
  - runtime-error
  - logic-error
  - class-log
summary: Panduan mengidentifikasi compile-time error, runtime crash dengan Logcat, dan inspeksi logic error menggunakan breakpoint interaktif di Android Studio.
related:
  - docs/references/07-gradle-setup-and-debugging/01-gradle-sdk-configuration.md
  - docs/references/07-gradle-setup-and-debugging/05-lab-experiments-and-task.md
---

# 🐞 Teknik Debugging Android (Logcat, Class Log, & Breakpoints)

[← Sebelumnya: Version Catalog (TOML) Guide](./02-version-catalog-toml-guide.md) | [Berikutnya: MainActivity & Manifest Entry Point →](./04-mainactivity-and-manifest-entry-point.md)

---

## 1. Tiga Kategori Error dalam Pengembangan Android

```
                                JENIS-JENIS KESALAHAN (ERROR)
                                              │
         ┌────────────────────────────────────┼────────────────────────────────────┐
         ▼                                    ▼                                    ▼
1. Compile-Time Error                 2. Runtime Error                     3. Logic Error
- Tertangkap saat kompilasi           - Crash saat aplikasi dijalankan     - Aplikasi tidak crash
- Contoh: typo sintaks,                - Contoh: pembagian dengan nol       - Namun hasil perhitungan salah
  missing import library                (ArithmeticException: / by zero)     (total = harga + qty)
```

---

## 2. Pemanfaatan Logcat & Class `Log`

**Logcat** adalah konsol terintegrasi di Android Studio yang menampilkan seluruh log sistem Android, exception pelacak (*stack trace*), dan pesan kustom dari aplikasi.

### A. Level Tingkatan Logging Android
```kotlin
import android.util.Log

Log.d("TAG_NAME", "Pesan debug untuk developer") // Debug (Log.d)
Log.i("TAG_NAME", "Informasi umum alur aplikasi") // Info (Log.i)
Log.w("TAG_NAME", "Peringatan potensi masalah")   // Warning (Log.w)
Log.e("TAG_NAME", "Pesan kegagalan fatal/error") // Error (Log.e)
```

### B. Menemukan Runtime Crash dengan Keyword `AndroidRuntime`
Ketika aplikasi tiba-tiba menutup paksa (*crash*):
1. Buka tab **Logcat** (ikon kepala kucing di bagian bawah Android Studio).
2. Ketikkan kata kunci filter: `AndroidRuntime`.
3. Logcat akan memfilter baris berwarna merah dan menampilkan jenis exception beserta baris file yang menyebabkannya:
   ```
   FATAL EXCEPTION: main
   Process: com.example.app, PID: 10844
   java.lang.ArithmeticException: divide by zero
       at com.example.app.MainActivity$onCreate$1.invoke(MainActivity.kt:23)
   ```

---

## 3. Menemukan Logic Error Menggunakan Breakpoints

Jika aplikasi **tidak mengalami crash, tetapi hasil output di layar salah** (misal seharusnya `total = harga * qty`, namun tertulis `total = harga + qty`):
Memasang puluhan perintah `Log.d` akan mengotori kode. Solusi profesional adalah menggunakan **Breakpoints**.

### Langkah Melakukan Debug Interaktif:
1. **Pasang Breakpoint**: Klik pada nomor baris kode di sebelah kiri editor (akan muncul titik merah).
2. **Jalankan Mode Debug**: Klik ikon kumbang hijau di toolbar atas atau gunakan tombol pintas **`Shift + F9`**.
3. **Inspeksi Variabel**: Ketika eksekusi program mencapai baris tersebut, aplikasi akan berhenti sejenak. Jendela **Debug > Threads & Variables** akan menampilkan nilai nyata setiap variabel saat itu (`harga = 10000`, `qty = 3`).
4. **Step Over (`F8`)**: Tekan tombol `F8` untuk menjalankan baris berikutnya selangkah demi selangkah sambil mengamati mutasi nilai `total`.

> [!TIP]
> **Keunggulan Breakpoint dibanding `Log.d`**:  
> Breakpoint memungkinkan developer menghentikan waktu eksekusi secara real-time, menginspeksi seluruh call stack dan variabel memori, tanpa perlu menulis atau menghapus kode logging tambahan.

---
[← Sebelumnya: Version Catalog (TOML) Guide](./02-version-catalog-toml-guide.md) | [Berikutnya: MainActivity & Manifest Entry Point →](./04-mainactivity-and-manifest-entry-point.md)
