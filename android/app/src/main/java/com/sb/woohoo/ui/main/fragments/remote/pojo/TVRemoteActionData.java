package com.sb.woohoo.ui.main.fragments.remote.pojo;

import com.google.gson.annotations.SerializedName;

public class TVRemoteActionData {
    @SerializedName("status")
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;
}
