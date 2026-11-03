package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FulfilmentResponse implements Serializable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("slotToFullfil")
    @Expose
    private String fulfilmentSlot;
    @SerializedName("outputSpeech")
    @Expose
    private SpeechData outputSpeech;

    public FulfilmentResponse(String type, String fulfilmentSlot, SpeechData outputSpeech) {
        this.type = type;
        this.fulfilmentSlot = fulfilmentSlot;
        this.outputSpeech = outputSpeech;
    }

    public FulfilmentResponse() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFulfilmentSlot() {
        return fulfilmentSlot;
    }

    public void setFulfilmentSlot(String fulfilmentSlot) {
        this.fulfilmentSlot = fulfilmentSlot;
    }

    public SpeechData getOutputSpeech() {
        return outputSpeech;
    }

    public void setOutputSpeech(SpeechData outputSpeech) {
        this.outputSpeech = outputSpeech;
    }
}
