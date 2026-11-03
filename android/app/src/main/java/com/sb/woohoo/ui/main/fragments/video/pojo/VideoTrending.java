package com.sb.woohoo.ui.main.fragments.video.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoTrending {
    @SerializedName("items")
    @Expose
    private List<Video> items = null;

    public List<Video> getItems() {
        return items;
    }
}
