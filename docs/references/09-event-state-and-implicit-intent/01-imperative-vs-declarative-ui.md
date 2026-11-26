---
id: ref-modul03-01
title: Paradigma Antarmuka Android (Imperatif XML vs Deklaratif Compose)
module: Modul 03 - Event, State & Implisit Intent
category: architecture
tags:
  - imperative-vs-declarative
  - ui-equals-f-state
  - recomposition
  - xml-view-system
  - jetpack-compose
summary: Perbandingan komparatif cara berpikir imperatif pada Android XML/View vs pendekatan deklaratif pada Jetpack Compose berdasarkan prinsip UI = f(state).
related:
  - docs/references/09-event-state-and-implicit-intent/02-event-handling-and-compose-state.md
  - docs/references/09-event-state-and-implicit-intent/05-lab-experiments-and-recomposition-analysis.md
---

# ⚖️ Paradigma Antarmuka Android: Imperatif XML vs Deklaratif Compose

[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Event Handling & Compose State →](./02-event-handling-and-compose-state.md)

---

## 1. Perbedaan Cara Berpikir (*Mental Model*)

Perbedaan paling fundamental antara sistem UI lama (XML / View System) dan sistem UI modern (Jetpack Compose) terletak pada cara program merespons perubahan:

```
+-----------------------------------------------------------------------------+
| PARADIGMA IMPERATIF (XML / VIEW SYSTEM)                                     |
| "Cari komponen UI tersebut, lalu perintahkan untuk mengubah propertinya!"   |
|                                                                             |
| [Event Klik] ──> findViewById(tvMessage) ──> tvMessage.text = "Teks Baru"  |
+-----------------------------------------------------------------------------+

+-----------------------------------------------------------------------------+
| PARADIGMA DEKLARATIF (JETPACK COMPOSE)                                      |
| "UI adalah cerminan dari State saat ini (UI = f(state)).                    |
|  Ubah statenya, dan sistem otomatis menggambar ulang UI!"                   |
|                                                                             |
| [Event Klik] ──> state = "Teks Baru" ──> Recomposition ──> Text(state)     |
+-----------------------------------------------------------------------------+
```

---

## 2. Perbandingan Implementasi Kode Nyata

### A. Pendekatan Imperatif (XML + Kotlin)
Pengembang memegang tanggung jawab penuh mencari objek tampilan dan memutasi nilainya:
```kotlin
val tvMessage = findViewById<TextView>(R.id.tvMessage)
val btnChange = findViewById<Button>(R.id.btnChange)

btnChange.setOnClickListener {
    // Memerintahkan objek TextView secara langsung
    tvMessage.text = "Tombol sudah diklik"
}
```

### B. Pendekatan Deklaratif (Jetpack Compose)
Pengembang tidak pernah mencari objek tampilan atau memanggil `setText()`. Pengembang hanya mendeklarasikan hubungan antara data (*state*) dan tampilan:
```kotlin
var message by remember { mutableStateOf("Belum diklik") }

Text(text = message, fontSize = 24.sp)

Button(onClick = {
    // Hanya memutasi nilai state
    message = "Tombol sudah diklik"
}) {
    Text("Ubah Teks")
}
```

---

## 3. Matriks Perbandingan Inti

| Aspek | Pendekatan Imperatif (XML) | Pendekatan Deklaratif (Compose) |
| :--- | :--- | :--- |
| **Pola Alur** | Event → Cari View → Ubah properti View | Event → Ubah State → Rekomposisi UI |
| **Manipulasi Objek** | Langsung via `findViewById` / `binding` | Tidak ada manipulasi objek langsung |
| **Rumus Matematika** | Manual Step-by-Step Mutations | $\text{UI} = f(\text{State})$ |
| **Potensi Bug** | Tinggi (mudah lupa mengupdate View tertentu) | Sangat Rendah (UI selalu sinkron dengan State) |

---
[← Kembali ke Rak Referensi](../README.md) | [Berikutnya: Event Handling & Compose State →](./02-event-handling-and-compose-state.md)
