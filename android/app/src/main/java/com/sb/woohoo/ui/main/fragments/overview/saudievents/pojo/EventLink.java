package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;

import com.google.gson.annotations.SerializedName;

   
public class EventLink {

   @SerializedName("hideMap")
   boolean hideMap;

   @SerializedName("url")
   String url;

   @SerializedName("urlWithExtension")
   String urlWithExtension;

   @SerializedName("urlSlingExporter")
   String urlSlingExporter;

   @SerializedName("copy")
   String copy;

   @SerializedName("targetInNewWindow")
   boolean targetInNewWindow;

   @SerializedName("internalUrl")
   String internalUrl;


    public void setHideMap(boolean hideMap) {
        this.hideMap = hideMap;
    }
    public boolean getHideMap() {
        return hideMap;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    
    public void setUrlWithExtension(String urlWithExtension) {
        this.urlWithExtension = urlWithExtension;
    }
    public String getUrlWithExtension() {
        return urlWithExtension;
    }
    
    public void setUrlSlingExporter(String urlSlingExporter) {
        this.urlSlingExporter = urlSlingExporter;
    }
    public String getUrlSlingExporter() {
        return urlSlingExporter;
    }
    
    public void setCopy(String copy) {
        this.copy = copy;
    }
    public String getCopy() {
        return copy;
    }
    
    public void setTargetInNewWindow(boolean targetInNewWindow) {
        this.targetInNewWindow = targetInNewWindow;
    }
    public boolean getTargetInNewWindow() {
        return targetInNewWindow;
    }
    
    public void setInternalUrl(String internalUrl) {
        this.internalUrl = internalUrl;
    }
    public String getInternalUrl() {
        return internalUrl;
    }
    
}