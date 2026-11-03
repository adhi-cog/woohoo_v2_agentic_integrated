package com.sb.woohoo.ui.main.pojo.template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoTemplate {

    @SerializedName("media")
    @Expose
    private VideoMedia media;

    public VideoMedia getMedia() {
        return media;
    }

    public void setMedia(VideoMedia media) {
        this.media = media;
    }

}
