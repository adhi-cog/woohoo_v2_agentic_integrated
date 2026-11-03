package com.sb.woohoo.utils

import android.app.Activity
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sb.woohoo.utils.Constants.RESTART_WORK_PERIODIC_MANAGER_TAG
import java.util.concurrent.TimeUnit

/**
 * Util Class for creating OneTimeWorkRequest
 * @param context
 * @param totalMintunes
 */
class WorkerUtil {
    fun workFun(context: Activity, totalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresStorageNotLow(false)
            .build()
        val backupWorkRequest = OneTimeWorkRequestBuilder<RestartWorker>()
            .setConstraints(constraints)
            .setInitialDelay(totalMinutes.toLong(), TimeUnit.MINUTES)
            .addTag(RESTART_WORK_PERIODIC_MANAGER_TAG)
            .build()
        WorkManager.getInstance(context).enqueue(backupWorkRequest)
    }
}