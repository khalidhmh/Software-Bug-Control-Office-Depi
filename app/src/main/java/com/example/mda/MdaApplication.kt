package com.example.mda

import android.app.Application
import android.os.Build
import androidx.work.*
import com.example.mda.work.InactiveUserWorker
import com.example.mda.work.SuggestedMovieWorker
import com.example.mda.work.TrendingReminderWorker
import java.util.concurrent.TimeUnit

class MdaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupBackgroundWorkers()
    }

    private fun setupBackgroundWorkers() {
        // 1. تحديد الشروط (مثلاً: لازم يكون في نت)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workManager = WorkManager.getInstance(this)

        // -----------------------------------------------------------
        // Worker 1: InactiveUserWorker
        // بيشتغل كل 12 ساعة يتأكد لو المستخدم غايب بقاله 48 ساعة
        // -----------------------------------------------------------
        val inactiveUserRequest = PeriodicWorkRequestBuilder<InactiveUserWorker>(
            12, TimeUnit.HOURS // تكرار كل 12 ساعة
        )
            .setConstraints(constraints)
            .build()

        // بنستخدم KEEP عشان لو الشغلانة موجودة قبل كدا ميعملش واحدة جديدة
        workManager.enqueueUniquePeriodicWork(
            "InactiveUserWork",
            ExistingPeriodicWorkPolicy.KEEP,
            inactiveUserRequest
        )

        // -----------------------------------------------------------
        // Worker 2: SuggestedMovieWorker
        // بيقترح فيلم عشوائي كل 24 ساعة
        // -----------------------------------------------------------
        val suggestedMovieRequest = PeriodicWorkRequestBuilder<SuggestedMovieWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "SuggestedMovieWork",
            ExistingPeriodicWorkPolicy.KEEP,
            suggestedMovieRequest
        )

        // -----------------------------------------------------------
        // Worker 3: TrendingReminderWorker
        // بيفكرك بالتريند كل 1 يوم
        // -----------------------------------------------------------
        val trendingRequest = PeriodicWorkRequestBuilder<TrendingReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "TrendingWork",
            ExistingPeriodicWorkPolicy.KEEP,
            trendingRequest
        )
    }
}