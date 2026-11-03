package com.sb.woohoo.ui.onboarding.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2/19/18.
 */

public class LoginModel {
    @SerializedName("status")
    @Expose
    private int success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private DataModel data;
    @SerializedName("error")
    @Expose
    private String error;

    public int isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DataModel getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}