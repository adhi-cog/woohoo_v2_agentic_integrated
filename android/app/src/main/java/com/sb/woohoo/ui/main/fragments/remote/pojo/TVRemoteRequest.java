package com.sb.woohoo.ui.main.fragments.remote.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TVRemoteRequest implements Serializable {

    @SerializedName("actionId")
    private String actionId;

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

}
