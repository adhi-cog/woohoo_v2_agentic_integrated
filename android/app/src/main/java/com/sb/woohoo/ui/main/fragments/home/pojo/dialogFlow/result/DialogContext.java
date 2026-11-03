package com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result;

import com.google.gson.annotations.SerializedName;

public class DialogContext {
    @SerializedName("name")
    private String name;
    @SerializedName("parameters")
    private Parameter parameters;
    @SerializedName("lifespan")
    private long lifespan;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parameter getParameters() {
        return parameters;
    }

    public void setParameters(Parameter parameters) {
        this.parameters = parameters;
    }

    public long getLifespan() {
        return lifespan;
    }

    public void setLifespan(long lifespan) {
        this.lifespan = lifespan;
    }
}