package com.sb.woohoo.ui.main.fragments.reservation.booking.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DiningRestaurantRequest implements Serializable {

    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("restaurantId")
    private String restaurantId;
    @SerializedName("no_of_person")
    private String no_of_person;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getNo_of_person() {
        return no_of_person;
    }

    public void setNo_of_person(String no_of_person) {
        this.no_of_person = no_of_person;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
