package com.sb.woohoo.ui.main.fragments.home.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HotelDetailsData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("logoUrl")
    @Expose
    private String logoUrl;

    @SerializedName("backgroundImageUrl")
    @Expose
    private String backgroundImageUrl;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;

    @SerializedName("screenColor")
    @Expose
    private String screenColor;

    public String getNightModeStartTime() {
        return nightModeStartTime;
    }

    public void setNightModeStartTime(String nightModeStartTime) {
        this.nightModeStartTime = nightModeStartTime;
    }

    public String getNightModeEndTime() {
        return nightModeEndTime;
    }

    public void setNightModeEndTime(String nightModeEndTime) {
        this.nightModeEndTime = nightModeEndTime;
    }

    @SerializedName("nightModeStartTime")
    @Expose
    private String nightModeStartTime;

    @SerializedName("nightModeEndTime")
    @Expose
    private String nightModeEndTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getScreenColor() {
        return screenColor;
    }

    public void setScreenColor(String screenColor) {
        this.screenColor = screenColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
