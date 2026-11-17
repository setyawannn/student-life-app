package com.example.student_life_app.features.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
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
import com.example.student_life_app.features.schedule.controller.ScheduleViewModel
import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.model.Schedule
import java.util.UUID

/**
 * Layar Form Jadwal. Jika [scheduleId] null -> mode Tambah, jika ada -> mode Ubah.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleFormScreen(
    scheduleId: String? = null,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: ScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEdit = scheduleId != null
    val existing = uiState.schedules.find { it.id == scheduleId }

    var courseName by remember { mutableStateOf("") }
    var day by remember { mutableStateOf(DayOfWeekEnum.SENIN) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var lecturer by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var roomError by remember { mutableStateOf<String?>(null) }
    var lecturerError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }

    // Mode ubah: isi form sekali dengan data jadwal yang dipilih
    LaunchedEffect(existing) {
        if (existing != null && !initialized) {
            courseName = existing.courseName
            day = existing.dayOfWeek
            startTime = existing.startTime
            endTime = existing.endTime
            room = existing.room
            lecturer = existing.lecturerName
            initialized = true
        }
    }

    StudentLifeScaffold(
        topBar = {
            AppTopBar(
                title = if (isEdit) "Ubah Jadwal" else "Tambah Jadwal",
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                value = courseName,
                onValueChange = { courseName = it; nameError = null },
                label = "Nama Mata Kuliah",
                placeholder = "Contoh: Pemrograman Mobile",
                errorMessage = nameError
            )

            // Pilihan hari
            Column {
                Text(
                    text = "Hari",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DayOfWeekEnum.entries.forEach { item ->
                        FilterChip(
                            selected = day == item,
                            onClick = { day = item },
                            label = { Text(item.name) }
                        )
                    }
                }
            }

            // Jam mulai & selesai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTextField(
                    value = startTime,
                    onValueChange = { startTime = it; timeError = null },
                    label = "Jam Mulai",
                    placeholder = "08:00",
                    modifier = Modifier.weight(1f)
                )
                AppTextField(
                    value = endTime,
                    onValueChange = { endTime = it; timeError = null },
                    label = "Jam Selesai",
                    placeholder = "10:30",
                    modifier = Modifier.weight(1f)
                )
            }
            if (timeError != null) {
                Text(
                    text = timeError ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            AppTextField(
                value = room,
                onValueChange = { room = it; roomError = null },
                label = "Ruang Kelas",
                placeholder = "Contoh: Lab Komputasi 2",
                errorMessage = roomError
            )

            AppTextField(
                value = lecturer,
                onValueChange = { lecturer = it; lecturerError = null },
                label = "Dosen Pengampu",
                placeholder = "Nama lengkap beserta gelar",
                errorMessage = lecturerError
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                text = "Simpan Jadwal",
                onClick = {
                    val service = viewModel.service
                    // Validasi per kolom supaya pesan error muncul tepat di bawah kolomnya
                    nameError = if (courseName.isBlank()) "Nama mata kuliah tidak boleh kosong" else null
                    roomError = if (room.isBlank()) "Ruang kelas tidak boleh kosong" else null
                    lecturerError = if (lecturer.isBlank()) "Nama dosen pengampu tidak boleh kosong" else null
                    timeError = service.validateTimeRange(startTime.trim(), endTime.trim())

                    val valid = nameError == null && roomError == null &&
                            lecturerError == null && timeError == null
                    if (valid) {
                        val schedule = Schedule(
                            id = existing?.id ?: UUID.randomUUID().toString(),
                            userId = existing?.userId ?: "default_user",
                            courseName = courseName.trim(),
                            dayOfWeek = day,
                            startTime = startTime.trim(),
                            endTime = endTime.trim(),
                            room = room.trim(),
                            lecturerName = lecturer.trim()
                        )
                        viewModel.saveSchedule(schedule, isEdit) { onSaved() }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}