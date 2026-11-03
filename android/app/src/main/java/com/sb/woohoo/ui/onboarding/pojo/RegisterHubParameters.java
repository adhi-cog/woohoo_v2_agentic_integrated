package com.sb.woohoo.ui.onboarding.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * POJO Class for Parameter Model of API : Register Hub.
 *
 * @author Ritwik Jamuar.
 */
public class RegisterHubParameters {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("macAddress")
    private String macAddress;
    @SerializedName("ipAddress")
    private String ipAddress;
    @SerializedName("firmware_version")
    private long firmwareVersion;
    @SerializedName("app_version")
    private long appVersion;
    @SerializedName("registered_user")
    private String registeredUser;
    @SerializedName("lat")
    private String latitude;
    @SerializedName("long")
    private String longitude;
    @SerializedName("timeZone")
    private String timeZone;
    @SerializedName("locale")
    private String locale;
    @SerializedName("wifiName")
    private String wifiConnected;
    @SerializedName("serial_number")
    private String serialNumber;
    @SerializedName("aiEngine")
    private String aiEngine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(long firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public long getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(long appVersion) {
        this.appVersion = appVersion;
    }

    public String getRegisteredUser() {
        return registeredUser;
    }

    public void setRegisteredUser(String registeredUser) {
        this.registeredUser = registeredUser;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getWifiConnected() {
        return wifiConnected;
    }

    public void setWifiConnected(String wifiConnected) {
        this.wifiConnected = wifiConnected;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAiEngine() {
        return aiEngine;
    }

    public void setAiEngine(String aiEngine) {
        this.aiEngine = aiEngine;
    }
}