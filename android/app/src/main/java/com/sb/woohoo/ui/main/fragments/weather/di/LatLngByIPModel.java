package com.sb.woohoo.ui.main.fragments.weather.di;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2/19/18.
 */

public class LatLngByIPModel {

    @SerializedName("data")
    public Data data;
    @SerializedName("error")
    private String error;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public class Data {
        @SerializedName("timezone")
        public String timezone;
        @SerializedName("longitude")
        public double lon;
        @SerializedName("latitude")
        public double lat;
        @SerializedName("country_code")
        public String countryCode;
        @SerializedName("zip")
        private String zip;
        @SerializedName("status")
        private String status;
        @SerializedName("region")
        private String region;
        @SerializedName("query")
        private String query;
        @SerializedName("org")
        private String org;
        @SerializedName("isp")
        private String isp;
        @SerializedName("country_name")
        private String country;

        @SerializedName("city")
        private String city;

        @SerializedName("as")
        private String as;
    }
}