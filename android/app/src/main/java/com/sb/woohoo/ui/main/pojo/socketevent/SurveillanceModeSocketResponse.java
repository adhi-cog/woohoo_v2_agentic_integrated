package com.sb.woohoo.ui.main.pojo.socketevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveillanceModeSocketResponse {

    @SerializedName("data")
    @Expose
    private SurveillanceModeSocketData data;

    public SurveillanceModeSocketData getData() {
        return data;
    }

    public void setData(SurveillanceModeSocketData data) {
        this.data = data;
    }
}
