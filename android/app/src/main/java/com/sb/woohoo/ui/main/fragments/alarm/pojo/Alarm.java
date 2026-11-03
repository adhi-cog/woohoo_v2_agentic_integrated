package com.sb.woohoo.ui.main.fragments.alarm.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.notification.Notifications;
import com.sb.woohoo.ui.main.fragments.todo.ToDoPresenter;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;
import com.sb.woohoo.utils.others.MethodUtils;

import java.io.Serializable;
import java.util.HashMap;

public class Alarm extends BaseModel<Alarm> implements Serializable, Notifications {

    public int index;
    public String UTCTime;
    @SerializedName("isEveryDayAlarm")
    @Expose
    private boolean everyday = false;
    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("alarmId")
    @Expose
    private String alarmID;
    private String date;
    private boolean snooze;
    private String timeInHours;

    public String getUTCTime() {
        return UTCTime;
    }

    public void setUTCTime(String UTCTime) {
        this.UTCTime = UTCTime;
    }

    public boolean isSnooze() {
        return snooze;
    }

    public void setSnooze(boolean snooze) {
        this.snooze = snooze;
    }

    public boolean isEveryday() {
        return everyday;
    }

    public void setEveryday(boolean everyday) {
        this.everyday = everyday;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTimeInHours() {
        return timeInHours;
    }

    public void setTimeInHours(String timeInHours) {
        this.timeInHours = timeInHours;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(String alarmID) {
        this.alarmID = alarmID;
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {
        setEveryday(false);
        if (hashMap.containsKey("time")) {
            this.timeInHours = hashMap.get("time").substring(0, 5);
        }
        if (hashMap.containsKey("date")) {
            this.date = hashMap.get("date");

        }
        if (hashMap.containsKey("everyday")) {
            if (!hashMap.get("everyday").isEmpty()) {
                setEveryday(true);
            }
        }
        if (hashMap.containsKey("number")) {
            this.index = Integer.parseInt(hashMap.get("number"));
        } else if (hashMap.containsKey("quantity")) {
            this.index = Integer.parseInt(hashMap.get("quantity"));
        }
    }

    //TODO Use it with Smart Flow
    public int getOrdinalInteger(String value) {
        double d = 0.0;
        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            MethodUtils.printLog(ToDoPresenter.class, e.getMessage());
        }
        return (int) d;
    }
}
