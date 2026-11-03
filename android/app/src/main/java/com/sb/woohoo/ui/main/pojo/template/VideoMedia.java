package com.sb.woohoo.ui.main.pojo.template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoMedia {

    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;
    @SerializedName("videoDescription")
    @Expose
    private String videoDescription;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

}
