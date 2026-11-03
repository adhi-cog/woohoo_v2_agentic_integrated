package com.sb.woohoo.ui.main.pojo.socketevent;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HotelCheckOutResponse {

    @SerializedName("eventName")
    @Expose
    private String eventName;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("hubId")
    @Expose
    private String hubId;
    @SerializedName("guestName")
    @Expose
    private String guestName;
    @SerializedName("roomNo")
    @Expose
    private String roomNo;
    @SerializedName("roomName")
    @Expose
    private String roomName;
    @SerializedName("hotelName")
    @Expose
    private String hotelName;
    @SerializedName("checkOutTime")
    @Expose
    private String checkOutTime;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("image")
    @Expose
    private String image;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
