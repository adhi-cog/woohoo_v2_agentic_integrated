package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parameters {

    @SerializedName("date-time")
    @Expose
    private String dateTime;
    @SerializedName("rname")
    @Expose
    private String rname;
    @SerializedName("recurrence")
    @Expose
    private String recurrence;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

}
