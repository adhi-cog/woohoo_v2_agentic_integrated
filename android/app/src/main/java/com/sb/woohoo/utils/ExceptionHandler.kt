package com.sb.woohoo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Process
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sb.woohoo.utils.others.FileUtils

class ExceptionHandler(private val context: Activity, c: Class<*>) :
    Thread.UncaughtExceptionHandler {
    private val myContext: Context
    private val myActivityClass: Class<*>
    override fun uncaughtException(thread: Thread, exception: Throwable) {
        FileUtils.writeFileOnInternalStorage(context, Constants.CRASH_LOG, exception.toString()+ "##woohoo-message##" + exception.localizedMessage + "##woohoo-stacktrace##" + exception.stackTraceToString());

        val intent = Intent(myContext, myActivityClass)
        context.startActivity(intent)
        Process.killProcess(Process.myPid())
        System.exit(0)
    }

    init {
        myContext = context
        myActivityClass = c
    }
}