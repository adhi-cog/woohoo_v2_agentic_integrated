package com.sb.woohoo.ui.main.fragments.video.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideoResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Video> data = null;
    @SerializedName("error")
    @Expose
    private String error;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Video> getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
