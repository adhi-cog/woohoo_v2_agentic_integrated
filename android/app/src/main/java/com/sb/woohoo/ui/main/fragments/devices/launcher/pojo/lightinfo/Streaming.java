package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Streaming {

    @SerializedName("renderer")
    @Expose
    private Boolean renderer;
    @SerializedName("proxy")
    @Expose
    private Boolean proxy;

    public Boolean getRenderer() {
        return renderer;
    }

    public void setRenderer(Boolean renderer) {
        this.renderer = renderer;
    }

    public Boolean getProxy() {
        return proxy;
    }

    public void setProxy(Boolean proxy) {
        this.proxy = proxy;
    }

}
