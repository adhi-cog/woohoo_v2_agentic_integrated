package com.sb.woohoo.ui.main.fragments.qna.pojo;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class WolframAlphaParameter {
    @SerializedName("query")
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}