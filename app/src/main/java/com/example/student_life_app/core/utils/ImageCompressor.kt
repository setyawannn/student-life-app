package com.example.student_life_app.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

/**
 * Utilitas kompresi gambar klien untuk mengoptimalkan unggahan foto ke Supabase Storage.
 * Mencegah error Out-Of-Memory (OOM) dan menghemat kuota transmisi data.
 */
object ImageCompressor {
    fun compress(
        context: Context,
        imageUri: Uri,
        maxDimension: Int = 1280,
        quality: Int = 80
    ): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

            val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
            val targetWidth: Int
            val targetHeight: Int

            if (originalBitmap.width > originalBitmap.height) {
                targetWidth = maxDimension
                targetHeight = (maxDimension / ratio).toInt()
            } else {
                targetHeight = maxDimension
                targetWidth = (maxDimension * ratio).toInt()
            }

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
