package com.sb.woohoo.ui.main.fragments.alarm.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteAlarm {
    @SerializedName("status")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data = null;
    @SerializedName("error")
    private String error;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public class Data {
        @SerializedName("ok")
        @Expose
        private int ok;
        @SerializedName("n")
        @Expose
        private String count;
    }
}
