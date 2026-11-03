package com.sb.woohoo.utils.others

import android.os.SystemClock
import android.view.View


/*
 To prevent multi clicks on same view
 */
abstract class SingleClickListener : View.OnClickListener
{
    private var defaultInterval: Int = 1000
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSingleClick(v)
    }

    abstract fun onSingleClick(v: View)

    companion object
    {
        var lastTimeClicked: Long = 0
    }
}