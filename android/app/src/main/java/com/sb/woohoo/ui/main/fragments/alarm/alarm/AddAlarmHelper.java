package com.sb.woohoo.ui.main.fragments.alarm.alarm;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;

import com.sb.woohoo.ui.main.fragments.alarm.pojo.Alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
@SuppressLint("SimpleDateFormat")
public class AddAlarmHelper {

    private int mHour, mMinute;

    void showTimePickerDialog(Context context, TimePickerListener timePickerListener){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view2, hourOfDay, minute) -> {
                    Date date = c.getTime();
                    timePickerListener.onTimeSet(hourOfDay, minute,c);
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    public Alarm setAlarm(int hourOfDay, int minute, Calendar calendar) {
        Alarm alarm = new Alarm();
        String time = getTimeInString(hourOfDay,minute);
        alarm.setDateTime(convertDateFormat(calendar.getTime(),"yyyy-MM-dd'T'HH:mm",false));
        alarm.setUTCTime(convertDateFormat(calendar.getTime(),"yyyy-MM-dd'T'HH:mm",true));
        alarm.setTimeInHours(time);
        return alarm;
    }

    public Alarm updateAlarm(int hourOfDay, int minute, Calendar calendar){
        Calendar currentTime = Calendar.getInstance();
        Alarm alarm = new Alarm();
        String time = getTimeInString(hourOfDay,minute);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        if (currentTime.getTime().after(calendar.getTime()))
            calendar.add(Calendar.DATE,1);
        alarm.setDateTime(convertDateFormat(calendar.getTime(),"yyyy-MM-dd'T'HH:mm",false));
        alarm.setUTCTime(convertDateFormat(calendar.getTime(),"yyyy-MM-dd'T'HH:mm",true));
        alarm.setTimeInHours(time);
        return alarm;
    }

    String getTimeInString(int hourOfDay, int minute){
        String time = "";
        if (hourOfDay<10)
            time = "0";
        time = time + hourOfDay + ":";

        if (minute < 10)
            time = time + "0";
        time = time + minute;
        return time;
    }

    public String convertDateFormat(Date date, String dateFormat, boolean shouldSetUTCTimeZone){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    public Date convertStringToDate(String dateInStr, String dateFormat){
       SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            Date date = format.parse(dateInStr);
            System.out.println(date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    interface TimePickerListener{
        void onTimeSet(int hourOfDay, int minute, Calendar c);
    }
}
