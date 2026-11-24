package com.example.student_life_app.features.schedule.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.AppButton
import com.example.student_life_app.core.designsystem.components.AppOutlinedButton
import com.example.student_life_app.core.designsystem.components.AppTopBar
import com.example.student_life_app.core.designsystem.components.ConfirmDialog
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.schedule.viewmodel.ScheduleViewModel
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.PrimaryBlueContainer
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.SurfaceVariantBlue
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary

/**
 * Layar Detail Jadwal: menampilkan satu jadwal lengkap + aksi Ubah dan Hapus.
 * Durasi dihitung otomatis dari jam mulai dan jam selesai.
 */
@Composable
fun ScheduleDetailScreen(
    scheduleId: String,
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    onDeleted: () -> Unit = {},
    viewModel: ScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val schedule = uiState.schedules.find { it.id == scheduleId }
    var showDeleteDialog by remember { mutableStateOf(false) }

    StudentLifeScaffold(
        topBar = {
            AppTopBar(
                title = "Detail Jadwal",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (schedule == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Jadwal tidak ditemukan", color = TextMuted)
            }
        } else {
            val duration = viewModel.formatScheduleDuration(schedule.startTime, schedule.endTime)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kartu judul: nama mata kuliah + chip hari & ruang
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceVariantBlue)
                        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = schedule.courseName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(icon = Icons.Outlined.CalendarMonth, text = schedule.dayOfWeek.name)
                        InfoChip(icon = Icons.Outlined.MeetingRoom, text = schedule.room)
                    }
                }

                // Dua kartu info: Waktu & Durasi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.AccessTime,
                        label = "Waktu",
                        value = "${schedule.startTime} - ${schedule.endTime}"
                    )
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Timer,
                        label = "Durasi",
                        value = duration
                    )
                }

                // Kartu dosen
                InfoCard(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Outlined.Person,
                    label = "Dosen Pengampu",
                    value = schedule.lecturerName
                )

                Spacer(modifier = Modifier.height(8.dp))

                AppButton(text = "Ubah Jadwal", onClick = { onEdit(schedule.id) })
                AppOutlinedButton(text = "Hapus Jadwal", onClick = { showDeleteDialog = true })

                // BARU: pesan error kalau hapus ke server gagal
                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            ConfirmDialog(
                show = showDeleteDialog,
                title = "Hapus Jadwal",
                message = "Jadwal ${schedule.courseName} akan dihapus. Lanjutkan?",
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.deleteSchedule(schedule.id) { onDeleted() }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PrimaryBlueContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBlue
        )
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
        }
    }
}