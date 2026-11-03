package com.sb.woohoo.ui.main.fragments.remote.pojo;

import com.google.gson.annotations.SerializedName;

public class TVRemoteActionResponse {
    @SerializedName("status")
    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    int status;

    @SerializedName("message")
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String message;

    @SerializedName("data")
    public TVRemoteActionData getData() {
        return this.data;
    }

    public void setData(TVRemoteActionData data) {
        this.data = data;
    }

    TVRemoteActionData data;

    @SerializedName("error")
    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    String error;
}

