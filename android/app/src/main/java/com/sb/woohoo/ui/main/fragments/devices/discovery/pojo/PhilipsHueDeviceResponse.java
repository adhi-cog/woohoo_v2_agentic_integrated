package com.sb.woohoo.ui.main.fragments.devices.discovery.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhilipsHueDeviceResponse {

    @SerializedName("error")
    @Expose
    private PhilipsHueDeviceError error;
    @SerializedName("success")
    @Expose
    private PhilipsHueDeviceSuccess success;

    public PhilipsHueDeviceError getError() {
        return error;
    }

    public void setError(PhilipsHueDeviceError error) {
        this.error = error;
    }

    public PhilipsHueDeviceSuccess getSuccess() {
        return success;
    }

    public void setSuccess(PhilipsHueDeviceSuccess success) {
        this.success = success;
    }

}