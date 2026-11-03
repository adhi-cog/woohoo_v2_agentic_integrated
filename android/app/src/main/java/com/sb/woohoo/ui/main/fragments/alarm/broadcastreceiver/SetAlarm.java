package com.sb.woohoo.ui.main.fragments.alarm.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmPresenter;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import static android.content.Context.ALARM_SERVICE;
import static com.sb.woohoo.ui.main.fragments.alarm.alarm.AlarmPresenter.DATE_PATTERN;
import static com.sb.woohoo.utils.others.MethodUtils.flagForPendingIntent;

public class SetAlarm {

    public static void setAlarm(Context context, Class<?> cls, Alarm alarm, int alarmRequestCode, boolean everyday, boolean snooze) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        Calendar setCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        try {
            if (everyday) {
                calendar.setTime(dateFormatter.parse(alarm.getDateTime()));
                setCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                setCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                if (snooze) {
                    setCalendar.add(Calendar.MINUTE, 10);
                    int year = setCalendar.get(Calendar.YEAR);
                    int month = setCalendar.get(Calendar.MONTH);
                    int date = setCalendar.get(Calendar.DATE);
                    int hour = setCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = setCalendar.get(Calendar.MINUTE);

                    String dateTime = year + "-" +
                            month + "-" +
                            date + "T" +
                            hour + ":" +
                            minute + "Z";

                    alarm.setDateTime(dateTime);
                    alarm.setSnooze(true);
                }
            } else {
                setCalendar.setTime(dateFormatter.parse(alarm.getDateTime()));
                if (snooze) {
                    setCalendar.add(Calendar.MINUTE, 10);
                    int year = setCalendar.get(Calendar.YEAR);
                    int month = setCalendar.get(Calendar.MONTH);
                    int date = setCalendar.get(Calendar.DATE);
                    int hour = setCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = setCalendar.get(Calendar.MINUTE);

                    String dateTime = year + "-" +
                            month + "-" +
                            date + "T" +
                            hour + ":" +
                            minute + "Z";

                    alarm.setDateTime(dateTime);
                    alarm.setSnooze(true);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // cancel already scheduled alarm
        cancelAlarm(context, cls, alarmRequestCode);

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
            out.writeObject(alarm);
            out.flush();
            byte[] data = byteArrayOutputStream.toByteArray();
            Bundle alarmBundle = new Bundle();
            alarmBundle.putSerializable(AlarmPresenter.ALARM_KEY, data);
            intent.putExtras(alarmBundle);
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
                alarmRequestCode, intent,
                flagForPendingIntent());
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            if (everyday) {
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setCalendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, setCalendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public static void snoozeAlarm(Intent intent, Context context, Class<?> cls, int snoozeCode) {
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                snoozeCode, intent,
                flagForPendingIntent());

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        am.setExact(AlarmManager.RTC_WAKEUP, 60 * 1000, pendingIntent);

    }

    public static void cancelAlarm(Context context, Class<?> cls, int reminderRequestCode) {
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

    public static void cancelAllAlarms(Context context, HashMap<String,String> alarmIdsMap){
        Set<String> alarmIds = alarmIdsMap.keySet();
        for (String alarmId : alarmIds){
            cancelAlarm(context,AlarmReceiver.class,Integer.parseInt(alarmId));
        }
    }
}
