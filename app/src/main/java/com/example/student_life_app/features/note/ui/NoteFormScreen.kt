package com.example.student_life_app.features.note.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.student_life_app.core.designsystem.components.AppButton
import com.example.student_life_app.core.designsystem.components.AppTextField
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.core.utils.ImageCompressor
import com.example.student_life_app.features.note.model.Note
import com.example.student_life_app.features.note.viewmodel.NoteViewModel
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Layar Form Catatan Belajar. Jika [noteId] null -> mode Tambah, jika ada -> mode Ubah.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteFormScreen(
    noteId: String? = null,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: NoteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEdit = noteId != null
    val existing = uiState.allNotes.find { it.id == noteId }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var courseName by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var existingImageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedImageUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var badgeType by rememberSaveable { mutableStateOf("Photo") }
    var initialized by rememberSaveable { mutableStateOf(false) }

    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var courseError by rememberSaveable { mutableStateOf<String?>(null) }
    var contentError by rememberSaveable { mutableStateOf<String?>(null) }
    var isCompressing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(existing) {
        if (existing != null && !initialized) {
            title = existing.title
            courseName = existing.courseName
            content = existing.content
            existingImageUrl = existing.imageUrl
            badgeType = existing.badgeType ?: "Photo"
            initialized = true
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUriString = uri.toString()
        }
    }

    val previewModel: Any? = selectedImageUriString ?: existingImageUrl

    StudentLifeScaffold(
        topBar = {
            AppTopBar(
                title = if (isEdit) "Ubah Catatan" else "Tambah Catatan",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                value = title,
                onValueChange = { title = it; titleError = null },
                label = "Judul Catatan",
                placeholder = "Contoh: Ringkasan Arsitektur MVVM",
                errorMessage = titleError
            )

            AppTextField(
                value = courseName,
                onValueChange = { courseName = it; courseError = null },
                label = "Nama Mata Kuliah",
                placeholder = "Contoh: Pemrograman Mobile",
                errorMessage = courseError
            )

            AppTextField(
                value = content,
                onValueChange = { content = it; contentError = null },
                label = "Isi Catatan",
                placeholder = "Tuliskan materi atau poin penting perkuliahan...",
                singleLine = false,
                errorMessage = contentError
            )

            // Lampiran Gambar (Photo Picker + Kompresi + Supabase Storage)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Lampiran Gambar (Opsional)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                if (previewModel != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF6FF))
                    ) {
                        AsyncImage(
                            model = previewModel,
                            contentDescription = "Preview Foto Catatan",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                selectedImageUriString = null
                                existingImageUrl = null
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xAA000000))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Hapus Gambar",
                                tint = Color.White
                            )
                        }
                    }

                    Text(
                        text = "Label Lampiran",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Photo", "Diag").forEach { type ->
                            FilterChip(
                                selected = badgeType == type,
                                onClick = { badgeType = type },
                                label = { Text(type) }
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving && !isCompressing
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddPhotoAlternate,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = if (previewModel != null) "Ganti Foto Catatan" else "Pilih Foto dari Galeri"
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val busy = uiState.isSaving || isCompressing
            AppButton(
                text = when {
                    isCompressing -> "Mengompres Gambar..."
                    uiState.isSaving -> "Menyimpan..."
                    isEdit -> "Simpan Perubahan"
                    else -> "Simpan Catatan"
                },
                enabled = !busy,
                onClick = {
                    val errors = viewModel.validateNoteForm(title, courseName, content)
                    if (errors.hasError) {
                        titleError = errors.titleError
                        courseError = errors.courseError
                        contentError = errors.contentError
                    } else {
                        coroutineScope.launch {
                            val compressedBytes = selectedImageUriString?.let { uriStr ->
                                isCompressing = true
                                try {
                                    withContext(Dispatchers.IO) {
                                        ImageCompressor.compress(context, Uri.parse(uriStr))
                                    }
                                } finally {
                                    isCompressing = false
                                }
                            }
                            val note = Note(
                                id = existing?.id ?: UUID.randomUUID().toString(),
                                userId = existing?.userId ?: "default_user",
                                title = title.trim(),
                                courseName = courseName.trim(),
                                content = content.trim(),
                                imageUrl = existingImageUrl,
                                badgeType = if (compressedBytes != null || existingImageUrl != null) badgeType else null,
                                dateText = existing?.dateText ?: "Baru saja",
                                isBookmarked = existing?.isBookmarked ?: false,
                                createdAt = existing?.createdAt
                            )
                            viewModel.saveNote(
                                note = note,
                                isEdit = isEdit,
                                imageBytes = compressedBytes,
                                onSuccess = onSaved
                            )
                        }
                    }
                }
            )
        }
    }
}
