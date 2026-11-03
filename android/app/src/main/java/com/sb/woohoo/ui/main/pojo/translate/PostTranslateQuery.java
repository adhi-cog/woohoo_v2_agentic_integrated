package com.sb.woohoo.ui.main.pojo.translate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostTranslateQuery {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("source")
    @Expose
    private String source;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @SerializedName("target")
    @Expose
    private String target;


}
