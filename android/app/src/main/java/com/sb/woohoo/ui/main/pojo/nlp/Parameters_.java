package com.sb.woohoo.ui.main.pojo.nlp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parameters_ {

    @SerializedName("recurrence")
    @Expose
    private String recurrence;
    @SerializedName("date-time.original")
    @Expose
    private String dateTimeOriginal;
    @SerializedName("rname")
    @Expose
    private String rname;
    @SerializedName("date-time")
    @Expose
    private String dateTime;
    @SerializedName("rname.original")
    @Expose
    private String rnameOriginal;
    @SerializedName("recurrence.original")
    @Expose
    private String recurrenceOriginal;

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public String getDateTimeOriginal() {
        return dateTimeOriginal;
    }

    public void setDateTimeOriginal(String dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getRnameOriginal() {
        return rnameOriginal;
    }

    public void setRnameOriginal(String rnameOriginal) {
        this.rnameOriginal = rnameOriginal;
    }

    public String getRecurrenceOriginal() {
        return recurrenceOriginal;
    }

    public void setRecurrenceOriginal(String recurrenceOriginal) {
        this.recurrenceOriginal = recurrenceOriginal;
    }

}
