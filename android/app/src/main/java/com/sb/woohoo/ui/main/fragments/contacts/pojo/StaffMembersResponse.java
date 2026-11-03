package com.sb.woohoo.ui.main.fragments.contacts.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StaffMembersResponse {
    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<StaffMember> data;
    @SerializedName("error")
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

    public List<StaffMember> getData() {
        return data;
    }

    public void setData(List<StaffMember> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}