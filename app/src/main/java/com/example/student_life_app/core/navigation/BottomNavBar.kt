package com.example.student_life_app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.student_life_app.ui.theme.BorderOutlineVariant
import com.example.student_life_app.ui.theme.PrimaryBlue
import com.example.student_life_app.ui.theme.SurfaceCard
import com.example.student_life_app.ui.theme.TextMuted

data class BottomNavItem(
    val title: String,
    val route: String,
    val outlineIcon: ImageVector
)

// Daftar menu Bottom Bar dengan ikon OUTLINE sesuai standar desain
val bottomNavItems = listOf(
    BottomNavItem("Tugas", ScreenRoute.TaskList.route, Icons.Outlined.CheckCircle),
    BottomNavItem("Jadwal", ScreenRoute.ScheduleList.route, Icons.Outlined.CalendarMonth),
    BottomNavItem("Catatan", ScreenRoute.NoteList.route, Icons.AutoMirrored.Outlined.MenuBook),
    BottomNavItem("Target", ScreenRoute.GoalList.route, Icons.Outlined.TrackChanges),
    BottomNavItem("Referensi", ScreenRoute.ReferenceList.route, Icons.Outlined.FolderOpen)
)

/**
 * Bottom Navigation Bar fixed yang bersih dan modern.
 * Menggunakan ikon Outline murni sesuai instruksi dan referensi screenshot desain.
 */
@Composable
fun StudentLifeBottomNavBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceCard)
            .navigationBarsPadding()
    ) {
        // Garis pemisah halus di atas navbar
        HorizontalDivider(
            thickness = 1.dp,
            color = BorderOutlineVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                val itemColor = if (isSelected) PrimaryBlue else TextMuted
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (!isSelected) {
                                onNavigateToRoute(item.route)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = item.outlineIcon,
                            contentDescription = item.title,
                            tint = itemColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = item.title,
                            color = itemColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
