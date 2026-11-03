package com.sb.woohoo.ui.main.fragments.settings.language.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageData {
    @Expose
    @SerializedName("languageCode")
    private String languageCode;
    @Expose
    @SerializedName("label")
    private String label;
    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("domainId")
    private int domainId;
    @Expose
    @SerializedName("introVideo")
    private String introVideo;

    private Boolean selectedLang;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getIntroVideo() {
        return introVideo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public Boolean getSelectedLang() {
        return selectedLang;
    }

    public void setSelectedLang(Boolean selectedLang) {
        this.selectedLang = selectedLang;
    }
}
