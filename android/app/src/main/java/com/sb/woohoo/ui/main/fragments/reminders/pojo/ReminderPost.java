package com.sb.woohoo.ui.main.fragments.reminders.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Satyaraj on 3/14/2018.
 */

public class ReminderPost implements Serializable {

    @SerializedName("status")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Reminder data;
    @SerializedName("error")
    @Expose
    private String error;

    public Reminder getData() {
        return data;
    }

    public int isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}