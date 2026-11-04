package com.example.student_life_app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.student_life_app.features.task.model.TaskStatus
import com.example.student_life_app.ui.theme.OnStatusErrorContainer
import com.example.student_life_app.ui.theme.OnStatusSuccessContainer
import com.example.student_life_app.ui.theme.OnStatusWarningContainer
import com.example.student_life_app.ui.theme.StatusErrorContainer
import com.example.student_life_app.ui.theme.StatusSuccessContainer
import com.example.student_life_app.ui.theme.StatusWarningContainer

@Composable
fun TaskStatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor) = when (status) {
        TaskStatus.TODO -> Triple("Belum Dikerjakan", StatusErrorContainer, OnStatusErrorContainer)
        TaskStatus.IN_PROGRESS -> Triple("Sedang Dikerjakan", StatusWarningContainer, OnStatusWarningContainer)
        TaskStatus.COMPLETED -> Triple("Selesai", StatusSuccessContainer, OnStatusSuccessContainer)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
