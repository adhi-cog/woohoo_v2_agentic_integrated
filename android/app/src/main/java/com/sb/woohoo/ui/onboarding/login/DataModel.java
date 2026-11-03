package com.sb.woohoo.ui.onboarding.login;

/**
 * Created by admin on 2/19/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class DataModel implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("id")
    @Expose
    private String userId;

    @SerializedName("email")
    @Expose
    @NonNull
    private String email;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("sessionToken")
    @Expose
    private String sessionToken;

    @SerializedName("expiresIn")
    @Expose
    private String expiryTime;

    @SerializedName("refreshToken")
    @Expose
    private String refreshToken;

    @SerializedName("isLogin")
    @Expose
    private String isLogin;

    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("userType")
    @Expose
    private String userIdType;

    @SerializedName("domainId")
    @Expose
    private String domainId;

    public String getInfrasysUrl() {
        return infrasysUrl;
    }

    public void setInfrasysUrl(String infrasysUrl) {
        this.infrasysUrl = infrasysUrl;
    }

    @SerializedName("infrasysUrl")
    @Expose
    private String infrasysUrl;

    public String getUserIdType() {
        return userIdType;
    }

    public void setUserIdType(String userIdType) {
        this.userIdType = userIdType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }


}