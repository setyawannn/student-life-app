package com.example.student_life_app.features.goal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.student_life_app.features.goal.model.StudyGoal
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoalCard(
    goal: StudyGoal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = goal.progress >= 100
    val progressFloat = (goal.progress.coerceIn(0, 100)) / 100f
    val themeColor = if (isCompleted) Color(0xFF00714D) else PrimaryBlue

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Header Row: Category Badge + Priority Pill + Chevron
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = goal.category,
                        color = PrimaryBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Priority or Completed Chip
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF00714D),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Selesai",
                                color = Color(0xFF00714D),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    val priorityBg = when (goal.priority) {
                        "Tinggi" -> Color(0xFFFFF3E0)
                        "Sedang" -> Color(0xFFFFF8E1)
                        else -> Color(0xFFF5F5F5)
                    }
                    val priorityText = when (goal.priority) {
                        "Tinggi" -> Color(0xFFE65100)
                        "Sedang" -> Color(0xFFF57F17)
                        else -> Color(0xFF616161)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(priorityBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Prioritas ${goal.priority}",
                            color = priorityText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Detail",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Goal Title
        Text(
            text = goal.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (!goal.description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = goal.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = TextSecondary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Deadline Info Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = formatGoalDeadline(goal.deadline, isCompleted),
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub-task Milestone Progress Text + Percentage
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalMilestones = goal.milestones.size
            val completedMilestones = goal.milestones.count { it.isCompleted }
            val milestoneLabel = if (isCompleted) {
                if (totalMilestones > 0) "Semua $totalMilestones modul terpenuhi" else "Semua modul terpenuhi"
            } else {
                if (totalMilestones > 0) "$completedMilestones dari $totalMilestones modul selesai" else "Pantau progres capaian"
            }

            Text(
                text = milestoneLabel,
                fontSize = 11.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "${goal.progress}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { progressFloat },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = themeColor,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

private fun formatGoalDeadline(deadlineStr: String, isCompleted: Boolean): String {
    return try {
        val datePart = deadlineStr.take(10)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(datePart)
        if (date != null) {
            val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID")).format(date)
            if (isCompleted) {
                "Selesai pada $formattedDate"
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
