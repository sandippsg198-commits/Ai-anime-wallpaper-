package com.example.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.model.AutoChangeInterval
import java.util.concurrent.TimeUnit

object WallpaperScheduler {
    private const val WORK_NAME_PERIODIC = "animai_daily_wallpaper_worker"
    private const val WORK_NAME_ONE_TIME = "animai_trigger_now_worker"

    fun schedulePeriodicAutoChange(context: Context, interval: AutoChangeInterval) {
        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<AutoWallpaperWorker>(
            interval.hours,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )
    }

    fun cancelAutoChange(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(WORK_NAME_PERIODIC)
    }

    fun triggerImmediateAutoChange(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val oneTimeRequest = OneTimeWorkRequestBuilder<AutoWallpaperWorker>()
            .build()
        workManager.enqueueUniqueWork(
            WORK_NAME_ONE_TIME,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest
        )
    }
}
