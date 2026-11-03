package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class TitleDetails {

   @SerializedName("mainTitle")
   String mainTitle;

   @SerializedName("readMoreButton")
   String readMoreButton;

   @SerializedName("readLessButton")
   String readLessButton;

   @SerializedName("mainTitleWeight")
   String mainTitleWeight;

   @SerializedName("introTitleWeight")
   String introTitleWeight;

   @SerializedName("introSubtitleWeight")
   String introSubtitleWeight;

   @SerializedName("introDescription")
   String introDescription;

   @SerializedName("categoryTags")
   List<String> categoryTags;

   @SerializedName("cssMarginClass")
   String cssMarginClass;

   @SerializedName("event")
   boolean event;

   @SerializedName("eventDescription")
   String eventDescription;


    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }
    public String getMainTitle() {
        return mainTitle;
    }
    
    public void setReadMoreButton(String readMoreButton) {
        this.readMoreButton = readMoreButton;
    }
    public String getReadMoreButton() {
        return readMoreButton;
    }
    
    public void setReadLessButton(String readLessButton) {
        this.readLessButton = readLessButton;
    }
    public String getReadLessButton() {
        return readLessButton;
    }
    
    public void setMainTitleWeight(String mainTitleWeight) {
        this.mainTitleWeight = mainTitleWeight;
    }
    public String getMainTitleWeight() {
        return mainTitleWeight;
    }
    
    public void setIntroTitleWeight(String introTitleWeight) {
        this.introTitleWeight = introTitleWeight;
    }
    public String getIntroTitleWeight() {
        return introTitleWeight;
    }
    
    public void setIntroSubtitleWeight(String introSubtitleWeight) {
        this.introSubtitleWeight = introSubtitleWeight;
    }
    public String getIntroSubtitleWeight() {
        return introSubtitleWeight;
    }
    
    public void setIntroDescription(String introDescription) {
        this.introDescription = introDescription;
    }
    public String getIntroDescription() {
        return introDescription;
    }
    
    public void setCategoryTags(List<String> categoryTags) {
        this.categoryTags = categoryTags;
    }
    public List<String> getCategoryTags() {
        return categoryTags;
    }
    
    public void setCssMarginClass(String cssMarginClass) {
        this.cssMarginClass = cssMarginClass;
    }
    public String getCssMarginClass() {
        return cssMarginClass;
    }
    
    public void setEvent(boolean event) {
        this.event = event;
    }
    public boolean getEvent() {
        return event;
    }
    
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
    public String getEventDescription() {
        return eventDescription;
    }
    
}