package com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HubStatus {
    @SerializedName("isBusy")
    @Expose
    private Boolean isBusy;
    @SerializedName("surveillanceMode")
    @Expose
    private Boolean surveillanceMode;
    @SerializedName("settingsMode")
    @Expose
    private Boolean settingsMode;
    @SerializedName("connected")
    @Expose
    private Boolean connected;
    @SerializedName("liveFeedAccess")
    @Expose
    private Boolean liveFeedAccess;

    public Boolean getLiveFeedAccess() {
        return liveFeedAccess;
    }

    public Boolean getSurveillanceMode() {
        return surveillanceMode;
    }

    public void setSurveillanceMode(Boolean surveillanceMode) {
        this.surveillanceMode = surveillanceMode;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Boolean getIsBusy() {
        return isBusy;
    }

    public void setIsBusy(Boolean isBusy) {
        this.isBusy = isBusy;
    }

    public Boolean getSettingsMode() {
        return settingsMode;
    }

    public void setSettingsMode(Boolean settingsMode) {
        this.settingsMode = settingsMode;
    }
}
