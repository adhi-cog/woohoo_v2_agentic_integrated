package com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RelaxationMusicCategoriesResponse {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<RelaxationMusicCategoryData> data = new ArrayList<>();
    @SerializedName("error")
    @Expose
    private String error;

    public static RelaxationMusicCategoriesResponse empty() {
        RelaxationMusicCategoriesResponse response = new RelaxationMusicCategoriesResponse();
        response.status = 200;
        response.data = new ArrayList<>();
        return response;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<RelaxationMusicCategoryData> getData() {
        return data == null ? new ArrayList<>() : data;
    }

    public String getError() {
        return error;
    }
}
