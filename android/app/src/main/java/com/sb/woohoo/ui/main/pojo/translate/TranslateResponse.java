package com.sb.woohoo.ui.main.pojo.translate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.NLPData;

public class TranslateResponse {

    @SerializedName("output_text")
    @Expose
    private String output_text;
    @SerializedName("in_text")
    @Expose
    private String in_text;

    @SerializedName("source_language_code")
    @Expose
    private String source_code;
    @SerializedName("target_language_code")
    @Expose
    private String target_code;

    public String getSource_code() {
        return source_code;
    }

    public void setSource_code(String source_code) {
        this.source_code = source_code;
    }

    public String getTarget_code() {
        return target_code;
    }

    public void setTarget_code(String target_code) {
        this.target_code = target_code;
    }


    public String getOutput_text() {
        return output_text;
    }

    public void setOutput_text(String output_text) {
        this.output_text = output_text;
    }

    public String getIn_text() {
        return in_text;
    }

    public void setIn_text(String in_text) {
        this.in_text = in_text;
    }
}
