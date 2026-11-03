package com.sb.woohoo.ui.main.fragments.reminders.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sb.woohoo.utils.Constants;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent reminderIntent) {
        Bundle reminderBundle = reminderIntent.getExtras();
        if (reminderBundle != null) {
            Intent mainActivityIntent = new Intent(Constants.MainActivity.REMINDER_INTENT);
            mainActivityIntent.putExtras(reminderBundle);
            context.sendBroadcast(mainActivityIntent);
        }
    }
}