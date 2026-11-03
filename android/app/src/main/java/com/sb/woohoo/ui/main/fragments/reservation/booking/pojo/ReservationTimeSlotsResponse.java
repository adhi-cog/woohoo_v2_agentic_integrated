package com.sb.woohoo.ui.main.fragments.reservation.booking.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReservationTimeSlotsResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("error")
    @Expose
    private String error;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public class Data {
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        String date;

        public ArrayList<TimeSlot> getSlots() {
            return slots;
        }

        public void setSlots(ArrayList<TimeSlot> slots) {
            this.slots = slots;
        }

        ArrayList<TimeSlot> slots;
    }

}
