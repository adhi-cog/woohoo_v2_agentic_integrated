package com.sb.woohoo.ui.main.fragments.contacts.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DropIn implements Parcelable {
    public static final Creator<DropIn> CREATOR = new Creator<DropIn>() {
        @Override
        public DropIn createFromParcel(Parcel in) {
            return new DropIn(in);
        }

        @Override
        public DropIn[] newArray(int size) {
            return new DropIn[size];
        }
    };
    @SerializedName("_id")
    private String id;
    @SerializedName("hubId")
    private String hubId;
    @SerializedName("access")
    private boolean access;

    protected DropIn(Parcel in) {
        id = in.readString();
        hubId = in.readString();
        access = in.readByte() != 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(hubId);
        parcel.writeByte((byte) (access ? 1 : 0));
    }
}