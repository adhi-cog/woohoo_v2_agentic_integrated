package com.sb.woohoo.ui.main.fragments.home.date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sb.woohoo.utils.Constants;

public class DateAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent homeIntent = new Intent(Constants.HomeFragment.HOME_INTENT);
        context.sendBroadcast(homeIntent);
    }
}
