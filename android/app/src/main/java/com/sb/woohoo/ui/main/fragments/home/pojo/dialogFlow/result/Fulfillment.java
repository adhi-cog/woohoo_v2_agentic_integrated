package com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result.fulfillment.Message;

public class Fulfillment {
    @SerializedName("speech")
    @Expose
    private String speech;
    @SerializedName("messages")
    @Expose
    private Message messages;

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public Message getMessages() {
        return messages;
    }

    public void setMessages(Message messages) {
        this.messages = messages;
    }
}