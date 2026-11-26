---
id: ref-modul11-02
title: Forward Data Passing dengan LazyColumn & Interactive Checkbox
module: Modul Pager YouTube Stream & Navigasi Terapan
category: navigation
tags:
  - forward-data-passing
  - lazycolumn
  - checkbox
  - taskdetailroute
  - toroute
  - textdecoration
summary: Implementasi pengiriman data searah dari LazyColumn interaktif ke layar detail menggunakan rute berparameter Type-Safe dan ekstraksi toRoute().
related:
  - docs/references/11-pager-and-youtube-streaming/01-type-safe-navigation-and-backstack-leak.md
  - docs/references/11-pager-and-youtube-streaming/03-return-back-data-savedstatehandle-serializable.md
  - docs/references/03-navigation/02-type-safe-navigation-guide.md
---

# 📦 Forward Data Passing dengan LazyColumn & Interactive Checkbox

[← Sebelumnya: Navigasi Dasar Type-Safe](./01-type-safe-navigation-and-backstack-leak.md) | [Berikutnya: Return-Back Data via SavedStateHandle →](./03-return-back-data-savedstatehandle-serializable.md)

---

## 1. Konsep Forward Data Passing

**Forward Data Passing** (Pengiriman Data Searah/Maju) terjadi ketika Layar Asal (misal: Daftar Tugas `TaskListScreen`) membuka Layar Tujuan (misal: Rincian Tugas `TaskDetailScreen`) sambil meneruskan argumen data spesifik.

Pada Navigation Compose 2.8+:
- Parameter rute dibungkus dalam konstruktor **`data class` beranotasi `@Serializable`**.
- Layar tujuan mengekstrak parameter tersebut secara otomatis menggunakan fungsi ekstensi **`.toRoute<T>()`**.

---

## 2. Pemodelan Data & Rute Berparameter

### A. Model Data Lokal (`model/TaskItem.kt`)
```kotlin
package com.example.appnavigasi.model

data class TaskItem(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)
```

### B. Rute Berparameter (`navigation/Destinations.kt`)
```kotlin
package com.example.appnavigasi.navigation

import kotlinx.serialization.Serializable

@Serializable
object TaskListRoute

@Serializable
data class TaskDetailRoute(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)
```

---

## 3. Implementasi Komponen Daftar Tugas (`TaskListScreen.kt`)

```kotlin
@Composable
fun TaskListScreen(
    onNavigateToDetail: (id: Int, title: String, description: String, isCompleted: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Daftar reaktif yang tahan rotasi layar
    val taskList = rememberSaveable {
        mutableStateListOf(
            TaskItem(1, "Belajar Jetpack Compose", "Memahami State Hoisting dan Navigation 2.8+"),
            TaskItem(2, "Mengerjakan Modul Praktikum", "Membuat Percobaan 2 dengan LazyColumn & Checkbox"),
            TaskItem(3, "Setup Kotlin Serialization", "Menambahkan plugin serialization di build.gradle"),
            TaskItem(4, "Diskusi Kelompok", "Membahas konsep SavedStateHandle tanpa ViewModel")
        )
    }

    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Daftar Tugas (Klik item untuk Detail)", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(taskList, key = { it.id }) { task ->
                    TaskRowItem(
                        task = task,
                        onCheckedChange = { isChecked ->
                            val index = taskList.indexOfFirst { it.id == task.id }
                            if (index != -1) {
                                taskList[index] = taskList[index].copy(isCompleted = isChecked)
                            }
                        },
                        onItemClick = {
                            onNavigateToDetail(task.id, task.title, task.description, task.isCompleted)
                        }
                    )
                }
            }
        }
    }
}
```

### Komponen Baris Item dengan Checkbox (`TaskRowItem.kt`):
```kotlin
@Composable
fun TaskRowItem(
    task: TaskItem,
    onCheckedChange: (Boolean) -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(">", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
```

---

## 4. Layar Detail Reaktif Berdasarkan Status (`TaskDetailScreen.kt`)

Layar detail mengubah warna latar belakang Kartu dan label Chip status berdasarkan parameter `isCompleted` yang dikirim dari layar daftar:

```kotlin
@Composable
fun TaskDetailScreen(
    id: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Detail Tugas #$id", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Judul:", style = MaterialTheme.typography.labelMedium)
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Deskripsi:", style = MaterialTheme.typography.labelMedium)
                    Text(description, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Status:", style = MaterialTheme.typography.labelMedium)
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(if (isCompleted) "Selesai ✓" else "Belum Selesai ✗")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack) {
                Text("< Kembali ke Daftar")
            }
        }
    }
}
```

---

## 5. Wiring NavHost & Ekstraksi `.toRoute()`

```kotlin
@Composable
fun AppNavigationP2() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = TaskListRoute) {
        composable<TaskListRoute> {
            TaskListScreen(onNavigateToDetail = { id, title, desc, completed ->
                navController.navigate(
                    TaskDetailRoute(id = id, title = title, description = desc, isCompleted = completed)
                )
            })
        }
        composable<TaskDetailRoute> { backStackEntry ->
            // Ekstraksi otomatis type-safe
            val routeArgs: TaskDetailRoute = backStackEntry.toRoute()
            TaskDetailScreen(
                id = routeArgs.id,
                title = routeArgs.title,
                description = routeArgs.description,
                isCompleted = routeArgs.isCompleted,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
```

---

## 6. Pembahasan Pertanyaan Pemahaman 2.1
> **Bagaimanakah variabel `isCompleted` dari item ke-2 dikirimkan secara maju dan merubah tampilan warna kartu?**

1. Ketika Checkbox dicentang di `TaskListScreen`, state `taskList[index].isCompleted` berubah menjadi `true`.
2. Saat baris diklik, nilai boolean `true` tersebut dikirim melalui lambda `onNavigateToDetail`.
3. NavController mengeksekusi `navController.navigate(TaskDetailRoute(..., isCompleted = true))`.
4. Berkat anotasi **`@Serializable`**, Kotlin Compiler menghasilkan serializer yang secara otomatis mengemas nilai boolean tersebut ke dalam rute Bundle tanpa risiko error runtime.
5. Pada `TaskDetailRoute`, `backStackEntry.toRoute()` mengekstrak nilai boolean tersebut, lalu meneruskannya ke `TaskDetailScreen`. Warna container Card otomatis mengevaluasi `if (isCompleted)` dan mengubah warnanya menjadi `primaryContainer` disertai Chip `"Selesai ✓"`.

---
[← Sebelumnya: Navigasi Dasar Type-Safe](./01-type-safe-navigation-and-backstack-leak.md) | [Berikutnya: Return-Back Data via SavedStateHandle →](./03-return-back-data-savedstatehandle-serializable.md)
