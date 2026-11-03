package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SpeechResponse implements Serializable {

    @SerializedName("onSuccessSpeech")
    @Expose
    private SpeechData onSuccessSpeech;

    @SerializedName("onFailureSpeech")
    @Expose
    private SpeechData onFailureSpeech;

    public SpeechResponse(SpeechData onSuccessSpeech, SpeechData onFailureSpeech) {
        this.onSuccessSpeech = onSuccessSpeech;
        this.onFailureSpeech = onFailureSpeech;
    }

    public SpeechResponse() {
    }

    public SpeechData getOnSuccessSpeech() {
        return onSuccessSpeech;
    }

    public void setOnSuccessSpeech(SpeechData onSuccessSpeech) {
        this.onSuccessSpeech = onSuccessSpeech;
    }

    public SpeechData getOnFailureSpeech() {
        return onFailureSpeech;
    }

    public void setOnFailureSpeech(SpeechData onFailureSpeech) {
        this.onFailureSpeech = onFailureSpeech;
    }
}
