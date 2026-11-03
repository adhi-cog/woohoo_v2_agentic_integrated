package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class State {

    @SerializedName("RemoteAlertModel")
    @Expose
    private String alert;
    @SerializedName("bri")
    @Expose
    private int bri;
    @SerializedName("colormode")
    @Expose
    private String colormode;
    @SerializedName("ct")
    @Expose
    private int ct;
    @SerializedName("effect")
    @Expose
    private String effect;
    @SerializedName("hue")
    @Expose
    private int hue;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("on")
    @Expose
    private boolean on;
    @SerializedName("reachable")
    @Expose
    private boolean reachable;
    @SerializedName("sat")
    @Expose
    private int sat;
    @SerializedName("xy")
    @Expose
    private List<Double> xy = null;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public int getBri() {
        return bri;
    }

    public void setBri(int bri) {
        this.bri = bri;
    }

    public String getColormode() {
        return colormode;
    }

    public void setColormode(String colormode) {
        this.colormode = colormode;
    }

    public int getCt() {
        return ct;
    }

    public void setCt(int ct) {
        this.ct = ct;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public List<Double> getXy() {
        return xy;
    }

    public void setXy(List<Double> xy) {
        this.xy = xy;
    }
}
