package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

   
public class DataList implements Serializable {

   @SerializedName("title")
   @Expose
   String title;

   @SerializedName("startDate")
   @Expose
   String startDate;

   @SerializedName("endDate")
   @Expose
   String endDate;

   @SerializedName("categoryTags")
   @Expose
   List<String> categoryTags;

   @SerializedName("targetGroupTags")
   @Expose
   List<String> targetGroupTags;

   @SerializedName("freePaid")
   @Expose
   String freePaid;

   @SerializedName("copy")
   @Expose
   String copy;

   @SerializedName("city")
   @Expose
   String city;

   @SerializedName("cityId")
   @Expose
   String cityId;

   @SerializedName("cityObj")
   @Expose
   CityObj cityObj;

   @SerializedName("featured")
   @Expose
   boolean featured;

   @SerializedName("copyLinkText")
   @Expose
   String copyLinkText;

   @SerializedName("addToCalendarText")
   @Expose
   String addToCalendarText;

   @SerializedName("shareButtonText")
   @Expose
   String shareButtonText;

   @SerializedName("titleDetails")
   @Expose
   TitleDetails titleDetails;

   @SerializedName("category")
   @Expose
   List<String> category;

   @SerializedName("targetGroup")
   @Expose
   List<String> targetGroup;

   @SerializedName("venue")
   @Expose
   List<String> venue;

   @SerializedName("venueTitles")
   @Expose
   List<String> venueTitles;

   @SerializedName("categoryTitles")
   @Expose
   List<String> categoryTitles;

   @SerializedName("targetTitles")
   @Expose
   List<String> targetTitles;

   @SerializedName("locationIcon")
   @Expose
   String locationIcon;

   @SerializedName("cardImage")
   @Expose
   String cardImage;

   @SerializedName("cardImageMobile")
   @Expose
   String cardImageMobile;

   @SerializedName("featureEventImage")
   @Expose
   String featureEventImage;

   @SerializedName("s7featureEventImage")
   @Expose
   String s7featureEventImage;

   @SerializedName("featureEventMobileImage")
   @Expose
   String featureEventMobileImage;

   @SerializedName("sliderImage")
   @Expose
   String sliderImage;

   @SerializedName("calendarEndDate")
   @Expose
   Date calendarEndDate;

   @SerializedName("calendarStartDate")
   @Expose
   Date calendarStartDate;

   @SerializedName("endDateCal")
   @Expose
   long endDateCal;

   @SerializedName("startDateCal")
   @Expose
   long startDateCal;

   @SerializedName("startTimeCal")
   @Expose
   long startTimeCal;

   @SerializedName("endTimeCal")
   @Expose
   long endTimeCal;

   @SerializedName("roundTheClock")
   @Expose
   boolean roundTheClock;

   @SerializedName("startTime")
   @Expose
   String startTime;

   @SerializedName("endTime")
   @Expose
   String endTime;

   @SerializedName("latitude")
   @Expose
   String latitude;

   @SerializedName("longitude")
   @Expose
   String longitude;

   @SerializedName("zoom")
   @Expose
   String zoom;

   @SerializedName("shortDescription")
   @Expose
   String shortDescription;

   @SerializedName("noSeasonTitle")
   @Expose
   String noSeasonTitle;

   @SerializedName("showOnMap")
   @Expose
   boolean showOnMap;

   @SerializedName("startDateMonth")
   @Expose
   String startDateMonth;

   @SerializedName("endDateMonth")
   @Expose
   String endDateMonth;

   @SerializedName("image")
   @Expose
   Image image;

   @SerializedName("id")
   @Expose
   String id;

   @SerializedName("appId")
   @Expose
   String appId;

   @SerializedName("year")
   @Expose
   String year;

   @SerializedName("startDateDay")
   @Expose
   String startDateDay;

   @SerializedName("endDateDay")
   @Expose
   String endDateDay;

   @SerializedName("displayedStartDate")
   @Expose
   String displayedStartDate;

   @SerializedName("displayedEndDate")
   @Expose
   String displayedEndDate;

   @SerializedName("eventLink")
   @Expose
   EventLink eventLink;

   @SerializedName("path")
   @Expose
   String path;

   @SerializedName("freePaidId")
   @Expose
   String freePaidId;

   @SerializedName("eventLinkCopy")
   @Expose
   String eventLinkCopy;

   @SerializedName("eventLinkAnalyticsDataCopy")
   @Expose
   String eventLinkAnalyticsDataCopy;

   @SerializedName("eventLinkUrl")
   @Expose
   String eventLinkUrl;

   @SerializedName("targetInNewWindow")
   @Expose
   boolean targetInNewWindow;

   @SerializedName("favId")
   @Expose
   String favId;

   @SerializedName("favCategory")
   @Expose
   String favCategory;

   @SerializedName("link")
   @Expose
   String link;

   @SerializedName("urlSlingExporter")
   @Expose
   String urlSlingExporter;

   @SerializedName("tags")
   @Expose
   List<Tags> tags;

   @SerializedName("internalBooking")
   @Expose
   boolean internalBooking;

   @SerializedName("eventType")
   @Expose
   String eventType;

   @SerializedName("weekendEvent")
   @Expose
   boolean weekendEvent;

   @SerializedName("featuredInMap")
   @Expose
   boolean featuredInMap;

   @SerializedName("mapLinks")
   @Expose
   List<MapLinks> mapLinks;

   @SerializedName("createdDate")
   @Expose
   Date createdDate;

   @SerializedName("lastModified")
   @Expose
   Date lastModified;

   @SerializedName("channel")
   @Expose
   List<String> channel;

   @SerializedName("showEndDate")
   @Expose
   boolean showEndDate;

   @SerializedName("halaYallaLink")
   @Expose
   boolean halaYallaLink;

   @SerializedName("tripPlan")
   @Expose
   boolean tripPlan;

    @SerializedName("package_url")
    @Expose
    String packageUrl;

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }
    public String getPackageUrl() {
        return packageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getStartDate() {
        return startDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getEndDate() {
        return endDate;
    }
    
    public void setCategoryTags(List<String> categoryTags) {
        this.categoryTags = categoryTags;
    }
    public List<String> getCategoryTags() {
        return categoryTags;
    }
    
    public void setTargetGroupTags(List<String> targetGroupTags) {
        this.targetGroupTags = targetGroupTags;
    }
    public List<String> getTargetGroupTags() {
        return targetGroupTags;
    }
    
    public void setFreePaid(String freePaid) {
        this.freePaid = freePaid;
    }
    public String getFreePaid() {
        return freePaid;
    }
    
    public void setCopy(String copy) {
        this.copy = copy;
    }
    public String getCopy() {
        return copy;
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
    
    public void setCityObj(CityObj cityObj) {
        this.cityObj = cityObj;
    }
    public CityObj getCityObj() {
        return cityObj;
    }
    
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    public boolean getFeatured() {
        return featured;
    }
    
    public void setCopyLinkText(String copyLinkText) {
        this.copyLinkText = copyLinkText;
    }
    public String getCopyLinkText() {
        return copyLinkText;
    }
    
    public void setAddToCalendarText(String addToCalendarText) {
        this.addToCalendarText = addToCalendarText;
    }
    public String getAddToCalendarText() {
        return addToCalendarText;
    }
    
    public void setShareButtonText(String shareButtonText) {
        this.shareButtonText = shareButtonText;
    }
    public String getShareButtonText() {
        return shareButtonText;
    }
    
    public void setTitleDetails(TitleDetails titleDetails) {
        this.titleDetails = titleDetails;
    }
    public TitleDetails getTitleDetails() {
        return titleDetails;
    }
    
    public void setCategory(List<String> category) {
        this.category = category;
    }
    public List<String> getCategory() {
        return category;
    }
    
    public void setTargetGroup(List<String> targetGroup) {
        this.targetGroup = targetGroup;
    }
    public List<String> getTargetGroup() {
        return targetGroup;
    }
    
    public void setVenue(List<String> venue) {
        this.venue = venue;
    }
    public List<String> getVenue() {
        return venue;
    }
    
    public void setVenueTitles(List<String> venueTitles) {
        this.venueTitles = venueTitles;
    }
    public List<String> getVenueTitles() {
        return venueTitles;
    }
    
    public void setCategoryTitles(List<String> categoryTitles) {
        this.categoryTitles = categoryTitles;
    }
    public List<String> getCategoryTitles() {
        return categoryTitles;
    }
    
    public void setTargetTitles(List<String> targetTitles) {
        this.targetTitles = targetTitles;
    }
    public List<String> getTargetTitles() {
        return targetTitles;
    }
    
    public void setLocationIcon(String locationIcon) {
        this.locationIcon = locationIcon;
    }
    public String getLocationIcon() {
        return locationIcon;
    }
    
    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }
    public String getCardImage() {
        return cardImage;
    }
    
    public void setCardImageMobile(String cardImageMobile) {
        this.cardImageMobile = cardImageMobile;
    }
    public String getCardImageMobile() {
        return cardImageMobile;
    }
    
    public void setFeatureEventImage(String featureEventImage) {
        this.featureEventImage = featureEventImage;
    }
    public String getFeatureEventImage() {
        return featureEventImage;
    }
    
    public void setS7featureEventImage(String s7featureEventImage) {
        this.s7featureEventImage = s7featureEventImage;
    }
    public String getS7featureEventImage() {
        return s7featureEventImage;
    }
    
    public void setFeatureEventMobileImage(String featureEventMobileImage) {
        this.featureEventMobileImage = featureEventMobileImage;
    }
    public String getFeatureEventMobileImage() {
        return featureEventMobileImage;
    }
    
    public void setSliderImage(String sliderImage) {
        this.sliderImage = sliderImage;
    }
    public String getSliderImage() {
        return sliderImage;
    }
    
    public void setCalendarEndDate(Date calendarEndDate) {
        this.calendarEndDate = calendarEndDate;
    }
    public Date getCalendarEndDate() {
        return calendarEndDate;
    }
    
    public void setCalendarStartDate(Date calendarStartDate) {
        this.calendarStartDate = calendarStartDate;
    }
    public Date getCalendarStartDate() {
        return calendarStartDate;
    }
    
    public void setEndDateCal(int endDateCal) {
        this.endDateCal = endDateCal;
    }
    public long getEndDateCal() {
        return endDateCal;
    }
    
    public void setStartDateCal(int startDateCal) {
        this.startDateCal = startDateCal;
    }
    public long getStartDateCal() {
        return startDateCal;
    }
    
    public void setStartTimeCal(int startTimeCal) {
        this.startTimeCal = startTimeCal;
    }
    public long getStartTimeCal() {
        return startTimeCal;
    }
    
    public void setEndTimeCal(int endTimeCal) {
        this.endTimeCal = endTimeCal;
    }
    public long getEndTimeCal() {
        return endTimeCal;
    }
    
    public void setRoundTheClock(boolean roundTheClock) {
        this.roundTheClock = roundTheClock;
    }
    public boolean getRoundTheClock() {
        return roundTheClock;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getStartTime() {
        return startTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getEndTime() {
        return endTime;
    }
    
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLatitude() {
        return latitude;
    }
    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLongitude() {
        return longitude;
    }
    
    public void setZoom(String zoom) {
        this.zoom = zoom;
    }
    public String getZoom() {
        return zoom;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setNoSeasonTitle(String noSeasonTitle) {
        this.noSeasonTitle = noSeasonTitle;
    }
    public String getNoSeasonTitle() {
        return noSeasonTitle;
    }
    
    public void setShowOnMap(boolean showOnMap) {
        this.showOnMap = showOnMap;
    }
    public boolean getShowOnMap() {
        return showOnMap;
    }
    
    public void setStartDateMonth(String startDateMonth) {
        this.startDateMonth = startDateMonth;
    }
    public String getStartDateMonth() {
        return startDateMonth;
    }
    
    public void setEndDateMonth(String endDateMonth) {
        this.endDateMonth = endDateMonth;
    }
    public String getEndDateMonth() {
        return endDateMonth;
    }
    
    public void setImage(Image image) {
        this.image = image;
    }
    public Image getImage() {
        return image;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getAppId() {
        return appId;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    public String getYear() {
        return year;
    }
    
    public void setStartDateDay(String startDateDay) {
        this.startDateDay = startDateDay;
    }
    public String getStartDateDay() {
        return startDateDay;
    }
    
    public void setEndDateDay(String endDateDay) {
        this.endDateDay = endDateDay;
    }
    public String getEndDateDay() {
        return endDateDay;
    }
    
    public void setDisplayedStartDate(String displayedStartDate) {
        this.displayedStartDate = displayedStartDate;
    }
    public String getDisplayedStartDate() {
        return displayedStartDate;
    }
    
    public void setDisplayedEndDate(String displayedEndDate) {
        this.displayedEndDate = displayedEndDate;
    }
    public String getDisplayedEndDate() {
        return displayedEndDate;
    }
    
    public void setEventLink(EventLink eventLink) {
        this.eventLink = eventLink;
    }
    public EventLink getEventLink() {
        return eventLink;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
    
    public void setFreePaidId(String freePaidId) {
        this.freePaidId = freePaidId;
    }
    public String getFreePaidId() {
        return freePaidId;
    }
    
    public void setEventLinkCopy(String eventLinkCopy) {
        this.eventLinkCopy = eventLinkCopy;
    }
    public String getEventLinkCopy() {
        return eventLinkCopy;
    }
    
    public void setEventLinkAnalyticsDataCopy(String eventLinkAnalyticsDataCopy) {
        this.eventLinkAnalyticsDataCopy = eventLinkAnalyticsDataCopy;
    }
    public String getEventLinkAnalyticsDataCopy() {
        return eventLinkAnalyticsDataCopy;
    }
    
    public void setEventLinkUrl(String eventLinkUrl) {
        this.eventLinkUrl = eventLinkUrl;
    }
    public String getEventLinkUrl() {
        return eventLinkUrl;
    }
    
    public void setTargetInNewWindow(boolean targetInNewWindow) {
        this.targetInNewWindow = targetInNewWindow;
    }
    public boolean getTargetInNewWindow() {
        return targetInNewWindow;
    }
    
    public void setFavId(String favId) {
        this.favId = favId;
    }
    public String getFavId() {
        return favId;
    }
    
    public void setFavCategory(String favCategory) {
        this.favCategory = favCategory;
    }
    public String getFavCategory() {
        return favCategory;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    public String getLink() {
        return link;
    }
    
    public void setUrlSlingExporter(String urlSlingExporter) {
        this.urlSlingExporter = urlSlingExporter;
    }
    public String getUrlSlingExporter() {
        return urlSlingExporter;
    }
    
    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }
    public List<Tags> getTags() {
        return tags;
    }
    
    public void setInternalBooking(boolean internalBooking) {
        this.internalBooking = internalBooking;
    }
    public boolean getInternalBooking() {
        return internalBooking;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public String getEventType() {
        return eventType;
    }
    
    public void setWeekendEvent(boolean weekendEvent) {
        this.weekendEvent = weekendEvent;
    }
    public boolean getWeekendEvent() {
        return weekendEvent;
    }
    
    public void setFeaturedInMap(boolean featuredInMap) {
        this.featuredInMap = featuredInMap;
    }
    public boolean getFeaturedInMap() {
        return featuredInMap;
    }
    
    public void setMapLinks(List<MapLinks> mapLinks) {
        this.mapLinks = mapLinks;
    }
    public List<MapLinks> getMapLinks() {
        return mapLinks;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    public Date getLastModified() {
        return lastModified;
    }
    
    public void setChannel(List<String> channel) {
        this.channel = channel;
    }
    public List<String> getChannel() {
        return channel;
    }
    
    public void setShowEndDate(boolean showEndDate) {
        this.showEndDate = showEndDate;
    }
    public boolean getShowEndDate() {
        return showEndDate;
    }
    
    public void setHalaYallaLink(boolean halaYallaLink) {
        this.halaYallaLink = halaYallaLink;
    }
    public boolean getHalaYallaLink() {
        return halaYallaLink;
    }
    
    public void setTripPlan(boolean tripPlan) {
        this.tripPlan = tripPlan;
    }
    public boolean getTripPlan() {
        return tripPlan;
    }
    
}