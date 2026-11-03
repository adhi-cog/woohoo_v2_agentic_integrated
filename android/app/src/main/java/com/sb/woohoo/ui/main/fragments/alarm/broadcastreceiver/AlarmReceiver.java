package com.sb.woohoo.ui.main.fragments.alarm.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sb.woohoo.utils.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        Bundle alarmBundle = alarmIntent.getExtras();
        if (alarmBundle != null) {
            Intent mainActivityIntent = new Intent(Constants.MainActivity.ALARM_INTENT);
            mainActivityIntent.putExtras(alarmBundle);
            context.sendBroadcast(mainActivityIntent);
        }
    }
}
