package com.example.student_life_app.features.note.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.student_life_app.core.designsystem.components.AppButton
import com.example.student_life_app.core.designsystem.components.AppOutlinedButton
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.ConfirmDialog
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.note.model.Note
import com.example.student_life_app.features.note.viewmodel.NoteViewModel
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.StudentlifeappTheme
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary

/**
 * Layar Detail Catatan Belajar (Stateful).
 */
@Composable
fun NoteDetailScreen(
    noteId: String,
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    onDeleted: () -> Unit = {},
    viewModel: NoteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val note = uiState.allNotes.find { it.id == noteId }

    NoteDetailContent(
        note = note,
        onBack = onBack,
        onToggleBookmark = { viewModel.onBookmarkToggle(noteId) },
        onEdit = { onEdit(noteId) },
        onDeleteConfirm = {
            viewModel.deleteNote(noteId) {
                onDeleted()
            }
        }
    )
}

/**
 * Konten Detail Catatan Belajar (Stateless).
 */
@Composable
fun NoteDetailContent(
    note: Note?,
    onBack: () -> Unit = {},
    onToggleBookmark: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDeleteConfirm: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    StudentLifeScaffold(
        topBar = {
            AppTopBar(
                title = "Detail Catatan",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (note == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Catatan tidak ditemukan", color = TextMuted)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Mata Kuliah + Bookmark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = note.courseName,
                                color = PrimaryBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(onClick = onToggleBookmark) {
                            Icon(
                                imageVector = if (note.isBookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (note.isBookmarked) PrimaryBlue else TextMuted
                            )
                        }
                    }

                    // Judul Catatan
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Tanggal + Badge Jenis Lampiran
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = note.dateText,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        if (!note.badgeType.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8EEF5))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = note.badgeType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Gambar Lampiran (jika ada)
                    if (!note.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = note.imageUrl,
                            contentDescription = note.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEFF6FF))
                        )
                    }

                    // Isi Catatan Lengkap
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Isi Catatan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = note.content,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Tombol Aksi Hapus & Ubah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppOutlinedButton(
                        text = "Hapus",
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Ubah",
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    ConfirmDialog(
        show = showDeleteDialog,
        title = "Hapus Catatan",
        message = "Apakah Anda yakin ingin menghapus catatan '${note?.title}'?",
        confirmText = "Hapus",
        dismissText = "Batal",
        onConfirm = {
            showDeleteDialog = false
            onDeleteConfirm()
        },
        onDismiss = { showDeleteDialog = false }
    )
}

@Preview(showBackground = true)
@Composable
private fun NoteDetailContentPreview() {
    StudentlifeappTheme {
        NoteDetailContent(
            note = Note(
                id = "1",
                title = "Arsitektur MVVM & StateFlow",
                courseName = "Pemrograman Mobile",
                content = "Pemisahan layer UI, ViewModel, dan Repository menghasilkan kode yang bersih dan mudah diuji.",
                dateText = "24 Nov 2026",
                isBookmarked = true
            )
        )
    }
}
