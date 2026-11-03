package com.sb.woohoo.ui.main.fragments.reservation.booking.pojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReservationDateSlot {
    
    private boolean isSelected;

    private Long mDateTimeInMillis  = 0L;

    private String dateMonth;

    public String getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }

    public Long getDate() {
        return mDateTimeInMillis;
    }

    public void setDate(Long mDate) {
        this.mDateTimeInMillis = mDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ReservationDateSlot(Long _date, boolean _isSelected) {
        this.mDateTimeInMillis = _date;
        this.isSelected = _isSelected;
    }

    public static ArrayList<ReservationDateSlot> getDateArray() {
        ArrayList<ReservationDateSlot> dateList = new ArrayList<ReservationDateSlot>();

        String day;
        int date;
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        day = dayFormat.format(calendar.getTime());
        date = Integer.parseInt(dateFormat.format(calendar.getTime()));

        for (int i = 1; i<=7; i++) {
            dateList.add(new ReservationDateSlot(calendar.getTimeInMillis(), false));
            calendar.add(Calendar.DATE, 1);
        }

        return dateList;
    }
}
