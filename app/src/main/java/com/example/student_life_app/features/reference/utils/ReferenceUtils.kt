package com.example.student_life_app.features.reference.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object ReferenceUtils {
    fun openInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
