package com.example.student_life_app.features.task.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.EmptyStateView

@Composable
fun TaskListScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Daftar Tugas Kuliah")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            EmptyStateView(
                icon = Icons.AutoMirrored.Filled.Assignment,
                title = "Belum Ada Tugas",
                description = "Catat tugas kuliah baru dengan menekan tombol tambah di bawah.",
                actionButtonText = "Tambah Tugas Pertama",
                onActionClick = onNavigateToCreate
            )
        }
    }
}
