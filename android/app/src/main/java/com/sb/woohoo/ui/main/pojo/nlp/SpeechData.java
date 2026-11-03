package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SpeechData implements Serializable {

    @SerializedName("type")
    @Expose
    private String speechType;
    @SerializedName("text")
    @Expose
    private String speechText;
    @SerializedName("ssml")
    @Expose
    private String speechSsml;
    @SerializedName("playBehavior")
    @Expose
    private String speechPlayBehavior;

    public SpeechData(String speechType, String speechText, String speechSsml, String speechPlayBehavior) {
        this.speechType = speechType;
        this.speechText = speechText;
        this.speechSsml = speechSsml;
        this.speechPlayBehavior = speechPlayBehavior;
    }

    public SpeechData() {
    }

    public String getSpeechType() {
        return speechType;
    }

    public void setSpeechType(String speechType) {
        this.speechType = speechType;
    }

    public String getSpeechText() {
        return speechText;
    }

    public void setSpeechText(String speechText) {
        this.speechText = speechText;
    }

    public String getSpeechSsml() {
        return speechSsml;
    }

    public void setSpeechSsml(String speechSsml) {
        this.speechSsml = speechSsml;
    }

    public String getSpeechPlayBehavior() {
        return speechPlayBehavior;
    }

    public void setSpeechPlayBehavior(String speechPlayBehavior) {
        this.speechPlayBehavior = speechPlayBehavior;
    }
}
