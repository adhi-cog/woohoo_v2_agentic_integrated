package com.sb.woohoo.ui.main.fragments.interel.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DynamicUtility {

    @SerializedName("states")
    @Expose
    private List<State> states = null;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("itemDesc")
    @Expose
    private String itemDesc;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("currentState")
    @Expose
    private String currentState;
    @SerializedName("type")
    @Expose
    private String type;

    private int drawableImageIcon;

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
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

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDrawableImageIcon() {
        return drawableImageIcon;
    }

    public void setDrawableImageIcon(int drawableImageIcon) {
        this.drawableImageIcon = drawableImageIcon;
    }

    public class State {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("stateName")
        @Expose
        private String stateName;
        @SerializedName("stateValue")
        @Expose
        private String stateValue;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }

        public String getStateValue() {
            return stateValue;
        }

        public void setStateValue(String stateValue) {
            this.stateValue = stateValue;
        }

    }
}
