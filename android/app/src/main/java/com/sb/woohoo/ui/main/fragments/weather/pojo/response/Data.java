package com.sb.woohoo.ui.main.fragments.weather.pojo.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("voiceResponse")
    @Expose
    private String voiceResponse;
    @SerializedName("currentWeather")
    @Expose
    private WeatherItem currentWeather;
    @SerializedName("forecast")
    @Expose
    private List<WeatherItem> forecast = null;

    public String getVoiceResponse() {
        return voiceResponse;
    }

    public void setVoiceResponse(String voiceResponse) {
        this.voiceResponse = voiceResponse;
    }

    public WeatherItem getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(WeatherItem currentWeather) {
        this.currentWeather = currentWeather;
    }

    public List<WeatherItem> getForecast() {
        return forecast;
    }

    public void setForecast(List<WeatherItem> forecast) {
        this.forecast = forecast;
    }

}
