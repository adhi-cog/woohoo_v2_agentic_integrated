package com.sb.woohoo.ui.main.fragments.relaxationmusic;

import java.io.Serializable;
import java.util.List;

public class RelaxationCategory implements Serializable {
    private final String id;
    private final String title;
    private final String subtitle;
    private final int accentColor;
    private final String icon;
    private final String imageUrl;
    private final List<RelaxationTrack> tracks;

    public RelaxationCategory(String id, String title, String subtitle, int accentColor,
                              String icon, List<RelaxationTrack> tracks) {
        this(id, title, subtitle, accentColor, icon, null, tracks);
    }

    public RelaxationCategory(String id, String title, String subtitle, int accentColor,
                              String icon, String imageUrl, List<RelaxationTrack> tracks) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.accentColor = accentColor;
        this.icon = icon;
        this.imageUrl = imageUrl;
        this.tracks = tracks;
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

    public int getAccentColor() {
        return accentColor;
    }

    public String getIcon() {
        return icon;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<RelaxationTrack> getTracks() {
        return tracks;
    }
}
