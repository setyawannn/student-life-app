package com.example.student_life_app.features.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.EmptyStateView
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.schedule.controller.ScheduleViewModel
import com.example.student_life_app.features.schedule.model.DayOfWeekEnum
import com.example.student_life_app.features.schedule.ui.components.ScheduleCard
import com.example.student_life_app.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

/**
 * Screen Jadwal Kuliah dengan arsitektur MVVM.
 * Data perkuliahan dan pemilihan tab hari dikendalikan oleh ScheduleViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    viewModel: ScheduleViewModel = viewModel(),
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val days = DayOfWeekEnum.entries
    val pagerState = rememberPagerState(pageCount = { days.size })
    val coroutineScope = rememberCoroutineScope()

    // Sinkronisasi perpindahan tab pager dengan ViewModel
    LaunchedEffect(pagerState.currentPage) {
        val currentDay = days[pagerState.currentPage]
        if (currentDay != uiState.selectedDay) {
            viewModel.selectDay(currentDay)
        }
    }

    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = "Jadwal Kuliah",
                badgeIcon = Icons.Outlined.CalendarMonth
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
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Tambah Jadwal",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Hari (Horizontal Pager Support)
            PrimaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                days.forEachIndexed { index, day ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = day.name) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val currentDay = days[page]
                if (uiState.schedules.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        EmptyStateView(
                            icon = Icons.Outlined.CalendarMonth,
                            title = "Jadwal Hari ${currentDay.name} Kosong",
                            description = "Belum ada perkuliahan yang terjadwal di hari ini.",
                            actionButtonText = "Tambah Jadwal Hari Ini",
                            onActionClick = onNavigateToCreate
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 84.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.schedules, key = { it.id }) { schedule ->
                            ScheduleCard(
                                schedule = schedule,
                                onClick = { onNavigateToDetail(schedule.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
