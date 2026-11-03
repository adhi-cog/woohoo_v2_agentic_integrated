package com.sb.woohoo.ui.main.fragments.alarm.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostAlarm {
    @SerializedName("status")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Alarm data;
    @SerializedName("error")
    @Expose
    private String error;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Alarm getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
