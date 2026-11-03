package com.sb.woohoo.ui.main.fragments.qna.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.util.HashMap;

public class WolframData extends BaseModel {

    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("image")
    @Expose
    private String image;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public void parseModel(HashMap hashMap) {

    }
}
