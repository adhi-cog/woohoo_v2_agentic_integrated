package com.sb.woohoo.leia.helpers

import android.content.Context
import android.content.pm.PackageManager

object InstalledAppHelper {

    const val LEIA_PACKAGE_NAME = "com.leia.woohoopoc"

    fun hasLeiaApp(appContext: Context) : Boolean {
        val packageManager: PackageManager = appContext.getPackageManager()
        return try {
            packageManager.getPackageInfo(LEIA_PACKAGE_NAME, 0)
            appContext.getPackageManager().getApplicationIcon(LEIA_PACKAGE_NAME) != null
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}