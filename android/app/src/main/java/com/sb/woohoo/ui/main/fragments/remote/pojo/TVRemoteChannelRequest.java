package com.sb.woohoo.ui.main.fragments.remote.pojo;

import com.google.gson.annotations.SerializedName;

public class TVRemoteChannelRequest {

    @SerializedName("actionId")
    public int getActionId() {
        return this.actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    int actionId;

    @SerializedName("actionData")
    public TVRemoteChannelActionData getActionData() {
        return this.actionData;
    }

    public void setActionData(TVRemoteChannelActionData actionData) {
        this.actionData = actionData;
    }

    TVRemoteChannelActionData actionData;
}
