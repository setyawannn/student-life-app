---
id: ref-modul01-04
title: MainActivity dan Pengaturan Entry Point di AndroidManifest.xml
module: Modul 01 - Setup Gradle, Debug & MainActivity
category: android-manifest
tags:
  - android-manifest
  - main-activity
  - entry-point
  - intent-filter
  - action-main
  - category-launcher
  - android-exported
summary: Penjelasan peran AndroidManifest.xml dalam menentukan entry point aplikasi via intent-filter MAIN dan LAUNCHER, serta konfigurasi android:exported.
related:
  - docs/references/07-gradle-setup-and-debugging/03-debugging-logcat-and-breakpoints.md
  - docs/references/07-gradle-setup-and-debugging/05-lab-experiments-and-task.md
  - docs/references/08-activity-lifecycle-and-basic-ui/01-activity-lifecycle-transitions.md
---

# 🚪 MainActivity dan Pengaturan Entry Point di AndroidManifest.xml

[← Sebelumnya: Debugging, Logcat, & Breakpoints](./03-debugging-logcat-and-breakpoints.md) | [Berikutnya: Lab Praktikum & Tugas Mandiri Modul 1 →](./05-lab-experiments-and-task.md)

---

## 1. Peran `AndroidManifest.xml`

File `AndroidManifest.xml` adalah **dokumen metadata utama** bagi sistem operasi Android. Dokumen ini mendeskripsikan:
- Nama paket aplikasi (*package name / namespace*).
- Izin akses perangkat keras & jaringan (`<uses-permission>`).
- Seluruh komponen inti aplikasi: **Activity**, **Service**, **Broadcast Receiver**, dan **Content Provider**.

---

## 2. Bagaimana Sistem Android Menentukan Layar Pertama (*Entry Point*)?

Sistem Android tidak secara otomatis mengetahui file mana yang harus dibuka pertama kali saat pengguna menekan ikon aplikasi di home screen HP.

Sistem mencari elemen `<activity>` yang memiliki deklarasi tag `<intent-filter>` khusus:

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.StudentLifeApp">
    
    <!-- Penanda Entry Point Aplikasi -->
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
    
</activity>
```

### Penjelasan Elemen Penting:
1. **`<action android:name="android.intent.action.MAIN" />`**:  
   Memberitahu OS bahwa Activity ini bertindak sebagai titik masuk utama (*Main Entry Point*) dan tidak memerlukan data inisialisasi awal.
2. **`<category android:name="android.intent.category.LAUNCHER" />`**:  
   Memerintahkan OS untuk memunculkan ikon aplikasi ini di daftar aplikasi (*App Drawer / Home Screen*) ponsel pengguna.
3. **`android:exported="true"`**:  
   Sejak Android 12 (API level 31+), setiap komponen Activity yang memiliki `<intent-filter>` **wajib** mendefinisikan atribut `android:exported="true"` secara eksplisit agar dapat diluncurkan oleh Launcher sistem.

---

## 3. Eksperimen Memindahkan Main Activity

Jika aplikasi Anda memiliki dua Activity (`MainActivity` dan `MainActivity2`), dan Anda ingin menjadikan `MainActivity2` sebagai layar awal:

1. Ubah `MainActivity` menjadi `android:exported="false"` dan hapus blok `<intent-filter>` dari dalamnya.
2. Tambahkan atribut `android:exported="true"` pada `MainActivity2`.
3. Pindahkan seluruh blok `<intent-filter>` ke dalam tag `MainActivity2`:

```xml
<application ...>
    <!-- Activity Biasa (Bukan Entry Point) -->
    <activity
        android:name=".MainActivity"
        android:exported="false" />

    <!-- Activity Utama Baru yang Muncul Pertama Kali -->
    <activity
        android:name=".MainActivity2"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

Saat aplikasi dijalankan ulang, OS Android akan langsung meluncurkan `MainActivity2` sebagai antarmuka pertama.

---
[← Sebelumnya: Debugging, Logcat, & Breakpoints](./03-debugging-logcat-and-breakpoints.md) | [Berikutnya: Lab Praktikum & Tugas Mandiri Modul 1 →](./05-lab-experiments-and-task.md)
