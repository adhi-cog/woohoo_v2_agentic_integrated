package com.sb.woohoo.ui.main.fragments.devices.discovery.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;

import java.util.HashMap;

public class PhilipsHueLightContainer {

    @SerializedName("lights")
    @Expose
    private HashMap<String, Light> lights;
    @SerializedName("scenes")
    @Expose
    private HashMap<String, PhilipsHueScenes> scenes;
    @SerializedName("config")
    @Expose
    private PhilipsHueConfig philipsHueConfig;

    public PhilipsHueConfig getPhilipsHueConfig() {
        return philipsHueConfig;
    }

    public void setPhilipsHueConfig(PhilipsHueConfig philipsHueConfig) {
        this.philipsHueConfig = philipsHueConfig;
    }

    public HashMap<String, Light> getLights() {
        return lights;
    }

    public void setLights(HashMap<String, Light> lights) {
        this.lights = lights;
    }

    public HashMap<String, PhilipsHueScenes> getScenes() {
        return scenes;
    }

    public void setScenes(HashMap<String, PhilipsHueScenes> scenes) {
        this.scenes = scenes;
    }

}
