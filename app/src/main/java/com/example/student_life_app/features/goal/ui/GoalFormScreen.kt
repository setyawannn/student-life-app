package com.example.student_life_app.features.goal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.goal.viewmodel.GoalViewModel
import com.example.student_life_app.features.goal.model.GoalMilestone
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.StatusError
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFormScreen(
    goalId: String? = null,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(goalId) {
        viewModel.loadGoals()
        if (goalId != null) {
            viewModel.selectGoal(goalId)
        }
    }

    val existing = uiState.goals.firstOrNull { it.id == goalId } ?: uiState.selectedGoal

    var title by remember(goalId, existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var category by remember(goalId, existing?.id) { mutableStateOf(existing?.category ?: "Pemrograman Mobile") }
    var priority by remember(goalId, existing?.id) { mutableStateOf(existing?.priority ?: "Tinggi") }
    var description by remember(goalId, existing?.id) { mutableStateOf(existing?.description.orEmpty()) }
    var deadline by remember(goalId, existing?.id) {
        mutableStateOf(
            existing?.deadline?.take(10)
                ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000))
        )
    }
    var milestones by remember(goalId, existing?.id) { mutableStateOf(existing?.milestones ?: emptyList()) }
    var milestoneInput by remember { mutableStateOf("") }
    var strategy by remember(goalId, existing?.id) { mutableStateOf(existing?.strategy.orEmpty()) }
    var weeklyReminder by remember(goalId, existing?.id) { mutableStateOf(existing?.weeklyReminder ?: true) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = if (goalId == null) "Tambah Target" else "Ubah Target",
                badgeIcon = Icons.Outlined.TrackChanges,
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Info Card
            FormSection {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detail Target Akademik",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Tentukan target belajar dan indikator pencapaian semester ini.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            // Field: Title
            FormSection {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FieldLabel("Nama Target Belajar *")
                    Text("Maksimal 60 karakter", fontSize = 10.sp, color = TextMuted)
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 60) title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Kuasai Jetpack Compose Navigation", fontSize = 13.sp) },
                    singleLine = true,
                    supportingText = { Text("${title.length}/60") }
                )
            }

            // Field: Category Dropdown
            FormSection {
                FieldLabel("Kategori / Mata Kuliah *")
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showCategoryMenu) }
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        listOf(
                            "Pemrograman Mobile",
                            "Basis Data Lanjut",
                            "Kecerdasan Buatan",
                            "Manajemen Proyek",
                            "Mata Kuliah Lainnya"
                        ).forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item, fontSize = 13.sp) },
                                onClick = {
                                    category = item
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Field: Priority Segmented Buttons
            FormSection {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FieldLabel("Tingkat Prioritas")
                    Text("$priority Dipilih", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    listOf("Rendah", "Sedang", "Tinggi").forEachIndexed { index, value ->
                        SegmentedButton(
                            selected = priority == value,
                            onClick = { priority = value },
                            shape = SegmentedButtonDefaults.itemShape(index, 3),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = PrimaryBlue,
                                activeContentColor = Color.White
                            )
                        ) {
                            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Field: Deadline Date Button
            FormSection {
                FieldLabel("Tanggal Tenggat *")
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.CalendarMonth, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(deadline, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            // Field: Milestones List & Input (Automatic Progress Calculation)
            FormSection {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FieldLabel("Daftar Rencana Aksi (Milestone)")
                    Text("${milestones.size} Tahapan", color = TextMuted, fontSize = 11.sp)
                }

                milestones.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}. ${item.text}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                milestones = milestones.filterIndexed { i, _ -> i != index }
                            }
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Hapus", tint = StatusError, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = milestoneInput,
                        onValueChange = { milestoneInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tambah sub-target / checklist", fontSize = 12.sp) },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (milestoneInput.isNotBlank()) {
                                milestones = milestones + GoalMilestone(text = milestoneInput.trim())
                                milestoneInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "Tambah", tint = PrimaryBlue)
                    }
                }
                Text("Progres awal dihitung otomatis dari jumlah checklist milestone yang selesai.", color = TextMuted, fontSize = 10.sp)
            }

            // Field: Notes & Strategy
            FormSection {
                FieldLabel("Catatan & Strategi (Opsional)")
                OutlinedTextField(
                    value = strategy,
                    onValueChange = { strategy = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    placeholder = {
                        Text(
                            "Fokuskan pada dokumentasi resmi Android Developers dan selesaikan codelab navigasi sebelum tanggal evaluasi kedua.",
                            fontSize = 12.sp
                        )
                    }
                )
            }

            // Card: Weekly Reminder Switch
            FormSection {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Pengingat Progres Mingguan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Kirim notifikasi evaluasi setiap akhir pekan", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                    Switch(
                        checked = weeklyReminder,
                        onCheckedChange = { weeklyReminder = it }
                    )
                }
            }

            uiState.errorMessage?.let {
                Text(it, color = StatusError, fontSize = 12.sp)
            }

            // Primary Action Button
            Button(
                onClick = {
                    val isoDeadline = if (deadline.length == 10) "${deadline}T00:00:00Z" else deadline
                    val completedMilestonesCount = milestones.count { it.isCompleted }
                    val totalMilestonesCount = milestones.size
                    val calculatedProgress = if (totalMilestonesCount > 0) {
                        ((completedMilestonesCount.toFloat() / totalMilestonesCount.toFloat()) * 100).toInt()
                    } else {
                        existing?.progress ?: 0
                    }

                    viewModel.saveGoal(
                        goalId = goalId,
                        title = title,
                        category = category,
                        priority = priority,
                        description = description.ifBlank { null },
                        deadline = isoDeadline,
                        progress = calculatedProgress,
                        milestones = milestones,
                        strategy = strategy.ifBlank { null },
                        weeklyReminder = weeklyReminder,
                        onSuccess = onSaved
                    )
                },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (goalId == null) "Simpan Target Belajar" else "Simpan Perubahan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Target akan otomatis tersinkronisasi dengan Supabase",
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }

    if (showDatePicker) {
        val initialMillis = runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(deadline)?.time }.getOrNull()
            ?: System.currentTimeMillis()
        val dateState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            deadline = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pilih", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}

@Composable
private fun FormSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
}
