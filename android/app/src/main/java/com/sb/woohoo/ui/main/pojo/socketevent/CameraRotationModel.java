package com.sb.woohoo.ui.main.pojo.socketevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CameraRotationModel {

    @SerializedName("receiverHubId")
    @Expose
    private String receiverHubId;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("data")
    @Expose
    private CameraRotationData data;
    @SerializedName("eventName")
    @Expose
    private String eventName;
    @SerializedName("socketId")
    @Expose
    private String socketId;
    @SerializedName("userId")
    @Expose
    private Integer userId;

    public String getReceiverHubId() {
        return receiverHubId;
    }

    public void setReceiverHubId(String receiverHubId) {
        this.receiverHubId = receiverHubId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CameraRotationData getData() {
        return data;
    }

    public void setData(CameraRotationData data) {
        this.data = data;
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
