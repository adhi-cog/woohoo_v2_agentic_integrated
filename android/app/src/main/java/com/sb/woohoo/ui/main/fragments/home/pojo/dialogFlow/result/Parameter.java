package com.sb.woohoo.ui.main.fragments.home.pojo.dialogFlow.result;

import com.google.gson.annotations.SerializedName;

public class Parameter {
    @SerializedName("date")
    private String date;
    @SerializedName("name.original")
    private String nameOriginal;
    @SerializedName("time.original")
    private String timeOriginal;
    @SerializedName("date.original")
    private String dateOriginal;
    @SerializedName("name")
    private String name;
    @SerializedName("time")
    private String time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNameOriginal() {
        return nameOriginal;
    }

    public void setNameOriginal(String nameOriginal) {
        this.nameOriginal = nameOriginal;
    }

    public String getTimeOriginal() {
        return timeOriginal;
    }

    public void setTimeOriginal(String timeOriginal) {
        this.timeOriginal = timeOriginal;
    }

    public String getDateOriginal() {
        return dateOriginal;
    }

    public void setDateOriginal(String dateOriginal) {
        this.dateOriginal = dateOriginal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}