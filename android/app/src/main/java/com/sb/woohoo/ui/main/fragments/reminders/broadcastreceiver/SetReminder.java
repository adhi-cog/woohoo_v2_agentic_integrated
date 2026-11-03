package com.sb.woohoo.ui.main.fragments.reminders.broadcastreceiver;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static com.sb.woohoo.ui.main.fragments.reminders.ReminderPresenter.DATE_PATTERN;
import static com.sb.woohoo.utils.others.MethodUtils.flagForPendingIntent;

public class SetReminder {

    public static final String REMINDER_KEY = "reminder";

    public static void setReminder(Context context, Class<?> cls, Reminder reminder, int reminderRequestCode) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        Calendar setCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        try {
            setCalendar.setTime(dateFormatter.parse(reminder.getDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // cancel already scheduled reminders
        cancelReminder(context, cls, reminderRequestCode);

        if (setCalendar.before(calendar))
            setCalendar.add(Calendar.DATE, 1);

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(reminder);
            out.flush();
            byte[] data = byteArrayOutputStream.toByteArray();
            Bundle reminderBundle = new Bundle();
            reminderBundle.putSerializable(REMINDER_KEY, data);
            intent.putExtras(reminderBundle);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                reminderRequestCode, intent,
                flagForPendingIntent());
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.setExact(AlarmManager.RTC_WAKEUP, setCalendar.getTimeInMillis(), pendingIntent);
        }
    }

    private static void cancelReminder(Context context, Class<?> cls, int reminderRequestCode) {
        // Disable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                reminderRequestCode, intent, flagForPendingIntent());
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }


}
