package com.sb.woohoo.ui.main.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StartSession implements Serializable {


    @Expose
    @SerializedName("error")
    private String error;
    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Data implements Serializable {
        @Expose
        @SerializedName("__v")
        private int __v;
        @Expose
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("endedAt")
        private String endedAt;
        @Expose
        @SerializedName("startedAt")
        private String startedAt;
        @Expose
        @SerializedName("domainId")
        private String domainId;
        @Expose
        @SerializedName("deviceId")
        private String deviceId;
        @Expose
        @SerializedName("appStoreName")
        private String appStoreName;
        @Expose
        @SerializedName("appStoreId")
        private String appStoreId;
        @Expose
        @SerializedName("appUrl")
        private String appUrl;
        @Expose
        @SerializedName("appKey")
        private String appKey;
        @Expose
        @SerializedName("appSecret")
        private String appSecret;
        @Expose
        @SerializedName("sessionId")
        private String sessionId;

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getEndedAt() {
            return endedAt;
        }

        public void setEndedAt(String endedAt) {
            this.endedAt = endedAt;
        }

        public String getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(String startedAt) {
            this.startedAt = startedAt;
        }


        public String getDomainId() {
            return domainId;
        }

        public void setDomainId(String domainId) {
            this.domainId = domainId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getAppStoreName() {
            return appStoreName;
        }

        public void setAppStoreName(String appStoreName) {
            this.appStoreName = appStoreName;
        }

        public String getAppStoreId() {
            return appStoreId;
        }

        public void setAppStoreId(String appStoreId) {
            this.appStoreId = appStoreId;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }
}
