package com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo;

import com.google.gson.annotations.SerializedName;

   
public class SaudiTourismResponse {

   @SerializedName("status")
   int status;

   @SerializedName("message")
   String message;

   @SerializedName("data")
   Data data;

   @SerializedName("error")
   String error;


    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    
    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    public String getError() {
        return error;
    }
    
}