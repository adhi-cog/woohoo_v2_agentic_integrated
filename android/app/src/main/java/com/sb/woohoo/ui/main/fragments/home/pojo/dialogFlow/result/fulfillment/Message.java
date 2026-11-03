package com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result.fulfillment;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("type")
    private long type;
    @SerializedName("speech")
    private String speech;

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }
}