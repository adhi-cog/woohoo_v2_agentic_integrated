package com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo;
import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class DataList implements Serializable {

   @SerializedName("id")
   String id;

   @SerializedName("name")
   String name;

   @SerializedName("description")
   String description;

   @SerializedName("slug")
   String slug;

   @SerializedName("price_start_from")
   String priceStartFrom;

   @SerializedName("city")
   String city;

   @SerializedName("city_id")
   String cityId;

   @SerializedName("primaryDestination")
   String primaryDestination;

   @SerializedName("secondary_destinations")
   List<String> secondaryDestinations;

   @SerializedName("listPrice")
   int listPrice;

   @SerializedName("discounted_price")
   double discountedPrice;

   @SerializedName("backgroundImage")
   String backgroundImage;

   @SerializedName("background_images")
   List<String> backgroundImages;

   @SerializedName("book_online")
   boolean bookOnline;

   @SerializedName("min_capacity")
   int minCapacity;

   @SerializedName("max_capacity")
   int maxCapacity;

   @SerializedName("duration")
   int duration;

   @SerializedName("duration_unit")
   String durationUnit;

   @SerializedName("category_name")
   String categoryName;

   @SerializedName("category_slug")
   String categorySlug;

   @SerializedName("contact_name")
   String contactName;

   @SerializedName("price_type")
   String priceType;

   @SerializedName("price_type_capacity")
   int priceTypeCapacity;

   @SerializedName("package_url")
   String packageUrl;

   @SerializedName("is_dmc")
   boolean isDmc;

   @SerializedName("has_waivers_booking")
   boolean hasWaiversBooking;

   @SerializedName("is_tabby")
   boolean isTabby;

   @SerializedName("waivers_view")
   List<String> waiversView;

   @SerializedName("waivers_required_input")
   List<String> waiversRequiredInput;

   @SerializedName("latitude")
   double latitude;

   @SerializedName("longitude")
   double longitude;

   @SerializedName("with_flexible_pass")
   boolean withFlexiblePass;

   @SerializedName("flexible_pass_valid_for")
   int flexiblePassValidFor;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public String getSlug() {
        return slug;
    }
    
    public void setPriceStartFrom(String priceStartFrom) {
        this.priceStartFrom = priceStartFrom;
    }
    public String getPriceStartFrom() {
        return priceStartFrom;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }
    
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
    public String getCityId() {
        return cityId;
    }
    
    public void setPrimaryDestination(String primaryDestination) {
        this.primaryDestination = primaryDestination;
    }
    public String getPrimaryDestination() {
        return primaryDestination;
    }
    
    public void setSecondaryDestinations(List<String> secondaryDestinations) {
        this.secondaryDestinations = secondaryDestinations;
    }
    public List<String> getSecondaryDestinations() {
        return secondaryDestinations;
    }
    
    public void setListPrice(int listPrice) {
        this.listPrice = listPrice;
    }
    public int getListPrice() {
        return listPrice;
    }
    
    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
    public double getDiscountedPrice() {
        return discountedPrice;
    }
    
    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    public String getBackgroundImage() {
        return backgroundImage;
    }
    
    public void setBackgroundImages(List<String> backgroundImages) {
        this.backgroundImages = backgroundImages;
    }
    public List<String> getBackgroundImages() {
        return backgroundImages;
    }
    
    public void setBookOnline(boolean bookOnline) {
        this.bookOnline = bookOnline;
    }
    public boolean getBookOnline() {
        return bookOnline;
    }
    
    public void setMinCapacity(int minCapacity) {
        this.minCapacity = minCapacity;
    }
    public int getMinCapacity() {
        return minCapacity;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getDuration() {
        return duration;
    }
    
    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }
    public String getDurationUnit() {
        return durationUnit;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }
    public String getCategorySlug() {
        return categorySlug;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public String getContactName() {
        return contactName;
    }
    
    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
    public String getPriceType() {
        return priceType;
    }
    
    public void setPriceTypeCapacity(int priceTypeCapacity) {
        this.priceTypeCapacity = priceTypeCapacity;
    }
    public int getPriceTypeCapacity() {
        return priceTypeCapacity;
    }
    
    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }
    public String getPackageUrl() {
        return packageUrl;
    }
    
    public void setIsDmc(boolean isDmc) {
        this.isDmc = isDmc;
    }
    public boolean getIsDmc() {
        return isDmc;
    }
    
    public void setHasWaiversBooking(boolean hasWaiversBooking) {
        this.hasWaiversBooking = hasWaiversBooking;
    }
    public boolean getHasWaiversBooking() {
        return hasWaiversBooking;
    }
    
    public void setIsTabby(boolean isTabby) {
        this.isTabby = isTabby;
    }
    public boolean getIsTabby() {
        return isTabby;
    }
    
    public void setWaiversView(List<String> waiversView) {
        this.waiversView = waiversView;
    }
    public List<String> getWaiversView() {
        return waiversView;
    }
    
    public void setWaiversRequiredInput(List<String> waiversRequiredInput) {
        this.waiversRequiredInput = waiversRequiredInput;
    }
    public List<String> getWaiversRequiredInput() {
        return waiversRequiredInput;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLatitude() {
        return latitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLongitude() {
        return longitude;
    }
    
    public void setWithFlexiblePass(boolean withFlexiblePass) {
        this.withFlexiblePass = withFlexiblePass;
    }
    public boolean getWithFlexiblePass() {
        return withFlexiblePass;
    }
    
    public void setFlexiblePassValidFor(int flexiblePassValidFor) {
        this.flexiblePassValidFor = flexiblePassValidFor;
    }
    public int getFlexiblePassValidFor() {
        return flexiblePassValidFor;
    }
    
}