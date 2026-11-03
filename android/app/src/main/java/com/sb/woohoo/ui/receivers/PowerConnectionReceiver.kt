package com.sb.woohoo.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class PowerConnectionReceiver() : BroadcastReceiver() {

    private var listener : IPowerConnectionChangeListener? = null

    fun setListener(listener: IPowerConnectionChangeListener?) {
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent) {
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        listener?.let {
            it.onPowerConnectionChange(isCharging)
        }
    }

   interface IPowerConnectionChangeListener {
      fun onPowerConnectionChange(isCharging: Boolean)
   }
}