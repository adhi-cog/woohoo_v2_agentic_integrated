package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class IntentData implements Serializable {

    @SerializedName("sessionId")
    @Expose
    private String sessionId;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("appUrl")
    @Expose
    private String appUrl;

    @SerializedName("slots")
    @Expose
    private HashMap<String, Object> slots;



//    @SerializedName("contexts")
//    @Expose
//    private List<Context> contexts = null;

    @SerializedName("speech")
    @Expose
    private SpeechResponse speech;

    @SerializedName("intentName")
    @Expose
    private String intentName;

    @SerializedName("utterance")
    @Expose
    private String utterance;

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public HashMap<String, Object> getSlots() {
        return slots;
    }

//    public List<Context> getContexts() {
//        return contexts;
//    }
//
//    public void setContexts(List<Context> contexts) {
//        this.contexts = contexts;
//    }

    public void setSlots(HashMap<String, Object> slots) {
        this.slots = slots;
    }

    public SpeechResponse getSpeech() {
        return speech;
    }

    public void setSpeech(SpeechResponse speech) {
        this.speech = speech;
    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }
}
