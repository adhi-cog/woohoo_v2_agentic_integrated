package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Capabilities {

    @SerializedName("certified")
    @Expose
    private Boolean certified;
    @SerializedName("control")
    @Expose
    private Control control;
    @SerializedName("streaming")
    @Expose
    private Streaming streaming;

    public Boolean getCertified() {
        return certified;
    }

    public void setCertified(Boolean certified) {
        this.certified = certified;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public Streaming getStreaming() {
        return streaming;
    }

    public void setStreaming(Streaming streaming) {
        this.streaming = streaming;
    }

}
