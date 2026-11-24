package com.example.student_life_app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.student_life_app.features.goal.ui.GoalListScreen
import com.example.student_life_app.features.note.ui.NoteListScreen
import com.example.student_life_app.features.reference.ui.ReferenceDetailScreen
import com.example.student_life_app.features.reference.ui.ReferenceFormScreen
import com.example.student_life_app.features.reference.ui.ReferenceListScreen
import com.example.student_life_app.features.schedule.ui.ScheduleDetailScreen
import com.example.student_life_app.features.schedule.ui.ScheduleFormScreen
import com.example.student_life_app.features.schedule.ui.ScheduleListScreen
import com.example.student_life_app.features.task.ui.TaskListScreen

@Composable
fun MainAppContainer(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            // Tampilkan BottomNavBar jika berada pada halaman utama list
            val isMainBottomRoute = bottomNavItems.any { item ->
                currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            }
            if (isMainBottomRoute || currentDestination == null) {
                StudentLifeBottomNavBar(
                    currentDestination = currentDestination,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo<ScreenRoute.TaskList> {
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
            startDestination = ScreenRoute.TaskList,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Fitur 1: Manajemen Tugas Kuliah (Gangsar Wijayanto)
            composable<ScreenRoute.TaskList> {
                TaskListScreen()
            }

            // Fitur 2: Manajemen Jadwal Kuliah (Muhammad Abdillah)
            composable<ScreenRoute.ScheduleList> {
                ScheduleListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(ScreenRoute.ScheduleDetail(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(ScreenRoute.ScheduleForm())
                    }
                )
            }
            composable<ScreenRoute.ScheduleDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.ScheduleDetail>()
                ScheduleDetailScreen(
                    scheduleId = args.scheduleId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(ScreenRoute.ScheduleForm(id)) },
                    onDeleted = { navController.popBackStack() }
                )
            }
            composable<ScreenRoute.ScheduleForm> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.ScheduleForm>()
                ScheduleFormScreen(
                    scheduleId = args.scheduleId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // Fitur 3: Catatan Belajar (Prayoga Adi Setyawan)
            composable<ScreenRoute.NoteList> {
                NoteListScreen()
            }

            // Fitur 4: Target Belajar (Tubagus Fatahudin Mulkan)
            composable<ScreenRoute.GoalList> {
                GoalListScreen()
            }

            // Fitur 5: Referensi & Link Materi (Qoid Kafi)
            composable<ScreenRoute.ReferenceList> {
                ReferenceListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(ScreenRoute.ReferenceDetail(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(ScreenRoute.ReferenceForm())
                    }
                )
            }
            composable<ScreenRoute.ReferenceDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.ReferenceDetail>()
                ReferenceDetailScreen(
                    referenceId = args.referenceId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(ScreenRoute.ReferenceForm(id)) },
                    onDeleted = { navController.popBackStack() }
                )
            }
            composable<ScreenRoute.ReferenceForm> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.ReferenceForm>()
                ReferenceFormScreen(
                    referenceId = args.referenceId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}