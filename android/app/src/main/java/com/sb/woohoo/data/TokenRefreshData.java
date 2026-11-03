package com.sb.woohoo.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenRefreshData {

    @SerializedName("sessionToken")
    @Expose
    private String sessionToken;
    @SerializedName("expiresIn")
    @Expose
    private String expiresIn;
    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
