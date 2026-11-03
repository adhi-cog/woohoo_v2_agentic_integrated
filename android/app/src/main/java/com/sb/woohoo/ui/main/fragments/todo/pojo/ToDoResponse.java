package com.sb.woohoo.ui.main.fragments.todo.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ToDoResponse {
    /**
     * POJO for To Do API that returns {@link ToDoData} as a {@link List}.
     */
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<ToDoData> data;
    @SerializedName("error")
    private String error;

    private boolean scrollToLast;

    public boolean isScrollToLast() {
        return scrollToLast;
    }

    public void setScrollToLast(boolean scrollToLast) {
        this.scrollToLast = scrollToLast;
    }

    public String getMessage() {
        return message;
    }

    public List<ToDoData> getData() {
        return data;
    }

    public String getError() {
        return error;
    }

}