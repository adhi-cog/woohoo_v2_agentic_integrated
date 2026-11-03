package com.sb.woohoo.ui.main.pojo.socketevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CameraRotationData {

    @SerializedName("direction")
    @Expose
    private Integer direction;
    @SerializedName("rotation_status")
    @Expose
    private Integer rotationStatus;

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getRotationStatus() {
        return rotationStatus;
    }

    public void setRotationStatus(Integer rotationStatus) {
        this.rotationStatus = rotationStatus;
    }
}
