package com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThingsToSayResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ThingsToSay> data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ThingsToSay> getData() {
        return data;
    }
}
