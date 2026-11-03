package com.sb.woohoo.ui.main.pojo.template;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.SpeechData;

import java.io.Serializable;
import java.util.HashMap;

public class TemplateData<T> implements Serializable {

    @SerializedName("displayTemplate")
    @Expose
    private HashMap<String, Object> displayTemplate;

    @SerializedName("outputSpeech")
    @Expose
    private SpeechData outputSpeech;

    private T template;

    public SpeechData getOutputSpeech() {
        return outputSpeech;
    }

    public void setOutputSpeech(SpeechData outputSpeech) {
        this.outputSpeech = outputSpeech;
    }

    public HashMap<String, Object> getDisplayTemplate() {
        return displayTemplate;
    }

    public void setDisplayTemplate(HashMap<String, Object> displayTemplate) {
        this.displayTemplate = displayTemplate;
    }

    public T getTemplate() {
        return template;
    }

    public void setTemplate(T template) {
        this.template = template;
    }

    public interface TemplateType {
        String TEXT = "Text";
        String TEXT_LIST = "Text-list";
        String IMAGE = "Image";
        String IMAGE_LIST = "Image Carousel";
        String VIDEO = "Video";
        String VIDEO_LIST = "Video Carousel";
    }
}
