package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NLPResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private NLPData data;
    @SerializedName("error")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NLPData getData() {
        return data;
    }

    public void setData(NLPData data) {
        this.data = data;
    }

}
