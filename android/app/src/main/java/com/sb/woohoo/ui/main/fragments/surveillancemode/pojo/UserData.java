package com.sb.woohoo.ui.main.fragments.surveillancemode.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("recoveryEmail")
    @Expose
    private String recoveryEmail;
    @SerializedName("isEmailVerified")
    @Expose
    private Boolean isEmailVerified;
    @SerializedName("isMobileNumberVerified")
    @Expose
    private Boolean isMobileNumberVerified;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("pin")
    @Expose
    private String pin;
    @SerializedName("isPinAvailable")
    @Expose
    private Boolean isPinAvailable;
    @SerializedName("voiceId")
    @Expose
    private Integer voiceId;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("distanceUnit")
    @Expose
    private String distanceUnit;
    @SerializedName("temperatureUnit")
    @Expose
    private String temperatureUnit;
    @SerializedName("lastLogin")
    @Expose
    private String lastLogin;
    @SerializedName("isEndUser")
    @Expose
    private Boolean isEndUser;
    @SerializedName("domainUniqueId")
    @Expose
    private String domainUniqueId;
    @SerializedName("userTypeId")
    @Expose
    private Integer userTypeId;

    public Boolean getPinAvailable() {
        return isPinAvailable;
    }

    public void setPinAvailable(Boolean pinAvailable) {
        isPinAvailable = pinAvailable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public Boolean getIsMobileNumberVerified() {
        return isMobileNumberVerified;
    }

    public void setIsMobileNumberVerified(Boolean isMobileNumberVerified) {
        this.isMobileNumberVerified = isMobileNumberVerified;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(Integer voiceId) {
        this.voiceId = voiceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getIsEndUser() {
        return isEndUser;
    }

    public void setIsEndUser(Boolean isEndUser) {
        this.isEndUser = isEndUser;
    }

    public String getDomainUniqueId() {
        return domainUniqueId;
    }

    public void setDomainUniqueId(String domainUniqueId) {
        this.domainUniqueId = domainUniqueId;
    }

    public Integer getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Integer userTypeId) {
        this.userTypeId = userTypeId;
    }

}
