package com.example.mda.work

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log // ✅ مضاف للـ Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mda.MainActivity
import com.example.mda.data.SettingsDataStore
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.database.AppDatabase
import com.example.mda.notifications.NotificationHelper
import kotlinx.coroutines.flow.first

class SuggestedMovieWorker(
    val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            // 1. التأكد من إعدادات المستخدم
            val settingsDataStore = SettingsDataStore(applicationContext)
            val isEnabled = settingsDataStore.notificationsFlow.first()

            if (!isEnabled) {
                return Result.success()
            }

            // 2. الوصول للداتا بيز
            val db = AppDatabase.getInstance(applicationContext)
            val repo = LocalRepository(db.mediaDao(), db.searchHistoryDao())

            // جلب البيانات
            val cached = repo.getAllOnce()

            // 🔍 Log عشان نعرف الـ Worker شايف كام فيلم
            Log.d("WorkerDebug", "🎬 SuggestedMovieWorker found ${cached.size} movies in DB")

            if (cached.isNotEmpty()) {
                // ✅ الحالة الأولى: فيه أفلام
                val movie = cached.random()

                // 🔗 نجهز الـ Intent بتاع الفيلم المحدد (Deep Link)
                // لاحظ: عرفنا الـ Intent هنا عشان نقدر نستخدم بيانات الـ movie
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    // 👇 بيانات التوجيه
                    putExtra("target_screen", "details")
                    putExtra("movie_id", movie.id)
                    putExtra("media_type", movie.mediaType ?: "movie")
                }

                val fullImageUrl = if (movie.posterPath != null) {
                    "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                } else null

                NotificationHelper.sendNotification(
                    applicationContext,
                    "${movie.name ?: movie.title} 🎬",
                    "جرب تشوف: ${movie.overview}",
                    imageUrl = fullImageUrl,
                    tapIntent = intent
                )
            } else {
                // ⚠️ الحالة الثانية: الداتا بيز فاضية

                // Intent عادي يفتح الصفحة الرئيسية
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                NotificationHelper.sendNotification(
                    applicationContext,
                    "تطبيق الأفلام جاهز! 🚀",
                    "لسه مفيش أفلام متسجلة.. افتح الصفحة الرئيسية وقلب شوية عشان نقدر نقترحلك حاجات تعجبك!",
                    imageUrl = null,
                    tapIntent = intent
                )
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WorkerDebug", "❌ Error in worker: ${e.message}")
            e.printStackTrace()
            Result.failure()
        }
    }
}