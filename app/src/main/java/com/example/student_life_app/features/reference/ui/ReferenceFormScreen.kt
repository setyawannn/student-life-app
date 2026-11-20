package com.example.student_life_app.features.reference.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.AppButton
import com.example.student_life_app.core.designsystem.components.AppTextField
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.reference.controller.ReferenceViewModel
import com.example.student_life_app.features.reference.model.ReferenceCategory
import com.example.student_life_app.features.reference.service.ReferenceServiceImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceFormScreen(
    referenceId: String? = null,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: ReferenceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEdit = referenceId != null
    val existing = uiState.allReferences.find { it.id == referenceId }
    val validator = remember { ReferenceServiceImpl() }

    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ReferenceCategory.JURNAL) }
    var courseName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var urlError by remember { mutableStateOf<String?>(null) }
    var courseError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(existing) {
        if (existing != null && !initialized) {
            title = existing.title
            url = existing.url
            category = existing.category
            courseName = existing.courseName
            notes = existing.notes.orEmpty()
            initialized = true
        }
    }

    StudentLifeScaffold(
        topBar = {
            AppTopBar(
                title = if (isEdit) "Ubah Referensi" else "Tambah Referensi",
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
                label = "Judul Referensi",
                placeholder = "Contoh: Modern Android Architecture",
                errorMessage = titleError
            )

            // Pilihan Kategori
            Column {
                Text(
                    text = "Kategori Materi",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReferenceCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.name) }
                        )
                    }
                }
            }

            AppTextField(
                value = courseName,
                onValueChange = { courseName = it; courseError = null },
                label = "Nama Mata Kuliah",
                placeholder = "Contoh: Pemrograman Mobile",
                errorMessage = courseError
            )

            AppTextField(
                value = url,
                onValueChange = { url = it; urlError = null },
                label = "Tautan URL (http:// / https://)",
                placeholder = "Contoh: https://developer.android.com",
                errorMessage = urlError
            )

            AppTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Catatan / Deskripsi (Opsional)",
                placeholder = "Tambahkan catatan singkat mengenai referensi ini...",
                singleLine = false
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                text = if (isEdit) "Simpan Perubahan" else "Simpan Referensi",
                onClick = {
                    val validationError = validator.validateReferenceInput(title, url, courseName)
                    if (validationError != null) {
                        if (title.isBlank()) titleError = "Judul tidak boleh kosong"
                        if (url.isBlank()) urlError = "URL tidak boleh kosong"
                        else if (!validator.isValidUrl(url)) urlError = "Format URL tidak valid"
                        if (courseName.isBlank()) courseError = "Mata kuliah tidak boleh kosong"
                    } else {
                        if (isEdit && referenceId != null) {
                            viewModel.updateReference(
                                id = referenceId,
                                title = title,
                                url = url,
                                category = category,
                                courseName = courseName,
                                notes = notes.ifBlank { null },
                                onSuccess = onSaved
                            )
                        } else {
                            viewModel.createReference(
                                title = title,
                                url = url,
                                category = category,
                                courseName = courseName,
                                notes = notes.ifBlank { null },
                                onSuccess = onSaved
                            )
                        }
                    }
                }
            )
        }
    }
}
