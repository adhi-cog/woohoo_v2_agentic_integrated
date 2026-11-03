package com.sb.woohoo.ui.main.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostQuery {

    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("deviceId")
    @Expose
    private String deviceId;

    @SerializedName("lang")
    @Expose
    private String lang;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String language) {
        this.lang = language;
    }
}
