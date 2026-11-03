package com.sb.woohoo.ui.main.fragments.amadeusServices.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmadeusOrderData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    private int imageLocalId;
    private int orderCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageLocalId() {
        return imageLocalId;
    }

    public void setImageLocalId(int imageLocalId) {
        this.imageLocalId = imageLocalId;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
