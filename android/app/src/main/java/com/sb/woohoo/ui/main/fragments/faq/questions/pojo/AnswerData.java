package com.sb.woohoo.ui.main.fragments.faq.questions.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnswerData implements Parcelable {

    @SerializedName("data")
    @Expose
    private String data;

    protected AnswerData(Parcel in) {
        data = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AnswerData> CREATOR = new Creator<AnswerData>() {
        @Override
        public AnswerData createFromParcel(Parcel in) {
            return new AnswerData(in);
        }

        @Override
        public AnswerData[] newArray(int size) {
            return new AnswerData[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
