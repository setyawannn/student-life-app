package com.example.student_life_app.features.reference.ui

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
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayCircle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.AppButton
import com.example.student_life_app.core.designsystem.components.AppOutlinedButton
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.ConfirmDialog
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.reference.viewmodel.ReferenceViewModel
import com.example.student_life_app.features.reference.model.ReferenceCategory
import com.example.student_life_app.features.reference.utils.ReferenceUtils
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.SurfaceVariantBlue
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary

@Composable
fun ReferenceDetailScreen(
    referenceId: String,
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    onDeleted: () -> Unit = {},
    viewModel: ReferenceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reference = uiState.allReferences.find { it.id == referenceId }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    StudentLifeScaffold(
        topBar = {
            AppTopBar(
                title = "Detail Referensi",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (reference == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Referensi tidak ditemukan", color = TextMuted)
            }
        } else {
            val categoryIcon = when (reference.category) {
                ReferenceCategory.JURNAL -> Icons.Outlined.Description
                ReferenceCategory.DRIVE -> Icons.Outlined.CloudQueue
                ReferenceCategory.VIDEO -> Icons.Outlined.PlayCircle
            }

            val categoryColor = when (reference.category) {
                ReferenceCategory.JURNAL -> PrimaryBlue
                ReferenceCategory.DRIVE -> Color(0xFF00714D)
                ReferenceCategory.VIDEO -> Color(0xFFBA1A1A)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kartu Detail Utama
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Kategori Chip
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = reference.category.name,
                                color = categoryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleFavorite(reference.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (reference.isFavorite) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Favorit",
                                tint = if (reference.isFavorite) PrimaryBlue else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Judul
                    Text(
                        text = reference.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    // Mata Kuliah
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mata Kuliah: ",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Text(
                            text = reference.courseName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }

                    // URL Box & Tombol Buka Tautan
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantBlue)
                            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tautan Materi:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Text(
                            text = reference.url,
                            fontSize = 13.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AppButton(
                            text = "Buka Tautan",
                            onClick = { ReferenceUtils.openInBrowser(context, reference.url) }
                        )
                    }

                    // Catatan Tambahan (jika ada)
                    if (!reference.notes.isNullOrBlank()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Catatan / Deskripsi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = reference.notes,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Tombol Aksi (Ubah & Hapus)
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
                        onClick = { onEdit(reference.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    ConfirmDialog(
        show = showDeleteDialog,
        title = "Hapus Referensi",
        message = "Apakah Anda yakin ingin menghapus referensi '${reference?.title}'?",
        confirmText = "Hapus",
        dismissText = "Batal",
        onConfirm = {
            showDeleteDialog = false
            viewModel.deleteReference(referenceId) {
                onDeleted()
            }
        },
        onDismiss = { showDeleteDialog = false }
    )
}
