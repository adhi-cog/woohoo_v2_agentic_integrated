package com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hub implements Parcelable {

    public static final Creator<Hub> CREATOR = new Creator<Hub>() {
        @Override
        public Hub createFromParcel(Parcel in) {
            return new Hub(in);
        }

        @Override
        public Hub[] newArray(int size) {
            return new Hub[size];
        }
    };
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("macAddress")
    @Expose
    private String macAddress;
    @SerializedName("ipAddress")
    @Expose
    private String ipAddress;
    @SerializedName("aiEngine")
    @Expose
    private String aiEngine;
    @SerializedName("wifiName")
    @Expose
    private String wifiName;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("long")
    @Expose
    private String _long;
    @SerializedName("appVersion")
    @Expose
    private Integer appVersion;
    @SerializedName("firmwareVersion")
    @Expose
    private Integer firmwareVersion;
    @SerializedName("connected")
    @Expose
    private Boolean connected;
    @SerializedName("isBusy")
    @Expose
    private Boolean isBusy;
    @SerializedName("surveillanceMode")
    @Expose
    private Boolean surveillanceMode;
    @SerializedName("userId")
    @Expose
    private Integer contactUserID;
    @SerializedName("liveFeedAccess")
    @Expose
    private Boolean liveFeedAccess;

    protected Hub(Parcel in) {
        id = in.readString();
        name = in.readString();
        macAddress = in.readString();
        ipAddress = in.readString();
        aiEngine = in.readString();
        wifiName = in.readString();
        locale = in.readString();
        timeZone = in.readString();
        lat = in.readString();
        _long = in.readString();
        if (in.readByte() == 0) {
            appVersion = null;
        } else {
            appVersion = in.readInt();
        }
        if (in.readByte() == 0) {
            firmwareVersion = null;
        } else {
            firmwareVersion = in.readInt();
        }
        byte tmpConnected = in.readByte();
        connected = tmpConnected == 0 ? null : tmpConnected == 1;
        byte tmpIsBusy = in.readByte();
        isBusy = tmpIsBusy == 0 ? null : tmpIsBusy == 1;
        byte tmpSurveillanceMode = in.readByte();
        surveillanceMode = tmpSurveillanceMode == 0 ? null : tmpSurveillanceMode == 1;
        if (in.readByte() == 0) {
            contactUserID = null;
        } else {
            contactUserID = in.readInt();
        }
        byte tmpLiveFeedAccess = in.readByte();
        liveFeedAccess = tmpLiveFeedAccess == 0 ? null : tmpLiveFeedAccess == 1;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAiEngine() {
        return aiEngine;
    }

    public void setAiEngine(String aiEngine) {
        this.aiEngine = aiEngine;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return _long;
    }

    public void setLong(String _long) {
        this._long = _long;
    }

    public Integer getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Integer appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(Integer firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Boolean getSurveillanceMode() {
        return surveillanceMode;
    }

    public void setSurveillanceMode(Boolean surveillanceMode) {
        this.surveillanceMode = surveillanceMode;
    }

    public Boolean getLiveFeedAccess() {
        return liveFeedAccess;
    }

    public Integer getContactUserID() {
        return contactUserID;
    }

    public void setContactUserID(Integer contactUserID) {
        this.contactUserID = contactUserID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(macAddress);
        dest.writeString(ipAddress);
        dest.writeString(aiEngine);
        dest.writeString(wifiName);
        dest.writeString(locale);
        dest.writeString(timeZone);
        dest.writeString(lat);
        dest.writeString(_long);
        if (appVersion == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(appVersion);
        }
        if (firmwareVersion == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(firmwareVersion);
        }
        dest.writeByte((byte) (connected == null ? 0 : connected ? 1 : 2));
        dest.writeByte((byte) (isBusy == null ? 0 : isBusy ? 1 : 2));
        dest.writeByte((byte) (surveillanceMode == null ? 0 : surveillanceMode ? 1 : 2));
        if (contactUserID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(contactUserID);
        }
        dest.writeByte((byte) (liveFeedAccess == null ? 0 : liveFeedAccess ? 1 : 2));
    }
}
