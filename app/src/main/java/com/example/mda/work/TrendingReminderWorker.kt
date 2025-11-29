package com.example.mda.work

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mda.data.SettingsDataStore
import com.example.mda.notifications.NotificationHelper
import kotlinx.coroutines.flow.first

class TrendingReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            // ---------------------- Read user settings ----------------------
            val settingsDataStore = SettingsDataStore(applicationContext)
            val notificationsEnabled = settingsDataStore.notificationsFlow.first()

            if (!notificationsEnabled) {
                // المستخدم مطفي الإشعارات → لا نفعل أي شيء
                return Result.success()
            }

            // ---------------------- Send Notification ----------------------
            NotificationHelper.sendNotification(
                applicationContext,
                "جديد اليوم 👀",
                "في أفلام جديدة في الـ Trending — الحق شاهدهم!"
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
