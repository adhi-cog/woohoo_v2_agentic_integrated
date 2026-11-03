package com.sb.woohoo.ui.main.fragments.devices.discovery.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.io.Serializable;
import java.util.HashMap;

public class PhilipsHueDevice extends BaseModel<PhilipsHueDevice> implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("internalipaddress")
    @Expose
    private String internalIpAddress;
    @SerializedName("isregisterdevice")
    @Expose
    private boolean isRegisterDevice = false;

    public PhilipsHueDevice(String id, String internalIpAddress) {
        this.id = id;
        this.internalIpAddress = internalIpAddress;
    }

    public boolean isRegisterDevice() {
        return isRegisterDevice;
    }

    public void setRegisterDevice(boolean registerDevice) {
        isRegisterDevice = registerDevice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternalIpAddress() {
        return internalIpAddress;
    }

    public void setInternalIpAddress(String internalIpAddress) {
        this.internalIpAddress = internalIpAddress;
    }

    @Override
    public void parseModel(HashMap hashMap) {

    }
}
