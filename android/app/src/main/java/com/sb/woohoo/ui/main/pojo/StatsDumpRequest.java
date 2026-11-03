package com.sb.woohoo.ui.main.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsData;


public class StatsDumpRequest {
    @SerializedName("category_name")
    @Expose
    private String categoryName;

    @SerializedName("event_name")
    @Expose
    private String eventName;

    @SerializedName("intent_identifier")
    @Expose
    private String intentIdentifier;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getIntentIdentifier() {
        return intentIdentifier;
    }

    public void setIntentIdentifier(String intentIdentifier) {
        this.intentIdentifier = intentIdentifier;
    }

    public String getIntentIdentifierName() {
        return intentIdentifierName;
    }

    public void setIntentIdentifierName(String intentIdentifierName) {
        this.intentIdentifierName = intentIdentifierName;
    }

    @SerializedName("intent_identifier_name")
    @Expose
    private String intentIdentifierName;
}
