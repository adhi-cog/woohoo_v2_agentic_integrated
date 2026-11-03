package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;
import java.util.List;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

   
public class Filters {

   @SerializedName("city")
   List<City> city;

   @SerializedName("region")
   List<Region> region;

   @SerializedName("category")
   List<Category> category;

   @SerializedName("target")
   List<Target> target;

   @SerializedName("freePaid")
   List<FreePaid> freePaid;

   @SerializedName("season")
   List<Season> season;

   @SerializedName("date")
   List<Date> date;

   @SerializedName("sortBy")
   List<SortBy> sortBy;


    public void setCity(List<City> city) {
        this.city = city;
    }
    public List<City> getCity() {
        return city;
    }
    
    public void setRegion(List<Region> region) {
        this.region = region;
    }
    public List<Region> getRegion() {
        return region;
    }
    
    public void setCategory(List<Category> category) {
        this.category = category;
    }
    public List<Category> getCategory() {
        return category;
    }
    
    public void setTarget(List<Target> target) {
        this.target = target;
    }
    public List<Target> getTarget() {
        return target;
    }
    
    public void setFreePaid(List<FreePaid> freePaid) {
        this.freePaid = freePaid;
    }
    public List<FreePaid> getFreePaid() {
        return freePaid;
    }
    
    public void setSeason(List<Season> season) {
        this.season = season;
    }
    public List<Season> getSeason() {
        return season;
    }
    
    public void setDate(List<Date> date) {
        this.date = date;
    }
    public List<Date> getDate() {
        return date;
    }
    
    public void setSortBy(List<SortBy> sortBy) {
        this.sortBy = sortBy;
    }
    public List<SortBy> getSortBy() {
        return sortBy;
    }
    
}