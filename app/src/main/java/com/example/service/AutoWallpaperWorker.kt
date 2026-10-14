package com.example.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.database.AppDatabase
import com.example.data.database.AutoWallpaperHistoryEntity
import com.example.data.model.AutoChangeInterval
import com.example.data.model.WallpaperTarget
import kotlinx.coroutines.flow.first

class AutoWallpaperWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val prefs = context.getSharedPreferences("animai_settings_prefs", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("auto_enabled", false)

        // If auto change is disabled, complete without changing
        if (!isEnabled) {
            Log.d("AutoWallpaperWorker", "Auto wallpaper is disabled, skipping")
            return Result.success()
        }

        val targetStr = prefs.getString("auto_target", WallpaperTarget.BOTH.name)
        val filter = prefs.getString("auto_style_filter", "ALL") ?: "ALL"

        val target = try {
            WallpaperTarget.valueOf(targetStr ?: WallpaperTarget.BOTH.name)
        } catch (_: Exception) {
            WallpaperTarget.BOTH
        }

        val database = AppDatabase.getDatabase(context)
        val dao = database.wallpaperDao()

        val allItems = dao.getAllWallpapers().first().map { it.toWallpaperItem() }
        if (allItems.isEmpty()) {
            return Result.success()
        }

        val candidates = when (filter) {
            "FAVORITES" -> {
                val favs = allItems.filter { it.isFavorite }
                if (favs.isNotEmpty()) favs else allItems
            }
            "ALL" -> allItems
            else -> {
                val byStyle = allItems.filter { it.style.id.equals(filter, ignoreCase = true) }
                if (byStyle.isNotEmpty()) byStyle else allItems
            }
        }

        val selected = candidates.random()
        val success = WallpaperManagerHelper.applyWallpaper(context, selected, target)

        if (success) {
            dao.insertHistoryEntry(
                AutoWallpaperHistoryEntity(
                    wallpaperId = selected.id,
                    wallpaperTitle = selected.title,
                    styleId = selected.style.id,
                    targetScreen = target.displayName,
                    timestamp = System.currentTimeMillis()
                )
            )

            val currentCount = prefs.getInt("total_changes_count", 0) + 1
            val now = System.currentTimeMillis()
            val intervalStr = prefs.getString("auto_interval", AutoChangeInterval.DAILY.name)
            val interval = try {
                AutoChangeInterval.valueOf(intervalStr ?: AutoChangeInterval.DAILY.name)
            } catch (_: Exception) {
                AutoChangeInterval.DAILY
            }
            val nextTime = now + (interval.hours * 3600 * 1000L)

            prefs.edit()
                .putInt("total_changes_count", currentCount)
                .putLong("last_changed_timestamp", now)
                .putLong("next_scheduled_timestamp", nextTime)
                .apply()
        }

        return Result.success()
    }
}
