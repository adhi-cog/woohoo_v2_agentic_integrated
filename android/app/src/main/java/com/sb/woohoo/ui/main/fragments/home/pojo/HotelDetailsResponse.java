package com.sb.woohoo.ui.main.fragments.home.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class HotelDetailsResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    private HotelDetailsData data;
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

    public HotelDetailsData getData() {
        return data;
    }

    public void setData(HotelDetailsData data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}