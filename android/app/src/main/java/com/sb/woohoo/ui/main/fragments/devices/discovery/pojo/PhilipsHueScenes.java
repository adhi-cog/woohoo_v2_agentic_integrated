package com.sb.woohoo.ui.main.fragments.devices.discovery.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.io.Serializable;
import java.util.HashMap;

public class PhilipsHueScenes extends BaseModel<Light> implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lights")
    @Expose
    private String lights[];
    @SerializedName("recycle")
    @Expose
    private boolean recycle;
    @SerializedName("locked")
    @Expose
    private boolean locked;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("version")
    @Expose
    private int version;
    @SerializedName("lastupdated")
    @Expose
    private String lastupdated;
    @SerializedName("scene_id")
    @Expose
    private String sceneId;

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getLights() {
        return lights;
    }

    public void setLights(String[] lights) {
        this.lights = lights;
    }

    public boolean isRecycle() {
        return recycle;
    }

    public void setRecycle(boolean recycle) {
        this.recycle = recycle;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {

    }
}
