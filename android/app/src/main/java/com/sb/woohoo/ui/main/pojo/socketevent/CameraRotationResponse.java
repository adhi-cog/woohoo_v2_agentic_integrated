package com.sb.woohoo.ui.main.pojo.socketevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CameraRotationResponse {
    @SerializedName("data")
    @Expose
    private CameraRotationModel data;

    public CameraRotationModel getData() {
        return data;
    }

    public void setData(CameraRotationModel data) {
        this.data = data;
    }
}
