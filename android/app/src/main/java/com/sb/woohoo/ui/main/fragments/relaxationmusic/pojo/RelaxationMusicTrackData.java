package com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RelaxationMusicTrackData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("propertyId")
    @Expose
    private Integer propertyId;
    @SerializedName("categoryId")
    @Expose
    private int categoryId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("audioUrl")
    @Expose
    private String audioUrl;
    @SerializedName("durationSeconds")
    @Expose
    private Integer durationSeconds;
    @SerializedName("isActive")
    @Expose
    private Boolean active;
    @SerializedName("sortOrder")
    @Expose
    private int sortOrder;

    public int getId() {
        return id;
    }

    public String getScope() {
        return scope;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public Boolean getActive() {
        return active;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
