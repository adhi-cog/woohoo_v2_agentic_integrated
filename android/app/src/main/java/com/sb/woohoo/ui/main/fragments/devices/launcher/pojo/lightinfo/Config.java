package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("archetype")
    @Expose
    private String archetype;
    @SerializedName("function")
    @Expose
    private String function;
    @SerializedName("direction")
    @Expose
    private String direction;

    public String getArchetype() {
        return archetype;
    }

    public void setArchetype(String archetype) {
        this.archetype = archetype;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

}
