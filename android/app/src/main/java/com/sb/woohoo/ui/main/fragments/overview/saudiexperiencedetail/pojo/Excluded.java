package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo;

import com.google.gson.annotations.SerializedName;

   
public class Excluded {

   @SerializedName("en")
   String en;

   @SerializedName("ar")
   String ar;


    public void setEn(String en) {
        this.en = en;
    }
    public String getEn() {
        return en;
    }
    
    public void setAr(String ar) {
        this.ar = ar;
    }
    public String getAr() {
        return ar;
    }
    
}