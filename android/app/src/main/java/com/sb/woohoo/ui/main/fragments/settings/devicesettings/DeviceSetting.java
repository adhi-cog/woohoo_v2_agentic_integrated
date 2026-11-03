package com.sb.woohoo.ui.main.fragments.settings.devicesettings;

public class DeviceSetting {
    private String deviceSettingName;
    private String deviceSettingContent;
    private int action;
    private boolean isLoading;

    public DeviceSetting(String deviceSettingName, String deviceSettingContent, int action) {
        this.deviceSettingName = deviceSettingName;
        this.deviceSettingContent = deviceSettingContent;
        this.action = action;
    }

    public DeviceSetting(String deviceSettingName, String deviceSettingContent, int action, boolean isLoading) {
        this.deviceSettingName = deviceSettingName;
        this.deviceSettingContent = deviceSettingContent;
        this.action = action;
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getDeviceSettingName() {
        return deviceSettingName;
    }

    public void setDeviceSettingName(String deviceSettingName) {
        this.deviceSettingName = deviceSettingName;
    }

    public String getDeviceSettingContent() {
        return deviceSettingContent;
    }

    public void setDeviceSettingContent(String deviceSettingContent) {
        this.deviceSettingContent = deviceSettingContent;
    }
}
