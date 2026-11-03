package com.sb.woohoo.ui.main.fragments.contacts.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactsResponse {
    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<ContactsData> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status
        ;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ContactsData> getData() {
        return data;
    }

    public void setData(List<ContactsData> data) {
        this.data = data;
    }
}