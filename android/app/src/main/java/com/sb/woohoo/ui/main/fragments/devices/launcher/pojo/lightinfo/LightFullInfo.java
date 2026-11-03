package com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LightFullInfo {

    @SerializedName("state")
    @Expose
    private LightInfoState state;
    @SerializedName("swupdate")
    @Expose
    private Swupdate swupdate;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("modelid")
    @Expose
    private String modelid;
    @SerializedName("manufacturername")
    @Expose
    private String manufacturername;
    @SerializedName("productname")
    @Expose
    private String productname;
    @SerializedName("capabilities")
    @Expose
    private Capabilities capabilities;
    @SerializedName("config")
    @Expose
    private Config config;
    @SerializedName("uniqueid")
    @Expose
    private String uniqueid;
    @SerializedName("swversion")
    @Expose
    private String swversion;

    public LightInfoState getState() {
        return state;
    }

    public void setState(LightInfoState state) {
        this.state = state;
    }

    public Swupdate getSwupdate() {
        return swupdate;
    }

    public void setSwupdate(Swupdate swupdate) {
        this.swupdate = swupdate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelid() {
        return modelid;
    }

    public void setModelid(String modelid) {
        this.modelid = modelid;
    }

    public String getManufacturername() {
        return manufacturername;
    }

    public void setManufacturername(String manufacturername) {
        this.manufacturername = manufacturername;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getSwversion() {
        return swversion;
    }

    public void setSwversion(String swversion) {
        this.swversion = swversion;
    }

}
