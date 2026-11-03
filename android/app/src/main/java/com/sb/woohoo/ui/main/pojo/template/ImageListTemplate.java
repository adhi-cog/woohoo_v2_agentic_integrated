package com.sb.woohoo.ui.main.pojo.template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageListTemplate {

    @SerializedName("media")
    @Expose
    private List<ImageMedia> media = null;

    public List<ImageMedia> getMedia() {
        return media;
    }

    public void setMedia(List<ImageMedia> media) {
        this.media = media;
    }
}
