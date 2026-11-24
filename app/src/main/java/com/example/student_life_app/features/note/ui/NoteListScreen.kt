package com.example.student_life_app.features.note.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.EmptyStateView
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.note.viewmodel.NoteUiState
import com.example.student_life_app.features.note.viewmodel.NoteViewModel
import com.example.student_life_app.features.note.ui.components.NoteCard
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.StudentlifeappTheme
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.TextMuted
import com.example.student_life_app.ui.theme.TextPrimary
import com.example.student_life_app.ui.theme.TextSecondary

/**
 * Screen Catatan Belajar (Stateful): menghubungkan NoteViewModel ke NoteListContent.
 */
@Composable
fun NoteListScreen(
    viewModel: NoteViewModel = viewModel(),
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NoteListContent(
        uiState = uiState,
        searchQuery = viewModel.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelect = viewModel::onCategorySelect,
        onBookmarkToggle = viewModel::onBookmarkToggle,
        onRetry = { viewModel.loadNotes() },
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToCreate = onNavigateToCreate
    )
}

/**
 * Konten Catatan Belajar (Stateless): murni menerima state dan callback tanpa ViewModel.
 */
@Composable
fun NoteListContent(
    uiState: NoteUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onBookmarkToggle: (String) -> Unit,
    onRetry: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToCreate: () -> Unit = {}
) {

    StudentLifeScaffold(
        topBar = {
            StudentLifeHeader(
                title = "Catatan Belajar",
                badgeIcon = Icons.AutoMirrored.Outlined.MenuBook
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
                    contentDescription = "Tambah Catatan",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        val loadError = uiState.errorMessage
        when {
            uiState.isLoading && uiState.allNotes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            loadError != null && uiState.allNotes.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = loadError,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text("Coba Lagi")
                    }
                }
            }
            else -> {
                // Konten Scrollable Dinamis di Tengah
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 84.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Kotak Pencarian (Full Width)
                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceCard)
                                .border(1.dp, BorderOutlineVariant, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Cari",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                placeholder = {
                                    Text(
                                        text = "Cari judul atau isi catatan...",
                                        color = TextMuted,
                                        fontSize = 14.sp
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Filter",
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // 2. Barisan Filter Chip Horizontal (Full Width)
                    item(span = { GridItemSpan(2) }) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.categoryFilters) { filter ->
                                val isSelected = filter == uiState.selectedCategory
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) PrimaryBlue else SurfaceCard)
                                        .border(
                                            1.dp,
                                            if (isSelected) PrimaryBlue else BorderOutlineVariant,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onCategorySelect(filter) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = filter,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // 3. Ringkasan Statistik KPI 3 Kartu (Full Width)
                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryStatCard(
                                count = "${uiState.totalNotesCount}",
                                label = "Total Catatan",
                                icon = Icons.Outlined.Description,
                                indicatorColor = PrimaryBlue,
                                modifier = Modifier.weight(1f)
                            )
                            SummaryStatCard(
                                count = "${uiState.photoNotesCount}",
                                label = "Dengan Foto",
                                icon = Icons.Outlined.CameraAlt,
                                indicatorColor = Color(0xFF00714D),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryStatCard(
                                count = "${uiState.coursesCount}",
                                label = "Mata Kuliah",
                                icon = Icons.Outlined.School,
                                indicatorColor = Color(0xFFE28B00),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 4. Daftar Kartu Catatan (2 Kolom Grid) atau Empty State
                    if (uiState.filteredNotes.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyStateView(
                                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                                    title = "Tidak Ada Catatan",
                                    description = if (uiState.searchQuery.isNotBlank()) {
                                        "Catatan dengan kata kunci '${uiState.searchQuery}' tidak ditemukan."
                                    } else {
                                        "Belum ada catatan belajar tersimpan. Ketuk + untuk membuat catatan baru."
                                    },
                                    actionButtonText = "Tambah Catatan Baru",
                                    onActionClick = onNavigateToCreate
                                )
                            }
                        }
                    } else {
                        items(uiState.filteredNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onBookmarkClick = { onBookmarkToggle(note.id) },
                                onClick = { onNavigateToDetail(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryStatCard(
    count: String,
    label: String,
    icon: ImageVector,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderOutlineVariant, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = indicatorColor,
                    modifier = Modifier.size(18.dp)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteListContentPreview() {
    StudentlifeappTheme {
        NoteListContent(
            uiState = NoteUiState(),
            searchQuery = "",
            onSearchQueryChange = {},
            onCategorySelect = {},
            onBookmarkToggle = {}
        )
    }
}
