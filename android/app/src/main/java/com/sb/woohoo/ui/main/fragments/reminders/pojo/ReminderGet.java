package com.sb.woohoo.ui.main.fragments.reminders.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shrey on 17/03/18.
 */

public class ReminderGet implements Serializable {

    @SerializedName("status")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Reminder> data = null;
    @SerializedName("error")
    @Expose
    private String error;

    public int isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Reminder> getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
