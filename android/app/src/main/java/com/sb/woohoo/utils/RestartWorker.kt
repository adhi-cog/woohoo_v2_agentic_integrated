package com.sb.woohoo.utils

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sb.woohoo.ui.onboarding.OnboardingActivity

/**
 * Once Periodic work manger works complete it calls {@property doWork()}
 * restart the app
 */

class RestartWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val intent = Intent(applicationContext, OnboardingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
        return Result.success()
    }
}