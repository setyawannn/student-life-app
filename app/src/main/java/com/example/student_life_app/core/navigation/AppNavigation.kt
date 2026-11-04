package com.example.student_life_app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.student_life_app.features.goal.ui.GoalListScreen
import com.example.student_life_app.features.note.ui.NoteListScreen
import com.example.student_life_app.features.reference.ui.ReferenceListScreen
import com.example.student_life_app.features.schedule.ui.ScheduleListScreen
import com.example.student_life_app.features.task.ui.TaskListScreen

@Composable
fun MainAppContainer(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Tampilkan BottomNavBar jika berada pada halaman utama list
            val isMainBottomRoute = bottomNavItems.any { it.route == currentRoute }
            if (isMainBottomRoute || currentRoute == null) {
                StudentLifeBottomNavBar(
                    currentRoute = currentRoute ?: ScreenRoute.TaskList.route,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(ScreenRoute.TaskList.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.TaskList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Fitur 1: Manajemen Tugas Kuliah (Gangsar Wijayanto)
            composable(ScreenRoute.TaskList.route) {
                TaskListScreen()
            }

            // Fitur 2: Manajemen Jadwal Kuliah (Muhammad Abdillah)
            composable(ScreenRoute.ScheduleList.route) {
                ScheduleListScreen()
            }

            // Fitur 3: Catatan Belajar (Prayoga Adi Setyawan)
            composable(ScreenRoute.NoteList.route) {
                NoteListScreen()
            }

            // Fitur 4: Target Belajar (Tubagus Fatahudin Mulkan)
            composable(ScreenRoute.GoalList.route) {
                GoalListScreen()
            }

            // Fitur 5: Referensi & Link Materi (Qoid Kafi)
            composable(ScreenRoute.ReferenceList.route) {
                ReferenceListScreen()
            }
        }
    }
}
