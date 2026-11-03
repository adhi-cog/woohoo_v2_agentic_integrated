package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.State;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.util.HashMap;

public class Light extends BaseModel {

    @SerializedName("uniqueid")
    @Expose
    private String uniqueid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("lightId")
    @Expose
    private String lightId;
    @SerializedName("state")
    @Expose
    private State state;
    @SerializedName("hubId")
    @Expose
    private String hubId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("isFavorite")
    @Expose
    private Boolean isFavorite;
    @SerializedName("bridgeId")
    @Expose
    private String bridgeId;
    @SerializedName("bridgeIp")
    @Expose
    private String bridgeIp;
    @SerializedName("bridgeUserId")
    @Expose
    private String bridgeUserId;

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
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

    public String getLightId() {
        return lightId;
    }

    public void setLightId(String lightId) {
        this.lightId = lightId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    public String getBridgeIp() {
        return bridgeIp;
    }

    public void setBridgeIp(String bridgeIp) {
        this.bridgeIp = bridgeIp;
    }

    public String getBridgeUserId() {
        return bridgeUserId;
    }

    public void setBridgeUserId(String bridgeUserId) {
        this.bridgeUserId = bridgeUserId;
    }

    @Override
    public void parseModel(HashMap hashMap) {

    }
}
