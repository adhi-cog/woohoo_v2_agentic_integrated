package com.sb.woohoo.ui.main.fragments.weather.pojo;

import com.google.gson.annotations.SerializedName;

public class WeatherRequest {
    @SerializedName("date")
    private String date;
    @SerializedName("lat")
    private String latitude;
    @SerializedName("long")
    private String longitude;
    @SerializedName("city")
    private String city;
    @SerializedName("weatherCode")
    private Integer weatherCode;
    @SerializedName("deviceId")
    private String deviceId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(Integer weatherCode) {
        if (weatherCode == 0) {
            this.weatherCode = null;
        } else {
            this.weatherCode = weatherCode;
        }

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
