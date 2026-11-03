package com.sb.woohoo.ui.main.pojo.template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TextTemplate {
    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("background")
    @Expose
    private TemplateBackground background;

    public TemplateBackground getBackground() {
        return background;
    }

    public void setBackground(TemplateBackground background) {
        this.background = background;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
