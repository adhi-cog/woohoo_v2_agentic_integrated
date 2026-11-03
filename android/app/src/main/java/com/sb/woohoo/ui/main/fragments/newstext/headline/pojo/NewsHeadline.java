package com.sb.woohoo.ui.main.fragments.newstext.headline.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsHeadline implements Parcelable {

    public static final Parcelable.Creator<NewsHeadline> CREATOR = new Parcelable.Creator<NewsHeadline>() {
        @Override
        public NewsHeadline createFromParcel(Parcel source) {
            return new NewsHeadline(source);
        }

        @Override
        public NewsHeadline[] newArray(int size) {
            return new NewsHeadline[size];
        }
    };
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private NewsData data;
    @SerializedName("error")
    @Expose
    private String error;

    public NewsHeadline() {
    }

    protected NewsHeadline(Parcel in) {
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.message = in.readString();
        this.data = in.readParcelable(NewsData.class.getClassLoader());
        this.error = in.readString();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NewsData getData() {
        return data;
    }

    public void setData(NewsData data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.status);
        dest.writeString(this.message);
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.error);
    }
}
