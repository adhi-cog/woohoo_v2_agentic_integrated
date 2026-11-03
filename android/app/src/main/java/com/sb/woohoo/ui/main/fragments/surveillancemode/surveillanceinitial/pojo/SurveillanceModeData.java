package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveillanceModeData {
    @SerializedName("playingStatus")
    @Expose
    private Boolean playingStatus;
    @SerializedName("volume")
    @Expose
    private Boolean volume;
    @SerializedName("surveillanceMode")
    @Expose
    private Boolean surveillanceMode;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("hubId")
    @Expose
    private String hubId;
    @SerializedName("userId")
    @Expose
    private String userId;

    public Boolean getPlayingStatus() {
        return playingStatus;
    }

    public void setPlayingStatus(Boolean playingStatus) {
        this.playingStatus = playingStatus;
    }

    public Boolean getVolume() {
        return volume;
    }

    public void setVolume(Boolean volume) {
        this.volume = volume;
    }

    public Boolean getSurveillanceMode() {
        return surveillanceMode;
    }

    public void setSurveillanceMode(Boolean surveillanceMode) {
        this.surveillanceMode = surveillanceMode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
