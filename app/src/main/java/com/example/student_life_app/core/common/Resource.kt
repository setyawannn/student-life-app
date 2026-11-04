package com.example.student_life_app.core.common

/**
 * Wrapper kelas generik untuk merepresentasikan kondisi data (Loading, Success, Error).
 * Digunakan oleh seluruh Repository dan ViewModel di aplikasi StudentLife.
 */
sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}
