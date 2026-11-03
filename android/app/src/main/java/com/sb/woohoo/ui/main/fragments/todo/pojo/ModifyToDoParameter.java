package com.sb.woohoo.ui.main.fragments.todo.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * POJO Class for API : Modify a To Do RemoteAlertModel.
 */
public class ModifyToDoParameter {
    @SerializedName("todo")
    private ToDoItem toDoItem;

    public void setToDoItem(ToDoItem toDoItem) {
        this.toDoItem = toDoItem;
    }
}