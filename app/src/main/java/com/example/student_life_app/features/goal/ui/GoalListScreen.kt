package com.example.student_life_app.features.goal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.EmptyStateView
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.goal.controller.GoalViewModel
import com.example.student_life_app.features.goal.ui.components.GoalCard
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary

@Composable
fun GoalListScreen(
    viewModel: GoalViewModel = viewModel(),
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadGoals()
    }

    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = "Target Belajar",
                badgeIcon = Icons.Outlined.TrackChanges,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Tambah Target",
                    modifier = Modifier.size(26.dp),
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 84.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 1. Kotak Pencarian
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderOutlineVariant, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Cari",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                text = "Cari sasaran target belajar...",
                                color = TextMuted,
                                fontSize = 14.sp,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // 2. Card Progres Akademik Header
            item {
                AcademicProgressCard(
                    percentage = uiState.academicProgressPercentage,
                    completedCount = uiState.completedGoalsCount,
                    totalCount = uiState.totalGoalsCount,
                )
            }

            // 3. Ringkasan Statistik Target KPI
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GoalSummaryStatCard(
                        count = "${uiState.totalGoalsCount}",
                        label = "Total Target",
                        icon = Icons.Outlined.TrackChanges,
                        indicatorColor = PrimaryBlue,
                        modifier = Modifier.weight(1f),
                    )
                    GoalSummaryStatCard(
                        count = "${uiState.activeGoalsCount}",
                        label = "Berjalan",
                        icon = Icons.Outlined.TrackChanges,
                        indicatorColor = Color(0xFFE28B00),
                        modifier = Modifier.weight(1f),
                    )
                    GoalSummaryStatCard(
                        count = "${uiState.completedGoalsCount}",
                        label = "Selesai",
                        icon = Icons.Outlined.CheckCircle,
                        indicatorColor = Color(0xFF00714D),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // 4. Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val tabs = listOf(
                        "Semua" to "Semua (${uiState.totalGoalsCount})",
                        "Sedang Berjalan" to "Sedang Berjalan (${uiState.activeGoalsCount})",
                        "Selesai" to "Selesai (${uiState.completedGoalsCount})",
                    )
                    tabs.forEach { (tabKey, label) ->
                        val isSelected = uiState.selectedFilterTab == tabKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PrimaryBlue else SurfaceCard)
                                .border(1.dp, if (isSelected) PrimaryBlue else BorderOutlineVariant, RoundedCornerShape(20.dp))
                                .clickable { viewModel.onFilterTabChange(tabKey) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            // 5. Daftar Kartu Target Belajar
            if (uiState.filteredGoals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyStateView(
                            icon = Icons.Outlined.TrackChanges,
                            title = "Tidak Ada Target Belajar",
                            description = if (uiState.searchQuery.isNotBlank()) {
                                "Target dengan kata kunci '${uiState.searchQuery}' tidak ditemukan."
                            } else {
                                "Tetapkan sasaran belajar pribadi dan pantau progres capaiannya."
                            },
                            actionButtonText = "Buat Target Baru",
                            onActionClick = onNavigateToCreate,
                        )
                    }
                }
            } else {
                items(uiState.filteredGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = { onNavigateToDetail(goal.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun AcademicProgressCard(
    percentage: Int,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PROGRES AKADEMIK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    letterSpacing = 0.5.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Semester Ganjil 2026/2027",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$completedCount dari $totalCount target pembelajaran tercapai",
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { (percentage.coerceIn(0, 100)) / 100f },
                    modifier = Modifier.size(54.dp),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFE8ECFA),
                    strokeWidth = 6.dp,
                )
                Text(
                    text = "$percentage%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
            }
        }
    }
}

@Composable
fun GoalSummaryStatCard(
    count: String,
    label: String,
    icon: ImageVector,
    indicatorColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = indicatorColor,
                    modifier = Modifier.size(18.dp),
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(indicatorColor),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
