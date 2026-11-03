package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.template.TemplateData;

import java.io.Serializable;
import java.util.List;

public class NLPData implements Serializable {

    @SerializedName("intentData")
    @Expose
    private IntentData intentData;

    @SerializedName("dialogData")
    @Expose
    private SpeechData fulfillmentData;

    @SerializedName("templateData")
    @Expose
    private TemplateData templateData;

    @SerializedName("deviceAction")
    @Expose
    private String deviceAction;

    @SerializedName("userAction")
    @Expose
    private boolean userAction;

    @SerializedName("speech_data")
    @Expose
    private SpeechData speechData;

    @SerializedName("actions")
    @Expose
    private List<NLPData> actions;

    public SpeechData getSpeechData() {
        return speechData;
    }

    public void setSpeechData(SpeechData speechData) {
        this.speechData = speechData;
    }

    public List<NLPData> getActions() {
        return actions;
    }

    public void setActions(List<NLPData> actions) {
        this.actions = actions;
    }

    public boolean isUserAction() {
        return userAction;
    }

    public void setUserAction(boolean userAction) {
        this.userAction = userAction;
    }

    public SpeechData getFulfillmentData() {
        return fulfillmentData;
    }

    public void setFulfillmentData(SpeechData fulfillmentData) {
        this.fulfillmentData = fulfillmentData;
    }

    public TemplateData getTemplateData() {
        return templateData;
    }

    public void setTemplateData(TemplateData templateData) {
        this.templateData = templateData;
    }

    public IntentData getIntentData() {
        return intentData;
    }

    public void setIntentData(IntentData intentData) {
        this.intentData = intentData;
    }

    public String getDeviceAction() {
        return deviceAction;
    }

    public void setDeviceAction(String deviceAction) {
        this.deviceAction = deviceAction;
    }


    public interface DeviceActions {
        String ACTION_INTENT = "intentAction";
        String ACTION_FULFILLMENT = "dialogAction";
        String ACTION_TEMPLATE = "templateAction";
        String ACTION_WEBVIEW = "webViewAction";
        String ACTION_SPEECH = "speech_data";
        String ACTION_SPEECH_LEGACY = "speechAction";
        String ACTION_MULTI = "multiAction";
    }

}
