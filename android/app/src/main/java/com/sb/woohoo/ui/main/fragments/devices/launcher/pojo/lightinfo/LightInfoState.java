package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LightInfoState {

    @SerializedName("on")
    @Expose
    private Boolean on;
    @SerializedName("bri")
    @Expose
    private Integer bri;
    @SerializedName("ct")
    @Expose
    private Integer ct;
    @SerializedName("RemoteAlertModel")
    @Expose
    private String alert;
    @SerializedName("colormode")
    @Expose
    private String colormode;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("reachable")
    @Expose
    private Boolean reachable;

    public Boolean getOn() {
        return on;
    }

    public void setOn(Boolean on) {
        this.on = on;
    }

    public Integer getBri() {
        return bri;
    }

    public void setBri(Integer bri) {
        this.bri = bri;
    }

    public Integer getCt() {
        return ct;
    }

    public void setCt(Integer ct) {
        this.ct = ct;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getColormode() {
        return colormode;
    }

    public void setColormode(String colormode) {
        this.colormode = colormode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Boolean getReachable() {
        return reachable;
    }

    public void setReachable(Boolean reachable) {
        this.reachable = reachable;
    }

}
