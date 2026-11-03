package com.sb.woohoo.ui.main.fragments.reminders.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.notification.Notifications;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import org.joda.time.DateTimeComparator;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Created by Satyaraj on 3/8/2018.
 */

public class Reminder extends BaseModel<Reminder> implements Serializable, Comparable<Reminder>, Notifications {

    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("reminderId")
    @Expose
    private String reminderId;
    @SerializedName("reminder")
    @Expose
    private String reminder;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Reminder data;
    @SerializedName("error")
    @Expose
    private String error;

    private String UTCTime;
    private int type;
    private String reminderTime;
    private String reminderDate;
    private Date reminderDateTime;
    private String date;
    private String time;

    public String getUTCTime() {
        return UTCTime;
    }

    public void setUTCTime(String UTCTime) {
        this.UTCTime = UTCTime;
    }

    public Reminder getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public Date getReminderDateTime() {
        return reminderDateTime;
    }

    public void setReminderDateTime(Date reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }

    @Override
    public int compareTo(@NonNull Reminder o) {
        if (reminderDateTime.before(o.reminderDateTime)) {
            return -1;
        } else if (reminderDateTime.after(o.reminderDateTime)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {
        if (hashMap.containsKey("name")) {
            this.reminder = hashMap.get("name");
        }
        if (hashMap.containsKey("date") && hashMap.containsKey("time") && !hashMap.get("date").isEmpty()) {
            this.date = hashMap.get("date");
            this.time = hashMap.get("time");
            this.dateTime = hashMap.get("date") + "T" + hashMap.get("time").substring(0, 5);
        }

        if (hashMap.containsKey("time") && hashMap.get("date").isEmpty()) {
            this.time = hashMap.get("time");
            Calendar calendar = getDay(time);
            if (calendar != null) {
                this.dateTime = String.valueOf(calendar.get(Calendar.YEAR) + "-"
                        + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + "T"
                        + hashMap.get("time").substring(0, 5));
            }
        }
    }

    private Calendar getDay(String time) {
        Calendar calendar = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        try {
            calendar.setTime(dateFormat.parse(time));
            c.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            if (comparator.compare(c, currentDate) > 0) {
                return currentDate;
            } else {
                currentDate.add(Calendar.DATE, 1);
                return currentDate;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
