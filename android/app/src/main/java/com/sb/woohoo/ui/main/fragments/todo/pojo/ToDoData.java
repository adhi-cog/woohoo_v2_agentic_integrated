package com.sb.woohoo.ui.main.fragments.todo.pojo;

import com.google.gson.annotations.SerializedName;
import com.sb.woohoo.ui.main.pojo.nlp.BaseModel;

import java.util.HashMap;

/**
 * POJO containing information about To Do.
 */
public class ToDoData extends BaseModel<ToDoData> {
    @SerializedName("name")
    private String title;
    @SerializedName("todoId")
    private String toDoID;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToDoID() {
        return toDoID;
    }

    public void setToDoID(String toDoID) {
        this.toDoID = toDoID;
    }

    @Override
    public void parseModel(HashMap<String, String> hashMap) {

    }
}