package com.sb.woohoo.ui.main.fragments.reservation.booking.pojo;

import java.util.Calendar;

public class DateModel {
    public Calendar getmDate() {
        return mDate;
    }

    public void setmDate(Calendar mDate) {
        this.mDate = mDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private Calendar mDate;
    private boolean isSelected;

}
