package com.example.student_life_app.features.note.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.EmptyStateView

@Composable
fun NoteListScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Catatan Belajar")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Catatan")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            EmptyStateView(
                icon = Icons.Default.Description,
                title = "Belum Ada Catatan",
                description = "Simpan ringkasan materi kuliah dan lampirkan foto papan tulis / slide.",
                actionButtonText = "Buat Catatan Baru",
                onActionClick = onNavigateToCreate
            )
        }
    }
}
