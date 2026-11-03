package com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveillanceModeResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private SurveillanceModeResponse.SurveillanceResponse data;
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

    public SurveillanceModeResponse.SurveillanceResponse getData() {
        return data;
    }

    public void setData(SurveillanceModeResponse.SurveillanceResponse data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public class SurveillanceResponse {
        @SerializedName("data")
        @Expose
        private SurveillanceModeData data;

        public SurveillanceModeData getData() {
            return data;
        }
    }
}
