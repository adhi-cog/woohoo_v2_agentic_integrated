package com.sb.woohoo.ui.main.pojo.template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageTemplate {

    @SerializedName("media")
    @Expose
    private ImageMedia media;

    public ImageMedia getMedia() {
        return media;
    }

    public void setMedia(ImageMedia media) {
        this.media = media;
    }
}
