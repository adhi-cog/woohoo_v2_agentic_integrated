package com.sb.woohoo.ui.main.fragments.reservation.booking.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReservationServicesResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<ReservationService> data;
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

    public ArrayList<ReservationService> getData() {
        return data;
    }

    public void setData(ArrayList<ReservationService> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public class ReservationService {
        /*"exceptions": [],
            "emails": [],*/
        int id, timeSlot, perSlotLimit, perBookingLimit, bookingLimit, perUserBookingLimit, domainId, propertyId;
        String name;
        String location;
        String description;
        String thumbnailUrl;
        String backgroundImageUrl;
        String tabletBackgroundImageUrl;
        String start_time;
        String end_time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTimeSlot() {
            return timeSlot;
        }

        public void setTimeSlot(int timeSlot) {
            this.timeSlot = timeSlot;
        }

        public int getPerSlotLimit() {
            return perSlotLimit;
        }

        public void setPerSlotLimit(int perSlotLimit) {
            this.perSlotLimit = perSlotLimit;
        }

        public int getPerBookingLimit() {
            return perBookingLimit;
        }

        public void setPerBookingLimit(int perBookingLimit) {
            this.perBookingLimit = perBookingLimit;
        }

        public int getBookingLimit() {
            return bookingLimit;
        }

        public void setBookingLimit(int bookingLimit) {
            this.bookingLimit = bookingLimit;
        }

        public int getPerUserBookingLimit() {
            return perUserBookingLimit;
        }

        public void setPerUserBookingLimit(int perUserBookingLimit) {
            this.perUserBookingLimit = perUserBookingLimit;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getBackgroundImageUrl() {
            return backgroundImageUrl;
        }

        public void setBackgroundImageUrl(String backgroundImageUrl) {
            this.backgroundImageUrl = backgroundImageUrl;
        }

        public String getTabletBackgroundImageUrl() {
            return tabletBackgroundImageUrl;
        }

        public void setTabletBackgroundImageUrl(String tabletBackgroundImageUrl) {
            this.tabletBackgroundImageUrl = tabletBackgroundImageUrl;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
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

        public String getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(String deletedAt) {
            this.deletedAt = deletedAt;
        }

        String createdAt;
        String updatedAt;
        String deletedAt;


        public boolean isSelected() {
            return isSelected;
        }
        public void setSelected(boolean selected) {
            isSelected = selected;
        }
        private boolean isSelected;
    }
}
