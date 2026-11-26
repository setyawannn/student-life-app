package com.example.student_life_app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.student_life_app.core.database.SupabaseManager
import com.example.student_life_app.features.auth.ui.LoginScreen
import com.example.student_life_app.features.auth.ui.RegisterScreen
import com.example.student_life_app.features.goal.ui.GoalListScreen
import com.example.student_life_app.features.note.ui.NoteDetailScreen
import com.example.student_life_app.features.note.ui.NoteFormScreen
import com.example.student_life_app.features.note.ui.NoteListScreen
import com.example.student_life_app.features.profile.ui.ProfileScreen
import com.example.student_life_app.features.reference.ui.ReferenceDetailScreen
import com.example.student_life_app.features.reference.ui.ReferenceFormScreen
import com.example.student_life_app.features.reference.ui.ReferenceListScreen
import com.example.student_life_app.features.schedule.ui.ScheduleDetailScreen
import com.example.student_life_app.features.schedule.ui.ScheduleFormScreen
import com.example.student_life_app.features.schedule.ui.ScheduleListScreen
import com.example.student_life_app.features.task.ui.TaskDetailScreen
import com.example.student_life_app.features.task.ui.TaskFormScreen
import com.example.student_life_app.features.task.ui.TaskListScreen
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun MainAppContainer(
    navController: NavHostController = rememberNavController()
) {
    val sessionStatus by SupabaseManager.client.auth.sessionStatus.collectAsStateWithLifecycle()

    // Tampilkan loading singkat saat Supabase memulihkan sesi tersimpan dari storage
    if (sessionStatus is SessionStatus.Initializing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val isAuthenticated = sessionStatus is SessionStatus.Authenticated
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Auth Middleware: lindungi seluruh rute utama jika belum login,
    // dan arahkan ke TaskList jika sudah terautentikasi
    LaunchedEffect(isAuthenticated, currentDestination) {
        val destination = currentDestination ?: return@LaunchedEffect
        val isOnAuthRoute = destination.hierarchy.any {
            it.hasRoute(ScreenRoute.Login::class) || it.hasRoute(ScreenRoute.Register::class)
        }

        if (!isAuthenticated && !isOnAuthRoute) {
            navController.navigate(ScreenRoute.Login) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        } else if (isAuthenticated && isOnAuthRoute) {
            navController.navigate(ScreenRoute.TaskList) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            // Tampilkan BottomNavBar hanya pada halaman utama list saat sudah login
            val isMainBottomRoute = isAuthenticated && bottomNavItems.any { item ->
                currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
            }
            if (isMainBottomRoute) {
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
        val navigateToProfile: () -> Unit = {
            navController.navigate(ScreenRoute.Profile) {
                launchSingleTop = true
            }
        }
        val initialDestination: ScreenRoute = if (isAuthenticated) {
            ScreenRoute.TaskList
        } else {
            ScreenRoute.Login
        }

        NavHost(
            navController = navController,
            startDestination = initialDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth: Login & Register
            composable<ScreenRoute.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(ScreenRoute.TaskList) {
                            popUpTo<ScreenRoute.Login> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(ScreenRoute.Register) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<ScreenRoute.Register> {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(ScreenRoute.TaskList) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // Fitur 1: Manajemen Tugas Kuliah (Gangsar Wijayanto)
            composable<ScreenRoute.TaskList> {
                TaskListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(ScreenRoute.TaskDetail(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(ScreenRoute.TaskForm())
                    },
                    onNavigateToProfile = navigateToProfile
                )
            }
            composable<ScreenRoute.TaskDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.TaskDetail>()
                TaskDetailScreen(
                    taskId = args.taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> navController.navigate(ScreenRoute.TaskForm(id)) }
                )
            }
            composable<ScreenRoute.TaskForm> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.TaskForm>()
                TaskFormScreen(
                    taskId = args.taskId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Fitur 2: Manajemen Jadwal Kuliah (Muhammad Abdillah)
            composable<ScreenRoute.ScheduleList> {
                ScheduleListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(ScreenRoute.ScheduleDetail(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(ScreenRoute.ScheduleForm())
                    },
                    onNavigateToProfile = navigateToProfile
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
                NoteListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(ScreenRoute.NoteDetail(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(ScreenRoute.NoteForm())
                    },
                    onNavigateToProfile = navigateToProfile
                )
            }
            composable<ScreenRoute.NoteDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.NoteDetail>()
                NoteDetailScreen(
                    noteId = args.noteId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(ScreenRoute.NoteForm(id)) },
                    onDeleted = { navController.popBackStack() }
                )
            }
            composable<ScreenRoute.NoteForm> { backStackEntry ->
                val args = backStackEntry.toRoute<ScreenRoute.NoteForm>()
                NoteFormScreen(
                    noteId = args.noteId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // Fitur 4: Target Belajar (Tubagus Fatahudin Mulkan)
            composable<ScreenRoute.GoalList> {
                GoalListScreen(
                    onNavigateToProfile = navigateToProfile
                )
            }

            // Fitur 5: Referensi & Link Materi (Qoid Kafi)
            composable<ScreenRoute.ReferenceList> {
                ReferenceListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(ScreenRoute.ReferenceDetail(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(ScreenRoute.ReferenceForm())
                    },
                    onNavigateToProfile = navigateToProfile
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

            // Profil & Pengaturan
            composable<ScreenRoute.Profile> {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(ScreenRoute.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
