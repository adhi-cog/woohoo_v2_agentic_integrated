package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateLight {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("updatedLightData")
    @Expose
    private UpdatedLightData updatedLightData;
    @SerializedName("error")
    @Expose
    private String error;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpdatedLightData getUpdatedLightData() {
        return updatedLightData;
    }

    public void setUpdatedLightData(UpdatedLightData updatedLightData) {
        this.updatedLightData = updatedLightData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
