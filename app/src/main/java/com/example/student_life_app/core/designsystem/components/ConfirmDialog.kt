package com.example.student_life_app.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    show: Boolean,
    title: String = "Konfirmasi Hapus",
    message: String = "Apakah Anda yakin ingin menghapus data ini?",
    confirmText: String = "Hapus",
    dismissText: String = "Batal",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
            text = { Text(text = message, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = confirmText, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissText)
                }
            }
        )
    }
}
