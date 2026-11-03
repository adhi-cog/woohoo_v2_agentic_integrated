package com.sb.woohoo.ui.main.fragments.screensaver.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScreenSaverMulti {


    @Expose
    @SerializedName("slideShowImages")
    private List<String> slideShowImages;
    @Expose
    @SerializedName("slideShow")
    private boolean slideShow;
    @Expose
    @SerializedName("slideShowTime")
    private String slideShowTime;
    @Expose
    @SerializedName("screenSaverTime")
    private int screenSaverTime;
    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("imageUrl")
    private String imageUrl;
    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("hubId")
    private String hubId;
    @Expose
    @SerializedName("userId")
    private int userId;
    @Expose
    @SerializedName("domainId")
    private int domainId;
    @Expose
    @SerializedName("eventName")
    private String eventName;

    public List<String> getSlideShowImages() {
        return slideShowImages;
    }

    public void setSlideShowImages(List<String> slideShowImages) {
        this.slideShowImages = slideShowImages;
    }

    public boolean getSlideShow() {
        return slideShow;
    }

    public void setSlideShow(boolean slideShow) {
        this.slideShow = slideShow;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSlideShowTime() {
        return this.slideShowTime;
    }

    public void setSlideShowTime(String slideShowTime){
        this.slideShowTime = slideShowTime;
    }

    public int getScreenSaverTime() {
        return this.screenSaverTime;
    }

    public void setScreenSaverTime(int screenSaverTime){
        this.screenSaverTime = screenSaverTime;
    }

    public long getSlideShowTimeInMillis(){
        if(slideShowTime == null)
            slideShowTime = "1";
        return Long.parseLong(String.valueOf(Math.round(Float.parseFloat(slideShowTime) * 60 * 1000)));
    }
}
