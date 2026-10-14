package com.example.service

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.data.model.WallpaperItem
import com.example.data.model.WallpaperTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WallpaperManagerHelper {
    private const val TAG = "WallpaperManagerHelper"

    suspend fun applyWallpaper(
        context: Context,
        item: WallpaperItem,
        target: WallpaperTarget
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val bitmap = loadBitmap(context, item) ?: return@withContext false
            val wallpaperManager = WallpaperManager.getInstance(context)

            val flags = when (target) {
                WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
                WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
                WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, flags)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            Log.d(TAG, "Successfully applied wallpaper: ${item.title} to ${target.displayName}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error applying wallpaper", e)
            false
        }
    }

    private fun loadBitmap(context: Context, item: WallpaperItem): Bitmap? {
        // If drawableRes is provided
        item.drawableRes?.let { resId ->
            try {
                val drawable = ContextCompat.getDrawable(context, resId)
                if (drawable != null) {
                    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1080
                    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1920
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    return bitmap
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load drawable bitmap", e)
            }
        }

        // If customUri is provided
        item.customUri?.let { uriStr ->
            try {
                val uri = Uri.parse(uriStr)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    return BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load custom uri bitmap", e)
            }
        }

        return null
    }

    /**
     * Creates an enhanced/upscaled variation bitmap for custom generated anime wallpapers
     */
    fun createEnhancedProceduralBitmap(
        baseBitmap: Bitmap?,
        prompt: String,
        styleId: String
    ): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        if (baseBitmap != null) {
            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
            canvas.drawBitmap(baseBitmap, null, android.graphics.Rect(0, 0, width, height), paint)
        }

        return bitmap
    }
}
