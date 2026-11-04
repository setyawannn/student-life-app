package com.example.student_life_app.core.navigation

/**
 * Definisi rute navigasi aplikasi StudentLife.
 * Digunakan bersama Jetpack Navigation Compose untuk alur multi-screen yang terstruktur.
 */
sealed class ScreenRoute(val route: String) {
    // Auth & Profile
    data object Login : ScreenRoute("login")
    data object Register : ScreenRoute("register")
    data object Profile : ScreenRoute("profile")

    // Fitur 1: Manajemen Tugas Kuliah (Gangsar Wijayanto)
    data object TaskList : ScreenRoute("task_list")
    data object TaskDetail : ScreenRoute("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    data object TaskForm : ScreenRoute("task_form?taskId={taskId}") {
        fun createRoute(taskId: String? = null) = if (taskId != null) "task_form?taskId=$taskId" else "task_form"
    }

    // Fitur 2: Manajemen Jadwal Kuliah (Muhammad Abdillah)
    data object ScheduleList : ScreenRoute("schedule_list")
    data object ScheduleDetail : ScreenRoute("schedule_detail/{scheduleId}") {
        fun createRoute(scheduleId: String) = "schedule_detail/$scheduleId"
    }
    data object ScheduleForm : ScreenRoute("schedule_form?scheduleId={scheduleId}") {
        fun createRoute(scheduleId: String? = null) = if (scheduleId != null) "schedule_form?scheduleId=$scheduleId" else "schedule_form"
    }

    // Fitur 3: Catatan Belajar (Prayoga Adi Setyawan)
    data object NoteList : ScreenRoute("note_list")
    data object NoteDetail : ScreenRoute("note_detail/{noteId}") {
        fun createRoute(noteId: String) = "note_detail/$noteId"
    }
    data object NoteForm : ScreenRoute("note_form?noteId={noteId}") {
        fun createRoute(noteId: String? = null) = if (noteId != null) "note_form?noteId=$noteId" else "note_form"
    }

    // Fitur 4: Manajemen Target Belajar (Tubagus Fatahudin Mulkan)
    data object GoalList : ScreenRoute("goal_list")
    data object GoalDetail : ScreenRoute("goal_detail/{goalId}") {
        fun createRoute(goalId: String) = "goal_detail/$goalId"
    }
    data object GoalForm : ScreenRoute("goal_form?goalId={goalId}") {
        fun createRoute(goalId: String? = null) = if (goalId != null) "goal_form?goalId=$goalId" else "goal_form"
    }

    // Fitur 5: Referensi & Link Materi (Qoid Kafi)
    data object ReferenceList : ScreenRoute("reference_list")
    data object ReferenceDetail : ScreenRoute("reference_detail/{referenceId}") {
        fun createRoute(referenceId: String) = "reference_detail/$referenceId"
    }
    data object ReferenceForm : ScreenRoute("reference_form?referenceId={referenceId}") {
        fun createRoute(referenceId: String? = null) = if (referenceId != null) "reference_form?referenceId=$referenceId" else "reference_form"
    }
}
