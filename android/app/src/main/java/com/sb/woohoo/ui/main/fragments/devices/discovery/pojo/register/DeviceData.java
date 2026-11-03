package com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceData {

    @SerializedName("ok")
    @Expose
    private Integer ok;
    @SerializedName("nModified")
    @Expose
    private Integer nModified;
    @SerializedName("n")
    @Expose
    private Integer n;

    public Integer getOk() {
        return ok;
    }

    public void setOk(Integer ok) {
        this.ok = ok;
    }

    public Integer getNModified() {
        return nModified;
    }

    public void setNModified(Integer nModified) {
        this.nModified = nModified;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

}
