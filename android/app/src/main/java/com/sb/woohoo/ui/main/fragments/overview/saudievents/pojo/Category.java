package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;

import com.google.gson.annotations.SerializedName;

   
public class Category {

   @SerializedName("id")
   String id;

   @SerializedName("value")
   String value;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    
}