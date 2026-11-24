---
id: ref-modul02-01
title: Transisi Siklus Hidup Activity (Activity Lifecycle Transitions)
module: Modul 02 - Activity Lifecycle, Composable Layout & Modifier
category: lifecycle
tags:
  - activity-lifecycle
  - oncreate
  - onresume
  - onpause
  - onstop
  - ondestroy
  - intent-transition
summary: Penjelasan siklus hidup Activity di Android, urutan transisi callback saat aplikasi dibuka, dialihkan ke aplikasi eksternal (WhatsApp), dan ditutup.
related:
  - docs/references/08-activity-lifecycle-and-basic-ui/02-composable-layouts-column-row-box.md
  - docs/references/08-activity-lifecycle-and-basic-ui/04-lab-contact-us-and-profile.md
  - docs/references/01-state-and-lifecycle/02-configuration-change-and-persistence.md
---

# 🔄 Transisi Siklus Hidup Activity (Activity Lifecycle Transitions)

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Composable Layouts (Column, Row, Box) →](./02-composable-layouts-column-row-box.md)

---

## 1. Diagram State Siklus Hidup Activity

Activity di Android diatur secara ketat oleh sistem operasi melalui serangkaian method callback:

```
[Pengguna Membuka Aplikasi]
           │
           ▼
      onCreate()      ──> Inisialisasi awal, panggil setContent { }
           │
           ▼
       onStart()      ──> UI mulai terlihat oleh mata pengguna
           │
           ▼
      onResume()      ──> [RESUMED / RUNNING] Pengguna dapat berinteraksi (mengetik/klik)
           │
           │ (Pengguna Membuka Aplikasi Lain, misal: WhatsApp)
           ▼
       onPause()      ──> Aplikasi kehilangan fokus, UI masih sebagian terlihat
           │
           ▼
       onStop()       ──> [STOPPED] Aplikasi berada di background (tidak terlihat)
           │
   ┌───────┴───────┐
   │               │
(Kembali ke App)   (Aplikasi Dimatikan oleh User / Sistem OOM)
   │               │
   ▼               ▼
onRestart()    onDestroy() ──> [DESTROYED] Activity dihapus dari memori
   │
   ▼
onStart() -> onResume()
```

---

## 2. Urutan Callback pada Skenario Nyata

Berdasarkan pengujian Logcat pada latihan praktikum:

### Skenario 1: Aplikasi Pertama Kali Dibuka
Sistem menjalankan tiga callback secara berurutan:
1. `onCreate dipanggil`
2. `onStart dipanggil`
3. `onResume dipanggil` *(Aplikasi berada di status aktif Resumed)*

### Skenario 2: Menekan Tombol "Hubungi via WhatsApp" (Pindah ke Aplikasi Lain)
Saat Intent eksternal meluncurkan WhatsApp:
1. `onPause dipanggil` *(Aplikasi kita kehilangan fokus input)*
2. `onStop dipanggil` *(Aplikasi kita sepenuhnya tertutup oleh layar WhatsApp)*

### Skenario 3: Menekan Tombol Back dari WhatsApp (Kembali ke Aplikasi)
Ketika pengguna kembali ke aplikasi kita:
1. `onRestart dipanggil`
2. `onStart dipanggil`
3. `onResume dipanggil` *(Aplikasi kembali siap menerima interaksi pengguna)*

### Skenario 4: Menutup Aplikasi dengan Tombol Back Sistem
1. `onPause dipanggil`
2. `onStop dipanggil`
3. `onDestroy dipanggil` *(Activity dihancurkan secara permanen)*

---

## 3. Titik Kritis: Di Mana Menyimpan Draft Data Sementara?

> [!IMPORTANT]
> **Analisis Siklus Hidup**:  
> Jika pengguna sedang mengetik formulir atau pesan lalu beralih ke aplikasi lain (seperti WhatsApp), callback mana yang paling tepat di-override untuk menyimpan draft data sementara agar tidak hilang?  
> **Jawabannya adalah `onPause()` atau `onStop()`!**  
> - `onPause()` dieksekusi secara instan begitu pengguna mulai meninggalkan layar.  
> - Jangan menunggu hingga `onDestroy()` karena jika sistem Android kekurangan RAM (*Low Memory Killer*), sistem dapat langsung mematikan proses di status `onStop()` tanpa sempat memanggil `onDestroy()`.

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Composable Layouts (Column, Row, Box) →](./02-composable-layouts-column-row-box.md)
