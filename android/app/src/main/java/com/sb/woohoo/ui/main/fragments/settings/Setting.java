package com.sb.woohoo.ui.main.fragments.settings;

import com.sb.woohoo.ui.base.FeatureFragment;

public class Setting {

    private int icon;
    private String settingsName;
    private CharSequence settingsContent;
    private boolean isUpdateAvailable;
    private boolean isLoading;
    private FeatureFragment featureFragment;
    private int type;
    private int action;
    private int fragmentType;
    private String versionCode;
    private String versionName;

    public Setting(int icon, String settingsName, String settingsContent, FeatureFragment featureFragment, int type, int fragmentType) {
        this.icon = icon;
        this.settingsName = settingsName;
        this.settingsContent = settingsContent;
        this.featureFragment = featureFragment;
        this.type = type;
        this.fragmentType = fragmentType;
    }

    public Setting(int icon, String settingsName, String settingsContent, FeatureFragment featureFragment, int type, int fragmentType, boolean isLoading) {
        this.icon = icon;
        this.settingsName = settingsName;
        this.settingsContent = settingsContent;
        this.featureFragment = featureFragment;
        this.type = type;
        this.fragmentType = fragmentType;
        this.isLoading = isLoading;
    }

    public Setting(int icon, String settingsName, String settingsContent, int type, int fragmentType) {
        this.icon = icon;
        this.settingsName = settingsName;
        this.settingsContent = settingsContent;
        this.type = type;
        this.fragmentType = fragmentType;
    }

    public Setting(int icon, String settingsName, String settingsContent, int type, int action, int fragmentType) {
        this.icon = icon;
        this.settingsName = settingsName;
        this.settingsContent = settingsContent;
        this.type = type;
        this.action = action;
        this.fragmentType = fragmentType;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public int getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FeatureFragment getFeatureFragment() {
        return featureFragment;
    }

    public void setFeatureFragment(FeatureFragment featureFragment) {
        this.featureFragment = featureFragment;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getSettingsName() {
        return settingsName;
    }

    public void setSettingsName(String settingsName) {
        this.settingsName = settingsName;
    }

    public CharSequence getSettingsContent() {
        return settingsContent;
    }

    public void setSettingsContent(CharSequence settingsContent) {
        this.settingsContent = settingsContent;
    }
}
