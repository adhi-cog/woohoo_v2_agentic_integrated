package com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaData {
    @SerializedName("intentId")
    @Expose
    private String intentId;
    @SerializedName("webhookUsed")
    @Expose
    private String webHookUsed;
    @SerializedName("webhookForSlotFillingUsed")
    @Expose
    private String webHookForSlotFillingUsed;
    @SerializedName("intentName")
    @Expose
    private String intentName;

    public String getIntentId() {
        return intentId;
    }

    public void setIntentId(String intentId) {
        this.intentId = intentId;
    }

    public String getWebhookUsed() {
        return webHookUsed;
    }

    public void setWebhookUsed(String webhookUsed) {
        this.webHookUsed = webhookUsed;
    }

    public String getWebHookForSlotFillingUsed() {
        return webHookForSlotFillingUsed;
    }

    public void setWebHookForSlotFillingUsed(String webHookForSlotFillingUsed) {
        this.webHookForSlotFillingUsed = webHookForSlotFillingUsed;
    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }
}