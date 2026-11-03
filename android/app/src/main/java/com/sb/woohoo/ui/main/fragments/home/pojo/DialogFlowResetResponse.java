package com.sb.woohoo.ui.main.fragments.home.pojo;

import com.google.gson.annotations.SerializedName;

public class DialogFlowResetResponse {
    @SerializedName("status")
    private boolean success;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}