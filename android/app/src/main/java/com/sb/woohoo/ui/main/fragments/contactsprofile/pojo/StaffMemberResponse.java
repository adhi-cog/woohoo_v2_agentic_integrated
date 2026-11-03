package com.sb.woohoo.ui.main.fragments.contactsprofile.pojo;

import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;

import java.util.List;

public class StaffMemberResponse {
    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private StaffMember data;
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

    public StaffMember getData() {
        return data;
    }

    public void setData(StaffMember data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}