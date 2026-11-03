package com.sb.woohoo.ui.main.fragments.devices.huecontrol.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LightStateChangeSuccess {

    @SerializedName("success")
    @Expose
    private String successMessage;

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
