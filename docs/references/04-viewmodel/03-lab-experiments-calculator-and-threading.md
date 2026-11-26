---
id: ref-vm-03
title: Lembar Percobaan dan Analisis Modul 7 (ViewModel & Concurrency)
module: Modul 07 - ViewModel & Coroutine
category: lab-guide
tags:
  - lab-guide
  - viewmodel
  - mutablestateflow
  - coroutines
  - dispatchers
  - anr-simulation
  - ssot
summary: Praktikum komprehensif Modul 7 Jetpack Compose meliputi kalkulator diskon dengan ViewModel, pemodelan data class User, simulasi freeze ANR, dan solusinya dengan Coroutine.
related:
  - docs/references/04-viewmodel/01-viewmodel-core-and-lifecycle.md
  - docs/references/04-viewmodel/02-coroutines-in-android.md
  - docs/references/05-mvvm-best-practices/02-state-management-stateflow-vs-mutablestate.md
---

# 🧪 Lembar Percobaan & Analisis Modul 7 (ViewModel & Concurrency)

[← Sebelumnya: Dasar Coroutines dalam Pemrograman Android](./02-coroutines-in-android.md) | [Berikutnya: Panduan MVVM Modern (4 Lapisan & UDF) →](../05-mvvm-best-practices/01-architecture-overview-and-udf.md)

---

## 1. Percobaan 7.5.1: State Management Kalkulator & SSoT

### A. Data Class & ViewModel (`CalculatorScreenViewModel.kt`)
```kotlin
package com.example.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class User(
    val name: String = "Guest",
    val memberStatus: Boolean = false
)

class CalculatorScreenViewModel : ViewModel() {
    // State Diskon (Primitif Int)
    private var _discount = MutableStateFlow(0)
    val discount = _discount.asStateFlow()

    // State User (Kompleks Data Class)
    private var _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    fun compute(number: Int) {
        // Aturan bisnis diskon: > 100.000 diskon 20%, selain itu 10%
        val percent = if (number > 100000) 0.2 else 0.1
        _discount.update { (percent * number).toInt() }

        if (number > 100000) {
            setMemberStatus(true)
            setMemberName("Setyawan")
        } else {
            resetUser()
        }
    }

    fun reset() {
        _discount.update { 0 }
        resetUser()
    }

    private fun setMemberName(name: String) {
        _user.update { it.copy(name = name) }
    }

    private fun setMemberStatus(status: Boolean) {
        _user.update { it.copy(memberStatus = status) }
    }

    private fun resetUser() {
        _user.update { it.copy(name = "Guest", memberStatus = false) }
    }
}
```

### B. Binding Composable View (`CalculatorScreen.kt` & `UserPanel.kt`)
```kotlin
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    discount: Int = 0,
    onCalculateClick: (Int) -> Unit,
    onResetClick: () -> Unit
) {
    var number by rememberSaveable { mutableStateOf(0) }

    Column(modifier = modifier.padding(32.dp).fillMaxWidth()) {
        Text("Transaction Amount", fontSize = 24.sp, fontWeight = FontWeight.Light)
        Spacer(Modifier.size(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = number.toString(),
            onValueChange = { number = it.toIntOrNull() ?: 0 },
            textStyle = LocalTextStyle.current.copy(fontSize = 28.sp)
        )

        Spacer(Modifier.size(16.dp))
        Row(Modifier.align(Alignment.End)) {
            Button(onClick = { onCalculateClick(number) }) { Text("Calculate") }
            Spacer(Modifier.size(8.dp))
            Button(onClick = {
                number = 0
                onResetClick() // Mengirim event reset ke ViewModel
            }) { Text("Reset") }
        }

        Spacer(Modifier.size(16.dp))
        HorizontalDivider()
        Spacer(Modifier.size(16.dp))
        Text("Discount: $discount", fontSize = 24.sp)
    }
}
```

---

## 2. Percobaan 7.5.2: Simulasi Freeze UI vs Coroutines

### A. Kondisi 1: Sinkron Memblokir Thread Utama (`Thread.sleep`)
```kotlin
fun compute(number: Int) {
    // SIMULASI TUGAS BERAT SINKRON (MEMBLOKIR MAIN THREAD)
    Thread.sleep(10000)

    val percent = if (number > 100000) 0.2 else 0.1
    _discount.update { (percent * number).toInt() }
}
```
**Hasil Pengamatan**:
- Sesaat setelah tombol Calculate ditekan, UI membeku total.
- Input TextField tidak dapat diketik, tombol Reset tidak memiliki animasi klik.
- Setelah 5 detik terblokir, sistem memicu peringatan crash sistem **ANR (Application Not Responding)**.

### B. Kondisi 2: Asinkron Menggunakan Coroutine (`viewModelScope`)
```kotlin
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun compute(number: Int) {
    viewModelScope.launch(Dispatchers.Default) {
        // Proses berat berjalan di background worker thread!
        Thread.sleep(10000) 

        val percent = if (number > 100000) 0.2 else 0.1
        _discount.update { (percent * number).toInt() }
    }
}
```
**Hasil Pengamatan**:
- Antarmuka pengguna tetap responsif 100%!
- Pengguna tetap dapat mengedit teks di TextField, menekan tombol Reset, atau berinteraksi dengan elemen UI lainnya selagi proses komputasi berlangsung di latar belakang.
- Bebas dari ancaman ANR.

---

## 3. Pembahasan Pertanyaan Teori & Analisis

### ❓ Pertanyaan 1: Mengapa perubahan nilai diskon menjadi 0 hanya boleh dilakukan di ViewModel (Prinsip SSoT)?
Jika Composable View memaksakan nilai diskon menjadi 0 secara lokal tanpa memberitahu ViewModel, maka data yang dipegang ViewModel dan data yang ditampilkan di UI akan berbeda (*desync*). Dengan menerapkan Single Source of Truth (SSoT), ViewModel menjadi satu-satunya otoritas yang menentukan nilai state; View hanya merefleksikan apa yang dikabarkan oleh ViewModel.

### ❓ Pertanyaan 2: Perbedaan kasus nyata penerapan `Dispatchers.IO` vs `Dispatchers.Default`?
- **`Dispatchers.IO`**: Digunakan untuk tugas yang menghabiskan waktu menunggu perangkat keras luar atau jaringan (*I/O-bound*), seperti memuat file dari memori penyimpanan lokal atau memanggil API server.
- **`Dispatchers.Default`**: Digunakan untuk tugas komputasi intensif yang menghabiskan siklus CPU (*CPU-bound*), seperti pemrosesan matriks, filtering puluhan ribu data di RAM, atau kompresi file multimedia.

---
[← Sebelumnya: Dasar Coroutines dalam Pemrograman Android](./02-coroutines-in-android.md) | [Berikutnya: Panduan MVVM Modern (4 Lapisan & UDF) →](../05-mvvm-best-practices/01-architecture-overview-and-udf.md)
