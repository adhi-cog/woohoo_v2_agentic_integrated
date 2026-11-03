package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;

import com.google.gson.annotations.SerializedName;

   
public class Tags {

   @SerializedName("id")
   String id;

   @SerializedName("copy")
   String copy;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setCopy(String copy) {
        this.copy = copy;
    }
    public String getCopy() {
        return copy;
    }
    
}