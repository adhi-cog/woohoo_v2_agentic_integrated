package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Control {

    @SerializedName("mindimlevel")
    @Expose
    private Integer mindimlevel;
    @SerializedName("maxlumen")
    @Expose
    private Integer maxlumen;
    @SerializedName("ct")
    @Expose
    private Ct ct;

    public Integer getMindimlevel() {
        return mindimlevel;
    }

    public void setMindimlevel(Integer mindimlevel) {
        this.mindimlevel = mindimlevel;
    }

    public Integer getMaxlumen() {
        return maxlumen;
    }

    public void setMaxlumen(Integer maxlumen) {
        this.maxlumen = maxlumen;
    }

    public Ct getCt() {
        return ct;
    }

    public void setCt(Ct ct) {
        this.ct = ct;
    }

}
