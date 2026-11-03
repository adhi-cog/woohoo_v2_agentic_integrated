package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;

import com.google.gson.annotations.SerializedName;

   
public class MapLinks {

   @SerializedName("name")
   String name;

   @SerializedName("label")
   String label;

   @SerializedName("link")
   String link;

   @SerializedName("typeMapLink")
   String typeMapLink;

   @SerializedName("platform")
   String platform;

   @SerializedName("active")
   boolean active;


    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    public String getLink() {
        return link;
    }
    
    public void setTypeMapLink(String typeMapLink) {
        this.typeMapLink = typeMapLink;
    }
    public String getTypeMapLink() {
        return typeMapLink;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public String getPlatform() {
        return platform;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean getActive() {
        return active;
    }
    
}