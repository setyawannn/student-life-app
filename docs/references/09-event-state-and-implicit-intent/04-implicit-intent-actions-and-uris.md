---
id: ref-modul03-04
title: Integrasi Implisit Intent (Actions & URI Schemes) di Compose
module: Modul 03 - Event, State & Implisit Intent
category: android-intent
tags:
  - implicit-intent
  - intent-action-view
  - intent-action-dial
  - intent-action-sendto
  - uri-schemes
  - intent-chooser
summary: Panduan pemanggilan aplikasi eksternal (Browser, Telepon, Email, Google Maps) menggunakan Implicit Intent dan skema URI pada Jetpack Compose.
related:
  - docs/references/09-event-state-and-implicit-intent/03-property-delegation-by-keyword.md
  - docs/references/09-event-state-and-implicit-intent/05-lab-experiments-and-recomposition-analysis.md
  - docs/references/08-activity-lifecycle-and-basic-ui/01-activity-lifecycle-transitions.md
---

# 📲 Integrasi Implisit Intent (Actions & URI Schemes) di Compose

[← Sebelumnya: Property Delegation (by Keyword)](./03-property-delegation-by-keyword.md) | [Berikutnya: Lab Praktikum & Evaluasi Modul 3 →](./05-lab-experiments-and-recomposition-analysis.md)

---

## 1. Perbedaan: Explicit Intent vs Implicit Intent

- **Explicit Intent**: Mengarahkan secara spesifik ke komponen kelas di dalam proyek sendiri (contoh: `Intent(this, SecondActivity::class.java)`).  
  *Pada arsitektur Jetpack Compose modern dengan Single Activity, perpindahan antarlayar ditangani oleh Navigation Compose, bukan Explicit Intent.*
- **Implicit Intent**: Meminta sistem Android untuk menjalankan tindakan umum tanpa mengetahui aplikasi spesifik apa yang akan melayaninya. OS Android akan mencocokkan tindakan (*Action*) dan data (*URI Scheme*) dengan aplikasi yang terpasang di HP pengguna.

---

## 2. Anatomi Implicit Intent: Pasangan `Action` & `Data URI`

Sistem Android menentukan aplikasi mana yang berhak menangani intent berdasarkan kombinasi **Action (Aksi)** dan **Data (Skema URI)**:

```
[Intent Action]             [Data URI Scheme]            [Aplikasi yang Muncul di HP]
Intent.ACTION_VIEW    +    https://...             ──>  Chrome, Firefox, Browser Bawaan
Intent.ACTION_DIAL    +    tel:08123456789         ──>  Dialer Telepon (Keypad)
Intent.ACTION_SENDTO  +    mailto:info@domain.com  ──>  Gmail, Outlook, Yahoo Mail
Intent.ACTION_VIEW    +    geo:-7.25,112.76        ──>  Google Maps, Waze
```

---

## 3. Implementasi 4 Kasus Nyata di Jetpack Compose

Untuk meluncurkan intent di Compose, gunakan `LocalContext.current`:

```kotlin
@Composable
fun ImplicitIntentTest() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Membuka Browser Web (ACTION_VIEW + https)
        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.google.com")
            }
            context.startActivity(intent)
        }) {
            Text("Buka Browser")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Membuka Panggilan Telepon (ACTION_DIAL + tel:)
        Button(onClick = {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:081234567890")
            }
            context.startActivity(intent)
        }) {
            Text("Buka Telepon")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Mengirim Email dengan Chooser Dialog (ACTION_SENDTO + mailto:)
        Button(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@studentlifeapp.com")
            }
            // createChooser memaksa OS menampilkan dialog pilihan aplikasi
            val chooser = Intent.createChooser(intent, "Pilih aplikasi email:")
            context.startActivity(chooser)
        }) {
            Text("Kirim Email")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Membuka Lokasi Koordinat Peta (ACTION_VIEW + geo:)
        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:-7.250445,112.768845")
            }
            context.startActivity(intent)
        }) {
            Text("Buka Maps")
        }
    }
}
```

---

## 4. Peran `Intent.createChooser`

Jika pengguna sudah menetapkan aplikasi default (misalnya Chrome untuk semua link), intent biasa akan langsung membukanya tanpa bertanya.  
Dengan membungkus intent di dalam **`Intent.createChooser(intent, "Judul Dialog")`**, aplikasi memaksa sistem Android selalu memunculkan jendela pop-up pilihan aplikasi, memberikan kenyamanan fleksibilitas bagi pengguna.

---
[← Sebelumnya: Property Delegation (by Keyword)](./03-property-delegation-by-keyword.md) | [Berikutnya: Lab Praktikum & Evaluasi Modul 3 →](./05-lab-experiments-and-recomposition-analysis.md)
