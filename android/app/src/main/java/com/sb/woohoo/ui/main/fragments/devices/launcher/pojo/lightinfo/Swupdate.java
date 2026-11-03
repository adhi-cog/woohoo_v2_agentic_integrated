package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Swupdate {

    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("lastinstall")
    @Expose
    private Object lastinstall;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getLastinstall() {
        return lastinstall;
    }

    public void setLastinstall(Object lastinstall) {
        this.lastinstall = lastinstall;
    }

}
