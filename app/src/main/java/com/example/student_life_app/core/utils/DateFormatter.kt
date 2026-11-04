package com.example.student_life_app.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utilitas pemformatan tanggal dan waktu lokal Indonesia.
 */
object DateFormatter {
    private val localeId = Locale.forLanguageTag("id-ID")
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", localeId)
    private val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", localeId)
    private val displayTimeFormat = SimpleDateFormat("HH:mm 'WIB'", localeId)
    private val displayDateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", localeId)

    fun formatDisplayDate(date: Date): String = displayDateFormat.format(date)

    fun formatDisplayTime(date: Date): String = displayTimeFormat.format(date)

    fun formatDisplayDateTime(date: Date): String = displayDateTimeFormat.format(date)
}
