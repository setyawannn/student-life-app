package com.example.student_life_app.features.goal.ui

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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.goal.controller.GoalViewModel
import com.example.student_life_app.features.goal.model.GoalMilestone
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.StatusError
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoalDetailScreen(
    goalId: String,
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var milestoneInput by remember { mutableStateOf("") }

    val goal = uiState.selectedGoal ?: uiState.goals.firstOrNull { it.id == goalId }

    LaunchedEffect(goalId) {
        viewModel.loadGoals()
        viewModel.selectGoal(goalId)
    }

    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = "Detail Target",
                badgeIcon = Icons.Outlined.TrackChanges,
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (goal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = PrimaryBlue)
                } else {
                    Text(uiState.errorMessage ?: "Target belajar tidak ditemukan.", color = TextMuted)
                }
            }
        } else {
            val completedMilestonesCount = goal.milestones.count { it.isCompleted }
            val totalMilestonesCount = goal.milestones.size
            val autoProgress = if (totalMilestonesCount > 0) {
                ((completedMilestonesCount.toFloat() / totalMilestonesCount.toFloat()) * 100).toInt()
            } else {
                goal.progress
            }
            val isCompleted = autoProgress >= 100

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Header Information Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = goal.category,
                                color = PrimaryBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Target Semester",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = goal.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (!goal.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = goal.description,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Deadline Info Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF8E1))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = formatDetailDeadline(goal.deadline, isCompleted),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                // 2. Automatic Circular Progress Gauge Card (Derived from Milestones)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(18.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { autoProgress / 100f },
                            modifier = Modifier.size(136.dp),
                            strokeWidth = 12.dp,
                            color = if (isCompleted) Color(0xFF00714D) else PrimaryBlue,
                            trackColor = Color(0xFFE8ECFA)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$autoProgress%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isCompleted) "Selesai" else "Sedang Berjalan",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) Color(0xFF00714D) else PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Progres dihitung otomatis dari penyelesaian checklist rencana aksi",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // 3. Rencana Aksi & Checklist Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rencana Aksi & Checklist",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$completedMilestonesCount / $totalMilestonesCount Selesai",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00714D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (goal.milestones.isEmpty()) {
                        Text(
                            text = "Belum ada rencana aksi. Tambahkan checklist di bawah untuk menghitung progres otomatis.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    } else {
                        goal.milestones.forEach { milestone ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = milestone.isCompleted,
                                    onCheckedChange = {
                                        viewModel.toggleMilestone(goal.id, milestone.id)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00714D))
                                )
                                Text(
                                    text = milestone.text,
                                    fontSize = 12.sp,
                                    color = if (milestone.isCompleted) TextMuted else TextPrimary,
                                    textDecoration = if (milestone.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Milestone Baru
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = milestoneInput,
                            onValueChange = { milestoneInput = it },
                            placeholder = { Text("Tambah langkah / checklist baru", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (milestoneInput.isNotBlank()) {
                                    val newMilestones = goal.milestones + GoalMilestone(text = milestoneInput.trim())
                                    viewModel.saveGoal(
                                        goalId = goal.id,
                                        title = goal.title,
                                        category = goal.category,
                                        priority = goal.priority,
                                        description = goal.description,
                                        deadline = goal.deadline,
                                        progress = goal.progress,
                                        milestones = newMilestones,
                                        strategy = goal.strategy,
                                        weeklyReminder = goal.weeklyReminder,
                                        onSuccess = {}
                                    )
                                    milestoneInput = ""
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = "Tambah", tint = PrimaryBlue)
                        }
                    }
                }

                // 4. Catatan & Strategi Card
                if (!goal.strategy.isNullOrBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceCard)
                            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Book,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Catatan & Strategi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = goal.strategy,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 5. Action Buttons Footer
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val completedAll = goal.milestones.map { it.copy(isCompleted = true) }
                            viewModel.saveGoal(
                                goalId = goal.id,
                                title = goal.title,
                                category = goal.category,
                                priority = goal.priority,
                                description = goal.description,
                                deadline = goal.deadline,
                                progress = 100,
                                milestones = completedAll.ifEmpty { listOf(GoalMilestone(text = "Target Utama Selesai", isCompleted = true)) },
                                strategy = goal.strategy,
                                weeklyReminder = goal.weeklyReminder,
                                onSuccess = {}
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00714D)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tandai Target Selesai", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onEdit(goal.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ubah Detail Target", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = StatusError, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hapus Target", color = StatusError, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Target Belajar?") },
            text = { Text("Target ini akan dihapus secara permanen dari Supabase.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteGoal(goalId, onBack)
                    }
                ) {
                    Text("Hapus", color = StatusError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

private fun formatDetailDeadline(deadlineStr: String, isCompleted: Boolean): String {
    return try {
        val datePart = deadlineStr.take(10)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(datePart)
        if (date != null) {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(date)
            if (isCompleted) {
                "Tercapai pada $formattedDate"
            } else {
                val now = Date()
                val diffMillis = date.time - now.time
                val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays >= 0) "Tenggat: $formattedDate • Sisa $diffDays Hari Lagi"
                else "Tenggat: $formattedDate • Lewat ${-diffDays} Hari"
            }
        } else "Tenggat: ${deadlineStr.take(10)}"
    } catch (_: Exception) {
        "Tenggat: ${deadlineStr.take(10)}"
    }
}
