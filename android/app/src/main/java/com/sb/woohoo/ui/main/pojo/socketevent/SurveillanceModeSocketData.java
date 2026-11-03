package com.sb.woohoo.ui.main.pojo.socketevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveillanceModeSocketData {

    @SerializedName("hubId")
    @Expose
    private String hubId;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("surveillanceMode")
    @Expose
    private Boolean surveillanceMode;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("connected")
    @Expose
    private Boolean connected;
    @SerializedName("eventName")
    @Expose
    private String eventName;

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getSurveillanceMode() {
        return surveillanceMode;
    }

    public void setSurveillanceMode(Boolean surveillanceMode) {
        this.surveillanceMode = surveillanceMode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
