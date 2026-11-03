package com.sb.woohoo.ui.main.fragments.faq.questions.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Answer {

    @SerializedName("answerData")
    @Expose
    private AnswerData answerData;
    @SerializedName("subType")
    @Expose
    private String subType;
    @SerializedName("type")
    @Expose
    private String type;

    public AnswerData getAnswerData() {
        return answerData;
    }

    public void setAnswerData(AnswerData answerData) {
        this.answerData = answerData;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
