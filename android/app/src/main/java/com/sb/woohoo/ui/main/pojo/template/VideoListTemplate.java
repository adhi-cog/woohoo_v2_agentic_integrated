package com.sb.woohoo.ui.main.pojo.template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoListTemplate {
    @SerializedName("media")
    @Expose
    private List<VideoMedia> media = null;

    public List<VideoMedia> getMedia() {
        return media;
    }

    public void setMedia(List<VideoMedia> media) {
        this.media = media;
    }
}
