package com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.data;

import com.google.gson.annotations.SerializedName;

public class Status {
    @SerializedName("code")
    private int code;
    @SerializedName("errorType")
    private String errorType;
    @SerializedName("webhookTimedOut")
    private boolean webHookTimedOut;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public boolean isWebHookTimedOut() {
        return webHookTimedOut;
    }

    public void setWebHookTimedOut(boolean webHookTimedOut) {
        this.webHookTimedOut = webHookTimedOut;
    }
}