package com.sb.woohoo.ui.main.pojo.socketevent;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveFeedParticipantModel {

    @SerializedName("data")
    @Expose
    private LiveFeedSocketData data;

    public LiveFeedSocketData getData() {
        return data;
    }

    public void setData(LiveFeedSocketData data) {
        this.data = data;
    }

}