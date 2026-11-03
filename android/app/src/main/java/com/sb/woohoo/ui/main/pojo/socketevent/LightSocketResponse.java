package com.sb.woohoo.ui.main.pojo.socketevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;

public class LightSocketResponse {
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("lightData")
    @Expose
    private Light lightData;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("eventName")
    @Expose
    private String eventName;
    @SerializedName("socketId")
    @Expose
    private String socketId;
    @SerializedName("userId")
    @Expose
    private Integer userId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Light getLightData() {
        return lightData;
    }

    public void setLightData(Light lightData) {
        this.lightData = lightData;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

