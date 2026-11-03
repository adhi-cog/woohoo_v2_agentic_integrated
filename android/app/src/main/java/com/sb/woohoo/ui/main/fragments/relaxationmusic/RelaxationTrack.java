package com.sb.woohoo.ui.main.fragments.relaxationmusic;

import java.io.Serializable;

public class RelaxationTrack implements Serializable {
    private final String id;
    private final String title;
    private final String subtitle;
    private final String categoryId;
    private final String categoryName;
    private final String streamUrl;
    private final String durationLabel;

    public RelaxationTrack(String id, String title, String subtitle, String categoryId,
                           String categoryName, String streamUrl, String durationLabel) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.streamUrl = streamUrl;
        this.durationLabel = durationLabel;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getDurationLabel() {
        return durationLabel;
    }
}
