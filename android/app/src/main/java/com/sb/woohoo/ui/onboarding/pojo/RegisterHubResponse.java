package com.sb.woohoo.ui.onboarding.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * POJO Class for Response Model of API : Register Hub.
 *
 * @author Ritwik Jamuar
 */
public class RegisterHubResponse {
    @SerializedName("status")
    private int success;
    @SerializedName("message")
    private String message;
    @SerializedName("error")
    private String error;

    public int isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}