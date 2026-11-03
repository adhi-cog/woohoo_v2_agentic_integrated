package com.sb.woohoo.ui.main.fragments.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.pojo.DiningRestaurant;

import java.util.List;

public class TVRemoteResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<TVRemoteData> data = null;
    @SerializedName("error")
    @Expose
    private String error;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TVRemoteData> getData() {
        return data;
    }

    public void setData(List<TVRemoteData> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
