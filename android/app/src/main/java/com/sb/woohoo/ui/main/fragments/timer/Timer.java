package com.sb.woohoo.ui.main.fragments.timer;

import com.sb.woohoo.ui.main.fragments.notification.Notifications;

import java.io.Serializable;

public class Timer implements Serializable, Notifications {
    private double time;
    private String unit;

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
