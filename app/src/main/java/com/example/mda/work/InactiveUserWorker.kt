package com.example.mda.work

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mda.data.SettingsDataStore
import com.example.mda.notifications.NotificationHelper
import kotlinx.coroutines.flow.first

class InactiveUserWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        Log.d("WorkerDebug", "🟢 InactiveUserWorker: بدأ العمل") // 1. هل اشتغل أصلاً؟

        return try {
            // ---------------------- Read user settings ----------------------
            val settingsDataStore = SettingsDataStore(applicationContext)

            // قراءة القيمة وطباعتها
            val notificationsEnabled = settingsDataStore.notificationsFlow.first()
            Log.d("WorkerDebug", "🧐 حالة الإشعارات في الإعدادات: $notificationsEnabled")

            if (!notificationsEnabled) {
                Log.e("WorkerDebug", "⛔ توقف: المستخدم لاغي الإشعارات من إعدادات التطبيق")
                return Result.success()
            }

            // ---------------------- Check last open ----------------------
            val prefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val lastOpen = prefs.getLong("last_open", 0L)

            // حساب الساعات وطباعتها
            val hours = (System.currentTimeMillis() - lastOpen) / (1000 * 60 * 60)
            Log.d("WorkerDebug", "⏳ آخر فتح كان من: $hours ساعات")

            // الشرط اللي إنت بتجرب بيه
            if (hours >= 0) {
                Log.d("WorkerDebug", "🚀 الشرط تحقق! جاري إرسال الإشعار...")

                NotificationHelper.sendNotification(
                    applicationContext,
                    "وحشتنا يا فنان!",
                    "بقالك فترة متفرجتش — الحق افتح popular وشوف الجديد."
                )
                Log.d("WorkerDebug", "✅ تم استدعاء دالة الإرسال")
            } else {
                Log.d("WorkerDebug", "⚠️ الشرط لم يتحقق (عدد الساعات غير كافي)")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WorkerDebug", "❌ خطأ (Crash) داخل الـ Worker: ${e.message}")
            e.printStackTrace()
            Result.failure()
        }
    }
}
