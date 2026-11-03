package com.sb.woohoo.ui.main.fragments.interel.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InterelUtilityData {
    @SerializedName("DevThermos")
    @Expose
    private List<DevThermo> devThermos = null;
    @SerializedName("Dnd")
    @Expose
    private Boolean dnd;
    @SerializedName("Mur")
    @Expose
    private Boolean mur;
    @SerializedName("Presence")
    @Expose
    private Boolean presence;

    public List<DevThermo> getDevThermos() {
        return devThermos;
    }

    public void setDevThermos(List<DevThermo> devThermos) {
        this.devThermos = devThermos;
    }

    public Boolean getDnd() {
        return dnd;
    }

    public void setDnd(Boolean dnd) {
        this.dnd = dnd;
    }

    public Boolean getMur() {
        return mur;
    }

    public void setMur(Boolean mur) {
        this.mur = mur;
    }

    public Boolean getPresence() {
        return presence;
    }

    public void setPresence(Boolean presence) {
        this.presence = presence;
    }
}
