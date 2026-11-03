package com.sb.woohoo.ui.main.fragments.reservation.booking.pojo;

import com.google.gson.annotations.SerializedName;

public class ReservationRequest {

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;
    @SerializedName("serviceId")
    private String serviceId;
    @SerializedName("count")
    private int count;


}
