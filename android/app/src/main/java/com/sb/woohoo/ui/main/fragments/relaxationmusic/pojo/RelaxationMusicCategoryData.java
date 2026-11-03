package com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RelaxationMusicCategoryData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("propertyId")
    @Expose
    private Integer propertyId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
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

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
