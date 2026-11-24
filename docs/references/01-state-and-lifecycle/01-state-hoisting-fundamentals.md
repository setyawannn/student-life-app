---
id: ref-state-01
title: State Hoisting Fundamentals di Jetpack Compose
module: Modul 04 - State & Lifecycle
category: state-management
tags:
  - state-hoisting
  - stateless-composables
  - single-source-of-truth
  - unidirectional-data-flow
  - jetpack-compose
summary: Penjelasan konsep pola State Hoisting di Jetpack Compose untuk memisahkan UI dan state, mewujudkan Single Source of Truth (SSoT), dan Unidirectional Data Flow (UDF).
related:
  - docs/references/01-state-and-lifecycle/02-configuration-change-and-persistence.md
  - docs/references/01-state-and-lifecycle/03-side-effects-launchedeffect.md
  - docs/references/05-mvvm-best-practices/01-architecture-overview-and-udf.md
---

# 📚 State Hoisting Fundamentals di Jetpack Compose

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Configuration Change & Persistence →](./02-configuration-change-and-persistence.md)

---

## 1. Definisi dan Tujuan State Hoisting

**State Hoisting** adalah pola desain (*design pattern*) dalam Jetpack Compose di mana state (data yang dapat berubah seiring waktu) dipindahkan dari dalam suatu fungsi composable anak (*child*) ke pemanggilnya atau composable induk (*parent/caller*).

Pola ini mengubah composable dari:
- **Stateful** (mengelola dan menyimpan datanya sendiri, sulit di-reuse dan dites)
- Menjadi **Stateless** (hanya menerima data via parameter dan mengirim event via lambda callback).

```
         +-------------------------------------+
         |      Composable Induk (Caller)      |
         |         (Stateful / Holder)         |
         +-------------------------------------+
                   |                ^
      Parameter    |                | Callback Event
      (State ↓)    |                | (Event ↑)
                   v                |
         +-------------------------------------+
         |      Composable Anak (Child)        |
         |             (Stateless)             |
         +-------------------------------------+
```

---

## 2. Keunggulan Utama State Hoisting

1. **Kemudahan Penggunaan Kembali (*Reusability*)**  
   Komponen stateless tidak terikat pada logika internal atau sumber data tertentu sehingga mudah digunakan di berbagai layar atau konteks aplikasi.
2. **Kemudahan Pengujian (*Testability*)**  
   Karena status diteruskan melalui argumen fungsi dan aksi berupa callback lambda, UI dapat diuji (*unit testing*) secara terisolasi tanpa perlu menginisialisasi arsitektur runtime yang kompleks.
3. **Single Source of Truth (SSoT)**  
   Mencegah duplikasi atau inkonsistensi data dengan mengelola status di satu lokasi pusat. Induk memegang kendali penuh atas perubahan data.
4. **Separation of Concerns (Pemisahan Tanggung Jawab)**  
   Memisahkan logika presentasi (tampilan UI) dari logika bisnis/manajemen state, menjaga kode tetap bersih (*clean architecture*).
5. **Alur Data Satu Arah (*Unidirectional Data Flow / UDF*)**  
   Data selalu mengalir ke bawah (*state down*), dan sinyal aksi/kejadian mengalir ke atas (*event up*).

---

## 3. Komponen Pola State Hoisting

Pola standar state hoisting biasanya melibatkan dua parameter utama pada composable:

| Parameter | Tipe | Peran |
| :--- | :--- | :--- |
| `value: T` | Nilai data | Data saat ini yang akan dirender oleh composable |
| `onValueChange: (T) -> Unit` | Lambda Callback | Event yang dipicu saat UI meminta perubahan data ke induk |

### Contoh Nyata: Komponen Bawaan `TextField`
Alih-alih `TextField` mengelola string internalnya sendiri, ia menerima parameter `value` dan callback `onValueChange`. Induk menentukan kapan dan bagaimana string tersebut diubah.

---

## 4. Perbandingan Kode: Stateful vs Hoisted (Stateless)

### A. Sebelum State Hoisting (Stateful - "Trapped State")
State terperangkap di dalam composable `Counter`, sehingga komponen induk tidak dapat memantau atau mengontrol nilai hitungan.

```kotlin
@Composable
fun Counter() {
    // State bersifat lokal dan terperangkap (trapped)
    var count by remember { mutableStateOf(0) }
    
    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}
```

### B. Setelah State Hoisting (Stateless)
Composable `Counter` kini murni stateless dan menerima parameter nilai serta callback:

```kotlin
@Composable
fun Counter(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onIncrement,
        modifier = modifier
    ) {
        Text("Count: $count")
    }
}
```

### C. Pemanggilan oleh Composable Induk (`CounterScreen`)

```kotlin
@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }
    
    // Passing state down (count), receiving events up (onIncrement)
    Counter(
        count = count,
        onIncrement = { count++ }
    )
}
```

---

## 5. Hubungan dengan Modul Lain
- Untuk memahami bagaimana mempertahankan state ini saat terjadi rotasi layar perangkat, lihat [02. Configuration Change & Persistence](./02-configuration-change-and-persistence.md).
- Untuk memahami bagaimana side effect dipicu berdasarkan perubahan state, lihat [03. Jetpack Compose Side Effects](./03-side-effects-launchedeffect.md).
- Untuk mengangkat state ke level arsitektur ViewModel, lihat [MVVM & UDF Best Practices](../05-mvvm-best-practices/01-architecture-overview-and-udf.md).

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Configuration Change & Persistence →](./02-configuration-change-and-persistence.md)
