package com.sb.woohoo.ui.main.fragments.weather.pojo.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherItem {

    @SerializedName("date")
    @Expose
    private String date; //TODO This date is yyyy/MM/dd
    @SerializedName("humidity")
    @Expose
    private Integer humidity;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("temperature")
    @Expose
    private Double temperature;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("windSpeed")
    @Expose
    private Double windSpeed;
    @SerializedName("windDegree")
    @Expose
    private Double windDegree;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("backgroundImage")
    @Expose
    private String backgroundImage;
    @SerializedName("tempMax")
    @Expose
    private Double tempMax;
    @SerializedName("tempMin")
    @Expose
    private Double tempMin;
    @SerializedName("weatherId")
    @Expose
    private Integer weatherId;
    @SerializedName("pressure")
    @Expose
    private Double pressure;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(Double windDegree) {
        this.windDegree = windDegree;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Double getTempMax() {
        return tempMax;
    }

    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

    public Double getTempMin() {
        return tempMin;
    }

    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    public Integer getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(Integer weatherId) {
        this.weatherId = weatherId;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }
}
