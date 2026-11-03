package com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

   
public class Data {

   @SerializedName("id")
   String id;

   @SerializedName("name")
   String name;

   @SerializedName("primaryDestination")
   String primaryDestination;

   @SerializedName("city")
   String city;

   @SerializedName("formattedAddress")
   String formattedAddress;
   @SerializedName("ageFrom")
   String ageFrom;

   @SerializedName("ageTo")
   String ageTo;
//
   @SerializedName("images")
   List<String> images;

   @SerializedName("refundPolicy")
   String refundPolicy;

    @SerializedName("qrLink")
    String qrLink;

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String qrLink() {
        return qrLink;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setPrimaryDestination(String primaryDestination) {
        this.primaryDestination = primaryDestination;
    }
    public String getPrimaryDestination() {
        return primaryDestination;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
    public String getFormattedAddress() {
        return formattedAddress;
    }
    public void setAgeFrom(String ageFrom) {
        this.ageFrom = ageFrom;
    }
    public String getAgeFrom() {
        return ageFrom;
    }

    public void setAgeTo(String ageTo) {
        this.ageTo = ageTo;
    }
    public String getAgeTo() {
        return ageTo;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }
    public List<String> getImages() {
        return images;
    }

    public void setRefundPolicy(String refundPolicy) {
        this.refundPolicy = refundPolicy;
    }
    public String getRefundPolicy() {
        return refundPolicy;
    }
}