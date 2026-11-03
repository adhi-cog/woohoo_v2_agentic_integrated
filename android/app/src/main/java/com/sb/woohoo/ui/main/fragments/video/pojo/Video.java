package com.sb.woohoo.ui.main.fragments.video.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.io.Serializable;
import java.util.HashMap;

public class Video extends BaseModel<Video> implements Serializable {

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SerializedName("id")
    @Expose
    private String id;

    public void setTitle(String title) {
        this.title = title;
    }

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("thumbnails")
    @Expose
    private Video thumbnails;
    @SerializedName("medium")
    @Expose
    private Video medium;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("snippet")
    @Expose
    private Video snippet;

    private String extractedUrl;

    private String type;

    private Integer ordinal;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Video getSnippet() {
        return snippet;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Video getMedium() {
        return medium;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Video getThumbnails() {
        return thumbnails;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {
        if (hashMap.containsKey("query")) {
            this.title = hashMap.get("query");
        }
        if (hashMap.containsKey("ordinal")) {
            this.ordinal = Integer.valueOf(hashMap.get("ordinal"));
        }
        if (hashMap.containsKey("type")) {
            this.type = hashMap.get("type");
        }
    }
}