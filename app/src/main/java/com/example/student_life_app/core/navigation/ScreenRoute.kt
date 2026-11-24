package com.example.student_life_app.core.navigation

import kotlinx.serialization.Serializable

/**
 * Definisi rute navigasi aplikasi StudentLife berbasis Type-Safe Navigation Compose.
 * Digunakan bersama Jetpack Navigation Compose dan Kotlinx Serialization untuk alur multi-screen yang aman dari typo.
 */
@Serializable
sealed interface ScreenRoute {
    // Auth & Profile
    @Serializable data object Login : ScreenRoute
    @Serializable data object Register : ScreenRoute
    @Serializable data object Profile : ScreenRoute

    // Fitur 1: Manajemen Tugas Kuliah (Gangsar Wijayanto)
    @Serializable data object TaskList : ScreenRoute
    @Serializable data class TaskDetail(val taskId: String) : ScreenRoute
    @Serializable data class TaskForm(val taskId: String? = null) : ScreenRoute

    // Fitur 2: Manajemen Jadwal Kuliah (Muhammad Abdillah)
    @Serializable data object ScheduleList : ScreenRoute
    @Serializable data class ScheduleDetail(val scheduleId: String) : ScreenRoute
    @Serializable data class ScheduleForm(val scheduleId: String? = null) : ScreenRoute

    // Fitur 3: Catatan Belajar (Prayoga Adi Setyawan)
    @Serializable data object NoteList : ScreenRoute
    @Serializable data class NoteDetail(val noteId: String) : ScreenRoute
    @Serializable data class NoteForm(val noteId: String? = null) : ScreenRoute

    // Fitur 4: Manajemen Target Belajar (Tubagus Fatahudin Mulkan)
    @Serializable data object GoalList : ScreenRoute
    @Serializable data class GoalDetail(val goalId: String) : ScreenRoute
    @Serializable data class GoalForm(val goalId: String? = null) : ScreenRoute

    // Fitur 5: Referensi & Link Materi (Qoid Kafi)
    @Serializable data object ReferenceList : ScreenRoute
    @Serializable data class ReferenceDetail(val referenceId: String) : ScreenRoute
    @Serializable data class ReferenceForm(val referenceId: String? = null) : ScreenRoute
}
