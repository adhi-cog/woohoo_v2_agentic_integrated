package com.sb.woohoo.ui.main.fragments.reservation.booking.pojo;

public class TimeSlot {

    public String getTime() {
        return time;
    }



    public void setTime(String time) {
        this.time = time;
    }

    String time;
    boolean available;
    String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected;
}