package com.sb.woohoo.ui.main.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravi on 26/09/17.
 */

public class RemoteSystemUpdateModel {

    @SerializedName("error")
    public String error;
    @SerializedName("data")
    public Data data;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public class Data {
        @SerializedName("__v")
        public int __v;
        @SerializedName("created")
        public String created;
        @SerializedName("description")
        public String description;
        @SerializedName("url")
        public String url;
        @SerializedName("versionName")
        public String versionName;
        @SerializedName("versionCode")
        public int versionCode;
        @SerializedName("name")
        public String name;
        @SerializedName("_id")
        public String _id;
        @SerializedName("domainId")
        public String domainId;
        @SerializedName("isCritical")
        public boolean isCritical;
    }
}
