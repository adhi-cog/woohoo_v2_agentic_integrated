package com.sb.woohoo.ui.main.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravi on 26/09/17.
 */

public class RemoteAlertModel {

    @SerializedName("error")
    public String error;
    @SerializedName("data")
    public DataEntity data;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public class DataEntity {
        @SerializedName("__v")
        public int __v;
        @SerializedName("mediaType")
        public String mediaType;
        @SerializedName("mediaUrl")
        public String mediaUrl;
        @SerializedName("voiceOutput")
        public String voiceOutput;
        @SerializedName("domainId")
        public String domainId;
        @SerializedName("title")
        public String title;
        @SerializedName("_id")
        public String _id;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("displayText")
        public String displayText;
        @SerializedName("displayType")
        public String displayType;
        @SerializedName("alertCategory")
        public String alertCategory;
        @SerializedName("timerOnScreen")
        public String timerOnScreen;
        @SerializedName("timerValue")
        public String timerValue;
        @SerializedName("price")
        public String price;
        @SerializedName("itemName")
        public String itemName;
        @SerializedName("voiceType")
        public String voiceType;
        @SerializedName("alertId")
        public String alertId;
    }
}
