package com.sb.woohoo.custom

import android.app.Activity
import android.content.Intent
import com.sb.woohoo.ui.onboarding.OnboardingActivity

class WoohooExceptionHandler(var activity: Activity) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        restart()
    }

    fun restart() {
        val intent = Intent(activity, OnboardingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        System.exit(0)
        activity.finish()
    }
}