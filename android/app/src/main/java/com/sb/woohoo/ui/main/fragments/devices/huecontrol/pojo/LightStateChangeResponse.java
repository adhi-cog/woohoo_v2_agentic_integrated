package com.sb.woohoo.ui.main.fragments.devices.huecontrol.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class LightStateChangeResponse {
    @SerializedName("error")
    @Expose
    private LightStateChangeError error;
    @SerializedName("success")
    @Expose
    private HashMap<String, String> successMessage;

    public HashMap<String, String> getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(HashMap<String, String> successMessage) {
        this.successMessage = successMessage;
    }

    public LightStateChangeError getError() {
        return error;
    }

    public void setError(LightStateChangeError error) {
        this.error = error;
    }

}
