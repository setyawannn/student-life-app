---
id: ref-modul03-05
title: Lembar Percobaan, Evaluasi 20 Pertanyaan, & Analisis Modul 3
module: Modul 03 - Event, State & Implisit Intent
category: lab-guide
tags:
  - lab-guide
  - imperative-vs-declarative
  - eventstatetest
  - recomposition-proof
  - outlinedtextfield
  - implicit-intent-test
summary: Dokumentasi pengujian lengkap Modul 3, perbandingan XML vs Compose, bukti eksperimental recomposition, dan kunci jawaban 20 pertanyaan analisis.
related:
  - docs/references/09-event-state-and-implicit-intent/01-imperative-vs-declarative-ui.md
  - docs/references/09-event-state-and-implicit-intent/02-event-handling-and-compose-state.md
  - docs/references/09-event-state-and-implicit-intent/04-implicit-intent-actions-and-uris.md
---

# 🧪 Lembar Percobaan, Evaluasi 20 Pertanyaan, & Analisis Modul 3

[← Sebelumnya: Implisit Intent (Actions & URI Schemes)](./04-implicit-intent-actions-and-uris.md) | [Berikutnya: Panduan Fungsi Lambda & Higher-Order Functions →](../10-kotlin-lambdas-and-hoisting/01-lambda-expressions-and-syntax.md)

---

## 1. Percobaan 1: Perbandingan Imperatif vs Deklaratif

### 🧠 Pembahasan Pertanyaan Evaluasi:
1. **Objek apa yang diubah secara langsung pada project XML/View?**  
   Properti objek visual `TextView` diubah langsung pada baris:  
   `tvMessage.text = "Tombol sudah diklik"`
2. **Nilai apa yang diubah pada project Compose?**  
   Hanya nilai variabel state Kotlin murni:  
   `message = "Tombol sudah diklik"`
3. **Mengapa pada Compose tidak terdapat perintah `setText()` atau `tvMessage.text`?**  
   Karena Compose bersifat deklaratif. Developer tidak mengontrol instance objek widget fisik; Compose secara otomatis memantau state dan menggambar ulang UI (*recomposition*).
4. **Alur Perubahan pada XML/View**:  
   `Event (klik tombol) -> Cari Objek View (findViewById) -> Mutasi Properti Objek -> Perubahan Tampilan`.
5. **Alur Perubahan pada Compose**:  
   `Event (onClick) -> Mutasi Nilai State (message) -> Recomposition -> Perubahan Tampilan (UI = f(state))`.
6. **Apa yang terjadi jika `message` diganti variabel biasa `var message = "Belum diklik"`?**  
   Teks di layar tidak akan pernah berubah ketika tombol diklik, karena variabel biasa tidak dapat dipantau oleh Compose Runtime untuk memicu rekomposisi.

---

## 2. Percobaan 2: Event Handling State (`EventStateTest`)

### Kode Lengkap Eksperimen State & Text Input:
```kotlin
@Composable
fun EventStateTest() {
    var count by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Counter : $count", fontSize = 28.sp)
        
        Button(onClick = {
            count++
            Log.d("TEST", "Counter berubah : $count")
        }) {
            Text("Tambah")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                Log.d("TEST", "Text berubah : $it")
            },
            label = { Text("Text Field Input") }
        )
    }
}
```

### 🧠 Kunci Jawaban Pertanyaan Analisis Logcat & Recomposition:
- **Saat menggunakan `var count = 0` biasa**: Nilai LogCat bertambah `1, 2, 3...`, namun UI layar tetap diam di `Counter : 0`. Hal ini membuktikan mutasi memori lokal tidak mengabarkan apa pun ke UI thread.
- **Saat diganti `var count by remember { mutableStateOf(0) }`**: Tampilan UI seketika berubah sinkron setiap kali tombol diklik.
- **Definisi Recomposition**: Proses pemanggilan ulang fungsi-fungsi Composable yang relevan oleh runtime Compose ketika nilai state yang dibacanya mengalami perubahan.
- **Daftar State pada Program**: `count` (tipe `Int`) dan `text` (tipe `String`).
- **Daftar Event pada Program**: `onClick` pada tombol Tambah dan `onValueChange` pada `OutlinedTextField`.

---

## 3. Percobaan 3: Implisit Intent

### 🧠 Kunci Jawaban Analisis Intent Android:
1. **Perbedaan Action Intent**:
   - `Intent.ACTION_VIEW`: Meminta sistem menampilkan data ke pengguna (membuka URL di browser atau menampilkan koordinat di peta).
   - `Intent.ACTION_DIAL`: Membuka papan tombol telepon (*dialer*) dengan nomor terisi tanpa langsung memanggil panggilan keluar.
   - `Intent.ACTION_SENDTO`: Mengirimkan pesan/email ke kontak penerima tertentu.
2. **Fungsi Data URI pada Intent**:  
   Menentukan target spesifik dan tipe skema data (`https://`, `tel:`, `mailto:`, `geo:`).
3. **Hubungan Action dan Data**:  
   Sistem operasi Android membaca kombinasi **Action + Skema Data** untuk mencocokkan filter pada `AndroidManifest.xml` dari seluruh aplikasi yang terpasang di perangkat. Misalnya, aksi `ACTION_VIEW` dengan skema `geo:` hanya akan ditawarkan ke aplikasi peta (*Google Maps*), bukan ke aplikasi browser.

---
[← Sebelumnya: Implisit Intent (Actions & URI Schemes)](./04-implicit-intent-actions-and-uris.md) | [Berikutnya: Panduan Fungsi Lambda & Higher-Order Functions →](../10-kotlin-lambdas-and-hoisting/01-lambda-expressions-and-syntax.md)
