package com.sb.woohoo.ui.main.pojo.socketevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.sb.woohoo.utils.Constants.LIVE_FEED_VIDEO_TYPE;

public class LiveFeedSocketData {

    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("contactLiveFeed")
    @Expose
    private String contactLiveFeed;
    @SerializedName("contactUserId")
    @Expose
    private String contactUserId;
    @SerializedName("senderClientId")
    @Expose
    private String senderClientId;
    @SerializedName("receiverHubId")
    @Expose
    private String receiverHubId;
    @SerializedName("eventName")
    @Expose
    private String eventName;
    @SerializedName("feedType")
    @Expose
    private String feedType;
    @SerializedName("senderHubId")
    @Expose
    private String senderHubId;

    public String getFeedType() {
        return feedType == null ? LIVE_FEED_VIDEO_TYPE : feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getSenderHubId() {
        return senderHubId;
    }

    public void setSenderHubId(String senderHubId) {
        this.senderHubId = senderHubId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContactLiveFeed() {
        return contactLiveFeed;
    }

    public void setContactLiveFeed(String contactLiveFeed) {
        this.contactLiveFeed = contactLiveFeed;
    }

    public String getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(String contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getSenderClientId() {
        return senderClientId;
    }

    public void setSenderClientId(String senderClientId) {
        this.senderClientId = senderClientId;
    }

    public String getReceiverHubId() {
        return receiverHubId;
    }

    public void setReceiverHubId(String receiverHubId) {
        this.receiverHubId = receiverHubId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
