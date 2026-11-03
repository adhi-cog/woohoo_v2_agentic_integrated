package com.sb.woohoo.ui.main.fragments.surveillancemode.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HubMode {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private HubModeData hubModeData;
    @SerializedName("error")
    @Expose
    private String error;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HubModeData getData() {
        return hubModeData;
    }

    public void setData(HubModeData data) {
        this.hubModeData = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    class HubModeData {

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
        @SerializedName("__v")
        @Expose
        private Integer v;

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

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

    }
}
