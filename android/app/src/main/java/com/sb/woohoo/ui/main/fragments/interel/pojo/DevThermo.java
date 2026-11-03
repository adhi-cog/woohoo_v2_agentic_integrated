package com.sb.woohoo.ui.main.fragments.interel.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DevThermo {

    @SerializedName("ActiveSetPoint")
    @Expose
    private Double activeSetPoint;
    @SerializedName("ActualTemp")
    @Expose
    private Double actualTemp;
    @SerializedName("FanSpeed")
    @Expose
    private String fanSpeed;
    @SerializedName("SetPoint")
    @Expose
    private Float setPoint;
    @SerializedName("ThermostatId")
    @Expose
    private String thermostatId;
    @SerializedName("WindowState")
    @Expose
    private String windowState;
    @SerializedName("ValveStatus")
    @Expose
    private String valveStatus;

    public Double getActiveSetPoint() {
        return activeSetPoint;
    }

    public void setActiveSetPoint(Double activeSetPoint) {
        this.activeSetPoint = activeSetPoint;
    }

    public Double getActualTemp() {
        return actualTemp;
    }

    public void setActualTemp(Double actualTemp) {
        this.actualTemp = actualTemp;
    }

    public String getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(String fanSpeed) {
        this.fanSpeed = fanSpeed;
    }

    public Float getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(Float setPoint) {
        this.setPoint = setPoint;
    }

    public String getThermostatId() {
        return thermostatId;
    }

    public void setThermostatId(String thermostatId) {
        this.thermostatId = thermostatId;
    }

    public String getWindowState() {
        return windowState;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public String getValveStatus() {
        return valveStatus;
    }

    public void setValveStatus(String valveStatus) {
        this.valveStatus = valveStatus;
    }
}
