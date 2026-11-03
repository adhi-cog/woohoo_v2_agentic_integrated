package com.sb.woohoo.ui.main.fragments.settings.devicesettings.pojo;

import com.google.gson.annotations.SerializedName;

public class TimeZoneUpdate {


    @SerializedName("error")
    public String error;
    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public class Data {
        @SerializedName("status")
        public boolean status;
        @SerializedName("liveFeedAccess")
        public boolean liveFeedAccess;
        @SerializedName("long")
        public String lon;
        @SerializedName("lat")
        public String lat;
        @SerializedName("deletedAt")
        public String deletedAt;
        @SerializedName("userId")
        public int userId;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("lastLogin")
        public String lastLogin;
        @SerializedName("firmwareVersion")
        public int firmwareVersion;
        @SerializedName("appVersion")
        public int appVersion;
        @SerializedName("deviceSimDetails")
        public String deviceSimDetails;
        @SerializedName("timeZone")
        public String timeZone;
        @SerializedName("locale")
        public String locale;
        @SerializedName("wifiName")
        public String wifiName;
        @SerializedName("aiEngine")
        public String aiEngine;
        @SerializedName("ipAddress")
        public String ipAddress;
        @SerializedName("macAddress")
        public String macAddress;
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public String id;
    }
}
