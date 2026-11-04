package com.example.student_life_app.features.reference.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.EmptyStateView

@Composable
fun ReferenceListScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Referensi & Link Materi")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Referensi")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            EmptyStateView(
                icon = Icons.Default.Bookmark,
                title = "Belum Ada Referensi",
                description = "Simpan tautan jurnal, Google Drive materi kuliah, atau video pembelajaran.",
                actionButtonText = "Tambah Referensi Baru",
                onActionClick = onNavigateToCreate
            )
        }
    }
}
