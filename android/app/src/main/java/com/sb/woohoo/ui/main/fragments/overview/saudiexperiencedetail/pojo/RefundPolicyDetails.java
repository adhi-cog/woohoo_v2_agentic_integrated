package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo;

import com.google.gson.annotations.SerializedName;

   
public class RefundPolicyDetails {

   @SerializedName("ar")
   String ar;

   @SerializedName("en")
   String en;


    public void setAr(String ar) {
        this.ar = ar;
    }
    public String getAr() {
        return ar;
    }
    
    public void setEn(String en) {
        this.en = en;
    }
    public String getEn() {
        return en;
    }
    
}