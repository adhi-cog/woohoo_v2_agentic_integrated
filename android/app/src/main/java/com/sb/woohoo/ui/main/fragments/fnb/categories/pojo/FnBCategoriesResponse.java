package com.sb.woohoo.ui.main.fragments.fnb.categories.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FnBCategoriesResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("error")
    @Expose
    private String error;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class Timings {
        @SerializedName("timezone")
        @Expose
        private String timezone;

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        @SerializedName("start_time")
        @Expose
        private String start_time;

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        @SerializedName("end_time")
        @Expose
        private String end_time;
    }
    public static class Category {

        public List<Timings> getTimings() {
            return timings;
        }

        public void setTimings(List<Timings> timings) {
            this.timings = timings;
        }

        @SerializedName("timings")
        @Expose
        private List<Timings> timings = null;
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("images")
        @Expose
        private List<String> images = null;
        @SerializedName("tags")
        @Expose
        private List<String> tags = null;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("hasSubCategory")
        @Expose
        private boolean hasSubCategory;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;
        @SerializedName("deletedAt")
        @Expose
        private Object deletedAt;
        @SerializedName("domainId")
        @Expose
        private int domainId;
        @SerializedName("propertyId")
        @Expose
        private int propertyId;

        @SerializedName("serviceTax")
        @Expose
        private int serviceTax;
        @SerializedName("gst")
        @Expose
        private int gst;
        @SerializedName("convenience")
        @Expose
        private int convenience;
        @SerializedName("gstName")
        @Expose
        private String gstName = null;
        @SerializedName("convenienceName")
        @Expose
        private String convenienceName = null;
        @SerializedName("serviceTaxName")
        @Expose
        private String serviceTaxName = null;
        @SerializedName("is_enabled")
        @Expose
        private String isEnabled;

        public int getServiceTax() {
            return serviceTax;
        }

        public void setServiceTax(int serviceTax) {
            this.serviceTax = serviceTax;
        }

        public int getGst() {
            return gst;
        }

        public void setGst(int gst) {
            this.gst = gst;
        }

        public int getConvenience() {
            return convenience;
        }

        public void setConvenience(int convenience) {
            this.convenience = convenience;
        }

        public String getGstName() {
            return gstName;
        }

        public void setGstName(String gstName) {
            this.gstName = gstName;
        }

        public String getConvenienceName() {
            return convenienceName;
        }

        public void setConvenienceName(String convenienceName) {
            this.convenienceName = convenienceName;
        }

        public String getServiceTaxName() {
            return serviceTaxName;
        }

        public void setServiceTaxName(String serviceTaxName) {
            this.serviceTaxName = serviceTaxName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isHasSubCategory() {
            return hasSubCategory;
        }

        public void setHasSubCategory(boolean hasSubCategory) {
            this.hasSubCategory = hasSubCategory;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

        public int getDomainId() {
            return domainId;
        }

        public void setDomainId(int domainId) {
            this.domainId = domainId;
        }

        public int getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(int propertyId) {
            this.propertyId = propertyId;
        }

        public String getIsEnabled() {
            return isEnabled;
        }

        public void setIsEnabled(String isEnabled) {
            this.isEnabled = isEnabled;
        }

    }

    public class Data {

        @SerializedName("banners")
        @Expose
        private List<Banner> banners = null;
        @SerializedName("categories")
        @Expose
        private List<Category> categories = null;

        public List<Banner> getBanners() {
            return banners;
        }

        public void setBanners(List<Banner> banners) {
            this.banners = banners;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

    }
}
