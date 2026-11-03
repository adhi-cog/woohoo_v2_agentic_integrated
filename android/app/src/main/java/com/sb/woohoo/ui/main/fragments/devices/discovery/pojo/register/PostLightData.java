package com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;

public class PostLightData {

    @SerializedName("uniqueid")
    @Expose
    private String uniqueid;
    @SerializedName("hubId")
    @Expose
    private String hubId;
    @SerializedName("lightId")
    @Expose
    private String lightId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("state")
    @Expose
    private State state;
    @SerializedName("bridgeId")
    @Expose
    private String bridgeId;
    @SerializedName("bridgeUserId")
    @Expose
    private String bridgeUserId;
    @SerializedName("bridgeIp")
    @Expose
    private String bridgeIp;

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getLightId() {
        return lightId;
    }

    public void setLightId(String lightId) {
        this.lightId = lightId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    public String getBridgeUserId() {
        return bridgeUserId;
    }

    public void setBridgeUserId(String bridgeUserId) {
        this.bridgeUserId = bridgeUserId;
    }

    public String getBridgeIp() {
        return bridgeIp;
    }

    public void setBridgeIp(String bridgeIp) {
        this.bridgeIp = bridgeIp;
    }

}
