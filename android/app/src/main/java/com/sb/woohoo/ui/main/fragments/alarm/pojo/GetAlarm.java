package com.sb.woohoo.ui.main.fragments.alarm.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAlarm {
    @SerializedName("status")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Alarm> data = null;
    @SerializedName("error")
    private String error;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Alarm> getData() {
        return data;
    }

    public String getError() {
        return error;
    }

}
