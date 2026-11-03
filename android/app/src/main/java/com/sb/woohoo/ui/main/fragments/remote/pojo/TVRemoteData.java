package com.sb.woohoo.ui.main.fragments.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TVRemoteData {
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

     public String getImageUrl() {
          return imageUrl;
     }

     public void setImageUrl(String imageUrl) {
          this.imageUrl = imageUrl;
     }

     public String getActionId() {
          return actionId;
     }

     public void setActionId(String actionId) {
          this.actionId = actionId;
     }

     public String getType() {
          return type;
     }

     public void setType(String type) {
          this.type = type;
     }

     @SerializedName("status")
     @Expose
     private String status;

     public String getStatus() {
          return status;
     }

     public void setStatus(String status) {
          this.status = status;
     }

     @SerializedName("name")
     @Expose
     private String name;
     @SerializedName("description")
     @Expose
     private String description;
     @SerializedName("imageUrl")
     @Expose
     private String imageUrl;
     @SerializedName("actionId")
     @Expose
     private String actionId;
     @SerializedName("type")
     @Expose
     private String type;
}
