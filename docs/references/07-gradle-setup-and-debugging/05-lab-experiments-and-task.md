---
id: ref-modul01-05
title: Lembar Percobaan, Tugas Mandiri, & Evaluasi Modul 1
module: Modul 01 - Setup Gradle, Debug & MainActivity
category: lab-guide
tags:
  - lab-guide
  - sdk-testing
  - coil-asyncimage
  - division-by-zero
  - version-catalog-task
  - manifest-launcher
summary: Dokumentasi langkah eksperimen praktikum Modul 1, troubleshooting error pembagian nol, integrasi Coil AsyncImage, dan solusi tugas mandiri.
related:
  - docs/references/07-gradle-setup-and-debugging/01-gradle-sdk-configuration.md
  - docs/references/07-gradle-setup-and-debugging/02-version-catalog-toml-guide.md
  - docs/references/07-gradle-setup-and-debugging/03-debugging-logcat-and-breakpoints.md
  - docs/references/07-gradle-setup-and-debugging/04-mainactivity-and-manifest-entry-point.md
---

# 🧪 Lembar Percobaan, Tugas Mandiri, & Evaluasi Modul 1

[← Sebelumnya: MainActivity & Manifest Entry Point](./04-mainactivity-and-manifest-entry-point.md) | [Berikutnya: Modul 2 - Activity Lifecycle →](../08-activity-lifecycle-and-basic-ui/01-activity-lifecycle-transitions.md)

---

## 1. Pembahasan Latihan Praktik 1.3: Setup SDK

### Uji Coba Hubungan SDK:
- Ketika `compileSdk = 34` tetapi `minSdk = 36`:  
  **Hasil**: Gradle Build gagal (*Sync / Compile Error*).  
  **Penyebab**: Compiler Android SDK 34 tidak memiliki definisi kelas dan API untuk perangkat berspesifikasi SDK 36.
- **Relasi Wajib**:
  $$\text{minSdk} \le \text{targetSdk} \le \text{compileSdk}$$

---

## 2. Pembahasan Latihan Praktik 1.4: Debugging Error

### Kasus 1: Runtime Error (ArithmeticException)
```kotlin
setContent {
    val angka = 10 / 0 // Memicu crash saat aplikasi dijalankan
    Text(text = "Hasil: $angka")
}
```
- **Jenis Exception**: `java.lang.ArithmeticException: divide by zero`.
- **Keyword Pelacak di Logcat**: `AndroidRuntime`.
- **Solusi**: Jangan pernah membagi bilangan dengan nilai nol; lakukan validasi atau berikan nilai default pembagi yang lebih besar dari nol.

### Kasus 2: Logic Error (Penjumlahan alih-alih Perkalian)
```kotlin
val harga = 10000
val qty = 3
val total = harga + qty // Salah logika: seharusnya harga * qty
```
- **Solusi**: Pasang breakpoint pada baris `val qty = 3` menggunakan **`Shift + F9`**. Amati variabel pada jendela Debug, lalu tekan **`F8` (Step Over)** untuk menemukan baris perhitungan yang keliru.

---

## 3. Pembahasan Latihan Praktik 1.5: Migrasi ke Version Catalog

### Langkah Migrasi Library Retrofit:
1. Tambahkan di `gradle/libs.versions.toml`:
   ```toml
   [versions]
   retrofit = "3.0.0"

   [libraries]
   retrofit-core = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
   retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
   ```
2. Ganti deklarasi di `app/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.retrofit.core)
       implementation(libs.retrofit.gson)
   }
   ```
3. Klik **Sync Now**.

---

## 4. Solusi Tugas Praktik 1.7: AsyncImage & Coil Integration

### A. Konfigurasi SDK & Izin Internet
1. Buka `app/build.gradle.kts` dan atur:
   ```kotlin
   android {
       compileSdk = 36
       defaultConfig {
           minSdk = 26
           targetSdk = 36
       }
   }
   ```
2. Tambahkan izin internet di `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

### B. Registrasi Library Coil di Version Catalog
Di `gradle/libs.versions.toml`:
```toml
[versions]
coil = "2.7.0"

[libraries]
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
```
Di `app/build.gradle.kts`:
```kotlin
implementation(libs.coil.compose)
```

### C. Kode Lengkap Composable `ActivityUtama.kt` (Perbaikan Division by Zero)
Agar variabel hasil bernilai 20 dari perhitungan `100 / angka`, maka nilai variabel `angka` harus diubah dari `0` menjadi `5`:

```kotlin
class ActivityUtama : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Nilai angka diubah menjadi 5 agar 100 / 5 = 20 (bebas division by zero)
            val angka = 5

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tugas Praktikum Compose",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Memuat gambar dari URL dengan fallback ke Vector Asset lokal
                AsyncImage(
                    model = "https://picsum.photos/800/600",
                    contentDescription = "Gambar Random Internet",
                    placeholder = painterResource(R.drawable.outline_10mp_24),
                    error = painterResource(R.drawable.outline_10mp_24),
                    modifier = Modifier.size(250.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val hasil = 100 / angka
                Text(
                    text = "Hasil: $hasil", // Menampilkan "Hasil: 20"
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
```

---
[← Sebelumnya: MainActivity & Manifest Entry Point](./04-mainactivity-and-manifest-entry-point.md) | [Berikutnya: Modul 2 - Activity Lifecycle →](../08-activity-lifecycle-and-basic-ui/01-activity-lifecycle-transitions.md)
