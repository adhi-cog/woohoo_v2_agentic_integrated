package com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThingsToSay {

    @SerializedName("skillName")
    @Expose
    private
    String skill;
    @SerializedName("utterances")
    @Expose
    private
    List<String> utterance;

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public List<String> getUtterance() {
        return utterance;
    }

    public void setUtterance(List<String> utterance) {
        this.utterance = utterance;
    }
}
