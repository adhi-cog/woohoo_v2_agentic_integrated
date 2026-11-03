package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;

import com.google.gson.annotations.SerializedName;

   
public class Image {

   @SerializedName("src")
   String src;


    public void setSrc(String src) {
        this.src = src;
    }
    public String getSrc() {
        return src;
    }
    
}