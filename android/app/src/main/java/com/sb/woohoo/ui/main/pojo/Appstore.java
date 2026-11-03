package com.sb.woohoo.ui.main.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Appstore {


    @Expose
    @SerializedName("error")
    private String error;
    @Expose
    @SerializedName("data")
    private List<Data> data;
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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
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

    public  class Data {
        @Expose
        @SerializedName("domainId")
        private int domainId;
        @Expose
        @SerializedName("updatedAt")
        private String updatedAt;
        @Expose
        @SerializedName("createdAt")
        private String createdAt;
        @Expose
        @SerializedName("appSecret")
        private String appSecret;
        @Expose
        @SerializedName("appKey")
        private String appKey;
        @Expose
        @SerializedName("serviceId")
        private String serviceId;
        @Expose
        @SerializedName("intentName")
        private String intentName;
        @Expose
        @SerializedName("published")
        private boolean published;
        @Expose
        @SerializedName("publishedOn")
        private String publishedOn;
        @Expose
        @SerializedName("approvedBy")
        private String approvedBy;
        @Expose
        @SerializedName("approvedOn")
        private String approvedOn;
        @Expose
        @SerializedName("approved")
        private boolean approved;
        @Expose
        @SerializedName("version")
        private int version;
        @Expose
        @SerializedName("type")
        private String type;
        @Expose
        @SerializedName("appUrl")
        private String appUrl;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("id")
        private int id;

        public int getDomainId() {
            return domainId;
        }

        public void setDomainId(int domainId) {
            this.domainId = domainId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getIntentName() {
            return intentName;
        }

        public void setIntentName(String intentName) {
            this.intentName = intentName;
        }

        public boolean getPublished() {
            return published;
        }

        public void setPublished(boolean published) {
            this.published = published;
        }

        public String getPublishedOn() {
            return publishedOn;
        }

        public void setPublishedOn(String publishedOn) {
            this.publishedOn = publishedOn;
        }

        public String getApprovedBy() {
            return approvedBy;
        }

        public void setApprovedBy(String approvedBy) {
            this.approvedBy = approvedBy;
        }

        public String getApprovedOn() {
            return approvedOn;
        }

        public void setApprovedOn(String approvedOn) {
            this.approvedOn = approvedOn;
        }

        public boolean getApproved() {
            return approved;
        }

        public void setApproved(boolean approved) {
            this.approved = approved;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
